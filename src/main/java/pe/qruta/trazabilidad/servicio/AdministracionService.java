package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Regulador;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


/** Gestión de la plataforma: crear reguladores y ver el resumen general (CU-04, CU-16). */
public class AdministracionService {

    private final UsuarioRepositorio usuarios;

    private final ProductoRepositorio productos;

    private final CifradorDeContrasenas cifrador;


    public AdministracionService(
            UsuarioRepositorio usuarios,
            ProductoRepositorio productos
    ) {
        this(usuarios, productos, new CifradorTextoPlano());
    }


    public AdministracionService(
            UsuarioRepositorio usuarios,
            ProductoRepositorio productos,
            CifradorDeContrasenas cifrador
    ) {
        this.usuarios = usuarios;
        this.productos = productos;
        this.cifrador = cifrador;
    }


    /** RE-11: los reguladores solo los crea el administrador y nacen ACTIVOS. */
    public Usuario crearRegulador(
            Usuario administrador,
            SolicitudRegistro solicitud
    ) {

        if (solicitud.getCodigoInstitucional() == null || solicitud.getCodigoInstitucional().isBlank()) {
            throw new DatoInvalidoException(
                "El código institucional es obligatorio"
            );
        }

        Regulador regulador = new Regulador(
            solicitud.getNombreUsuario(),
            solicitud.getNombreCompleto(),
            solicitud.getCorreo(),
            cifrador.cifrar(solicitud.getContrasena()),
            EstadoCuenta.ACTIVA,
            solicitud.getInstitucion(),
            solicitud.getCodigoInstitucional()
        );

        usuarios.guardar(regulador);

        return regulador;
    }
}
