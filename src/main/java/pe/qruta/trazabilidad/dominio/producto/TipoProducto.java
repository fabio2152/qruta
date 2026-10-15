package pe.qruta.trazabilidad.dominio.producto;


/** Productos de la plataforma con sus parámetros de conservación (sección 6.3). */
public enum TipoProducto {

    POLLO(168),
    PALTA(504),
    BANANO(336);


    private final int vidaUtilBaseHoras;


    TipoProducto(int vidaUtilBaseHoras) {
        this.vidaUtilBaseHoras = vidaUtilBaseHoras;
    }


    public int getVidaUtilBaseHoras() {
        return vidaUtilBaseHoras;
    }
}
