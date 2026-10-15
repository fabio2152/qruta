package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class TransporteService {

    private final ProductoRepositorio productos;

    private final UsuarioRepositorio usuarios;


    public TransporteService(
            ProductoRepositorio productos,
            UsuarioRepositorio usuarios
    ) {
        this.productos = productos;
        this.usuarios = usuarios;
    }


    public void aceptar(
            Long idProducto,
            Transportista transportista
    ) {
    }
}
