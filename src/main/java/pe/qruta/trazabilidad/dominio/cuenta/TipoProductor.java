package pe.qruta.trazabilidad.dominio.cuenta;

import pe.qruta.trazabilidad.dominio.producto.CategoriaProducto;


public enum TipoProductor {
    AGRICOLA,
    PECUARIO,
    AGROPECUARIO;


    /** RE-19: el agrícola solo produce agrícolas, el pecuario solo pecuarios y el agropecuario cualquiera. */
    public boolean puedeProducir(CategoriaProducto categoria) {
        return this == AGROPECUARIO || name().equals(categoria.name());
    }
}
