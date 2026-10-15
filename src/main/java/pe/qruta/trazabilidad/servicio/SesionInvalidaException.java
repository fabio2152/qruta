package pe.qruta.trazabilidad.servicio;


public class SesionInvalidaException
        extends RuntimeException {


    public SesionInvalidaException(
            String mensaje
    ) {
        super(mensaje);
    }
}
