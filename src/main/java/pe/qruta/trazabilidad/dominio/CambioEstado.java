package pe.qruta.trazabilidad.dominio;

import java.time.LocalDateTime;


/**
 * Registro del historial. La observación guarda, por ejemplo, el motivo de un bloqueo o desbloqueo (RE-34);
 * en los cambios de etapa normales queda vacía.
 */
public record CambioEstado(
        EstadoProducto estadoAnterior,
        EstadoProducto estadoNuevo,
        LocalDateTime fechaHora,
        Usuario usuario,
        String observacion
) {

    /** Cambio de etapa normal, sin observación (fase 1: T15, T20). */
    public CambioEstado(
            EstadoProducto estadoAnterior,
            EstadoProducto estadoNuevo,
            LocalDateTime fechaHora,
            Usuario usuario
    ) {
        this(estadoAnterior, estadoNuevo, fechaHora, usuario, null);
    }
}
