package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


public class Regulador extends Usuario {


    public Regulador(String nombreUsuario) {
        super(nombreUsuario, Rol.REGULADOR);
    }


    public String getCodigoInstitucional() {
        return null;
    }
}
