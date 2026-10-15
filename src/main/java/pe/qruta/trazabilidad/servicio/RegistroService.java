package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


public class RegistroService {

    private final UsuarioRepositorio usuarios;


    public RegistroService(
            UsuarioRepositorio usuarios
    ) {
        this.usuarios = usuarios;
    }


    public Usuario registrar(SolicitudRegistro solicitud) {
        return null;
    }
}
