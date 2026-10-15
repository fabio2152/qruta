package pe.qruta.trazabilidad.servicio;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
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


    public Evaluacion registrarRecepcion(
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

        Medicion medicion =
            new Medicion(LocalDateTime.now(reloj), temperatura, humedad, puntoDeRecepcion);

        producto.registrarRecepcion(medicion);

        productos.guardar(producto);

        long horasDesdeLaProduccion =
            Duration.between(producto.getFechaProduccion(), medicion.fechaHora()).toHours();

        TipoProducto tipo = producto.getTipoProducto();

        long vidaUtilRestante = tipo.getVidaUtilBaseHoras() - horasDesdeLaProduccion;

        boolean fueraDeRango =
            temperatura.compareTo(tipo.getTemperaturaMinima()) < 0
                || temperatura.compareTo(tipo.getTemperaturaMaxima()) > 0;

        if (fueraDeRango) {
            long horasDeTransporte = producto.getDuracionTransporte().toHours();
            vidaUtilRestante -= (tipo.getFactorDeterioro() - 1) * horasDeTransporte;
        }

        return new Evaluacion(
            medicion.fechaHora(), temperatura, vidaUtilRestante, false, NivelRiesgo.BAJO, true
        );
    }
}
