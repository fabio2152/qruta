package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.RelojAjustable;
import pe.qruta.trazabilidad.dominio.CambioDeEstadoNoPermitidoException;
import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
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


class RegulacionTest {

    private RelojAjustable reloj;

    private UsuarioRepositorioEnMemoria usuarios;

    private ProductoService servicio;

    private TransporteService transporte;

    private RecepcionService recepcion;

    private RegulacionService regulacion;

    private Productor productor;

    private Transportista transportista;

    private Mercado mercado;

    private Usuario regulador;


    @BeforeEach
    void prepararServicios() {

        reloj = new RelojAjustable(horaDelDia(7, 0));

        ProductoRepositorioEnMemoria productos = new ProductoRepositorioEnMemoria();

        usuarios = new UsuarioRepositorioEnMemoria();

        servicio = new ProductoService(productos, reloj);

        transporte = new TransporteService(productos, usuarios);

        recepcion = new RecepcionService(productos, reloj);

        regulacion = new RegulacionService(productos, usuarios, reloj);

        productor = new Productor(
            "productor1", "Juan Mamani Quispe", "juan.mamani@correo.pe", "clave123", EstadoCuenta.ACTIVA,
            "Granja Mamani", TipoDocumento.DNI, "45678912", "984111222", TipoProductor.AGROPECUARIO, "Cusco"
        );

        transportista = new Transportista(
            "transportista1", "Luis Huamán Torres", "luis.huaman@correo.pe", "clave123", EstadoCuenta.ACTIVA,
            "Transportes Andinos SAC", "20601234567", "987654321", "Lima", "ABC-123"
        );

        mercado = new Mercado(
            "mercado1", "Carmen Rojas Villa", "mercado1@correo.pe", "clave123", EstadoCuenta.ACTIVA,
            "Mercado Mayorista de Lima", "20509876543", "014567890", "Lima"
        );

        mercado.configurarNecesidades(Set.of(TipoProducto.POLLO));

        regulador = new Usuario("regulador1", "clave123", Rol.REGULADOR);

        usuarios.guardar(productor);
        usuarios.guardar(transportista);
        usuarios.guardar(mercado);
        usuarios.guardar(regulador);
    }


    @Test
    void elReguladorBloqueaUnProductoConMotivoYElBloqueoApareceEnElHistorial() {

        // Dado
        Producto pollo = polloEnTransito();

        reloj.ajustarA(horaDelDia(10, 0));

        // Cuando
        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        // Entonces
        assertThat(pollo.getEstado())
            .isEqualTo(EstadoProducto.BLOQUEADO);

        List<CambioEstado> historial = pollo.getHistorial();

        assertThat(historial.get(historial.size() - 1))
            .isEqualTo(new CambioEstado(
                EstadoProducto.EN_TRANSITO,
                EstadoProducto.BLOQUEADO,
                horaDelDia(10, 0),
                regulador,
                "Sospecha de contaminación"
            ));
    }


    @Test
    void unBloqueoSinMotivoSeRechaza() {

        // Dado
        Producto pollo = polloEnTransito();

        // Cuando / Entonces
        assertThatThrownBy(() -> regulacion.bloquear(pollo.getId(), regulador, "  "))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("El motivo del bloqueo es obligatorio");
    }


