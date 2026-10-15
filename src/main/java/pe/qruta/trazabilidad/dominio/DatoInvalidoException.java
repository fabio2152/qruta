package pe.qruta.trazabilidad.dominio;


public class DatoInvalidoException
        extends RuntimeException {


    public DatoInvalidoException(
            String mensaje
    ) {
        super(mensaje);
    }
}
