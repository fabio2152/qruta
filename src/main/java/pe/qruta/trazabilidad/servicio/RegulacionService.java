package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import pe.qruta.trazabilidad.dominio.DatoInvalidoException;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.OperacionNoAutorizadaException;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCarga;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegulacionService {

    private final ProductoRepositorio productos;

    private final UsuarioRepositorio usuarios;

    private final Clock reloj;


    public RegulacionService(
            ProductoRepositorio productos,
            UsuarioRepositorio usuarios,
            Clock reloj
    ) {
        this.productos = productos;
        this.usuarios = usuarios;
        this.reloj = reloj;
    }


    public void bloquear(
            Long idProducto,
            Usuario regulador,
            String motivo
    ) {

        Producto producto =
            productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );

        validarQueEsRegulador(regulador);

        producto.bloquear(motivo, regulador, reloj);

        productos.guardar(producto);
    }


    public void desbloquear(
            Long idProducto,
            Usuario regulador,
            String motivo
    ) {

        Producto producto =
            productos
                .buscarPorId(idProducto)
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con id " + idProducto
                    )
                );

        validarQueEsRegulador(regulador);

        producto.desbloquear(motivo, regulador, reloj);

        productos.guardar(producto);
    }


    public void suspender(
            String nombreUsuario,
            Usuario regulador
    ) {

        Usuario usuario =
            usuarios
                .buscarPorNombreUsuario(nombreUsuario)
                .orElseThrow(
                    () -> new DatoInvalidoException(
                        "No existe el usuario " + nombreUsuario
                    )
                );

        if (usuario instanceof Transportista transportista
                && transportista.getEstadoCarga() == EstadoCarga.OCUPADO) {
            throw new OperacionNoAutorizadaException(
                "No se puede suspender a un transportista OCUPADO: primero debe terminar su traslado"
            );
        }

        usuario.suspender();

        usuarios.guardar(usuario);
    }


    public String reporteSanitarioCsv(Usuario regulador) {

        StringBuilder csv = new StringBuilder(
            "codigo,tipo,productor,region_origen,etapa,transportista,destino,duracion_transporte_horas,"
                + "temperatura_recepcion,vida_util_restante_horas,ruptura,nivel_riesgo,apto,bloqueado\n"
        );

        for (Producto producto : productos.listarTodos()) {

            Evaluacion evaluacion = producto.getUltimaEvaluacion();

            String razonSocial = producto.getProductor().getNombreUsuario();
            String region = "";

            if (producto.getProductor() instanceof Productor productor) {
                razonSocial = productor.getRazonSocial();
                region = productor.getRegion();
            }

            List<String> celdas = List.of(
                texto(producto.getCodigo()),
                producto.getTipoProducto() == null ? "" : producto.getTipoProducto().name(),
                razonSocial,
                texto(region),
                producto.getEstado().name(),
                producto.getIntermediarioAsignado() == null ? "" : producto.getIntermediarioAsignado().getNombreUsuario(),
                producto.getDestino() == null ? "" : producto.getDestino().getRazonSocial(),
                producto.getHoraLlegada() == null ? "" : String.valueOf(producto.getDuracionTransporte().toHours()),
                producto.getMediciones().isEmpty() ? "" : producto.getMediciones().get(0).temperatura().toPlainString(),
                evaluacion == null ? "" : String.valueOf(evaluacion.vidaUtilRestanteHoras()),
                evaluacion == null ? "" : siNo(evaluacion.rupturaCadenaFrio()),
                evaluacion == null ? "" : evaluacion.nivelRiesgo().name(),
                evaluacion == null ? "" : siNo(evaluacion.aptoConsumo()),
                siNo(producto.getEstado() == EstadoProducto.BLOQUEADO)
            );

            csv.append(celdas.stream().map(this::celda).collect(Collectors.joining(","))).append("\n");
        }

        return csv.toString();
    }


    private String texto(String valor) {
        return valor == null ? "" : valor;
    }


    private String siNo(boolean valor) {
        return valor ? "SI" : "NO";
    }


    /** Un texto con comas o comillas va entre comillas, como manda el formato CSV. */
    private String celda(String valor) {

        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }

        return valor;
    }


    /** RE-33, RE-34 y RE-40: el control sanitario es exclusivo del regulador. */
    private void validarQueEsRegulador(Usuario usuario) {

        if (usuario.getRol() != Rol.REGULADOR) {
            throw new OperacionNoAutorizadaException(
                "Solo el regulador puede bloquear o desbloquear productos"
            );
        }
    }
}
