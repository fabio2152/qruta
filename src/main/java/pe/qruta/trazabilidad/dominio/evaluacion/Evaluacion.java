package pe.qruta.trazabilidad.dominio.evaluacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/** Resultado de evaluar un producto con la medición de su recepción (RF-14). */
public record Evaluacion(
        LocalDateTime fechaHora,
        BigDecimal temperaturaEvaluada,
        long vidaUtilRestanteHoras,
        boolean rupturaCadenaFrio,
        NivelRiesgo nivelRiesgo,
        boolean aptoConsumo
) {
}
