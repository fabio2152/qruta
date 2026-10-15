package pe.qruta.trazabilidad.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


class SesionServiceTest {

    private static final ZoneId ZONA_LIMA =
        ZoneId.of("America/Lima");


    @Test
    void unTokenEmitidoALasOchoEsValidoALas1559YSeRechazaALas1600() {

        // Dado
        RelojAjustable reloj = new RelojAjustable(horaDelDia(8, 0));

        SesionService sesiones = new SesionService(reloj);

        Usuario productor =
            new Usuario("productor1", "clave123", Rol.PRODUCTOR);

        String token = sesiones.iniciar(productor);

        // Cuando / Entonces
        reloj.ajustarA(horaDelDia(15, 59));

        assertThat(sesiones.validar(token))
            .isEqualTo(productor);

        reloj.ajustarA(horaDelDia(16, 0));

        assertThatThrownBy(() -> sesiones.validar(token))
            .isInstanceOf(SesionInvalidaException.class)
            .hasMessage("La sesión expiró, inicie sesión nuevamente");
    }


    private LocalDateTime horaDelDia(
            int hora,
            int minuto
    ) {
        return LocalDateTime.of(2026, 9, 1, hora, minuto);
    }


    /** Reloj de prueba al que se le puede cambiar la hora. */
    private static class RelojAjustable extends Clock {

        private Instant ahora;


        RelojAjustable(LocalDateTime fechaHora) {
            ajustarA(fechaHora);
        }


        void ajustarA(LocalDateTime fechaHora) {
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
}
