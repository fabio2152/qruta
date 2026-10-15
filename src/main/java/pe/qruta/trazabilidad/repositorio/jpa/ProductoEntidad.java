package pe.qruta.trazabilidad.repositorio.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.EstadoMaduracion;
import pe.qruta.trazabilidad.dominio.producto.ProductoAgro;
import pe.qruta.trazabilidad.dominio.producto.ProductoPecuario;
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


/**
 * Una sola tabla para Producto, ProductoPecuario y ProductoAgro.
 * La columna clase es la discriminadora: BASE (fase 1), PECUARIO o AGRO.
 */
@Entity
@Table(name = "productos")
public class ProductoEntidad {

    private static final String BASE = "BASE";
    private static final String PECUARIO = "PECUARIO";
    private static final String AGRO = "AGRO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Control de concurrencia (D-13): dos aceptaciones simultáneas no pueden ganar las dos. */
    @Version
    private Long version;

    @Column(nullable = false, length = 10)
    private String clase;

    @Column(unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne(optional = false)
    private UsuarioEntidad productor;

    @ManyToOne
    private UsuarioEntidad intermediarioAsignado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estado;

    private LocalDateTime horaInicioTransporte;

    private LocalDateTime horaLlegada;

    private boolean publicado;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TipoProducto tipoProducto;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TipoProduccion tipoProduccion;

    @Column(length = 40)
    private String lote;

    private LocalDateTime fechaProduccion;

    @Column(length = 80)
    private String especie;

    @Column(length = 40)
    private String codigoPlanta;

    @Column(length = 60)
    private String certificadoSanitarioSenasa;

    @Column(length = 60)
    private String variedad;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private EstadoMaduracion estadoMaduracion;

    private Integer calidad;

    @Column(length = 60)
    private String certificadoFitosanitario;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id")
    @OrderBy("id")
    private List<CambioEstadoEntidad> historial = new ArrayList<>();


    protected ProductoEntidad() {
    }


    /** Crea la fila de un producto nuevo con sus datos fijos (los que no cambian después). */
    public ProductoEntidad(
            Producto producto,
            UsuarioEntidad productor
    ) {
        this.nombre = producto.getNombre();
        this.productor = productor;
        this.codigo = producto.getCodigo();
        this.tipoProducto = producto.getTipoProducto();
        this.tipoProduccion = producto.getTipoProduccion();
        this.lote = producto.getLote();
        this.fechaProduccion = producto.getFechaProduccion();
        this.clase = BASE;

        if (producto instanceof ProductoPecuario pecuario) {
            this.clase = PECUARIO;
            this.especie = pecuario.getEspecie();
            this.codigoPlanta = pecuario.getCodigoPlanta();
            this.certificadoSanitarioSenasa = pecuario.getCertificadoSanitarioSenasa();
        }

        if (producto instanceof ProductoAgro agro) {
            this.clase = AGRO;
            this.variedad = agro.getVariedad();
            this.estadoMaduracion = agro.getEstadoMaduracion();
            this.calidad = agro.getCalidad();
            this.certificadoFitosanitario = agro.getCertificadoFitosanitario();
        }
    }


    public Long getId() {
        return id;
    }


    public Long getVersion() {
        return version;
    }


    public List<CambioEstadoEntidad> getHistorial() {
        return historial;
    }


    /** Copia el estado actual del producto de dominio en la entidad. */
    public void actualizarDesde(
            Producto producto,
            UsuarioEntidad intermediario
    ) {
        this.intermediarioAsignado = intermediario;
        this.estado = producto.getEstado();
        this.horaInicioTransporte = producto.getHoraInicioTransporte();
        this.horaLlegada = producto.getHoraLlegada();
        this.publicado = producto.isPublicado();
    }


    public Producto aDominio() {

        Usuario productorDominio = productor.aDominio();

        Usuario intermediario =
            intermediarioAsignado == null ? null : intermediarioAsignado.aDominio();

        List<CambioEstado> cambios =
            historial.stream().map(CambioEstadoEntidad::aDominio).toList();

        Producto producto = switch (clase) {
            case PECUARIO -> new ProductoPecuario(codigo, productorDominio, datos());
            case AGRO -> new ProductoAgro(codigo, productorDominio, datos());
            default -> new Producto(nombre, productorDominio, intermediario);
        };

        producto.restaurar(
            id, intermediario, estado, horaInicioTransporte, horaLlegada, cambios, publicado, version
        );

        return producto;
    }


    private DatosProducto datos() {

        DatosProducto datos = new DatosProducto();

        datos.setNombre(nombre);
        datos.setTipoProducto(tipoProducto);
        datos.setTipoProduccion(tipoProduccion);
        datos.setLote(lote);
        datos.setFechaProduccion(fechaProduccion);
        datos.setEspecie(especie);
        datos.setCodigoPlanta(codigoPlanta);
        datos.setCertificadoSanitarioSenasa(certificadoSanitarioSenasa);
        datos.setVariedad(variedad);
        datos.setEstadoMaduracion(estadoMaduracion);
        datos.setCalidad(calidad);
        datos.setCertificadoFitosanitario(certificadoFitosanitario);

        return datos;
    }
}
