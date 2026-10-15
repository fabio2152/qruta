package pe.qruta.trazabilidad.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.UsuarioRespuesta;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;
import pe.qruta.trazabilidad.servicio.RegistroService;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepositorio usuarios;

    private final RegistroService registroService;


    public UsuarioController(
            UsuarioRepositorio usuarios,
            RegistroService registroService
    ) {
        this.usuarios = usuarios;
        this.registroService = registroService;
    }


    /** Solo el regulador: lista productores e intermediarios con el estado de su cuenta. */
    @GetMapping
    public List<UsuarioRespuesta> listar(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario
    ) {

        if (usuario.getRol() != Rol.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "Solo el regulador puede ver los usuarios del sistema"
            );
        }

        return usuarios
                .listarTodos()
                .stream()
                .filter(otro -> otro.getRol() != Rol.REGULADOR)
                .map(UsuarioRespuesta::desde)
                .toList();
    }


    /** Datos de la sesión actual (por ejemplo, el estado de carga del transportista). */
    @GetMapping("/yo")
    public UsuarioRespuesta yo(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario
    ) {
        return UsuarioRespuesta.desde(usuario);
    }


    /** CU-03: el regulador (o el administrador, según RE-15) aprueba una cuenta pendiente. */
    @PostMapping("/{nombreUsuario}/aprobar")
    public UsuarioRespuesta aprobar(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable String nombreUsuario
    ) {

        registroService.aprobar(nombreUsuario, usuario);

        return UsuarioRespuesta.desde(
            usuarios.buscarPorNombreUsuario(nombreUsuario).orElseThrow()
        );
    }
}
