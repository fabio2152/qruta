package pe.qruta.trazabilidad.servicio;


/** El producto ya no está disponible para la operación pedida (conflicto, HTTP 409). */
public class ProductoNoDisponibleException
        extends RuntimeException {


    public ProductoNoDisponibleException(
            String mensaje
    ) {
        super(mensaje);
    }
}
