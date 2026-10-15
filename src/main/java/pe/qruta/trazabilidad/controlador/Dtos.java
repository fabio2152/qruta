package pe.qruta.trazabilidad.controlador;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CentroDeAcopio;
import pe.qruta.trazabilidad.dominio.cuenta.Intermediario;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.PuntoDeRecepcion;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.dominio.producto.ProductoAgro;
import pe.qruta.trazabilidad.dominio.producto.ProductoPecuario;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


/**
 * Objetos que entran y salen de la API REST.
 * Separan el formato JSON de las clases del dominio y nunca incluyen contraseñas (RNF-02).
 */
final class Dtos {

    private static final DateTimeFormatter FORMATO_HORA =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");


    private Dtos() {
    }


    record LoginSolicitud(
            String nombreUsuario,
            String contrasena
    ) {
    }


    record LoginRespuesta(
            String token,
            UsuarioRespuesta usuario
    ) {
    }


    record CambioEstadoSolicitud(
            String estado
    ) {
    }


    record UsuarioRespuesta(
            String nombreUsuario,
            String nombreCompleto,
            String rol,
            String tipoCuenta,
            String estadoCuenta,
            String estadoCarga,
            String razonSocial,
            String region,
            String ubicacionActual,
            List<String> necesidades
    ) {

        static UsuarioRespuesta desde(Usuario usuario) {

            String razonSocial = null;
            String region = null;
            String estadoCarga = null;
            String ubicacionActual = null;
            List<String> necesidades = null;

            if (usuario instanceof Productor productor) {
                razonSocial = productor.getRazonSocial();
                region = productor.getRegion();
            }

            if (usuario instanceof Intermediario intermediario) {
                razonSocial = intermediario.getRazonSocial();
                region = intermediario.getRegion();
            }

            if (usuario instanceof Transportista transportista) {
                estadoCarga = transportista.getEstadoCarga().name();
                ubicacionActual = transportista.getUbicacionActual();
            }

            if (usuario instanceof PuntoDeRecepcion punto) {
                necesidades = punto.getNecesidades().stream().map(Enum::name).sorted().toList();
            }

            return new UsuarioRespuesta(
                usuario.getNombreUsuario(),
                usuario.getNombreCompleto(),
                usuario.getRol().name(),
                tipoCuenta(usuario),
                usuario.getEstadoCuenta() == null ? null : usuario.getEstadoCuenta().name(),
                estadoCarga,
                razonSocial,
                region,
                ubicacionActual,
                necesidades
            );
        }


        /** Tipo de cuenta para la interfaz: la clase del dominio o, si es un usuario simple, su rol. */
        private static String tipoCuenta(Usuario usuario) {

            if (usuario instanceof Productor) return "PRODUCTOR";
            if (usuario instanceof Transportista) return "TRANSPORTISTA";
            if (usuario instanceof Mercado) return "MERCADO";
            if (usuario instanceof CentroDeAcopio) return "CENTRO_DE_ACOPIO";

            return usuario.getRol().name();
        }
    }


    record ProductoRespuesta(
            Long id,
            String codigo,
            String nombre,
            String tipoProducto,
            String variedadOEspecie,
            String lote,
            String fechaProduccion,
            String productor,
            String intermediario,
            String estado,
            String siguienteEstado,
            boolean publicado,
            String horaInicioTransporte,
            String horaLlegada,
            String duracionTransporte,
            String destino,
            String destinoRazonSocial,
            String horaEntrega,
            boolean recepcionRegistrada,
            String temperaturaRecepcion,
            String motivoBloqueo
    ) {

        static ProductoRespuesta desde(Producto producto) {

            Medicion recepcion =
                producto.getMediciones().isEmpty() ? null : producto.getMediciones().get(0);


            EstadoProducto siguiente =
                producto.getEstado().siguiente();

            return new ProductoRespuesta(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getTipoProducto() == null ? null : producto.getTipoProducto().name(),
                variedadOEspecie(producto),
                producto.getLote(),
                formatear(producto.getFechaProduccion()),
                producto.getProductor().getNombreUsuario(),
                producto.getIntermediarioAsignado() == null
                    ? null
                    : producto.getIntermediarioAsignado().getNombreUsuario(),
                producto.getEstado().name(),
                siguiente == null ? null : siguiente.name(),
                producto.isPublicado(),
                formatear(producto.getHoraInicioTransporte()),
                formatear(producto.getHoraLlegada()),
                producto.getHoraLlegada() == null
                    ? null
                    : formatear(producto.getDuracionTransporte()),
                producto.getDestino() == null ? null : producto.getDestino().getNombreUsuario(),
                producto.getDestino() == null ? null : producto.getDestino().getRazonSocial(),
                formatear(producto.getHoraEntrega()),
                recepcion != null,
                recepcion == null ? null : recepcion.temperatura().toPlainString(),
                producto.getMotivoBloqueo()
            );
        }


        private static String variedadOEspecie(Producto producto) {

            if (producto instanceof ProductoAgro agro) return agro.getVariedad();
            if (producto instanceof ProductoPecuario pecuario) return pecuario.getEspecie();

            return null;
        }
    }


    record CambioEstadoRespuesta(
            String estadoAnterior,
            String estadoNuevo,
            String fechaHora,
            String usuario
    ) {

        static CambioEstadoRespuesta desde(CambioEstado cambio) {
            return new CambioEstadoRespuesta(
                cambio.estadoAnterior().name(),
                cambio.estadoNuevo().name(),
                formatear(cambio.fechaHora()),
                cambio.usuario().getNombreUsuario()
            );
        }
    }


    record DestinoSolicitud(
            String destino
    ) {
    }


    record RecepcionSolicitud(
            BigDecimal temperatura,
            BigDecimal humedad
    ) {
    }


    record UbicacionSolicitud(
            String ubicacion
    ) {
    }


    record NecesidadesSolicitud(
            Set<TipoProducto> necesidades
    ) {
    }


    record DestinoRespuesta(
            String nombreUsuario,
            String razonSocial,
            String tipoCuenta,
            String region
    ) {

        static DestinoRespuesta desde(PuntoDeRecepcion punto) {
            return new DestinoRespuesta(
                punto.getNombreUsuario(),
                punto.getRazonSocial(),
                punto instanceof Mercado ? "MERCADO" : "CENTRO_DE_ACOPIO",
                punto.getRegion()
            );
        }
    }


    record EvaluacionRespuesta(
            String fechaHora,
            String temperatura,
            long vidaUtilRestanteHoras,
            boolean rupturaCadenaFrio,
            String nivelRiesgo,
            boolean aptoConsumo
    ) {

        static EvaluacionRespuesta desde(Evaluacion evaluacion) {
            return new EvaluacionRespuesta(
                formatear(evaluacion.fechaHora()),
                evaluacion.temperaturaEvaluada().toPlainString(),
                evaluacion.vidaUtilRestanteHoras(),
                evaluacion.rupturaCadenaFrio(),
                evaluacion.nivelRiesgo().name(),
                evaluacion.aptoConsumo()
            );
        }
    }


    record ErrorRespuesta(
            String mensaje
    ) {
    }


    private static String formatear(LocalDateTime fechaHora) {
        return fechaHora == null ? null : fechaHora.format(FORMATO_HORA);
    }


    private static String formatear(Duration duracion) {
        return duracion.toHours() + " h " + duracion.toMinutesPart() + " min";
    }
}
