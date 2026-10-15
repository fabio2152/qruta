package pe.qruta.trazabilidad.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import pe.qruta.trazabilidad.controlador.Dtos.LoginRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.LoginSolicitud;
import pe.qruta.trazabilidad.controlador.Dtos.UsuarioRespuesta;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.servicio.LoginService;
import pe.qruta.trazabilidad.servicio.RegistroService;
import pe.qruta.trazabilidad.servicio.SesionService;
import pe.qruta.trazabilidad.servicio.SolicitudRegistro;


/** Registro público, inicio y cierre de sesión (CU-01, CU-02). */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistroService registroService;

    private final LoginService loginService;

    private final SesionService sesionService;


    public AuthController(
            RegistroService registroService,
            LoginService loginService,
            SesionService sesionService
    ) {
        this.registroService = registroService;
        this.loginService = loginService;
        this.sesionService = sesionService;
    }


    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRespuesta registrar(
            @RequestBody SolicitudRegistro solicitud
    ) {
        return UsuarioRespuesta.desde(
            registroService.registrar(solicitud)
        );
    }


    @PostMapping("/login")
    public LoginRespuesta iniciarSesion(
            @RequestBody LoginSolicitud solicitud
    ) {

        Usuario usuario =
            loginService.iniciarSesion(
                solicitud.nombreUsuario(),
                solicitud.contrasena()
            );

        return new LoginRespuesta(
            sesionService.iniciar(usuario),
            UsuarioRespuesta.desde(usuario)
        );
    }


    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cerrarSesion(HttpServletRequest peticion) {
        sesionService.cerrar(InterceptorDeSesion.token(peticion));
    }
}
