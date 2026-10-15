package pe.qruta.trazabilidad.configuracion;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;


/** Cifra las contraseñas con BCrypt (solo la librería spring-security-crypto). */
public class CifradorBCrypt
        implements CifradorDeContrasenas {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();


    @Override
    public String cifrar(String contrasena) {
        return bcrypt.encode(contrasena);
    }


    @Override
    public boolean coincide(String contrasena, String contrasenaCifrada) {
        return bcrypt.matches(contrasena, contrasenaCifrada);
    }
}
