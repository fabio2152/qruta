package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class LoginService {

    private final UsuarioRepositorio usuarios;


    public LoginService(
            UsuarioRepositorio usuarios
    ) {
        this.usuarios = usuarios;
    }


    public Usuario iniciarSesion(
            String nombreUsuario,
            String contrasena
    ) {

        Usuario usuario =
            usuarios
                .buscarPorNombreUsuario(nombreUsuario)
                .filter(encontrado -> encontrado.tieneContrasena(contrasena))
                .orElseThrow(
                    () -> new CredencialesInvalidasException(
                        "Usuario o contraseña incorrectos"
                    )
                );

        if (usuario.getEstadoCuenta() != EstadoCuenta.ACTIVA) {
            throw new CuentaNoActivaException(
                usuario.getEstadoCuenta() == EstadoCuenta.PENDIENTE
                    ? "Su cuenta está pendiente de aprobación"
                    : "Su cuenta está suspendida"
            );
        }

        return usuario;
    }
}
