package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.configuracion.CifradorBCrypt;
import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorioEnMemoria;


class RegistroServiceTest {

    private UsuarioRepositorioEnMemoria usuarios;

    private RegistroService registro;


    @BeforeEach
    void prepararServicio() {

        usuarios = new UsuarioRepositorioEnMemoria();

        registro = new RegistroService(usuarios);
    }


    @Test
    void unProductorSeRegistraConUnaContrasenaDeOchoCaracteresYQuedaPendiente() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeProductor();

        // Cuando
        Usuario registrado = registro.registrar(solicitud);

        // Entonces
        assertThat(registrado).isInstanceOf(Productor.class);

        assertThat(registrado.getRol())
            .isEqualTo(Rol.PRODUCTOR);

        assertThat(registrado.getEstadoCuenta())
            .isEqualTo(EstadoCuenta.PENDIENTE);

        assertThat(usuarios.buscarPorNombreUsuario("productor9"))
            .contains(registrado);
    }


    @Test
    void unRegistroConUnaContrasenaDeSieteCaracteresSeRechaza() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeProductor();

        solicitud.setContrasena("clave12");

        // Cuando / Entonces
        assertThatThrownBy(() -> registro.registrar(solicitud))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("La contraseña debe tener al menos 8 caracteres");
    }


    @Test
    void laContrasenaSeGuardaCifradaYSePuedeVerificar() {

        // Dado
        CifradorDeContrasenas cifrador = new CifradorBCrypt();

        RegistroService registroConCifrado =
            new RegistroService(usuarios, cifrador);

        // Cuando
        Usuario registrado =
            registroConCifrado.registrar(solicitudDeProductor());

        // Entonces
        assertThat(registrado.getContrasena())
            .isNotEqualTo("clave123");

        assertThat(cifrador.coincide("clave123", registrado.getContrasena()))
            .isTrue();
    }


    /** Solicitud válida de un productor; la contraseña "clave123" tiene exactamente 8 caracteres. */
    private SolicitudRegistro solicitudDeProductor() {

        SolicitudRegistro solicitud = new SolicitudRegistro();

        solicitud.setTipoCuenta(TipoCuenta.PRODUCTOR);
        solicitud.setNombreUsuario("productor9");
        solicitud.setNombreCompleto("Rosa Quispe Mamani");
        solicitud.setCorreo("rosa.quispe@correo.pe");
        solicitud.setContrasena("clave123");
        solicitud.setTipoDocumento(TipoDocumento.DNI);
        solicitud.setNumeroDocumento("45678912");
        solicitud.setRazonSocial("Rosa Quispe Mamani");
        solicitud.setTelefono("984123456");
        solicitud.setTipoProductor(TipoProductor.AGROPECUARIO);
        solicitud.setRegion("Cusco");

        return solicitud;
    }
}
