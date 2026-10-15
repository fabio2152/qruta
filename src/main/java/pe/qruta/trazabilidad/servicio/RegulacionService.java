package pe.qruta.trazabilidad.servicio;

import java.time.Clock;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


/** Acciones exclusivas del regulador: bloqueo, desbloqueo, suspensión y reporte sanitario (CU-13, CU-14). */
public class RegulacionService {

    private final ProductoRepositorio productos;

    private final UsuarioRepositorio usuarios;

    private final Clock reloj;

    private final ReporteSanitario reporte = new ReporteSanitario();


    public RegulacionService(
            ProductoRepositorio productos,
            UsuarioRepositorio usuarios,
            Clock reloj
    ) {
        this.productos = productos;
        this.usuarios = usuarios;
        this.reloj = reloj;
    }


    public void bloquear(
            Long idProducto,
            Usuario regulador,
            String motivo
    ) {

        Producto producto = buscarProducto(idProducto);

        validarQueEsRegulador(regulador);

        producto.bloquear(motivo, regulador, reloj);

        productos.guardar(producto);
    }


    public void desbloquear(
            Long idProducto,
            Usuario regulador,
            String motivo
    ) {

        Producto producto = buscarProducto(idProducto);

        validarQueEsRegulador(regulador);

        producto.desbloquear(motivo, regulador, reloj);

        productos.guardar(producto);
    }


    /** RE-35. Si es un transportista OCUPADO, el propio Transportista rechaza la suspensión. */
    public void suspender(
            String nombreUsuario,
            Usuario regulador
    ) {

        Usuario usuario =
            usuarios
                .buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(
                    () -> new DatoInvalidoException(
                        "No existe el usuario " + nombreUsuario
                    )
                );

        usuario.suspender();

        usuarios.guardar(usuario);
    }


    public String reporteSanitarioCsv(Usuario regulador) {
        return reporte.generar(productos.listarTodos());
    }


    private Producto buscarProducto(Long idProducto) {

        return productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );
    }


    /** RE-33, RE-34 y RE-40: el control sanitario es exclusivo del regulador. */
    private void validarQueEsRegulador(Usuario usuario) {

        if (usuario.getRol() != Rol.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "Solo el regulador puede bloquear o desbloquear productos"
            );
        }
    }
}
