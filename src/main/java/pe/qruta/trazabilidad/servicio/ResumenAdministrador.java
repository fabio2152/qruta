package pe.qruta.trazabilidad.servicio;

import java.util.Map;

import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Rol;


/** Panel general del administrador (RE-40): cuántos usuarios hay de cada rol y cuántos productos en cada etapa. */
public record ResumenAdministrador(
        Map<Rol, Long> usuariosPorRol,
        Map<EstadoProducto, Long> productosPorEtapa
) {
}
