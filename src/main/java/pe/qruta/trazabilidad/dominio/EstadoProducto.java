package pe.qruta.trazabilidad.dominio;


public enum EstadoProducto {
    EN_ORIGEN,
    EN_TRANSITO,
    EN_DESTINO,
    ENTREGADO,

    /** Fuera de la secuencia normal: solo se llega con bloquear y solo se sale con desbloquear (6.6). */
    BLOQUEADO;


    public EstadoProducto siguiente() {

        return switch (this) {
            case EN_ORIGEN -> EN_TRANSITO;
            case EN_TRANSITO -> EN_DESTINO;
            case EN_DESTINO -> ENTREGADO;
            case ENTREGADO, BLOQUEADO -> null;
        };
    }


    public boolean esFinal() {
        return siguiente() == null;
    }


    public boolean puedePasarA(
            EstadoProducto nuevoEstado
    ) {
        return siguiente() == nuevoEstado;
    }
}
