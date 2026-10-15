package pe.qruta.trazabilidad.dominio;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pe.qruta.trazabilidad.dominio.cuenta.PuntoDeRecepcion;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


public class Producto {

    private Long id;

    private final String nombre;

    private final Usuario productor;

    private Usuario intermediarioAsignado;

    private EstadoProducto estado = EstadoProducto.EN_ORIGEN;

    private LocalDateTime horaInicioTransporte;

    private LocalDateTime horaLlegada;

    private final List<CambioEstado> historial = new ArrayList<>();

    private String codigo;

    private TipoProducto tipoProducto;

    private TipoProduccion tipoProduccion;

    private String lote;

    private LocalDateTime fechaProduccion;

    private boolean publicado;

    private PuntoDeRecepcion destino;

    private Long version;


    /** Asignación directa: un productor con transportista propio (D-04, fase 1). */
    public Producto(
            String nombre,
            Usuario productor,
            Usuario intermediarioAsignado
    ) {
        this.nombre = nombre;
        this.productor = productor;
        this.intermediarioAsignado = intermediarioAsignado;
    }


    /** Flujo de la plataforma: nace sin transportista, sin publicar y con su código QR (RE-18). */
    protected Producto(
            String codigo,
            Usuario productor,
            DatosProducto datos
    ) {
        this(datos.getNombre(), productor, (Usuario) null);
        this.codigo = codigo;
        this.tipoProducto = datos.getTipoProducto();
        this.tipoProduccion = datos.getTipoProduccion();
        this.lote = datos.getLote();
        this.fechaProduccion = datos.getFechaProduccion();
    }


    public static Producto reconstruir(
            Long id,
            String nombre,
            Usuario productor,
            Usuario intermediarioAsignado,
            EstadoProducto estado,
            LocalDateTime horaInicioTransporte,
            LocalDateTime horaLlegada,
            List<CambioEstado> historial
    ) {

        Producto producto =
            new Producto(nombre, productor, intermediarioAsignado);

        producto.restaurar(id, intermediarioAsignado, estado, horaInicioTransporte, horaLlegada, historial, false, null);

        return producto;
    }


    /**
     * Devuelve al producto el estado que tenía guardado en la base de datos.
     * Lo usan reconstruir(...) y el adaptador JPA para ProductoPecuario y ProductoAgro.
     */
    public void restaurar(
            Long id,
            Usuario intermediarioAsignado,
            EstadoProducto estado,
            LocalDateTime horaInicioTransporte,
            LocalDateTime horaLlegada,
            List<CambioEstado> historial,
            boolean publicado,
            Long version
    ) {
        this.id = id;
        this.intermediarioAsignado = intermediarioAsignado;
        this.estado = estado;
        this.horaInicioTransporte = horaInicioTransporte;
        this.horaLlegada = horaLlegada;
        this.historial.clear();
        this.historial.addAll(historial);
        this.publicado = publicado;
        this.version = version;
    }


    /** Versión leída de la base de datos: permite detectar dos aceptaciones simultáneas (D-13). */
    public Long getVersion() {
        return version;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getNombre() {
        return nombre;
    }


    public Usuario getProductor() {
        return productor;
    }


    public Usuario getIntermediarioAsignado() {
        return intermediarioAsignado;
    }


    public String getCodigo() {
        return codigo;
    }


    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }


    public TipoProduccion getTipoProduccion() {
        return tipoProduccion;
    }


    public String getLote() {
        return lote;
    }


    public LocalDateTime getFechaProduccion() {
        return fechaProduccion;
    }


    public boolean isPublicado() {
        return publicado;
    }


    /** RE-20: solo el productor que creó el producto puede publicarlo. */
    public void publicar(Usuario quienPublica) {

        if (!quienPublica.equals(productor)) {
            throw new OperacionNoAutorizadaException(
                "Solo el productor que creó el producto puede publicarlo"
            );
        }

        publicado = true;
    }


    public PuntoDeRecepcion getDestino() {
        return destino;
    }


    public void asignarDestino(PuntoDeRecepcion destino) {
        this.destino = destino;
    }


    public List<Medicion> getMediciones() {
        return List.of();
    }


    public boolean tieneTransportista() {
        return intermediarioAsignado != null;
    }


    /** Publicado y todavía sin transportista: aparece en las publicaciones disponibles. */
    public boolean estaDisponibleParaTransporte() {
        return publicado && !tieneTransportista();
    }


    public void retirarPublicacion() {
        publicado = false;
    }


    public void asignarIntermediario(Usuario intermediario) {
        intermediarioAsignado = intermediario;
    }


    public boolean esVisiblePara(Usuario usuario) {

        return switch (usuario.getRol()) {
            case PRODUCTOR -> productor.equals(usuario);
            case INTERMEDIARIO -> usuario.equals(intermediarioAsignado);
            case REGULADOR -> true;
        };
    }


    public EstadoProducto getEstado() {
        return estado;
    }


    public void cambiarEstado(
            EstadoProducto nuevoEstado,
            Usuario usuario,
            Clock reloj
    ) {

        validarPermiso(usuario);

        validarCambioDeEtapa(nuevoEstado);

        LocalDateTime ahora = LocalDateTime.now(reloj);

        historial.add(
            new CambioEstado(estado, nuevoEstado, ahora, usuario)
        );

        estado = nuevoEstado;

        registrarHora(nuevoEstado, ahora);
    }


    private void validarPermiso(
            Usuario usuario
    ) {

        if (usuario.getRol() != Rol.INTERMEDIARIO) {
            throw new OperacionNoAutorizadaException(
                "Solo un intermediario puede cambiar el estado de un producto"
            );
        }

        if (!usuario.equals(intermediarioAsignado)) {
            throw new OperacionNoAutorizadaException(
                "El usuario no está asignado a este producto"
            );
        }
    }


    private void validarCambioDeEtapa(
            EstadoProducto nuevoEstado
    ) {

        if (estado.esFinal()) {
            throw new CambioDeEstadoNoPermitidoException(
                "El producto ya fue entregado y no puede cambiar de estado"
            );
        }

        if (!estado.puedePasarA(nuevoEstado)) {
            throw new CambioDeEstadoNoPermitidoException(
                "No se puede pasar de " + estado + " a " + nuevoEstado
            );
        }
    }


    private void registrarHora(
            EstadoProducto nuevoEstado,
            LocalDateTime ahora
    ) {

        if (nuevoEstado == EstadoProducto.EN_TRANSITO) {
            horaInicioTransporte = ahora;
        }

        if (nuevoEstado == EstadoProducto.EN_DESTINO) {
            horaLlegada = ahora;
        }
    }


    public List<CambioEstado> getHistorial() {
        return List.copyOf(historial);
    }


    public LocalDateTime getHoraInicioTransporte() {
        return horaInicioTransporte;
    }


    public LocalDateTime getHoraLlegada() {
        return horaLlegada;
    }


    public Duration getDuracionTransporte() {

        if (horaLlegada == null) {
            throw new DuracionNoDisponibleException(
                "La duración del transporte aún no está disponible: falta la hora de llegada"
            );
        }

        return Duration.between(horaInicioTransporte, horaLlegada);
    }
}
