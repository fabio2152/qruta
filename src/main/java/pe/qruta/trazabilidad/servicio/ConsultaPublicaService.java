package pe.qruta.trazabilidad.servicio;

import java.time.Clock;

import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


public class ConsultaPublicaService {

    private final ProductoRepositorio productos;

    private final Clock reloj;


    public ConsultaPublicaService(
            ProductoRepositorio productos,
            Clock reloj
    ) {
        this.productos = productos;
        this.reloj = reloj;
    }


    public FichaPublica consultar(String codigo) {
        return null;
    }
}
