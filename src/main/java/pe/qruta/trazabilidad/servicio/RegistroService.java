package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegistroService {

    private final UsuarioRepositorio usuarios;


    public RegistroService(
            UsuarioRepositorio usuarios
    ) {
        this.usuarios = usuarios;
    }


    public RegistroService(
            UsuarioRepositorio usuarios,
            CifradorDeContrasenas cifrador
    ) {
        this.usuarios = usuarios;
    }


    public Usuario registrar(SolicitudRegistro solicitud) {

        if (solicitud.getContrasena().length() < 8) {
            throw new DatoInvalidoException(
                "La contraseña debe tener al menos 8 caracteres"
            );
        }

        Productor productor =
            new Productor(
                solicitud.getNombreUsuario(),
                solicitud.getNombreCompleto(),
                solicitud.getCorreo(),
                solicitud.getContrasena(),
                EstadoCuenta.PENDIENTE,
                solicitud.getRazonSocial(),
                solicitud.getTipoDocumento(),
                solicitud.getNumeroDocumento(),
                solicitud.getTelefono(),
                solicitud.getTipoProductor(),
                solicitud.getRegion()
            );

        usuarios.guardar(productor);

        return productor;
    }
}
