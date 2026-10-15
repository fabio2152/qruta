package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.producto.ProductoAgro;
import pe.qruta.trazabilidad.dominio.producto.ProductoPecuario;
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
                .orElseThrow();

        String productor = producto.getProductor().getNombreUsuario();
        String region = null;

        if (producto.getProductor() instanceof Productor conDatos) {
            productor = conDatos.getRazonSocial();
            region = conDatos.getRegion();
        }

        String variedadOEspecie = null;
        List<String> certificados = new ArrayList<>();

        if (producto instanceof ProductoPecuario pecuario) {
            variedadOEspecie = pecuario.getEspecie();
            certificados.add(pecuario.getCertificadoSanitarioSenasa());
        }

        if (producto instanceof ProductoAgro agro) {
            variedadOEspecie = agro.getVariedad();
            certificados.add(agro.getCertificadoFitosanitario());
        }

        return new FichaPublica(
            producto.getCodigo(),
            producto.getNombre(),
            producto.getTipoProducto(),
            variedadOEspecie,
            productor,
            region,
            producto.getLote(),
            producto.getFechaProduccion(),
            producto.getEstado(),
            certificados
        );
    }
}
