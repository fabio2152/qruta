package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


/** Acciones del transportista sobre los productos: aceptar (y, en el ciclo 10, elegir destino). */
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

        transportista.validarQueEstaLibre();

        if (producto.tieneTransportista()) {
            throw new ProductoNoDisponibleException(
                "El producto ya fue aceptado por otro transportista"
            );
        }

        transportista.cargar(producto);

        productos.guardar(producto);

        usuarios.guardar(transportista);
    }


    public void elegirDestino(
            Long idProducto,
            Usuario transportista,
            String nombreUsuarioDestino
    ) {
    }
}
