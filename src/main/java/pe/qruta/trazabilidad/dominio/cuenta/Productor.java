package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;


public class Productor extends Usuario {

    private final String razonSocial;

    private final TipoDocumento tipoDocumento;

    private final String numeroDocumento;

    private final String telefono;

    private final TipoProductor tipoProductor;

    private final String region;


    public Productor(
            String nombreUsuario,
            String nombreCompleto,
            String correo,
            String contrasena,
            EstadoCuenta estadoCuenta,
            String razonSocial,
            TipoDocumento tipoDocumento,
            String numeroDocumento,
            String telefono,
            TipoProductor tipoProductor,
            String region
    ) {
        super(nombreUsuario, nombreCompleto, correo, contrasena, Rol.PRODUCTOR, estadoCuenta);
        this.razonSocial = razonSocial;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.telefono = telefono;
        this.tipoProductor = tipoProductor;
        this.region = region;
    }


    public String getRazonSocial() {
        return razonSocial;
    }


    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }


    public String getNumeroDocumento() {
        return numeroDocumento;
    }


    public String getTelefono() {
        return telefono;
    }


    public TipoProductor getTipoProductor() {
        return tipoProductor;
    }


    public String getRegion() {
        return region;
    }
}
