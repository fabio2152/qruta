package pe.qruta.trazabilidad.dominio.cuenta;


/**
 * Cifra y verifica contraseñas. El dominio solo conoce esta interfaz;
 * la implementación con BCrypt vive en el paquete configuracion.
 */
public interface CifradorDeContrasenas {

    String cifrar(String contrasena);

    boolean coincide(String contrasena, String contrasenaCifrada);
}
