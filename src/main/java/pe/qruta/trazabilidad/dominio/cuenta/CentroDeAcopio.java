package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


public class CentroDeAcopio extends Usuario {


    public CentroDeAcopio(String nombreUsuario) {
        super(nombreUsuario, Rol.INTERMEDIARIO);
    }
}
