package pe.qruta.trazabilidad.dominio.producto;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.Usuario;


/** Producto de origen animal (pollo). */
public class ProductoPecuario extends Producto {

    private final String especie;

    private final String codigoPlanta;

    private final String certificadoSanitarioSenasa;


    public ProductoPecuario(
            String codigo,
            Usuario productor,
            DatosProducto datos
    ) {
        super(codigo, productor, datos);
        this.especie = datos.getEspecie();
        this.codigoPlanta = datos.getCodigoPlanta();
        this.certificadoSanitarioSenasa = datos.getCertificadoSanitarioSenasa();
    }


    public String getEspecie() {
        return especie;
    }


    public String getCodigoPlanta() {
        return codigoPlanta;
    }


    public String getCertificadoSanitarioSenasa() {
        return certificadoSanitarioSenasa;
    }
}
