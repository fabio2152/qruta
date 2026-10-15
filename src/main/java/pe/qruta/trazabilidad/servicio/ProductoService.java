package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.producto.CategoriaProducto;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.dominio.producto.ProductoAgro;
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

        if (productor.getRol() != Rol.PRODUCTOR) {
            throw new OperacionNoAutorizadaException(
                "Solo un productor puede crear productos"
            );
        }

        CategoriaProducto categoria = datos.getTipoProducto().getCategoria();

        if (productor instanceof Productor conTipo
                && conTipo.getTipoProductor() != TipoProductor.AGROPECUARIO
                && !conTipo.getTipoProductor().name().equals(categoria.name())) {
            throw new OperacionNoAutorizadaException(
                "Un productor " + conTipo.getTipoProductor()
                    + " no puede crear productos de categoría " + categoria
            );
        }

        if (datos.getFechaProduccion().isAfter(LocalDateTime.now(reloj))) {
            throw new DatoInvalidoException(
                "La fecha de producción no puede estar en el futuro"
            );
        }

        long horasDesdeLaProduccion =
            Duration.between(datos.getFechaProduccion(), LocalDateTime.now(reloj)).toHours();

        if (horasDesdeLaProduccion >= datos.getTipoProducto().getVidaUtilBaseHoras()) {
            throw new DatoInvalidoException(
                "La vida útil del producto ya se agotó"
            );
        }

        Producto producto =
            categoria == CategoriaProducto.PECUARIO
                ? new ProductoPecuario(generarCodigo(), productor, datos)
                : new ProductoAgro(generarCodigo(), productor, datos);

        return repositorio.guardar(producto);
    }


    public void publicar(
            Long idProducto,
            Usuario productor
    ) {

        Producto producto = buscarProductoExistente(idProducto);

        producto.publicar();

        repositorio.guardar(producto);
    }


    /** Publicaciones disponibles para los transportistas, en un listado propio (D-06). */
    public List<Producto> listarPublicaciones(Usuario transportista) {

        return repositorio
                .listarTodos()
                .stream()
                .filter(Producto::isPublicado)
                .toList();
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
