package pe.qruta.trazabilidad.dominio.cuenta;


/** Destino donde se recibe el producto: mercado o centro de acopio (D-03). */
public abstract class PuntoDeRecepcion extends Intermediario {


    protected PuntoDeRecepcion(
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
