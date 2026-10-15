package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import pe.qruta.trazabilidad.dominio.Usuario;


/** Emite y valida los tokens de sesión. Un token vale 8 horas desde que se emite (RE-17). */
public class SesionService {

    private final Clock reloj;

    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();


    public SesionService(
            Clock reloj
    ) {
        this.reloj = reloj;
    }


    public String iniciar(Usuario usuario) {

        String token = UUID.randomUUID().toString();

        sesiones.put(token, new Sesion(usuario, LocalDateTime.now(reloj)));

        return token;
    }


    public Usuario validar(String token) {

        Sesion sesion = token == null ? null : sesiones.get(token);

        if (sesion == null) {
            throw new SesionInvalidaException("Debe iniciar sesión");
        }

        if (!LocalDateTime.now(reloj).isBefore(sesion.emitida().plusHours(8))) {
            throw new SesionInvalidaException(
                "La sesión expiró, inicie sesión nuevamente"
            );
        }

        return sesion.usuario();
    }


    private record Sesion(
            Usuario usuario,
            LocalDateTime emitida
    ) {
    }
}
