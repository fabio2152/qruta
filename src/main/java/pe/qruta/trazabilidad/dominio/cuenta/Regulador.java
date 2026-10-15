package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


/** Autoridad sanitaria (por ejemplo, SENASA). Solo la crea el administrador (RE-11). */
public class Regulador extends Usuario {

    private final String institucion;

    private final String codigoInstitucional;


    public Regulador(
            String nombreUsuario,
            String nombreCompleto,
            String correo,
            String contrasena,
            EstadoCuenta estadoCuenta,
            String institucion,
            String codigoInstitucional
    ) {
        super(nombreUsuario, nombreCompleto, correo, contrasena, Rol.REGULADOR, estadoCuenta);
        this.institucion = institucion;
        this.codigoInstitucional = codigoInstitucional;
    }


    public String getInstitucion() {
        return institucion;
    }


    public String getCodigoInstitucional() {
        return codigoInstitucional;
    }
}
