package pe.qruta.trazabilidad.dominio.producto;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;


public class ProductoAgro extends Producto {


    public ProductoAgro(
            String codigo,
            Usuario productor,
            DatosProducto datos
    ) {
        super(codigo, productor, datos);
    }


    public String getVariedad() {
        return null;
    }


    public Integer getCalidad() {
        return null;
    }
}
