package pe.qruta.trazabilidad.servicio;

import java.util.List;
import java.util.stream.Collectors;

import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;


/** Arma el reporte sanitario en CSV: una fila por producto con las columnas de RE-36. */
class ReporteSanitario {

    static final String ENCABEZADO =
        "codigo,tipo,productor,region_origen,etapa,transportista,destino,duracion_transporte_horas,"
            + "temperatura_recepcion,vida_util_restante_horas,ruptura,nivel_riesgo,apto,bloqueado";


    String generar(List<Producto> productos) {

        StringBuilder csv = new StringBuilder(ENCABEZADO).append("\n");

        for (Producto producto : productos) {
            csv.append(fila(producto)).append("\n");
        }

        return csv.toString();
    }


    private String fila(Producto producto) {

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

        return celdas.stream().map(this::celda).collect(Collectors.joining(","));
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
}
