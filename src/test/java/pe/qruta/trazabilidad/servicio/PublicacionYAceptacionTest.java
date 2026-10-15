package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.EstadoMaduracion;
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorioEnMemoria;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorioEnMemoria;


class PublicacionYAceptacionTest {

    private static final ZoneId ZONA_LIMA =
        ZoneId.of("America/Lima");

    /** Hora del sistema en todas las pruebas: 01/09/2026 a las 08:00. */
    private static final LocalDateTime AHORA =
        LocalDateTime.of(2026, 9, 1, 8, 0);

    private ProductoRepositorioEnMemoria productos;

    private UsuarioRepositorioEnMemoria usuarios;

    private ProductoService servicio;

    private Productor productor;

    private Transportista transportista;

    private Transportista otroTransportista;


    @BeforeEach
    void prepararServicios() {

        Clock reloj = Clock.fixed(AHORA.atZone(ZONA_LIMA).toInstant(), ZONA_LIMA);

        productos = new ProductoRepositorioEnMemoria();

        usuarios = new UsuarioRepositorioEnMemoria();

        servicio = new ProductoService(productos, reloj);

        productor = productor("productor1", "juan.mamani@correo.pe");

        transportista = transportista("transportista1", "luis.huaman@correo.pe");

        otroTransportista = transportista("transportista2", "ana.flores@correo.pe");

        usuarios.guardar(productor);
        usuarios.guardar(transportista);
        usuarios.guardar(otroTransportista);
    }


    @Test
    void elProductorPublicaSuProductoYApareceEnLasPublicacionesDeUnTransportistaLibre() {

        // Dado
        Producto palta = servicio.crearProducto(datosDePalta(), productor);

        // Cuando
        servicio.publicar(palta.getId(), productor);

        // Entonces
        assertThat(servicio.listarPublicaciones(transportista))
            .containsExactly(palta);
    }


    private Productor productor(
            String nombreUsuario,
            String correo
    ) {
        return new Productor(
            nombreUsuario,
            "Juan Mamani Quispe",
            correo,
            "clave123",
            EstadoCuenta.ACTIVA,
            "Granja Mamani",
            TipoDocumento.DNI,
            "45678912",
            "984111222",
            TipoProductor.AGROPECUARIO,
            "Cusco"
        );
    }


    private Transportista transportista(
            String nombreUsuario,
            String correo
    ) {
        return new Transportista(
            nombreUsuario,
            "Luis Huamán Torres",
            correo,
            "clave123",
            EstadoCuenta.ACTIVA,
            "Transportes Andinos SAC",
            "20601234567",
            "987654321",
            "Lima",
            "ABC-123"
        );
    }


    /** Palta Hass producida el 30/08/2026 a las 08:00. */
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
