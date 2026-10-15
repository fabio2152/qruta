package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


/** Punto de destino donde se recibe el producto. Tiene rol INTERMEDIARIO (D-02). */
public class Mercado extends Usuario {

    private final String razonSocial;

    private final String ruc;

    private final String telefono;

    private final String region;


    public Mercado(
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
