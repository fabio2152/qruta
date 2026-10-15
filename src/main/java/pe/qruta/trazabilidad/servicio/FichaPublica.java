package pe.qruta.trazabilidad.servicio;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.producto.ProductoAgro;
import pe.qruta.trazabilidad.dominio.producto.ProductoPecuario;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


/**
 * Lo que ve el consumidor al escanear el QR (RE-37).
 * Solo tiene campos permitidos: nunca correos, documentos, teléfonos ni contraseñas (RNF-03).
 */
public record FichaPublica(
        String codigo,
        String nombre,
        TipoProducto tipoProducto,
        String variedadOEspecie,
        String productor,
        String regionDeOrigen,
        String lote,
        LocalDateTime fechaProduccion,
        EstadoProducto etapa,
        List<String> certificados,
        String veredicto,
        String motivo,
        Long horasEnElMercado,
        List<PasoDelRecorrido> recorrido,
        Long duracionTransporteHoras,
        Evaluacion ultimaEvaluacion
) {

    static final String APTO = "APTO PARA CONSUMO";

    static final String NO_APTO = "NO APTO PARA CONSUMO";


    /** Construye la ficha copiando del producto solo los datos públicos. */
    static FichaPublica desde(
            Producto producto,
            LocalDateTime ahora
    ) {

        String productor = producto.getProductor().getNombreUsuario();
        String region = null;

        if (producto.getProductor() instanceof Productor conDatos) {
            productor = conDatos.getRazonSocial();
            region = conDatos.getRegion();
        }

        String variedadOEspecie = null;
        List<String> certificados = new ArrayList<>();

        if (producto instanceof ProductoPecuario pecuario) {
            variedadOEspecie = pecuario.getEspecie();
            certificados.add(pecuario.getCertificadoSanitarioSenasa());
        }

        if (producto instanceof ProductoAgro agro) {
            variedadOEspecie = agro.getVariedad();
            certificados.add(agro.getCertificadoFitosanitario());
        }

        return new FichaPublica(
            producto.getCodigo(),
            producto.getNombre(),
            producto.getTipoProducto(),
            variedadOEspecie,
            productor,
            region,
            producto.getLote(),
            producto.getFechaProduccion(),
            producto.getEstado(),
            certificados,
            producto.esAptoParaConsumo() ? APTO : NO_APTO,
            producto.getMotivoBloqueo(),
            producto.getHoraEntrega() == null ? null : Duration.between(producto.getHoraEntrega(), ahora).toHours(),
            recorrido(producto),
            producto.getHoraLlegada() == null ? null : producto.getDuracionTransporte().toHours(),
            producto.getUltimaEvaluacion()
        );
    }


    /** El recorrido muestra las etapas y sus fechas, pero no quién hizo cada cambio. */
    private static List<PasoDelRecorrido> recorrido(Producto producto) {

        return producto.getHistorial()
                .stream()
                .map(cambio -> new PasoDelRecorrido(cambio.estadoNuevo(), cambio.fechaHora(), cambio.observacion()))
                .toList();
    }
}
