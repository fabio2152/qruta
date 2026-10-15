package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class AdministracionService {

    private final UsuarioRepositorio usuarios;

    private final ProductoRepositorio productos;


    public AdministracionService(
            UsuarioRepositorio usuarios,
            ProductoRepositorio productos
    ) {
        this.usuarios = usuarios;
        this.productos = productos;
    }


    public Usuario crearRegulador(
            Usuario administrador,
            SolicitudRegistro solicitud
    ) {
        return null;
    }
}
