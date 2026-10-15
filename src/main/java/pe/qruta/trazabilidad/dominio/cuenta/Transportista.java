package pe.qruta.trazabilidad.dominio.cuenta;


public class Transportista extends Intermediario {

    private final String placaVehiculo;

    private EstadoCarga estadoCarga = EstadoCarga.LIBRE;


    public Transportista(
            String nombreUsuario,
            String nombreCompleto,
            String correo,
            String contrasena,
            EstadoCuenta estadoCuenta,
            String razonSocial,
            String ruc,
            String telefono,
            String region,
            String placaVehiculo
    ) {
        super(nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta, razonSocial, ruc, telefono, region);
        this.placaVehiculo = placaVehiculo;
    }


    public String getPlacaVehiculo() {
        return placaVehiculo;
    }


    public EstadoCarga getEstadoCarga() {
        return estadoCarga;
    }
}
