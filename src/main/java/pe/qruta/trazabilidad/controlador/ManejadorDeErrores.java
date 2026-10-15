package pe.qruta.trazabilidad.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import pe.qruta.trazabilidad.controlador.Dtos.ErrorRespuesta;
import pe.qruta.trazabilidad.dominio.CambioDeEstadoNoPermitidoException;
import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.DuracionNoDisponibleException;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.servicio.CredencialesInvalidasException;
import pe.qruta.trazabilidad.servicio.CuentaNoActivaException;
import pe.qruta.trazabilidad.servicio.ProductoNoDisponibleException;
import pe.qruta.trazabilidad.servicio.ProductoNoEncontradoException;
import pe.qruta.trazabilidad.servicio.SesionInvalidaException;
import pe.qruta.trazabilidad.servicio.UsuarioYaExisteException;


/**
 * Traduce las excepciones del dominio y del servicio a respuestas HTTP
 * con el cuerpo {"mensaje": "..."} y el mismo mensaje claro que verifican las pruebas (sección 9.2).
 */
@RestControllerAdvice
public class ManejadorDeErrores {


    @ExceptionHandler({
        DatoInvalidoException.class,
        IllegalArgumentException.class
    })
    public ResponseEntity<ErrorRespuesta> datoInvalido(RuntimeException error) {
        return responder(HttpStatus.BAD_REQUEST, error.getMessage());
    }


    @ExceptionHandler({
        CredencialesInvalidasException.class,
        SesionInvalidaException.class
    })
    public ResponseEntity<ErrorRespuesta> sinSesion(RuntimeException error) {
        return responder(HttpStatus.UNAUTHORIZED, error.getMessage());
    }


    @ExceptionHandler({
        OperacionNoAutorizadaException.class,
        CuentaNoActivaException.class
    })
    public ResponseEntity<ErrorRespuesta> sinPermiso(RuntimeException error) {
        return responder(HttpStatus.FORBIDDEN, error.getMessage());
    }


    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> noEncontrado(RuntimeException error) {
        return responder(HttpStatus.NOT_FOUND, error.getMessage());
    }


    @ExceptionHandler({
        CambioDeEstadoNoPermitidoException.class,
        DuracionNoDisponibleException.class,
        UsuarioYaExisteException.class,
        ProductoNoDisponibleException.class
    })
    public ResponseEntity<ErrorRespuesta> conflicto(RuntimeException error) {
        return responder(HttpStatus.CONFLICT, error.getMessage());
    }


    /** D-13: otro usuario modificó el producto al mismo tiempo (por ejemplo, dos aceptaciones). */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorRespuesta> modificadoAlMismoTiempo(RuntimeException error) {
        return responder(
            HttpStatus.CONFLICT,
            "Otro usuario modificó este producto al mismo tiempo. Actualice e intente de nuevo"
        );
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorRespuesta> estadoHttp(ResponseStatusException error) {
        return ResponseEntity
                .status(error.getStatusCode())
                .body(new ErrorRespuesta(error.getReason()));
    }


    private ResponseEntity<ErrorRespuesta> responder(
            HttpStatus estado,
            String mensaje
    ) {
        return ResponseEntity
                .status(estado)
                .body(new ErrorRespuesta(mensaje));
    }
}
