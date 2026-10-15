package pe.qruta.trazabilidad.controlador;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;
import pe.qruta.trazabilidad.servicio.CuentaNoActivaException;
import pe.qruta.trazabilidad.servicio.SesionInvalidaException;
import pe.qruta.trazabilidad.servicio.SesionService;


/**
 * Exige un token válido en la cabecera "Authorization: Bearer <token>" (RE-17, D-11).
 * Deja al usuario, leído de nuevo del repositorio, en el atributo USUARIO de la petición.
 */
@Component
public class InterceptorDeSesion
        implements HandlerInterceptor {

    public static final String USUARIO = "usuarioActual";

    private static final String PREFIJO = "Bearer ";

    private final SesionService sesiones;

    private final UsuarioRepositorio usuarios;


    public InterceptorDeSesion(
            SesionService sesiones,
            UsuarioRepositorio usuarios
    ) {
        this.sesiones = sesiones;
        this.usuarios = usuarios;
    }


    @Override
    public boolean preHandle(
            HttpServletRequest peticion,
            HttpServletResponse respuesta,
            Object controlador
    ) {

        Usuario deLaSesion = sesiones.validar(token(peticion));

        Usuario usuario =
            usuarios
                .buscarPorNombreUsuario(deLaSesion.getNombreUsuario())
                .orElseThrow(() -> new SesionInvalidaException("Debe iniciar sesión"));

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw new CuentaNoActivaException("Su cuenta ya no está activa");
        }

        peticion.setAttribute(USUARIO, usuario);

        return true;
    }


    public static String token(HttpServletRequest peticion) {

        String cabecera = peticion.getHeader("Authorization");

        return cabecera != null && cabecera.startsWith(PREFIJO)
            ? cabecera.substring(PREFIJO.length())
            : null;
    }
}
