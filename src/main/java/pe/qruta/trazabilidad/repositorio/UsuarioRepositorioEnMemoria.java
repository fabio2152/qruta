package pe.qruta.trazabilidad.repositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pe.qruta.trazabilidad.dominio.Usuario;


public class UsuarioRepositorioEnMemoria
        implements UsuarioRepositorio {

    private final List<Usuario> usuarios = new ArrayList<>();


    @Override
    public void guardar(Usuario usuario) {

        int posicion = usuarios.indexOf(usuario);

        if (posicion >= 0) {
            usuarios.set(posicion, usuario);
        } else {
            usuarios.add(usuario);
        }
    }


    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {

        return usuarios
                .stream()
                .filter(usuario -> usuario.getNombreUsuario().equals(nombreUsuario))
                .findFirst();
    }


    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }
}
