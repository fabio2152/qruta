package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCarga;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class TransporteService {

    private final ProductoRepositorio productos;

    private final UsuarioRepositorio usuarios;


    public TransporteService(
            ProductoRepositorio productos,
            UsuarioRepositorio usuarios
    ) {
        this.productos = productos;
        this.usuarios = usuarios;
    }


    public void aceptar(
            Long idProducto,
            Transportista transportista
    ) {

        Producto producto =
            productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );

        if (transportista.getEstadoCarga() == EstadoCarga.OCUPADO) {
            throw new OperacionNoAutorizadaException(
                "Un transportista OCUPADO no puede aceptar otro producto"
            );
        }

        producto.asignarIntermediario(transportista);

        transportista.ocupar();

        productos.guardar(producto);

        usuarios.guardar(transportista);
    }
}
