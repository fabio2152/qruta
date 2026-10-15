package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.cuenta.TipoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;


/** Datos que una persona completa al registrarse en la plataforma. */
public class SolicitudRegistro {

    private TipoCuenta tipoCuenta;

    private String nombreUsuario;

    private String nombreCompleto;

    private String correo;

    private String contrasena;

    private TipoDocumento tipoDocumento;

    private String numeroDocumento;

    private String razonSocial;

    private String telefono;

    private TipoProductor tipoProductor;

    private String region;

    private String placaVehiculo;

    private String institucion;

    private String codigoInstitucional;


    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }


    public void setTipoCuenta(TipoCuenta tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }


    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }


    public String getNombreCompleto() {
        return nombreCompleto;
    }


    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }


    public String getCorreo() {
        return correo;
    }


    public void setCorreo(String correo) {
        this.correo = correo;
    }


    public String getContrasena() {
        return contrasena;
    }


    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }


    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }


    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }


    public String getNumeroDocumento() {
        return numeroDocumento;
    }


    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }


    public String getRazonSocial() {
        return razonSocial;
    }


    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }


    public String getTelefono() {
        return telefono;
    }


    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }


    public TipoProductor getTipoProductor() {
        return tipoProductor;
    }


    public void setTipoProductor(TipoProductor tipoProductor) {
        this.tipoProductor = tipoProductor;
    }


    public String getRegion() {
        return region;
    }


    public void setRegion(String region) {
        this.region = region;
    }


    public String getPlacaVehiculo() {
        return placaVehiculo;
    }


    public void setPlacaVehiculo(String placaVehiculo) {
        this.placaVehiculo = placaVehiculo;
    }


    public String getInstitucion() {
        return institucion;
    }


    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }


    public String getCodigoInstitucional() {
        return codigoInstitucional;
    }


    public void setCodigoInstitucional(String codigoInstitucional) {
        this.codigoInstitucional = codigoInstitucional;
    }
}
