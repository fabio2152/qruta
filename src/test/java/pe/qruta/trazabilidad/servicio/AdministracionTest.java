package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Regulador;
import pe.qruta.trazabilidad.dominio.cuenta.TipoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorioEnMemoria;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorioEnMemoria;


class AdministracionTest {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    private UsuarioRepositorioEnMemoria usuarios;

    private ProductoRepositorioEnMemoria productos;

    private AdministracionService administracion;

    private Usuario administrador;


    @BeforeEach
    void prepararServicios() {

        usuarios = new UsuarioRepositorioEnMemoria();

        productos = new ProductoRepositorioEnMemoria();

        administracion = new AdministracionService(usuarios, productos);

        administrador = new Usuario("admin1", "clave123", Rol.ADMINISTRADOR);

        usuarios.guardar(administrador);
    }


    @Test
    void elAdministradorCreaUnReguladorConCodigoInstitucionalYQuedaActivo() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeRegulador();

        // Cuando
        Usuario creado = administracion.crearRegulador(administrador, solicitud);

        // Entonces
        assertThat(creado).isInstanceOf(Regulador.class);

        assertThat(creado.getRol()).isEqualTo(Rol.REGULADOR);

        assertThat(creado.getEstadoCuenta()).isEqualTo(EstadoCuenta.ACTIVA);

        assertThat(((Regulador) creado).getCodigoInstitucional()).isEqualTo("SENASA-LIM-0042");

        assertThat(usuarios.buscarPorNombreUsuario("regulador2")).contains(creado);
    }


    @Test
    void crearUnReguladorSinCodigoInstitucionalSeRechaza() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeRegulador();

        solicitud.setCodigoInstitucional(" ");

        // Cuando / Entonces
        assertThatThrownBy(() -> administracion.crearRegulador(administrador, solicitud))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("El código institucional es obligatorio");
    }


    @Test
    void elAdministradorApruebaUnMercado() {

        // Dado
        RegistroService registro = new RegistroService(usuarios);

        SolicitudRegistro solicitud = new SolicitudRegistro();

        solicitud.setTipoCuenta(TipoCuenta.MERCADO);
        solicitud.setNombreUsuario("mercado9");
        solicitud.setNombreCompleto("Carmen Rojas Villa");
        solicitud.setCorreo("mercado.santa.anita@correo.pe");
        solicitud.setContrasena("clave123");
        solicitud.setTipoDocumento(TipoDocumento.RUC);
        solicitud.setNumeroDocumento("20509876543");
        solicitud.setRazonSocial("Mercado Mayorista Santa Anita");
        solicitud.setRegion("Lima");

        registro.registrar(solicitud);

        // Cuando
        registro.aprobar("mercado9", administrador);

        // Entonces
        assertThat(usuarios.buscarPorNombreUsuario("mercado9").orElseThrow().getEstadoCuenta())
            .isEqualTo(EstadoCuenta.ACTIVA);
    }


    private SolicitudRegistro solicitudDeRegulador() {

        SolicitudRegistro solicitud = new SolicitudRegistro();

        solicitud.setNombreUsuario("regulador2");
        solicitud.setNombreCompleto("Elena Paredes Soto");
        solicitud.setCorreo("eparedes@senasa.gob.pe");
        solicitud.setContrasena("clave123");
        solicitud.setInstitucion("SENASA");
        solicitud.setCodigoInstitucional("SENASA-LIM-0042");

        return solicitud;
    }


    private Clock relojFijo() {
        return Clock.fixed(LocalDateTime.of(2026, 9, 1, 8, 0).atZone(ZONA_LIMA).toInstant(), ZONA_LIMA);
    }
}
