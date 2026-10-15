package pe.qruta.trazabilidad.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.qruta.trazabilidad.controlador.Dtos.DestinoRespuesta;
import pe.qruta.trazabilidad.controlador.Dtos.NecesidadesSolicitud;
import pe.qruta.trazabilidad.controlador.Dtos.UsuarioRespuesta;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.PuntoDeRecepcion;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


/** Mercados y centros de acopio como destinos (CU-07). */
@RestController
@RequestMapping("/api/destinos")
public class DestinoController {

    private final UsuarioRepositorio usuarios;


    public DestinoController(
            UsuarioRepositorio usuarios
    ) {
        this.usuarios = usuarios;
    }


    /** Destinos activos que necesitan ese tipo de producto: las opciones que ve el transportista. */
    @GetMapping
    public List<DestinoRespuesta> listar(
            @RequestParam TipoProducto tipo
    ) {
        return usuarios
                .listarTodos()
                .stream()
                .filter(usuario -> usuario instanceof PuntoDeRecepcion)
                .map(usuario -> (PuntoDeRecepcion) usuario)
                .filter(punto -> punto.getEstadoCuenta() == EstadoCuenta.ACTIVA)
                .filter(punto -> punto.necesita(tipo))
                .map(DestinoRespuesta::desde)
                .toList();
    }


    /** El punto de recepción configura los tipos de producto que necesita. */
    @PutMapping("/yo")
    public UsuarioRespuesta configurar(
            @RequestAttribute(InterceptorDeSesion.USUARIO) Usuario usuario,
            @RequestBody NecesidadesSolicitud solicitud
    ) {

        if (!(usuario instanceof PuntoDeRecepcion punto)) {
            throw new OperacionNoAutorizadaException(
                "Solo un mercado o un centro de acopio configura sus necesidades"
            );
        }

        punto.configurarNecesidades(solicitud.necesidades());

        usuarios.guardar(punto);

        return UsuarioRespuesta.desde(punto);
    }
}
