package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegistroService {

    private final UsuarioRepositorio usuarios;

    private final CifradorDeContrasenas cifrador;


    public RegistroService(
            UsuarioRepositorio usuarios
    ) {
        this(usuarios, new CifradorTextoPlano());
    }


    public RegistroService(
            UsuarioRepositorio usuarios,
            CifradorDeContrasenas cifrador
    ) {
        this.usuarios = usuarios;
        this.cifrador = cifrador;
    }


    public Usuario registrar(SolicitudRegistro solicitud) {

        if (solicitud.getTipoCuenta() == TipoCuenta.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "El registro público no está disponible para reguladores"
            );
        }

        if (solicitud.getContrasena().length() < 8) {
            throw new DatoInvalidoException(
                "La contraseña debe tener al menos 8 caracteres"
            );
        }

        if (!solicitud.getCorreo().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new DatoInvalidoException(
                "El correo no tiene un formato válido"
            );
        }

        if (solicitud.getTipoDocumento() == TipoDocumento.RUC
                && !solicitud.getNumeroDocumento().matches("\\d{11}")) {
            throw new DatoInvalidoException(
                "El RUC debe tener exactamente 11 dígitos"
            );
        }

        if (usuarios.buscarPorNombreUsuario(solicitud.getNombreUsuario()).isPresent()) {
            throw new UsuarioYaExisteException(
                "El nombre de usuario " + solicitud.getNombreUsuario() + " ya está registrado"
            );
        }

        Productor productor =
            new Productor(
                solicitud.getNombreUsuario(),
                solicitud.getNombreCompleto(),
                solicitud.getCorreo(),
                cifrador.cifrar(solicitud.getContrasena()),
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
