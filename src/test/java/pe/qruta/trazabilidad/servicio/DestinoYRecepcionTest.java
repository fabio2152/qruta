package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.RelojAjustable;
import pe.qruta.trazabilidad.dominio.CambioDeEstadoNoPermitidoException;
import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.EstadoMaduracion;
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorioEnMemoria;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorioEnMemoria;


class DestinoYRecepcionTest {

    private RelojAjustable reloj;

    private ProductoRepositorioEnMemoria productos;

    private UsuarioRepositorioEnMemoria usuarios;

    private ProductoService servicio;

    private TransporteService transporte;

    private RecepcionService recepcion;

    private Productor productor;

    private Transportista transportista;

    private Transportista otroTransportista;

    private Mercado mercado;

    private Mercado mercadoSoloDePollo;


    @BeforeEach
    void prepararServicios() {

        reloj = new RelojAjustable(horaDelDia(7, 0));

        productos = new ProductoRepositorioEnMemoria();

        usuarios = new UsuarioRepositorioEnMemoria();

        servicio = new ProductoService(productos, reloj);

        transporte = new TransporteService(productos, usuarios);

        recepcion = new RecepcionService(productos, reloj);

        productor = new Productor(
            "productor1", "Juan Mamani Quispe", "juan.mamani@correo.pe", "clave123", EstadoCuenta.ACTIVA,
            "Granja Mamani", TipoDocumento.DNI, "45678912", "984111222", TipoProductor.AGROPECUARIO, "Cusco"
        );

        transportista = transportista("transportista1", "luis.huaman@correo.pe");

        otroTransportista = transportista("transportista2", "ana.flores@correo.pe");

        mercado = mercado("mercado1", "Mercado Mayorista de Lima", Set.of(TipoProducto.POLLO, TipoProducto.PALTA));

        mercadoSoloDePollo = mercado("mercado2", "Mercado Avícola del Callao", Set.of(TipoProducto.POLLO));

        usuarios.guardar(productor);
        usuarios.guardar(transportista);
        usuarios.guardar(otroTransportista);
        usuarios.guardar(mercado);
        usuarios.guardar(mercadoSoloDePollo);
    }


    @Test
    void elTransportistaAsignadoEligeComoDestinoUnMercadoQueNecesitaPalta() {

        // Dado
        Producto palta = paltaAceptadaPorElTransportista();

        // Cuando
        transporte.elegirDestino(palta.getId(), transportista, "mercado1");

        // Entonces
        assertThat(palta.getDestino()).isEqualTo(mercado);
    }


    @Test
    void elTransportistaIntentaElegirComoDestinoAOtroTransportistaYSeRechaza() {

        // Dado
        Producto palta = paltaAceptadaPorElTransportista();

        // Cuando / Entonces
        assertThatThrownBy(() -> transporte.elegirDestino(palta.getId(), transportista, "transportista2"))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("El destino debe ser un mercado o un centro de acopio");
    }


    @Test
    void elTransportistaEligeUnMercadoQueNoNecesitaEseTipoDeProductoYSeRechaza() {

        // Dado
        Producto palta = paltaAceptadaPorElTransportista();

        // Cuando / Entonces
        assertThatThrownBy(() -> transporte.elegirDestino(palta.getId(), transportista, "mercado2"))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("Mercado Avícola del Callao no necesita PALTA");
    }


    @Test
    void elDestinoRegistraLaRecepcionAyLaMedicionQuedaGuardadaConLaHoraDelSistema() {

        // Dado
        Producto palta = paltaEnDestino();

        // Cuando
        recepcion.registrarRecepcion(palta.getId(), mercado, new BigDecimal("5"), new BigDecimal("85"));

        // Entonces
        assertThat(palta.getMediciones())
            .containsExactly(
                new Medicion(horaDelDia(14, 0), new BigDecimal("5"), new BigDecimal("85"), mercado)
            );
    }


    @Test
    void unMercadoQueNoEsElDestinoIntentaRegistrarLaRecepcionYSeRechaza() {

        // Dado
        Producto palta = paltaEnDestino();

        // Cuando / Entonces
        assertThatThrownBy(() ->
            recepcion.registrarRecepcion(palta.getId(), mercadoSoloDePollo, new BigDecimal("5"), new BigDecimal("85"))
        )
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("Solo el destino del producto puede registrar la recepción");
    }


    @Test
    void registrarLaRecepcionDeUnProductoEnTransitoSeRechaza() {

        // Dado
        Producto palta = paltaAceptadaPorElTransportista();

        transporte.elegirDestino(palta.getId(), transportista, "mercado1");

        reloj.ajustarA(horaDelDia(8, 0));

        servicio.cambiarEstado(palta.getId(), EstadoProducto.EN_TRANSITO, transportista);

        // Cuando / Entonces
        assertThatThrownBy(() ->
            recepcion.registrarRecepcion(palta.getId(), mercado, new BigDecimal("5"), new BigDecimal("85"))
        )
            .isInstanceOf(CambioDeEstadoNoPermitidoException.class)
            .hasMessage("Solo se puede registrar la recepción de un producto en EN_DESTINO");
    }


    /** Palta aceptada, con destino mercado1, que viajó de 08:00 a 14:00 y está EN_DESTINO. */
    private Producto paltaEnDestino() {

        Producto palta = paltaAceptadaPorElTransportista();

        transporte.elegirDestino(palta.getId(), transportista, "mercado1");

        reloj.ajustarA(horaDelDia(8, 0));

        servicio.cambiarEstado(palta.getId(), EstadoProducto.EN_TRANSITO, transportista);

        reloj.ajustarA(horaDelDia(14, 0));

        servicio.cambiarEstado(palta.getId(), EstadoProducto.EN_DESTINO, transportista);

        return palta;
    }


    /** Palta creada y publicada por el productor y aceptada por transportista1. */
    private Producto paltaAceptadaPorElTransportista() {

        Producto palta = servicio.crearProducto(datosDePalta(), productor);

        servicio.publicar(palta.getId(), productor);

        transporte.aceptar(palta.getId(), transportista);

        return palta;
    }


    private LocalDateTime horaDelDia(
            int hora,
            int minuto
    ) {
        return LocalDateTime.of(2026, 9, 1, hora, minuto);
    }


    private Transportista transportista(
            String nombreUsuario,
            String correo
    ) {
        return new Transportista(
            nombreUsuario, "Luis Huamán Torres", correo, "clave123", EstadoCuenta.ACTIVA,
            "Transportes Andinos SAC", "20601234567", "987654321", "Lima", "ABC-123"
        );
    }


    private Mercado mercado(
            String nombreUsuario,
            String razonSocial,
            Set<TipoProducto> necesidades
    ) {

        Mercado nuevo = new Mercado(
            nombreUsuario, "Carmen Rojas Villa", nombreUsuario + "@correo.pe", "clave123", EstadoCuenta.ACTIVA,
            razonSocial, "20509876543", "014567890", "Lima"
        );

        nuevo.configurarNecesidades(necesidades);

        return nuevo;
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
