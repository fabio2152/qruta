package pe.qruta.trazabilidad.servicio;

import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.EstadoProducto;


/** Un paso del recorrido que ve el consumidor: la etapa, cuándo y, si hubo, la observación (por ejemplo, un bloqueo). */
public record PasoDelRecorrido(
        EstadoProducto etapa,
        LocalDateTime fechaHora,
        String observacion
) {
}
