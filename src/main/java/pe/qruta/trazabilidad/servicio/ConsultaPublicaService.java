package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.time.LocalDateTime;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;


/** Consulta del consumidor final por el código QR, sin iniciar sesión (CU-15). */
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

        Producto producto =
            productos
                .listarTodos()
                .stream()
                .filter(encontrado -> codigo.equals(encontrado.getCodigo()))
                .findFirst()
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con el código " + codigo
                    )
                );

        return FichaPublica.desde(producto, LocalDateTime.now(reloj));
    }
}
