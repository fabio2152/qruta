package pe.qruta.trazabilidad.configuracion;

import java.time.Clock;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.repositorio.ProductoRepositorio;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;
import pe.qruta.trazabilidad.servicio.AdministracionService;
import pe.qruta.trazabilidad.servicio.ConsultaPublicaService;
import pe.qruta.trazabilidad.servicio.LoginService;
import pe.qruta.trazabilidad.servicio.ProductoService;
import pe.qruta.trazabilidad.servicio.RecepcionService;
import pe.qruta.trazabilidad.servicio.RegistroService;
import pe.qruta.trazabilidad.servicio.RegulacionService;
import pe.qruta.trazabilidad.servicio.SesionService;
import pe.qruta.trazabilidad.servicio.TransporteService;


/**
 * Conecta las clases de dominio y servicio (Java puro, sin Spring)
 * con el contenedor de Spring. Aquí se decide qué reloj y qué cifrador usa la aplicación real;
 * los repositorios son los de PostgreSQL (paquete repositorio.jpa).
 */
@Configuration
public class ConfiguracionAplicacion {


    @Bean
    public Clock relojDelSistema() {
        return Clock.system(ZoneId.of("America/Lima"));
    }


    @Bean
    public CifradorDeContrasenas cifradorDeContrasenas() {
        return new CifradorBCrypt();
    }


    @Bean
    public ProductoService productoService(
            ProductoRepositorio productoRepositorio,
            Clock relojDelSistema
    ) {
        return new ProductoService(
            productoRepositorio,
            relojDelSistema
        );
    }


    @Bean
    public LoginService loginService(
            UsuarioRepositorio usuarioRepositorio,
            CifradorDeContrasenas cifrador
    ) {
        return new LoginService(usuarioRepositorio, cifrador);
    }


    @Bean
    public RegistroService registroService(
            UsuarioRepositorio usuarioRepositorio,
            CifradorDeContrasenas cifrador
    ) {
        return new RegistroService(usuarioRepositorio, cifrador);
    }


    @Bean
    public SesionService sesionService(
            Clock relojDelSistema
    ) {
        return new SesionService(relojDelSistema);
    }


    @Bean
    public RecepcionService recepcionService(
            ProductoRepositorio productoRepositorio,
            Clock relojDelSistema
    ) {
        return new RecepcionService(productoRepositorio, relojDelSistema);
    }


    @Bean
    public RegulacionService regulacionService(
            ProductoRepositorio productoRepositorio,
            UsuarioRepositorio usuarioRepositorio,
            Clock relojDelSistema
    ) {
        return new RegulacionService(productoRepositorio, usuarioRepositorio, relojDelSistema);
    }


    @Bean
    public ConsultaPublicaService consultaPublicaService(
            ProductoRepositorio productoRepositorio,
            Clock relojDelSistema
    ) {
        return new ConsultaPublicaService(productoRepositorio, relojDelSistema);
    }


    @Bean
    public AdministracionService administracionService(
            UsuarioRepositorio usuarioRepositorio,
            ProductoRepositorio productoRepositorio,
            CifradorDeContrasenas cifrador
    ) {
        return new AdministracionService(usuarioRepositorio, productoRepositorio, cifrador);
    }


    @Bean
    public TransporteService transporteService(
            ProductoRepositorio productoRepositorio,
            UsuarioRepositorio usuarioRepositorio
    ) {
        return new TransporteService(productoRepositorio, usuarioRepositorio);
    }
}
