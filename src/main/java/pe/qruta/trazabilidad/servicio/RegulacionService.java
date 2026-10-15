package pe.qruta.trazabilidad.servicio;

import java.time.Clock;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegulacionService {

    private final ProductoRepositorio productos;

    private final UsuarioRepositorio usuarios;

    private final Clock reloj;


    public RegulacionService(
            ProductoRepositorio productos,
            UsuarioRepositorio usuarios,
            Clock reloj
    ) {
        this.productos = productos;
        this.usuarios = usuarios;
        this.reloj = reloj;
    }


    public void bloquear(
            Long idProducto,
            Usuario regulador,
            String motivo
    ) {
    }
}
