package pe.qruta.trazabilidad.repositorio.jpa;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pe.qruta.trazabilidad.dominio.CambioEstado;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.Medicion;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


/**
 * Implementación de ProductoRepositorio sobre PostgreSQL con Spring Data JPA.
 * Traduce entre el Producto del dominio (Java puro) y ProductoEntidad (tabla).
 */
@Repository
@Transactional
public class ProductoRepositorioJpa
        implements ProductoRepositorio {

    private final ProductoJpa productos;

    private final UsuarioJpa usuarios;


    public ProductoRepositorioJpa(
            ProductoJpa productos,
            UsuarioJpa usuarios
    ) {
        this.productos = productos;
        this.usuarios = usuarios;
    }


    @Override
    public Producto guardar(Producto producto) {

        ProductoEntidad entidad =
            producto.getId() == null
                ? new ProductoEntidad(
                    producto,
                    usuarioEntidad(producto.getProductor().getNombreUsuario())
                )
                : productos.findById(producto.getId()).orElseThrow();

        // D-13: si otro usuario guardó el producto después de que lo leímos, la operación se rechaza.
        if (producto.getId() != null && !Objects.equals(producto.getVersion(), entidad.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(ProductoEntidad.class, producto.getId());
        }

        UsuarioEntidad intermediario =
            producto.getIntermediarioAsignado() == null
                ? null
                : usuarioEntidad(producto.getIntermediarioAsignado().getNombreUsuario());

        // El producto puede haber cambiado el estado de carga de su transportista (RE-28, RE-41).
        if (producto.getIntermediarioAsignado() instanceof Transportista transportista) {
            intermediario.actualizarDesde(transportista);
        }

        entidad.actualizarDesde(
            producto,
            intermediario,
            producto.getDestino() == null
                ? null
                : usuarioEntidad(producto.getDestino().getNombreUsuario())
        );

        // Las mediciones, igual que el historial, solo crecen.
        List<Medicion> mediciones = producto.getMediciones();

        for (int i = entidad.getMediciones().size(); i < mediciones.size(); i++) {

            Medicion medicion = mediciones.get(i);

            entidad.getMediciones().add(
                new MedicionEntidad(
                    medicion,
                    usuarioEntidad(medicion.registradaPor().getNombreUsuario())
                )
            );
        }

        List<Evaluacion> evaluaciones = producto.getEvaluaciones();

        for (int i = entidad.getEvaluaciones().size(); i < evaluaciones.size(); i++) {
            entidad.getEvaluaciones().add(new EvaluacionEntidad(evaluaciones.get(i)));
        }

        // El historial solo crece: se agregan los cambios que todavía no están guardados.
        List<CambioEstado> historial = producto.getHistorial();

        for (int i = entidad.getHistorial().size(); i < historial.size(); i++) {

            CambioEstado cambio = historial.get(i);

            entidad.getHistorial().add(
                new CambioEstadoEntidad(
                    cambio,
                    usuarioEntidad(cambio.usuario().getNombreUsuario())
                )
            );
        }

        ProductoEntidad guardada = productos.saveAndFlush(entidad);

        producto.restaurar(
            guardada.getId(),
            producto.getIntermediarioAsignado(),
            producto.getEstado(),
            producto.getHoraInicioTransporte(),
            producto.getHoraLlegada(),
            producto.getHistorial(),
            producto.isPublicado(),
            guardada.getVersion()
        );

        return producto;
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarPorId(Long id) {
        return productos
                .findById(id)
                .map(ProductoEntidad::aDominio);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productos
                .findAll(Sort.by("id"))
                .stream()
                .map(ProductoEntidad::aDominio)
                .toList();
    }


    private UsuarioEntidad usuarioEntidad(String nombreUsuario) {
        return usuarios
                .findByNombreUsuario(nombreUsuario)
                .orElseThrow();
    }
}
