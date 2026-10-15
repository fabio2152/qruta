package pe.qruta.trazabilidad.repositorio.jpa;

import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CentroDeAcopio;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCarga;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Intermediario;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.PuntoDeRecepcion;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


/**
 * Una sola tabla para toda la jerarquía de usuarios (D-02).
 * La columna tipo_cuenta es la discriminadora: indica qué clase del dominio se reconstruye.
 */
@Entity
@Table(name = "usuarios")
public class UsuarioEntidad {

    static final String USUARIO = "USUARIO";
    static final String PRODUCTOR = "PRODUCTOR";
    static final String TRANSPORTISTA = "TRANSPORTISTA";
    static final String MERCADO = "MERCADO";
    static final String CENTRO_DE_ACOPIO = "CENTRO_DE_ACOPIO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    private String tipoCuenta;

    @Column(nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, length = 100)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoCuenta estadoCuenta;

    @Column(length = 120)
    private String nombreCompleto;

    @Column(length = 120)
    private String correo;

    @Column(length = 120)
    private String razonSocial;

    @Enumerated(EnumType.STRING)
    @Column(length = 5)
    private TipoDocumento tipoDocumento;

    @Column(length = 11)
    private String numeroDocumento;

    @Column(length = 20)
    private String telefono;

    @Column(length = 60)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private TipoProductor tipoProductor;

    @Column(length = 15)
    private String placaVehiculo;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private EstadoCarga estadoCarga;

    @Column(length = 120)
    private String ubicacionActual;

    /** Tipos de producto que necesita el punto de recepción, separados por comas (por ejemplo "POLLO,PALTA"). */
    @Column(length = 60)
    private String necesidades;


    protected UsuarioEntidad() {
    }


    public UsuarioEntidad(Usuario usuario) {
        actualizarDesde(usuario);
    }


    /** Copia en la fila todos los datos del usuario del dominio, según su clase. */
    public void actualizarDesde(Usuario usuario) {

        this.nombreUsuario = usuario.getNombreUsuario();
        this.contrasena = usuario.getContrasena();
        this.rol = usuario.getRol();
        this.estadoCuenta = usuario.getEstadoCuenta();
        this.nombreCompleto = usuario.getNombreCompleto();
        this.correo = usuario.getCorreo();
        this.tipoCuenta = USUARIO;

        if (usuario instanceof Productor productor) {
            this.tipoCuenta = PRODUCTOR;
            this.razonSocial = productor.getRazonSocial();
            this.tipoDocumento = productor.getTipoDocumento();
            this.numeroDocumento = productor.getNumeroDocumento();
            this.telefono = productor.getTelefono();
            this.tipoProductor = productor.getTipoProductor();
            this.region = productor.getRegion();
        }

        if (usuario instanceof Intermediario intermediario) {
            this.razonSocial = intermediario.getRazonSocial();
            this.tipoDocumento = TipoDocumento.RUC;
            this.numeroDocumento = intermediario.getRuc();
            this.telefono = intermediario.getTelefono();
            this.region = intermediario.getRegion();
        }

        if (usuario instanceof Transportista transportista) {
            this.tipoCuenta = TRANSPORTISTA;
            this.placaVehiculo = transportista.getPlacaVehiculo();
            this.estadoCarga = transportista.getEstadoCarga();
            this.ubicacionActual = transportista.getUbicacionActual();
        }

        if (usuario instanceof PuntoDeRecepcion punto) {
            this.necesidades =
                punto.getNecesidades().stream().map(Enum::name).sorted().collect(Collectors.joining(","));
        }

        if (usuario instanceof Mercado) {
            this.tipoCuenta = MERCADO;
        }

        if (usuario instanceof CentroDeAcopio) {
            this.tipoCuenta = CENTRO_DE_ACOPIO;
        }
    }


    public Usuario aDominio() {

        Usuario usuario = crearSegunTipo();

        if (usuario instanceof Transportista transportista) {
            transportista.actualizarUbicacion(ubicacionActual);
        }

        if (usuario instanceof PuntoDeRecepcion punto && necesidades != null && !necesidades.isBlank()) {
            punto.configurarNecesidades(
                Arrays.stream(necesidades.split(",")).map(TipoProducto::valueOf).collect(Collectors.toSet())
            );
        }

        return usuario;
    }


    private Usuario crearSegunTipo() {

        return switch (tipoCuenta) {

            case PRODUCTOR -> new Productor(
                nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta,
                razonSocial, tipoDocumento, numeroDocumento, telefono, tipoProductor, region
            );

            case TRANSPORTISTA -> new Transportista(
                nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta,
                razonSocial, numeroDocumento, telefono, region, placaVehiculo, estadoCarga
            );

            case MERCADO -> new Mercado(
                nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta,
                razonSocial, numeroDocumento, telefono, region
            );

            case CENTRO_DE_ACOPIO -> new CentroDeAcopio(
                nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta,
                razonSocial, numeroDocumento, telefono, region
            );

            default -> new Usuario(
                nombreUsuario, nombreCompleto, correo, contrasena, rol, estadoCuenta
            );
        };
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }
}
