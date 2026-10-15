package pe.qruta.trazabilidad.dominio.producto;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;


public class ProductoPecuario extends Producto {


    public ProductoPecuario(String nombre, Usuario productor) {
        super(nombre, productor, null);
    }
}
