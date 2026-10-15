package pe.qruta.trazabilidad.dominio.cuenta;


/** Reglas de formato de los datos de una cuenta (RE-12, RE-14). */
public final class ValidadorDeFormato {

    private static final String PATRON_CORREO = "[^@\\s]+@[^@\\s]+\\.[^@\\s]+";

    private static final String PATRON_RUC = "\\d{11}";


    private ValidadorDeFormato() {
    }


    public static boolean esCorreoValido(String correo) {
        return correo != null && correo.matches(PATRON_CORREO);
    }


    public static boolean esRucValido(String ruc) {
        return ruc != null && ruc.matches(PATRON_RUC);
    }
}
