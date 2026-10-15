package pe.qruta.trazabilidad.dominio;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.PuntoDeRecepcion;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;
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

    private LocalDateTime horaEntrega;

    private final List<CambioEstado> historial = new ArrayList<>();

    private String codigo;

    private TipoProducto tipoProducto;

    private TipoProduccion tipoProduccion;

    private String lote;

    private LocalDateTime fechaProduccion;

    private boolean publicado;

    private PuntoDeRecepcion destino;

    private final List<Medicion> mediciones = new ArrayList<>();

    private final List<Evaluacion> evaluaciones = new ArrayList<>();

    private EstadoProducto estadoAntesDelBloqueo;

    private String motivoBloqueo;

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


    /** Complementa restaurar(...) con el recorrido guardado: destino, entrega, bloqueo y mediciones. */
    public void restaurarRecorrido(
            PuntoDeRecepcion destino,
            LocalDateTime horaEntrega,
            EstadoProducto estadoAntesDelBloqueo,
            String motivoBloqueo,
            List<Medicion> mediciones,
            List<Evaluacion> evaluaciones
    ) {
        this.destino = destino;
        this.horaEntrega = horaEntrega;
        this.estadoAntesDelBloqueo = estadoAntesDelBloqueo;
        this.motivoBloqueo = motivoBloqueo;
        this.mediciones.clear();
        this.mediciones.addAll(mediciones);
        this.evaluaciones.clear();
        this.evaluaciones.addAll(evaluaciones);
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


    /** RE-24 y RE-25: el transportista asignado elige, antes de llegar, un destino activo que necesite el producto. */
    public void elegirDestino(
            Usuario quienElige,
            PuntoDeRecepcion nuevoDestino
    ) {

        if (!quienElige.equals(intermediarioAsignado)) {
            throw new OperacionNoAutorizadaException(
                "Solo el transportista asignado elige el destino"
            );
        }

        if (estado != EstadoProducto.EN_ORIGEN && estado != EstadoProducto.EN_TRANSITO) {
            throw new CambioDeEstadoNoPermitidoException(
                "El destino solo se elige mientras el producto está EN_ORIGEN o EN_TRANSITO"
            );
        }

        if (nuevoDestino.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw new DatoInvalidoException(
                nuevoDestino.getRazonSocial() + " no tiene la cuenta activa"
            );
        }

        if (!nuevoDestino.necesita(tipoProducto)) {
            throw new DatoInvalidoException(
                nuevoDestino.getRazonSocial() + " no necesita " + tipoProducto
            );
        }

        this.destino = nuevoDestino;
    }


    /** Lo saca de la secuencia normal y recuerda la etapa que tenía, para poder desbloquearlo (6.6). */
    public void bloquear(
            String motivo,
            Usuario quienBloquea,
            Clock reloj
    ) {

        if (motivo == null || motivo.isBlank()) {
            throw new DatoInvalidoException(
                "El motivo del bloqueo es obligatorio"
            );
        }

        historial.add(
            new CambioEstado(estado, EstadoProducto.BLOQUEADO, LocalDateTime.now(reloj), quienBloquea, motivo)
        );

        estadoAntesDelBloqueo = estado;
        motivoBloqueo = motivo;
        estado = EstadoProducto.BLOQUEADO;

        // RE-41: si el destino ya registró la recepción, el traslado terminó y el transportista queda libre.
        if (!mediciones.isEmpty() && intermediarioAsignado instanceof Transportista transportista) {
            transportista.liberar();
        }
    }


    /** RE-34: vuelve a la etapa que tenía antes del bloqueo y lo deja registrado en el historial. */
    public void desbloquear(
            String motivo,
            Usuario quienDesbloquea,
            Clock reloj
    ) {
        historial.add(
            new CambioEstado(estado, estadoAntesDelBloqueo, LocalDateTime.now(reloj), quienDesbloquea, motivo)
        );

        estado = estadoAntesDelBloqueo;
        estadoAntesDelBloqueo = null;
        motivoBloqueo = null;
    }


    public EstadoProducto getEstadoAntesDelBloqueo() {
        return estadoAntesDelBloqueo;
    }


    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }


    public void registrarEvaluacion(Evaluacion evaluacion) {
        evaluaciones.add(evaluacion);
    }


    public List<Evaluacion> getEvaluaciones() {
        return List.copyOf(evaluaciones);
    }


    /** RE-38: un producto bloqueado o con riesgo CRITICO no es apto para el consumo. */
    public boolean esAptoParaConsumo() {

        Evaluacion ultima = getUltimaEvaluacion();

        return estado != EstadoProducto.BLOQUEADO
            && (ultima == null || ultima.nivelRiesgo() != NivelRiesgo.CRITICO);
    }


    /** La evaluación más reciente, o null si el producto todavía no fue recibido. */
    public Evaluacion getUltimaEvaluacion() {
        return evaluaciones.isEmpty() ? null : evaluaciones.get(evaluaciones.size() - 1);
    }


    public LocalDateTime getHoraEntrega() {
        return horaEntrega;
    }


    public List<Medicion> getMediciones() {
        return List.copyOf(mediciones);
    }


    /** RE-26: solo el destino, y solo cuando el producto ya llegó (EN_DESTINO). */
    public void registrarRecepcion(Medicion medicion) {

        if (!medicion.registradaPor().equals(destino)) {
            throw new OperacionNoAutorizadaException(
                "Solo el destino del producto puede registrar la recepción"
            );
        }

        if (estado != EstadoProducto.EN_DESTINO) {
            throw new CambioDeEstadoNoPermitidoException(
                "Solo se puede registrar la recepción de un producto en EN_DESTINO"
            );
        }

        if (!mediciones.isEmpty()) {
            throw new CambioDeEstadoNoPermitidoException(
                "La recepción del producto ya fue registrada"
            );
        }

        mediciones.add(medicion);
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
            case INTERMEDIARIO -> usuario.equals(intermediarioAsignado) || usuario.equals(destino);
            case REGULADOR, ADMINISTRADOR -> true;
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

        if (estado == EstadoProducto.BLOQUEADO) {
            throw new CambioDeEstadoNoPermitidoException(
                "El producto está bloqueado y no puede cambiar de etapa"
            );
        }

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

        if (nuevoEstado == EstadoProducto.ENTREGADO && destino != null && mediciones.isEmpty()) {
            throw new CambioDeEstadoNoPermitidoException(
                "El destino aún no registró la recepción del producto"
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

        if (nuevoEstado == EstadoProducto.ENTREGADO) {
            horaEntrega = ahora;

            if (intermediarioAsignado instanceof Transportista transportista) {
                transportista.liberar();
            }
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
