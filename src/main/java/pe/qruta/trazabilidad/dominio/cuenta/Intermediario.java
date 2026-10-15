package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


/** Datos comunes de transportistas, mercados y centros de acopio. Todos tienen rol INTERMEDIARIO (D-02). */
public abstract class Intermediario extends Usuario {

    private final String razonSocial;

    private final String ruc;

    private final String telefono;

    private final String region;


    protected Intermediario(
            String nombreUsuario,
            String nombreCompleto,
            String correo,
            String contrasena,
            EstadoCuenta estadoCuenta,
            String razonSocial,
            String ruc,
            String telefono,
            String region
    ) {
        super(nombreUsuario, nombreCompleto, correo, contrasena, Rol.INTERMEDIARIO, estadoCuenta);
        this.razonSocial = razonSocial;
        this.ruc = ruc;
        this.telefono = telefono;
        this.region = region;
    }


    public String getRazonSocial() {
        return razonSocial;
    }


    public String getRuc() {
        return ruc;
    }


    public String getTelefono() {
        return telefono;
    }


    public String getRegion() {
        return region;
    }
}
