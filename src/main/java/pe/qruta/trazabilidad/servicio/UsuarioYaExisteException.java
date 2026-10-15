package pe.qruta.trazabilidad.servicio;


public class UsuarioYaExisteException
        extends RuntimeException {


    public UsuarioYaExisteException(
            String mensaje
    ) {
        super(mensaje);
    }
}
