package pe.qruta.trazabilidad.dominio.producto;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;


/** Producto agrícola (palta o banano). */
public class ProductoAgro extends Producto {

    private final String variedad;

    private final EstadoMaduracion estadoMaduracion;

    private final Integer calidad;

    private final String certificadoFitosanitario;


    public ProductoAgro(
            String codigo,
            Usuario productor,
            DatosProducto datos
    ) {
        super(codigo, productor, datos);
        this.variedad = datos.getVariedad();
        this.estadoMaduracion = datos.getEstadoMaduracion();
        this.calidad = datos.getCalidad();
        this.certificadoFitosanitario = datos.getCertificadoFitosanitario();
    }


    public String getVariedad() {
        return variedad;
    }


    public EstadoMaduracion getEstadoMaduracion() {
        return estadoMaduracion;
    }


    public Integer getCalidad() {
        return calidad;
    }


    public String getCertificadoFitosanitario() {
        return certificadoFitosanitario;
    }
}
