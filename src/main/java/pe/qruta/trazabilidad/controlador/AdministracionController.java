package pe.qruta.trazabilidad.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.ResumenRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.UsuarioRespuesta;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.servicio.AdministracionService;
import pe.qruta.trazabilidad.servicio.SolicitudRegistro;


/** Gestión de la plataforma (CU-04, CU-16). Solo el administrador; no tiene poder sanitario (D-07). */
@RestController
public class AdministracionController {

    private final AdministracionService administracionService;


    public AdministracionController(
            AdministracionService administracionService
    ) {
        this.administracionService = administracionService;
    }


    @PostMapping("/api/reguladores")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioRespuesta crearRegulador(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @RequestBody SolicitudRegistro solicitud
    ) {

        exigirAdministrador(usuario);

        return UsuarioRespuesta.desde(
            administracionService.crearRegulador(usuario, solicitud)
        );
    }


    @GetMapping("/api/admin/resumen")
    public ResumenRespuesta resumen(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario
    ) {

        exigirAdministrador(usuario);

        return ResumenRespuesta.desde(administracionService.resumen(usuario));
    }


    private void exigirAdministrador(Usuario usuario) {

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            throw new OperacionNoAutorizadaException(
                "Solo el administrador puede realizar esta operación"
            );
        }
    }
}