    @Test
    void unProductoBloqueadoNoPuedeCambiarDeEtapa() {

        // Dado
        Producto pollo = polloEnTransito();

        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.cambiarEstado(pollo.getId(), EstadoProducto.EN_DESTINO, transportista))
            .isInstanceOf(CambioDeEstadoNoPermitidoException.class)
            .hasMessage("El producto está bloqueado y no puede cambiar de etapa");
    }


    @Test
    void elReguladorDesbloqueaElProductoYVuelveALaEtapaQueTenia() {

        // Dado
        Producto pollo = polloEnTransito();

        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        reloj.ajustarA(horaDelDia(11, 0));

        // Cuando
        regulacion.desbloquear(pollo.getId(), regulador, "Análisis de laboratorio negativo");

        // Entonces
        assertThat(pollo.getEstado())
            .isEqualTo(EstadoProducto.EN_TRANSITO);

        List<CambioEstado> historial = pollo.getHistorial();

        assertThat(historial.get(historial.size() - 1))
            .isEqualTo(new CambioEstado(
                EstadoProducto.BLOQUEADO,
                EstadoProducto.EN_TRANSITO,
                horaDelDia(11, 0),
                regulador,
                "Análisis de laboratorio negativo"
            ));
    }


    @Test
    void elAdministradorIntentaBloquearUnProductoYSeRechaza() {

        // Dado
        Producto pollo = polloEnTransito();

        Usuario administrador = new Usuario("admin1", "clave123", Rol.ADMINISTRADOR);

        // Cuando / Entonces
        assertThatThrownBy(() -> regulacion.bloquear(pollo.getId(), administrador, "Prueba"))
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("Solo el regulador puede bloquear o desbloquear productos");
    }


    @Test
    void elReguladorIntentaSuspenderAUnTransportistaOcupadoYSeRechaza() {

        // Dado
        polloEnTransito();

        // Cuando / Entonces
        assertThatThrownBy(() -> regulacion.suspender("transportista1", regulador))
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("No se puede suspender a un transportista OCUPADO: primero debe terminar su traslado");
    }


    @Test
    void elReguladorSuspendeAUnProductorQueYaNoPuedeIniciarSesion() {

        // Dado
        // productor1 activo (prepararServicios)

        // Cuando
        regulacion.suspender("productor1", regulador);

        // Entonces
        assertThat(usuarios.buscarPorNombreUsuario("productor1").orElseThrow().getEstadoCuenta())
            .isEqualTo(EstadoCuenta.SUSPENDIDA);

        assertThatThrownBy(() -> new LoginService(usuarios).iniciarSesion("productor1", "clave123"))
            .isInstanceOf(CuentaNoActivaException.class)
            .hasMessage("Su cuenta está suspendida");
    }


    @Test
    void elReporteCsvTieneUnaFilaPorProductoConLasColumnasDeRe36() {

        // Dado: un pollo recibido a 7,1 °C (bloqueado) y una palta todavía EN_ORIGEN
        Producto pollo = polloEnTransito();

        reloj.ajustarA(horaDelDia(14, 0));

        servicio.cambiarEstado(pollo.getId(), EstadoProducto.EN_DESTINO, transportista);

        recepcion.registrarRecepcion(pollo.getId(), mercado, new BigDecimal("7.1"), new BigDecimal("80"));

        Producto palta = servicio.crearProducto(datosDePalta(), productor);

        // Cuando
        String csv = regulacion.reporteSanitarioCsv(regulador);

        // Entonces
        assertThat(csv)
            .isEqualTo(
                "codigo,tipo,productor,region_origen,etapa,transportista,destino,duracion_transporte_horas,"
                    + "temperatura_recepcion,vida_util_restante_horas,ruptura,nivel_riesgo,apto,bloqueado\n"
                    + pollo.getCodigo() + ",POLLO,Granja Mamani,Cusco,BLOQUEADO,transportista1,"
                    + "Mercado Mayorista de Lima,6,7.1,126,SI,CRITICO,NO,SI\n"
                    + palta.getCodigo() + ",PALTA,Granja Mamani,Cusco,EN_ORIGEN,,,,,,,,,NO\n"
            );
    }


    @Test
    void unProductoBloqueadoNoApareceEnLasPublicacionesDisponibles() {

        // Dado
        Producto pollo = polloPublicado();

        // Cuando
        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        // Entonces
        assertThat(servicio.listarPublicaciones(transportista)).isEmpty();
    }


    @Test
    void unTransportistaIntentaAceptarUnProductoBloqueadoYSeRechaza() {

        // Dado
        Producto pollo = polloPublicado();

        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        // Cuando / Entonces
        assertThatThrownBy(() -> transporte.aceptar(pollo.getId(), transportista))
            .isInstanceOf(ProductoNoDisponibleException.class)
            .hasMessage("El producto está bloqueado y no puede aceptarse");
    }


    @Test
    void elProductorIntentaPublicarUnProductoBloqueadoYSeRechaza() {

        // Dado
        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        // Cuando / Entonces
        assertThatThrownBy(() -> servicio.publicar(pollo.getId(), productor))
            .isInstanceOf(CambioDeEstadoNoPermitidoException.class)
            .hasMessage("Solo se publica un producto EN_ORIGEN y sin transportista");
    }


    /** Pollo creado y publicado, todavía sin transportista. */
    private Producto polloPublicado() {

        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        servicio.publicar(pollo.getId(), productor);

        return pollo;
    }


    /** Pollo aceptado por transportista1, con destino mercado1, que salió a las 08:00. */
    private Producto polloEnTransito() {

        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        servicio.publicar(pollo.getId(), productor);

        transporte.aceptar(pollo.getId(), transportista);

        transporte.elegirDestino(pollo.getId(), transportista, "mercado1");

        reloj.ajustarA(horaDelDia(8, 0));

        servicio.cambiarEstado(pollo.getId(), EstadoProducto.EN_TRANSITO, transportista);

        return pollo;
    }


    private LocalDateTime horaDelDia(
            int hora,
            int minuto
    ) {
        return LocalDateTime.of(2026, 9, 1, hora, minuto);
    }


    /** Pollo producido el 31/08/2026 a las 14:00. */
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
