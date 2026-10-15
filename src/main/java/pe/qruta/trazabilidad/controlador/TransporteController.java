package pe.qruta.trazabilidad.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.ProductoRespuesta;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCarga;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.servicio.ProductoService;
import pe.qruta.trazabilidad.servicio.TransporteService;


/** Acciones del transportista: ver publicaciones disponibles y aceptar una (CU-09). */
@RestController
public class TransporteController {

    private final ProductoService productoService;

    private final TransporteService transporteService;


    public TransporteController(
            ProductoService productoService,
            TransporteService transporteService
    ) {
        this.productoService = productoService;
        this.transporteService = transporteService;
    }


    /** RE-10 ampliada: el transportista ve las publicaciones solo mientras está LIBRE. */
    @GetMapping("/api/publicaciones")
    public List<ProductoRespuesta> publicaciones(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario
    ) {

        Transportista transportista = comoTransportista(usuario);

        if (transportista.getEstadoCarga() != EstadoCarga.LIBRE) {
            return List.of();
        }

        return productoService
                .listarPublicaciones(transportista)
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();
    }


    @PostMapping("/api/productos/{id}/aceptar")
    public ProductoRespuesta aceptar(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id
    ) {

        transporteService.aceptar(id, comoTransportista(usuario));

        return ProductoRespuesta.desde(productoService.buscarPorId(id));
    }


    private Transportista comoTransportista(Usuario usuario) {

        if (usuario instanceof Transportista transportista) {
            return transportista;
        }

        throw new OperacionNoAutorizadaException(
            "Solo un transportista puede realizar esta operación"
        );
    }
}
