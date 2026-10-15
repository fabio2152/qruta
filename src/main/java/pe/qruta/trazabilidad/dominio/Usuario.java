package pe.qruta.trazabilidad.dominio;

import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;


public class Usuario {

    private final String nombreUsuario;

    private final String contrasena;

    private final Rol rol;

    private final String nombreCompleto;

    private final String correo;

    private EstadoCuenta estadoCuenta;


    public Usuario(
            String nombreUsuario,
            Rol rol
    ) {
        this(nombreUsuario, null, rol);
    }


    /** Cuenta creada por el sistema (datos iniciales y pruebas): nace ACTIVA. */
    public Usuario(
            String nombreUsuario,
            String contrasena,
            Rol rol
    ) {
        this(nombreUsuario, null, null, contrasena, rol, EstadoCuenta.ACTIVA);
    }


    public Usuario(
            String nombreUsuario,
            String nombreCompleto,
            String correo,
            String contrasena,
            Rol rol,
            EstadoCuenta estadoCuenta
    ) {
        this.nombreUsuario = nombreUsuario;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estadoCuenta = estadoCuenta;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }


    public String getContrasena() {
        return contrasena;
    }


    public Rol getRol() {
        return rol;
    }


    public String getNombreCompleto() {
        return nombreCompleto;
    }


    public String getCorreo() {
        return correo;
    }


    public EstadoCuenta getEstadoCuenta() {
        return estadoCuenta;
    }


    public boolean tieneContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }


    @Override
    public boolean equals(Object otro) {
        return otro instanceof Usuario usuario
            && nombreUsuario.equals(usuario.nombreUsuario);
    }


    @Override
    public int hashCode() {
        return nombreUsuario.hashCode();
    }
}
