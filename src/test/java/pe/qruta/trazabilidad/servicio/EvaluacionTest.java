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
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
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
}
