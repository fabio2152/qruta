package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.configuracion.CifradorBCrypt;
import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
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


    @Test
    void unRegistroConUnNombreDeUsuarioYaExistenteSeRechaza() {

        // Dado
        registro.registrar(solicitudDeProductor());

        SolicitudRegistro otraSolicitud = solicitudDeProductor();

        otraSolicitud.setCorreo("otro.correo@correo.pe");

        // Cuando / Entonces
        assertThatThrownBy(() -> registro.registrar(otraSolicitud))
            .isInstanceOf(UsuarioYaExisteException.class)
            .hasMessage("El nombre de usuario productor9 ya está registrado");
    }


    @Test
    void unRegistroConUnCorreoSinFormatoValidoSeRechaza() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeProductor();

        solicitud.setCorreo("rosa.quispe.correo.pe");

        // Cuando / Entonces
        assertThatThrownBy(() -> registro.registrar(solicitud))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("El correo no tiene un formato válido");
    }


    @Test
    void unTransportistaConUnRucDeDiezDigitosSeRechaza() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeTransportista();

        solicitud.setNumeroDocumento("2060123456");

        // Cuando / Entonces
        assertThatThrownBy(() -> registro.registrar(solicitud))
            .isInstanceOf(DatoInvalidoException.class)
            .hasMessage("El RUC debe tener exactamente 11 dígitos");
    }


    @Test
    void unRegistroPublicoConRolDeReguladorSeRechaza() {

        // Dado
        SolicitudRegistro solicitud = solicitudDeProductor();

        solicitud.setTipoCuenta(TipoCuenta.REGULADOR);

        // Cuando / Entonces
        assertThatThrownBy(() -> registro.registrar(solicitud))
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("El registro público no está disponible para reguladores");
    }


    @Test
    void elReguladorApruebaAlProductorYYaPuedeIniciarSesion() {

        // Dado
        CifradorDeContrasenas cifrador = new CifradorBCrypt();

        RegistroService registroConCifrado =
            new RegistroService(usuarios, cifrador);

        registroConCifrado.registrar(solicitudDeProductor());

        Usuario regulador =
            new Usuario("regulador1", "clave123", Rol.REGULADOR);

        // Cuando
        registroConCifrado.aprobar("productor9", regulador);

        // Entonces
        assertThat(usuarios.buscarPorNombreUsuario("productor9").orElseThrow().getEstadoCuenta())
            .isEqualTo(EstadoCuenta.ACTIVA);

        LoginService login = new LoginService(usuarios, cifrador);

        assertThatCode(() -> login.iniciarSesion("productor9", "clave123"))
            .doesNotThrowAnyException();
    }


    @Test
    void elReguladorIntentaAprobarUnMercadoYSeRechaza() {

        // Dado
        registro.registrar(solicitudDeMercado());

        Usuario regulador =
            new Usuario("regulador1", "clave123", Rol.REGULADOR);

        // Cuando / Entonces
        assertThatThrownBy(() -> registro.aprobar("mercado9", regulador))
            .isInstanceOf(OperacionNoAutorizadaException.class)
            .hasMessage("Las cuentas de mercados y centros de acopio las aprueba el administrador");
    }


    /** Solicitud válida de un mercado, con RUC de 11 dígitos. */
    private SolicitudRegistro solicitudDeMercado() {

        SolicitudRegistro solicitud = new SolicitudRegistro();

        solicitud.setTipoCuenta(TipoCuenta.MERCADO);
        solicitud.setNombreUsuario("mercado9");
        solicitud.setNombreCompleto("Carmen Rojas Villa");
        solicitud.setCorreo("mercado.santa.anita@correo.pe");
        solicitud.setContrasena("clave123");
        solicitud.setTipoDocumento(TipoDocumento.RUC);
        solicitud.setNumeroDocumento("20509876543");
        solicitud.setRazonSocial("Mercado Mayorista Santa Anita");
        solicitud.setTelefono("014567890");
        solicitud.setRegion("Lima");

        return solicitud;
    }


    /** Solicitud válida de un transportista, con RUC de 11 dígitos. */
    private SolicitudRegistro solicitudDeTransportista() {

        SolicitudRegistro solicitud = new SolicitudRegistro();

        solicitud.setTipoCuenta(TipoCuenta.TRANSPORTISTA);
        solicitud.setNombreUsuario("transportista9");
        solicitud.setNombreCompleto("Luis Huamán Torres");
        solicitud.setCorreo("luis.huaman@correo.pe");
        solicitud.setContrasena("clave123");
        solicitud.setTipoDocumento(TipoDocumento.RUC);
        solicitud.setNumeroDocumento("20601234567");
        solicitud.setRazonSocial("Transportes Andinos SAC");
        solicitud.setTelefono("987654321");
        solicitud.setRegion("Lima");
        solicitud.setPlacaVehiculo("ABC-123");

        return solicitud;
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
