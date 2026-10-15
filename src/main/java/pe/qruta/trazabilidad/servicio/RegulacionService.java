package pe.qruta.trazabilidad.servicio;

import java.time.Clock;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegulacionService {

    private final ProductoRepositorio productos;

    private final UsuarioRepositorio usuarios;

    private final Clock reloj;


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

        Producto producto =
            productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );

        producto.bloquear(motivo, regulador, reloj);

        productos.guardar(producto);
    }


    public void desbloquear(
            Long idProducto,
            Usuario regulador,
            String motivo
    ) {

        Producto producto =
            productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );

        producto.desbloquear(motivo, regulador, reloj);

        productos.guardar(producto);
    }
}
