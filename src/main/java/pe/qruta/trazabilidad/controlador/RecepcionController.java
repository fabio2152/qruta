package pe.qruta.trazabilidad.controlador;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.EvaluacionRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.RecepcionSolicitud;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.servicio.RecepcionService;


/** CU-12: el destino registra la recepción y recibe la evaluación del producto. */
@RestController
public class RecepcionController {

    private final RecepcionService recepcionService;


    public RecepcionController(
            RecepcionService recepcionService
    ) {
        this.recepcionService = recepcionService;
    }


    @PostMapping("/api/productos/{id}/recepcion")
    public EvaluacionRespuesta registrarRecepcion(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id,
            @RequestBody RecepcionSolicitud solicitud
    ) {
        return EvaluacionRespuesta.desde(
            recepcionService.registrarRecepcion(
                id,
                usuario,
                solicitud.temperatura(),
                solicitud.humedad()
            )
        );
    }
}
