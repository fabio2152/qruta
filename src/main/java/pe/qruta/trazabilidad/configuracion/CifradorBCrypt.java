package pe.qruta.trazabilidad.configuracion;

import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;


public class CifradorBCrypt
        implements CifradorDeContrasenas {


    @Override
    public String cifrar(String contrasena) {
        return null;
    }


    @Override
    public boolean coincide(String contrasena, String contrasenaCifrada) {
        return false;
    }
}
