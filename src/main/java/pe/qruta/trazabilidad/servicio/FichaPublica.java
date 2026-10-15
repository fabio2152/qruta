package pe.qruta.trazabilidad.servicio;

import java.time.LocalDateTime;
import java.util.List;

import pe.qruta.trazabilidad.dominio.EstadoProducto;
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
        String motivo
) {
}
