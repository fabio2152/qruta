package pe.qruta.trazabilidad;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


/** Reloj de prueba al que se le puede cambiar la hora entre un paso y otro. */
public class RelojAjustable extends Clock {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");

    private Instant ahora;


    public RelojAjustable(LocalDateTime fechaHora) {
        ajustarA(fechaHora);
    }


    public void ajustarA(LocalDateTime fechaHora) {
        ahora = fechaHora.atZone(ZONA_LIMA).toInstant();
    }


    @Override
    public ZoneId getZone() {
        return ZONA_LIMA;
    }


    @Override
    public Clock withZone(ZoneId zona) {
        return this;
    }


    @Override
    public Instant instant() {
        return ahora;
    }
}
