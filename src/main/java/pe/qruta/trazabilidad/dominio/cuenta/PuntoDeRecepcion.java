package pe.qruta.trazabilidad.dominio.cuenta;

import java.util.EnumSet;
import java.util.Set;

import pe.qruta.trazabilidad.dominio.producto.TipoProducto;


/** Destino donde se recibe el producto: mercado o centro de acopio (D-03). */
public abstract class PuntoDeRecepcion extends Intermediario {

    private final Set<TipoProducto> necesidades = EnumSet.noneOf(TipoProducto.class);


    protected PuntoDeRecepcion(
            String nombreUsuario,
            String nombreCompleto,
            String correo,
            String contrasena,
            EstadoCuenta estadoCuenta,
            String razonSocial,
            String ruc,
            String telefono,
            String region
    ) {
        super(nombreUsuario, nombreCompleto, correo, contrasena, estadoCuenta, razonSocial, ruc, telefono, region);
    }


    public void configurarNecesidades(Set<TipoProducto> necesidades) {
        this.necesidades.clear();
        this.necesidades.addAll(necesidades);
    }


    public Set<TipoProducto> getNecesidades() {
        return Set.copyOf(necesidades);
    }


    /** RE-25: solo se puede elegir un destino que necesite ese tipo de producto. */
    public boolean necesita(TipoProducto tipoProducto) {
        return necesidades.contains(tipoProducto);
    }
}
