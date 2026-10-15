package pe.qruta.trazabilidad.servicio;


public class CuentaNoActivaException
        extends RuntimeException {


    public CuentaNoActivaException(
            String mensaje
    ) {
        super(mensaje);
    }
}
