package pe.qruta.trazabilidad.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.DestinoSolicitud;
import pe.qruta.trazabilidad.controlador.Dtos.ProductoRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.UbicacionSolicitud;
import pe.qruta.trazabilidad.controlador.Dtos.UsuarioRespuesta;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCarga;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;
import pe.qruta.trazabilidad.servicio.ProductoService;
import pe.qruta.trazabilidad.servicio.TransporteService;


/** Acciones del transportista: publicaciones, aceptación, destino y ubicación (CU-08 a CU-10). */
@RestController
public class TransporteController {

    private final ProductoService productoService;

    private final TransporteService transporteService;

    private final UsuarioRepositorio usuarios;


    public TransporteController(
            ProductoService productoService,
            TransporteService transporteService,
            UsuarioRepositorio usuarios
    ) {
        this.productoService = productoService;
        this.transporteService = transporteService;
        this.usuarios = usuarios;
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


    /** CU-10: el transportista elige el mercado o centro de acopio de destino. */
    @PutMapping("/api/productos/{id}/destino")
    public ProductoRespuesta elegirDestino(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @PathVariable Long id,
            @RequestBody DestinoSolicitud solicitud
    ) {

        transporteService.elegirDestino(id, usuario, solicitud.destino());

        return ProductoRespuesta.desde(productoService.buscarPorId(id));
    }


    /** CU-08: el transportista publica su ubicación actual. */
    @PutMapping("/api/transportistas/yo/ubicacion")
    public UsuarioRespuesta actualizarUbicacion(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @RequestBody UbicacionSolicitud solicitud
    ) {

        Transportista transportista = comoTransportista(usuario);

        transportista.actualizarUbicacion(solicitud.ubicacion());

        usuarios.guardar(transportista);

        return UsuarioRespuesta.desde(transportista);
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
