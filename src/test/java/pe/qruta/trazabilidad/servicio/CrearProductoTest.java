package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.EstadoMaduracion;
import pe.qruta.trazabilidad.dominio.producto.ProductoAgro;
import pe.qruta.trazabilidad.dominio.producto.ProductoPecuario;
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorioEnMemoria;


class CrearProductoTest {

    private static final ZoneId ZONA_LIMA =
        ZoneId.of("America/Lima");

    /** Hora del sistema en todas las pruebas: 01/09/2026 a las 08:00. */
    private static final LocalDateTime AHORA =
        LocalDateTime.of(2026, 9, 1, 8, 0);

    private ProductoService servicio;

    private Productor productor;


    @BeforeEach
    void prepararServicio() {

        servicio =
            new ProductoService(
                new ProductoRepositorioEnMemoria(),
                Clock.fixed(AHORA.atZone(ZONA_LIMA).toInstant(), ZONA_LIMA)
            );

        productor = productorDeTipo(TipoProductor.AGROPECUARIO);
    }


    @Test
    void unProductorActivoCreaUnPolloQueNaceEnOrigenSinTransportistaSinPublicarYConCodigoUnico() {

        // Dado
        // un productor agropecuario activo (prepararServicio)

        // Cuando
        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        Producto otroPollo = servicio.crearProducto(datosDePollo(), productor);

        // Entonces
        assertThat(pollo).isInstanceOf(ProductoPecuario.class);

        assertThat(pollo.getEstado())
            .isEqualTo(EstadoProducto.EN_ORIGEN);

        assertThat(pollo.getIntermediarioAsignado()).isNull();

        assertThat(pollo.isPublicado()).isFalse();

        assertThat(pollo.getCodigo())
            .isNotBlank()
            .isNotEqualTo(otroPollo.getCodigo());
    }


    @Test
    void unTransportistaIntentaCrearUnProductoYSeRechaza() {

        // Dado
        Usuario transportista =
            new Usuario("transportista1", "clave123", Rol.INTERMEDIARIO);

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.crearProducto(datosDePollo(), transportista))
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("Solo un productor puede crear productos");
    }


    @Test
    void unProductoConFechaDeProduccionEnElFuturoSeRechaza() {

        // Dado
        DatosProducto datos = datosDePollo();

        datos.setFechaProduccion(LocalDateTime.of(2026, 9, 1, 9, 0));

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.crearProducto(datos, productor))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("La fecha de producción no puede estar en el futuro");
    }


    @Test
    void unPolloProducidoHaceExactamente168HorasSeRechazaPorqueSuVidaUtilEstaAgotada() {

        // Dado
        DatosProducto datos = datosDePollo();

        datos.setFechaProduccion(AHORA.minusHours(168));

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.crearProducto(datos, productor))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("La vida útil del producto ya se agotó");
    }


    @Test
    void unProductorAgricolaIntentaCrearUnPolloYSeRechaza() {

        // Dado
        Productor productorAgricola = productorDeTipo(TipoProductor.AGRICOLA);

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.crearProducto(datosDePollo(), productorAgricola))
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("Un productor AGRICOLA no puede crear productos de categoría PECUARIO");
    }


    @Test
    void unProductorCreaUnaPaltaQueQuedaComoProductoAgroConSuVariedadYCalidad() {

        // Dado
        // un productor agropecuario activo (prepararServicio)

        // Cuando
        Producto palta = servicio.crearProducto(datosDePalta(), productor);

        // Entonces
        assertThat(palta).isInstanceOf(ProductoAgro.class);

        ProductoAgro agro = (ProductoAgro) palta;

        assertThat(agro.getVariedad()).isEqualTo("Hass");

        assertThat(agro.getCalidad()).isEqualTo(1);
    }


    @Test
    void unPolloSinCertificadoSanitarioSenasaSeRechaza() {

        // Dado
        DatosProducto datos = datosDePollo();

        datos.setCertificadoSanitarioSenasa("");

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.crearProducto(datos, productor))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("Los productos pecuarios requieren certificado sanitario SENASA");
    }


    private Productor productorDeTipo(TipoProductor tipoProductor) {

        return new Productor(
            "productor1",
            "Juan Mamani Quispe",
            "juan.mamani@correo.pe",
            "clave123",
            EstadoCuenta.ACTIVA,
            "Granja Mamani",
            TipoDocumento.DNI,
            "45678912",
            "984111222",
            tipoProductor,
            "Cusco"
        );
    }


    /** Pollo producido el 31/08/2026 a las 14:00, con su certificado SENASA. */
    private DatosProducto datosDePollo() {

        DatosProducto datos = new DatosProducto();

        datos.setNombre("Pollo entero");
        datos.setTipoProducto(TipoProducto.POLLO);
        datos.setTipoProduccion(TipoProduccion.ESTANDAR);
        datos.setLote("L-001");
        datos.setFechaProduccion(LocalDateTime.of(2026, 8, 31, 14, 0));
        datos.setEspecie("Gallus gallus domesticus");
        datos.setCodigoPlanta("PL-0457");
        datos.setCertificadoSanitarioSenasa("SENASA-2026-0815");

        return datos;
    }


    /** Palta Hass producida el 30/08/2026 a las 08:00, con certificado fitosanitario. */
    private DatosProducto datosDePalta() {

        DatosProducto datos = new DatosProducto();

        datos.setNombre("Palta Hass");
        datos.setTipoProducto(TipoProducto.PALTA);
        datos.setTipoProduccion(TipoProduccion.ORGANICO);
        datos.setLote("L-002");
        datos.setFechaProduccion(LocalDateTime.of(2026, 8, 30, 8, 0));
        datos.setVariedad("Hass");
        datos.setEstadoMaduracion(EstadoMaduracion.VERDE);
        datos.setCalidad(1);
        datos.setCertificadoFitosanitario("SENASA-FITO-2026-0311");

        return datos;
    }
}
