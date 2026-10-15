package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.ProductoPecuario;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


public class ProductoService {

    private final ProductoRepositorio repositorio;

    private final Clock reloj;


    public ProductoService(
            ProductoRepositorio repositorio,
            Clock reloj
    ) {
        this.repositorio = repositorio;
        this.reloj = reloj;
    }


    public Producto crearProducto(
            String nombre,
            Usuario productor,
            Usuario intermediarioAsignado
    ) {

        Producto producto =
            new Producto(nombre, productor, intermediarioAsignado);

        return repositorio.guardar(producto);
    }


    public Producto crearProducto(
            DatosProducto datos,
            Usuario productor
    ) {

        Producto producto =
            new ProductoPecuario(generarCodigo(), productor, datos);

        return repositorio.guardar(producto);
    }


    /** Código único del producto: es el valor que se guarda en el QR. */
    private String generarCodigo() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }


    public Producto buscarPorId(Long id) {

        return repositorio
                .buscarPorId(id)
                .orElse(null);
    }


    public List<Producto> listarProductos(Usuario usuario) {

        return repositorio
                .listarTodos()
                .stream()
                .filter(producto -> producto.esVisiblePara(usuario))
                .toList();
    }


    public void cambiarEstado(
            Long idProducto,
            EstadoProducto nuevoEstado,
            Usuario usuario
    ) {

        Producto producto =
            buscarProductoExistente(idProducto);

        producto.cambiarEstado(nuevoEstado, usuario, reloj);

        repositorio.guardar(producto);
    }


    private Producto buscarProductoExistente(Long idProducto) {

        return repositorio
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );
    }


    public List<CambioEstado> obtenerHistorial(Long idProducto) {
        return buscarPorId(idProducto).getHistorial();
    }
}
