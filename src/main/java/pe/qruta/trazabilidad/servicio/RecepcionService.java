package pe.qruta.trazabilidad.servicio;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.evaluacion.EvaluadorDeProducto;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


/**
 * El punto de recepción de destino registra la llegada del producto con su medición (CU-12).
 * Cada recepción genera una evaluación; si es CRITICA, el producto se bloquea solo (RE-32).
 */
public class RecepcionService {

    static final String MOTIVO_EVALUACION_CRITICA = "Evaluación crítica";

    private final ProductoRepositorio productos;

    private final Clock reloj;

    private final EvaluadorDeProducto evaluador = new EvaluadorDeProducto();


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

        Evaluacion evaluacion = evaluador.evaluar(producto, medicion);

        if (evaluacion.nivelRiesgo() == NivelRiesgo.CRITICO) {
            producto.bloquear(MOTIVO_EVALUACION_CRITICA);
        }

        productos.guardar(producto);

        return evaluacion;
    }
}
