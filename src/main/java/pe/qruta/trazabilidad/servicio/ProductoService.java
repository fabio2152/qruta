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

        validarNuevoProducto(datos, productor);

        Producto producto =
            datos.getTipoProducto().getCategoria() == CategoriaProducto.PECUARIO
                ? new ProductoPecuario(generarCodigo(), productor, datos)
                : new ProductoAgro(generarCodigo(), productor, datos);

        return repositorio.guardar(producto);
    }


    /** RE-02 y RE-19: quién puede crear el producto y si todavía tiene vida útil. */
    private void validarNuevoProducto(
            DatosProducto datos,
            Usuario productor
    ) {

        CategoriaProducto categoria = datos.getTipoProducto().getCategoria();

        validarQuienCrea(productor, categoria);

        validarFechaYVidaUtil(datos);

        validarCertificados(datos, categoria);
    }


    /** RE-02 y RE-19: solo un productor, y solo de su categoría. */
    private void validarQuienCrea(
            Usuario productor,
            CategoriaProducto categoria
    ) {

        if (productor.getRol() != Rol.PRODUCTOR) {
            throw new OperacionNoAutorizadaException(
                "Solo un productor puede crear productos"
            );
        }

        if (productor instanceof Productor conTipo
                && !conTipo.getTipoProductor().puedeProducir(categoria)) {
            throw new OperacionNoAutorizadaException(
                "Un productor " + conTipo.getTipoProductor()
                    + " no puede crear productos de categoría " + categoria
            );
        }
    }


    /** RE-19: la producción no es futura y la vida útil base todavía no se agotó. */
    private void validarFechaYVidaUtil(DatosProducto datos) {

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
    }


    /** RE-19: los pecuarios exigen certificado SENASA; los agrícolas, certificado fitosanitario y calidad de 1 a 3. */
    private void validarCertificados(
            DatosProducto datos,
            CategoriaProducto categoria
    ) {

        if (categoria == CategoriaProducto.PECUARIO
                && estaVacio(datos.getCertificadoSanitarioSenasa())) {
            throw new DatoInvalidoException(
                "Los productos pecuarios requieren certificado sanitario SENASA"
            );
        }

        if (categoria == CategoriaProducto.AGRICOLA
                && (datos.getCalidad() == null || datos.getCalidad() < 1 || datos.getCalidad() > 3)) {
            throw new DatoInvalidoException(
                "La calidad debe estar entre 1 y 3"
            );
        }

        if (categoria == CategoriaProducto.AGRICOLA
                && estaVacio(datos.getCertificadoFitosanitario())) {
            throw new DatoInvalidoException(
                "Los productos agrícolas requieren certificado fitosanitario"
            );
        }
    }


    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }


    public void publicar(
            Long idProducto,
            Usuario productor
    ) {

        Producto producto = buscarProductoExistente(idProducto);

        producto.publicar(productor);

        repositorio.guardar(producto);
    }


    public void retirarPublicacion(
            Long idProducto,
            Usuario productor
    ) {

        Producto producto = buscarProductoExistente(idProducto);

        if (producto.tieneTransportista()) {
            throw new ProductoNoDisponibleException(
                "No se puede retirar una publicación que ya fue aceptada"
            );
        }

        producto.retirarPublicacion();

        repositorio.guardar(producto);
    }


    /** Publicaciones disponibles para los transportistas, en un listado propio (D-06). */
    public List<Producto> listarPublicaciones(Usuario transportista) {

        return repositorio
                .listarTodos()
                .stream()
                .filter(Producto::estaDisponibleParaTransporte)
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
