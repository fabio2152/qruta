package pe.qruta.trazabilidad.dominio.producto;

import java.math.BigDecimal;


/**
 * Productos de la plataforma con sus parámetros de conservación (sección 6.3).
 * Son referencias para el prototipo: deben validarse con fuentes técnicas antes de producción.
 */
public enum TipoProducto {

    POLLO(CategoriaProducto.PECUARIO, "0", "4", 168, 4),
    PALTA(CategoriaProducto.AGRICOLA, "5", "7", 504, 2),
    BANANO(CategoriaProducto.AGRICOLA, "13", "15", 336, 2);


    private final CategoriaProducto categoria;

    private final BigDecimal temperaturaMinima;

    private final BigDecimal temperaturaMaxima;

    private final int vidaUtilBaseHoras;

    private final int factorDeterioro;


    TipoProducto(
            CategoriaProducto categoria,
            String temperaturaMinima,
            String temperaturaMaxima,
            int vidaUtilBaseHoras,
            int factorDeterioro
    ) {
        this.categoria = categoria;
        this.temperaturaMinima = new BigDecimal(temperaturaMinima);
        this.temperaturaMaxima = new BigDecimal(temperaturaMaxima);
        this.vidaUtilBaseHoras = vidaUtilBaseHoras;
        this.factorDeterioro = factorDeterioro;
    }


    public CategoriaProducto getCategoria() {
        return categoria;
    }


    public BigDecimal getTemperaturaMinima() {
        return temperaturaMinima;
    }


    public BigDecimal getTemperaturaMaxima() {
        return temperaturaMaxima;
    }


    public int getVidaUtilBaseHoras() {
        return vidaUtilBaseHoras;
    }


    public int getFactorDeterioro() {
        return factorDeterioro;
    }
}
