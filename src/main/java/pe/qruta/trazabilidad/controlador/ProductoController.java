package pe.qruta.trazabilidad.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.CambioEstadoRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.CambioEstadoSolicitud;
import pe.qruta.trazabilidad.controlador.Dtos.ProductoRespuesta;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.producto.DatosProducto;
import pe.qruta.trazabilidad.servicio.ProductoService;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;


    public ProductoController(
            ProductoService service
    ) {
        this.service = service;
    }


    /** El productor crea un producto: nace en EN_ORIGEN, sin publicar y con su código QR (CU-05). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoRespuesta crear(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @RequestBody DatosProducto datos
    ) {
        return ProductoRespuesta.desde(
            service.crearProducto(datos, usuario)
        );
    }


    @GetMapping
    public List<ProductoRespuesta> listar(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario
    ) {
        return service
                .listarProductos(usuario)
                .stream()
                .map(ProductoRespuesta::desde)
                .toList();
    }


    /** CU-06: el productor publica su producto para que los transportistas puedan tomarlo. */
    @PostMapping("/{id}/publicar")
    public ProductoRespuesta publicar(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id
    ) {

        service.publicar(id, usuario);

        return ProductoRespuesta.desde(service.buscarPorId(id));
    }


    @PostMapping("/{id}/retirar-publicacion")
    public ProductoRespuesta retirarPublicacion(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id
    ) {

        service.retirarPublicacion(id, usuario);

        return ProductoRespuesta.desde(service.buscarPorId(id));
    }


    @PutMapping("/{id}/estado")
    public ProductoRespuesta cambiarEstado(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id,
            @RequestBody CambioEstadoSolicitud solicitud
    ) {

        service.cambiarEstado(
            id,
            EstadoProducto.valueOf(solicitud.estado()),
            usuario
        );

        return ProductoRespuesta.desde(
            service.buscarPorId(id)
        );
    }


    @GetMapping("/{id}/historial")
    public List<CambioEstadoRespuesta> historial(
            @PathVariable Long id
    ) {
        return service
                .obtenerHistorial(id)
                .stream()
                .map(CambioEstadoRespuesta::desde)
                .toList();
    }
}
