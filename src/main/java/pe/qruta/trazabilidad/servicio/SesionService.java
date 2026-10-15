package pe.qruta.trazabilidad.servicio;

import java.time.Clock;

import pe.qruta.trazabilidad.dominio.Usuario;


public class SesionService {

    private final Clock reloj;


    public SesionService(
            Clock reloj
    ) {
        this.reloj = reloj;
    }


    public String iniciar(Usuario usuario) {
        return null;
    }


    public Usuario validar(String token) {
        return null;
    }
}
