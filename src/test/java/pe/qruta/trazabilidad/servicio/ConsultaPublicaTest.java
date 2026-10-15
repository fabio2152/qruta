package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.RelojAjustable;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
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
import pe.qruta.trazabilidad.dominio.producto.TipoProduccion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorioEnMemoria;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorioEnMemoria;


class ConsultaPublicaTest {

    private RelojAjustable reloj;

    private ProductoService servicio;

    private TransporteService transporte;

    private RecepcionService recepcion;

    private RegulacionService regulacion;

    private ConsultaPublicaService consulta;

    private Productor productor;

    private Transportista transportista;

    private Mercado mercado;

    private Usuario regulador;


    @BeforeEach
    void prepararServicios() {

        reloj = new RelojAjustable(horaDelDia(7, 0));

        ProductoRepositorioEnMemoria productos = new ProductoRepositorioEnMemoria();

        UsuarioRepositorioEnMemoria usuarios = new UsuarioRepositorioEnMemoria();

        servicio = new ProductoService(productos, reloj);

        transporte = new TransporteService(productos, usuarios);

        recepcion = new RecepcionService(productos, reloj);

        regulacion = new RegulacionService(productos, usuarios, reloj);

        consulta = new ConsultaPublicaService(productos, reloj);

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
    void laConsultaPorCodigoDevuelveLosDatosPublicosSinCorreoDocumentoNiTelefono() {

        // Dado
        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        // Cuando
        FichaPublica ficha = consulta.consultar(pollo.getCodigo());

        // Entonces
        assertThat(ficha).isNotNull();

        assertThat(ficha)
            .extracting(
                FichaPublica::nombre,
                FichaPublica::tipoProducto,
                FichaPublica::variedadOEspecie,
                FichaPublica::productor,
                FichaPublica::regionDeOrigen,
                FichaPublica::lote,
                FichaPublica::etapa,
                FichaPublica::certificados
            )
            .containsExactly(
                "Pollo entero",
                TipoProducto.POLLO,
                "Gallus gallus domesticus",
                "Granja Mamani",
                "Cusco",
                "L-001",
                EstadoProducto.EN_ORIGEN,
                List.of("SENASA-2026-0815")
            );

        assertThat(ficha.toString())
            .doesNotContain("juan.mamani@correo.pe", "45678912", "984111222");
    }


    @Test
    void unProductoBloqueadoSeMuestraComoNoAptoParaConsumoConSuMotivo() {

        // Dado
        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        regulacion.bloquear(pollo.getId(), regulador, "Sospecha de contaminación");

        // Cuando
        FichaPublica ficha = consulta.consultar(pollo.getCodigo());

        // Entonces
        assertThat(ficha.veredicto())
            .isEqualTo("NO APTO PARA CONSUMO");

        assertThat(ficha.motivo())
            .isEqualTo("Sospecha de contaminación");
    }


    @Test
    void unProductoEntregadoALasDiezYConsultadoALasDieciseisLlevaSeisHorasEnElMercado() {

        // Dado
        Producto pollo = servicio.crearProducto(datosDePollo(), productor);

        servicio.publicar(pollo.getId(), productor);

        transporte.aceptar(pollo.getId(), transportista);

        transporte.elegirDestino(pollo.getId(), transportista, "mercado1");

        reloj.ajustarA(horaDelDia(8, 0));

        servicio.cambiarEstado(pollo.getId(), EstadoProducto.EN_TRANSITO, transportista);

        reloj.ajustarA(horaDelDia(9, 0));

        servicio.cambiarEstado(pollo.getId(), EstadoProducto.EN_DESTINO, transportista);

        recepcion.registrarRecepcion(pollo.getId(), mercado, new BigDecimal("3"), new BigDecimal("80"));

        reloj.ajustarA(horaDelDia(10, 0));

        servicio.cambiarEstado(pollo.getId(), EstadoProducto.ENTREGADO, transportista);

        reloj.ajustarA(horaDelDia(16, 0));

        // Cuando
        FichaPublica ficha = consulta.consultar(pollo.getCodigo());

        // Entonces
        assertThat(ficha.horasEnElMercado())
            .isEqualTo(6L);
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
}
