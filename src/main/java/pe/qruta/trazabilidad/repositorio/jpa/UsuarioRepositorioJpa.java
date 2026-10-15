package pe.qruta.trazabilidad.repositorio.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


/** Implementación de UsuarioRepositorio sobre PostgreSQL con Spring Data JPA. */
@Repository
@Transactional
public class UsuarioRepositorioJpa
        implements UsuarioRepositorio {

    private final UsuarioJpa jpa;


    public UsuarioRepositorioJpa(UsuarioJpa jpa) {
        this.jpa = jpa;
    }


    /** Si el usuario ya existe, actualiza su fila (por ejemplo, al aprobarlo o al ocuparse). */
    @Override
    public void guardar(Usuario usuario) {

        UsuarioEntidad entidad =
            jpa
                .findByNombreUsuario(usuario.getNombreUsuario())
                .orElseGet(() -> new UsuarioEntidad(usuario));

        entidad.actualizarDesde(usuario);

        jpa.save(entidad);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return jpa
                .findByNombreUsuario(nombreUsuario)
                .map(UsuarioEntidad::aDominio);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return jpa
                .findAll(Sort.by("id"))
                .stream()
                .map(UsuarioEntidad::aDominio)
                .toList();
    }
}
