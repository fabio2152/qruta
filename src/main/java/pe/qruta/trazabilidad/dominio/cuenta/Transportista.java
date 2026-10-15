package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;


public class Transportista extends Intermediario {

    private final String placaVehiculo;

    private EstadoCarga estadoCarga = EstadoCarga.LIBRE;

    private String ubicacionActual;


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


    /** Para reconstruir desde la base de datos un transportista con su estado de carga guardado. */
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
            String placaVehiculo,
            EstadoCarga estadoCarga
    ) {
        this(nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta, razonSocial, ruc, telefono, region, placaVehiculo);
        this.estadoCarga = estadoCarga;
    }


    public String getPlacaVehiculo() {
        return placaVehiculo;
    }


    public EstadoCarga getEstadoCarga() {
        return estadoCarga;
    }


    public String getUbicacionActual() {
        return ubicacionActual;
    }


    /** RF-08: el transportista publica dónde está (texto libre; el GPS queda para la fase futura). */
    public void actualizarUbicacion(String ubicacion) {
        this.ubicacionActual = ubicacion;
    }


    /** RE-22: un transportista lleva un solo producto a la vez. */
    public void validarQueEstaLibre() {

        if (estadoCarga == EstadoCarga.OCUPADO) {
            throw new OperacionNoAutorizadaException(
                "Un transportista OCUPADO no puede aceptar otro producto"
            );
        }
    }


    /** RE-28: al entregar el producto vuelve a quedar LIBRE. */
    public void liberar() {
        estadoCarga = EstadoCarga.LIBRE;
    }


    /** Queda asignado como intermediario del producto y pasa a OCUPADO. */
    public void cargar(Producto producto) {

        producto.asignarIntermediario(this);

        estadoCarga = EstadoCarga.OCUPADO;
    }
}
