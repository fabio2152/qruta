package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


public class Transportista extends Usuario {


    public Transportista(String nombreUsuario) {
        super(nombreUsuario, Rol.INTERMEDIARIO);
    }


    public EstadoCarga getEstadoCarga() {
        return null;
    }
}
