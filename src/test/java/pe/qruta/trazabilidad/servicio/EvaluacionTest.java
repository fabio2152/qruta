package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.RelojAjustable;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCarga;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.EstadoMaduracion;
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorioEnMemoria;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorioEnMemoria;


/**
 * Escenario base (sección 10, ciclo 11): el pollo se produjo el 31/08/2026 a las 14:00,
 * viajó de 08:00 a 14:00 del 01/09/2026 (6 horas) y se recibió a las 14:00 (24 horas después).
 */
class EvaluacionTest {

    private RelojAjustable reloj;

    private ProductoService servicio;

    private TransporteService transporte;

    private RecepcionService recepcion;

    private Productor productor;

    private Transportista transportista;

    private Mercado mercado;

    private Producto recibido;


    @BeforeEach
    void prepararServicios() {

        reloj = new RelojAjustable(LocalDateTime.of(2026, 9, 1, 8, 0));

        ProductoRepositorioEnMemoria productos = new ProductoRepositorioEnMemoria();

        UsuarioRepositorioEnMemoria usuarios = new UsuarioRepositorioEnMemoria();

        servicio = new ProductoService(productos, reloj);

        transporte = new TransporteService(productos, usuarios);

        recepcion = new RecepcionService(productos, reloj);

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

        mercado.configurarNecesidades(Set.of(TipoProducto.POLLO, TipoProducto.PALTA, TipoProducto.BANANO));

        usuarios.guardar(productor);
        usuarios.guardar(transportista);
        usuarios.guardar(mercado);
    }


    @Test
    void unPolloRecibidoATresGradosTiene144HorasDeVidaUtilRiesgoBajoYEsApto() {

        // Dado
        // el pollo del escenario base

        // Cuando
        Evaluacion evaluacion = recibirPolloDelEscenarioBase("3");

        // Entonces
        assertThat(evaluacion)
            .isEqualTo(new Evaluacion(
                horaDelDia(14, 0), new BigDecimal("3"), 144, false, NivelRiesgo.BAJO, true
            ));
    }


    @Test
    void unPolloRecibidoASieteGradosNoTieneRupturaPeroSuTransporteSePenaliza() {

        // Dado
        // el pollo del escenario base, recibido exactamente 3 °C sobre el máximo de su rango (0 °C a 4 °C)

        // Cuando
        Evaluacion evaluacion = recibirPolloDelEscenarioBase("7.0");

        // Entonces: 168 − 24 − (4 − 1) × 6 = 126 horas
        assertThat(evaluacion)
            .isEqualTo(new Evaluacion(
                horaDelDia(14, 0), new BigDecimal("7.0"), 126, false, NivelRiesgo.BAJO, true
            ));
    }


    @Test
    void unPolloRecibidoASieteComaUnoTieneRupturaEsCriticoNoEsAptoYQuedaBloqueado() {

        // Dado
        // el pollo del escenario base, recibido 3,1 °C sobre el máximo de su rango

        // Cuando
        Evaluacion evaluacion = recibirPolloDelEscenarioBase("7.1");

        // Entonces
        assertThat(evaluacion)
            .isEqualTo(new Evaluacion(
                horaDelDia(14, 0), new BigDecimal("7.1"), 126, true, NivelRiesgo.CRITICO, false
            ));

        assertThat(recibido.getEstado())
            .isEqualTo(EstadoProducto.BLOQUEADO);
    }


    @Test
    void unaPaltaRecibidaADiezComaCincoTieneRupturaRiesgoAltoEsAptaYNoSeBloquea() {

        // Dado
        DatosProducto palta = datosDePalta(LocalDateTime.of(2026, 8, 30, 8, 0));

        // Cuando
        Evaluacion evaluacion = recibir(palta, horaDelDia(8, 0), horaDelDia(14, 0), "10.5");

        // Entonces: 504 − 54 − (2 − 1) × 6 = 444 horas
        assertThat(evaluacion)
            .isEqualTo(new Evaluacion(
                horaDelDia(14, 0), new BigDecimal("10.5"), 444, true, NivelRiesgo.ALTO, true
            ));

        assertThat(recibido.getEstado())
            .isEqualTo(EstadoProducto.EN_DESTINO);
    }


    @Test
    void unBananoConSesentaYSeisHorasRestantesTieneRiesgoMedio() {

        // Dado
        DatosProducto banano =
            datosAgricolas("Banano orgánico", TipoProducto.BANANO, "Cavendish", LocalDateTime.of(2026, 9, 1, 8, 0));

        LocalDateTime inicio = LocalDateTime.of(2026, 9, 12, 8, 0);

        LocalDateTime llegada = LocalDateTime.of(2026, 9, 12, 14, 0);

        // Cuando
        Evaluacion evaluacion = recibir(banano, inicio, llegada, "14");

        // Entonces: 336 − 270 = 66 horas, el 20 % o menos de 336
        assertThat(evaluacion)
            .isEqualTo(new Evaluacion(
                llegada, new BigDecimal("14"), 66, false, NivelRiesgo.MEDIO, true
            ));
    }


