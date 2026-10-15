package pe.qruta.trazabilidad.servicio;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import pe.qruta.trazabilidad.dominio.EstadoProducto;
import pe.qruta.trazabilidad.dominio.Producto;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.evaluacion.Evaluacion;
import pe.qruta.trazabilidad.dominio.evaluacion.NivelRiesgo;
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
                .orElseThrow(
                    () -> new ProductoNoEncontradoException(
                        "No existe un producto con el código " + codigo
                    )
                );

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
            certificados,
            esApto(producto) ? "APTO PARA CONSUMO" : "NO APTO PARA CONSUMO",
            producto.getMotivoBloqueo(),
            producto.getHoraEntrega() == null
                ? null
                : Duration.between(producto.getHoraEntrega(), LocalDateTime.now(reloj)).toHours()
        );
    }


    /** RE-38: un producto bloqueado o con riesgo CRITICO no es apto para el consumo. */
    private boolean esApto(Producto producto) {

        Evaluacion evaluacion = producto.getUltimaEvaluacion();

        return producto.getEstado() != EstadoProducto.BLOQUEADO
            && (evaluacion == null || evaluacion.nivelRiesgo() != NivelRiesgo.CRITICO);
    }
}
