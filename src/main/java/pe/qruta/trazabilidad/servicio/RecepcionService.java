package pe.qruta.trazabilidad.servicio;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


/** El punto de recepción de destino registra la llegada del producto con su medición (CU-12). */
public class RecepcionService {

    private final ProductoRepositorio productos;

    private final Clock reloj;


    public RecepcionService(
            ProductoRepositorio productos,
            Clock reloj
    ) {
        this.productos = productos;
        this.reloj = reloj;
    }


    public void registrarRecepcion(
            Long idProducto,
            Usuario puntoDeRecepcion,
            BigDecimal temperatura,
            BigDecimal humedad
    ) {

        Producto producto =
            productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );

        producto.registrarMedicion(
            new Medicion(LocalDateTime.now(reloj), temperatura, humedad, puntoDeRecepcion)
        );

        productos.guardar(producto);
    }
}
