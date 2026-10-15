package pe.qruta.trazabilidad.dominio.evaluacion;

import java.math.BigDecimal;
import java.time.Duration;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.producto.CategoriaProducto;
import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


/**
 * Motor de reglas que evalúa un producto con la medición de su recepción (RE-29 a RE-31, D-09).
 * Es determinístico: la base verificable sobre la que más adelante podría entrenarse un modelo predictivo.
 */
public class EvaluadorDeProducto {

    /** RE-30: hay ruptura cuando la temperatura se aleja del rango en MÁS de 3 °C. */
    private static final BigDecimal MARGEN_DE_RUPTURA = new BigDecimal("3");

    /** RE-31: el riesgo es MEDIO cuando queda el 20 % o menos de la vida útil base. */
    private static final int PORCENTAJE_DE_VIDA_UTIL_MEDIO = 20;


    public Evaluacion evaluar(
            Producto producto,
            Medicion medicion
    ) {

        TipoProducto tipo = producto.getTipoProducto();

        BigDecimal temperatura = medicion.temperatura();

        long vidaUtilRestante = vidaUtilRestante(producto, medicion);

        boolean ruptura = hayRuptura(tipo, temperatura);

        NivelRiesgo riesgo = nivelDeRiesgo(tipo, vidaUtilRestante, ruptura);

        return new Evaluacion(
            medicion.fechaHora(),
            temperatura,
            vidaUtilRestante,
            ruptura,
            riesgo,
            riesgo != NivelRiesgo.CRITICO
        );
    }


    /** RE-29: vida base menos las horas desde la producción, con el transporte penalizado si llegó fuera de rango. */
    private long vidaUtilRestante(
            Producto producto,
            Medicion medicion
    ) {

        TipoProducto tipo = producto.getTipoProducto();

        long horasDesdeLaProduccion =
            Duration.between(producto.getFechaProduccion(), medicion.fechaHora()).toHours();

        long restante = tipo.getVidaUtilBaseHoras() - horasDesdeLaProduccion;

        if (estaFueraDeRango(tipo, medicion.temperatura())) {
            long horasDeTransporte = producto.getDuracionTransporte().toHours();
            restante -= (tipo.getFactorDeterioro() - 1) * horasDeTransporte;
        }

        return Math.max(0, restante);
    }


    private boolean estaFueraDeRango(
            TipoProducto tipo,
            BigDecimal temperatura
    ) {
        return temperatura.compareTo(tipo.getTemperaturaMinima()) < 0
            || temperatura.compareTo(tipo.getTemperaturaMaxima()) > 0;
    }


    /** RE-30: exactamente 3 °C fuera del rango no es ruptura. */
    private boolean hayRuptura(
            TipoProducto tipo,
            BigDecimal temperatura
    ) {
        return temperatura.compareTo(tipo.getTemperaturaMaxima().add(MARGEN_DE_RUPTURA)) > 0
            || temperatura.compareTo(tipo.getTemperaturaMinima().subtract(MARGEN_DE_RUPTURA)) < 0;
    }


    /** RE-31: CRITICO sin vida útil o con ruptura en pecuario; ALTO con ruptura en agrícola; MEDIO con 20 % o menos. */
    private NivelRiesgo nivelDeRiesgo(
            TipoProducto tipo,
            long vidaUtilRestante,
            boolean ruptura
    ) {

        if (vidaUtilRestante == 0) {
            return NivelRiesgo.CRITICO;
        }

        if (ruptura) {
            return tipo.getCategoria() == CategoriaProducto.PECUARIO
                ? NivelRiesgo.CRITICO
                : NivelRiesgo.ALTO;
        }

        if (vidaUtilRestante * 100 <= (long) tipo.getVidaUtilBaseHoras() * PORCENTAJE_DE_VIDA_UTIL_MEDIO) {
            return NivelRiesgo.MEDIO;
        }

        return NivelRiesgo.BAJO;
    }
}
