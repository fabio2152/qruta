package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


public class Productor extends Usuario {


    public Productor(String nombreUsuario) {
        super(nombreUsuario, Rol.PRODUCTOR);
    }
}