    @Test
    void unPolloRecibido168HorasDespuesDeSuProduccionNoTieneVidaUtilEsCriticoYQuedaBloqueado() {

        // Dado: producido el 25/08 a las 14:00, recibido el 01/09 a las 14:00
        DatosProducto pollo = datosDePollo(LocalDateTime.of(2026, 8, 25, 14, 0));

        // Cuando
        Evaluacion evaluacion = recibir(pollo, horaDelDia(8, 0), horaDelDia(14, 0), "3");

        // Entonces
        assertThat(evaluacion)
            .isEqualTo(new Evaluacion(
                horaDelDia(14, 0), new BigDecimal("3"), 0, false, NivelRiesgo.CRITICO, false
            ));

        assertThat(recibido.getEstado())
            .isEqualTo(EstadoProducto.BLOQUEADO);
    }


    @Test
    void despuesDelBloqueoAutomaticoElTransportistaVuelveAQuedarLibre() {

        // Dado
        // el pollo del escenario base, recibido a 7,1 °C: la evaluación lo bloquea (T56)

        // Cuando
        recibirPolloDelEscenarioBase("7.1");

        // Entonces (RE-41: el traslado físico ya terminó con la recepción)
        assertThat(transportista.getEstadoCarga())
            .isEqualTo(EstadoCarga.LIBRE);
    }


    private Evaluacion recibirPolloDelEscenarioBase(String temperatura) {
        return recibir(datosDePollo(LocalDateTime.of(2026, 8, 31, 14, 0)), horaDelDia(8, 0), horaDelDia(14, 0), temperatura);
    }


    /** Crea, publica, acepta, elige destino, transporta y recibe el producto a la hora de llegada. */
    private Evaluacion recibir(
            DatosProducto datos,
            LocalDateTime inicioDelTransporte,
            LocalDateTime llegada,
            String temperatura
    ) {

        recibido = servicio.crearProducto(datos, productor);

        servicio.publicar(recibido.getId(), productor);

        transporte.aceptar(recibido.getId(), transportista);

        transporte.elegirDestino(recibido.getId(), transportista, "mercado1");

        reloj.ajustarA(inicioDelTransporte);

        servicio.cambiarEstado(recibido.getId(), EstadoProducto.EN_TRANSITO, transportista);

        reloj.ajustarA(llegada);

        servicio.cambiarEstado(recibido.getId(), EstadoProducto.EN_DESTINO, transportista);

        return recepcion.registrarRecepcion(
            recibido.getId(), mercado, new BigDecimal(temperatura), new BigDecimal("85")
        );
    }


    private LocalDateTime horaDelDia(
            int hora,
            int minuto
    ) {
        return LocalDateTime.of(2026, 9, 1, hora, minuto);
    }


    private DatosProducto datosDePollo(LocalDateTime fechaProduccion) {

        DatosProducto datos = new DatosProducto();

        datos.setNombre("Pollo entero");
        datos.setTipoProducto(TipoProducto.POLLO);
        datos.setTipoProduccion(TipoProduccion.ESTANDAR);
        datos.setLote("L-001");
        datos.setFechaProduccion(fechaProduccion);
        datos.setEspecie("Gallus gallus domesticus");
        datos.setCodigoPlanta("PL-0457");
        datos.setCertificadoSanitarioSenasa("SENASA-2026-0815");

        return datos;
    }


    private DatosProducto datosDePalta(LocalDateTime fechaProduccion) {
        return datosAgricolas("Palta Hass", TipoProducto.PALTA, "Hass", fechaProduccion);
    }


    private DatosProducto datosAgricolas(
            String nombre,
            TipoProducto tipo,
            String variedad,
            LocalDateTime fechaProduccion
    ) {

        DatosProducto datos = new DatosProducto();

        datos.setNombre(nombre);
        datos.setTipoProducto(tipo);
        datos.setTipoProduccion(TipoProduccion.ORGANICO);
        datos.setLote("L-002");
        datos.setFechaProduccion(fechaProduccion);
        datos.setVariedad(variedad);
        datos.setEstadoMaduracion(EstadoMaduracion.VERDE);
        datos.setCalidad(1);
        datos.setCertificadoFitosanitario("SENASA-FITO-2026-0311");

        return datos;
    }
}
