package pe.qruta.trazabilidad.servicio;

import java.math.BigDecimal;
import java.time.Clock;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


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
    }
}
