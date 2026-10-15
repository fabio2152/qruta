package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class LoginService {

    private final UsuarioRepositorio usuarios;

    private final CifradorDeContrasenas cifrador;


    public LoginService(
            UsuarioRepositorio usuarios
    ) {
        this(usuarios, new CifradorTextoPlano());
    }


    public LoginService(
            UsuarioRepositorio usuarios,
            CifradorDeContrasenas cifrador
    ) {
        this.usuarios = usuarios;
        this.cifrador = cifrador;
    }


    public Usuario iniciarSesion(
            String nombreUsuario,
            String contrasena
    ) {

        Usuario usuario =
            usuarios
                .buscarPorNombreUsuario(nombreUsuario)
                .filter(encontrado -> cifrador.coincide(contrasena, encontrado.getContrasena()))
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
