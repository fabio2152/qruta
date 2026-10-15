package pe.qruta.trazabilidad.dominio.evaluacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.Usuario;


/** Lectura de temperatura y humedad que registra el destino al recibir el producto (D-08). */
public record Medicion(
        LocalDateTime fechaHora,
        BigDecimal temperatura,
        BigDecimal humedad,
        Usuario registradaPor
) {

    /** RE-26: fuera de este rango la lectura del instrumento no es válida. */
    private static final BigDecimal TEMPERATURA_MINIMA = new BigDecimal("-40");

    private static final BigDecimal TEMPERATURA_MAXIMA = new BigDecimal("60");


    public Medicion {

        if (temperatura.compareTo(TEMPERATURA_MINIMA) < 0
                || temperatura.compareTo(TEMPERATURA_MAXIMA) > 0) {
            throw new DatoInvalidoException(
                "Lectura inválida del instrumento: la temperatura debe estar entre -40 °C y 60 °C"
            );
        }
    }
}
