package pe.qruta.trazabilidad.dominio.cuenta;


public class CentroDeAcopio extends PuntoDeRecepcion {


    public CentroDeAcopio(
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
        super(nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta, razonSocial, ruc, telefono, region);
    }
}
