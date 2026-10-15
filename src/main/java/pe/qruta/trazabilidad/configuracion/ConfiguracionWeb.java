package pe.qruta.trazabilidad.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pe.qruta.trazabilidad.controlador.InterceptorDeSesion;


/** Todas las rutas /api exigen sesión, salvo el registro, el login y la consulta pública (RE-17). */
@Configuration
public class ConfiguracionWeb
        implements WebMvcConfigurer {

    private final InterceptorDeSesion interceptor;


    public ConfiguracionWeb(
            InterceptorDeSesion interceptor
    ) {
        this.interceptor = interceptor;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registro) {

        registro
            .addInterceptor(interceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/registro",
                "/api/auth/login",
                "/api/publico/**"
            );
    }
}
