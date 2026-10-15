package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Regulador;
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
