package pe.qruta.trazabilidad.dominio.evaluacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.Usuario;


/** Lectura de temperatura y humedad que registra el destino al recibir el producto (D-08). */
public record Medicion(
        LocalDateTime fechaHora,
        BigDecimal temperatura,
        BigDecimal humedad,
        Usuario registradaPor
) {
}
