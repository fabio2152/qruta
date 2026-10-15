package pe.qruta.trazabilidad.dominio.producto;


/** Productos de la plataforma con sus parámetros de conservación (sección 6.3). */
public enum TipoProducto {

    POLLO(CategoriaProducto.PECUARIO, 168),
    PALTA(CategoriaProducto.AGRICOLA, 504),
    BANANO(CategoriaProducto.AGRICOLA, 336);


    private final CategoriaProducto categoria;

    private final int vidaUtilBaseHoras;


    TipoProducto(
            CategoriaProducto categoria,
            int vidaUtilBaseHoras
    ) {
        this.categoria = categoria;
        this.vidaUtilBaseHoras = vidaUtilBaseHoras;
    }


    public CategoriaProducto getCategoria() {
        return categoria;
    }


    public int getVidaUtilBaseHoras() {
        return vidaUtilBaseHoras;
    }
}
