package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.ValidadorDeFormato;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegistroService {

    private static final int LARGO_MINIMO_CONTRASENA = 8;

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

        validar(solicitud);

        Usuario nuevo = crearCuenta(solicitud);

        usuarios.guardar(nuevo);

        return nuevo;
    }


    private void validar(SolicitudRegistro solicitud) {

        if (solicitud.getTipoCuenta() == TipoCuenta.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "El registro público no está disponible para reguladores"
            );
        }

        if (solicitud.getContrasena().length() < LARGO_MINIMO_CONTRASENA) {
            throw new DatoInvalidoException(
                "La contraseña debe tener al menos " + LARGO_MINIMO_CONTRASENA + " caracteres"
            );
        }

        if (!ValidadorDeFormato.esCorreoValido(solicitud.getCorreo())) {
            throw new DatoInvalidoException(
                "El correo no tiene un formato válido"
            );
        }

        if (solicitud.getTipoDocumento() == TipoDocumento.RUC
                && !ValidadorDeFormato.esRucValido(solicitud.getNumeroDocumento())) {
            throw new DatoInvalidoException(
                "El RUC debe tener exactamente 11 dígitos"
            );
        }

        if (solicitud.getTipoDocumento() == TipoDocumento.DNI
                && !ValidadorDeFormato.esDniValido(solicitud.getNumeroDocumento())) {
            throw new DatoInvalidoException(
                "El DNI debe tener exactamente 8 dígitos"
            );
        }

        if (usuarios.buscarPorNombreUsuario(solicitud.getNombreUsuario()).isPresent()) {
            throw new UsuarioYaExisteException(
                "El nombre de usuario " + solicitud.getNombreUsuario() + " ya está registrado"
            );
        }

        boolean correoRegistrado =
            usuarios
                .listarTodos()
                .stream()
                .anyMatch(usuario -> solicitud.getCorreo().equalsIgnoreCase(usuario.getCorreo()));

        if (correoRegistrado) {
            throw new UsuarioYaExisteException(
                "El correo " + solicitud.getCorreo() + " ya está registrado"
            );
        }
    }


    /** Toda cuenta registrada nace PENDIENTE y con la contraseña cifrada (RE-13, RE-15). */
    private Usuario crearCuenta(SolicitudRegistro solicitud) {

        String contrasenaCifrada = cifrador.cifrar(solicitud.getContrasena());

        if (solicitud.getTipoCuenta() == TipoCuenta.MERCADO) {
            return new Mercado(
                solicitud.getNombreUsuario(),
                solicitud.getNombreCompleto(),
                solicitud.getCorreo(),
                contrasenaCifrada,
                EstadoCuenta.PENDIENTE,
                solicitud.getRazonSocial(),
                solicitud.getNumeroDocumento(),
                solicitud.getTelefono(),
                solicitud.getRegion()
            );
        }

        return new Productor(
            solicitud.getNombreUsuario(),
            solicitud.getNombreCompleto(),
            solicitud.getCorreo(),
            contrasenaCifrada,
            EstadoCuenta.PENDIENTE,
            solicitud.getRazonSocial(),
            solicitud.getTipoDocumento(),
            solicitud.getNumeroDocumento(),
            solicitud.getTelefono(),
            solicitud.getTipoProductor(),
            solicitud.getRegion()
        );
    }


    public void aprobar(
            String nombreUsuario,
            Usuario aprobador
    ) {

        Usuario usuario =
            usuarios
                .buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(
                    () -> new DatoInvalidoException(
                        "No existe el usuario " + nombreUsuario
                    )
                );

        if (usuario instanceof Mercado && aprobador.getRol() == Rol.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "Las cuentas de mercados y centros de acopio las aprueba el administrador"
            );
        }

        usuario.activar();

        usuarios.guardar(usuario);
    }
}
