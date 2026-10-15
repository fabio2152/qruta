package pe.qruta.trazabilidad.servicio;

import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;


/**
 * No cifra: compara las contraseñas tal cual.
 * Solo lo usan los constructores simples de los servicios, que corresponden
 * a las pruebas de la fase 1 (T16, T17). La aplicación real usa CifradorBCrypt.
 */
class CifradorTextoPlano
        implements CifradorDeContrasenas {


    @Override
    public String cifrar(String contrasena) {
        return contrasena;
    }


    @Override
    public boolean coincide(String contrasena, String contrasenaCifrada) {
        return contrasena.equals(contrasenaCifrada);
    }
}
