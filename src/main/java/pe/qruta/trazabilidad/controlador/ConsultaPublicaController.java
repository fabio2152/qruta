package pe.qruta.trazabilidad.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.FichaPublicaRespuesta;
import pe.qruta.trazabilidad.servicio.ConsultaPublicaService;


/** CU-15: consulta del consumidor por el código QR. No exige sesión (ver ConfiguracionWeb). */
@RestController
public class ConsultaPublicaController {

    private final ConsultaPublicaService consultaPublicaService;


    public ConsultaPublicaController(
            ConsultaPublicaService consultaPublicaService
    ) {
        this.consultaPublicaService = consultaPublicaService;
    }


    @GetMapping("/api/publico/productos/{codigo}")
    public FichaPublicaRespuesta consultar(
            @PathVariable String codigo
    ) {
        return FichaPublicaRespuesta.desde(
            consultaPublicaService.consultar(codigo.trim().toUpperCase())
        );
    }
}
