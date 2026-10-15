package pe.qruta.trazabilidad.configuracion;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import pe.qruta.trazabilidad.dominio.Rol;
import pe.qruta.trazabilidad.dominio.Usuario;
import pe.qruta.trazabilidad.dominio.cuenta.CentroDeAcopio;
import pe.qruta.trazabilidad.dominio.cuenta.CifradorDeContrasenas;
import pe.qruta.trazabilidad.dominio.cuenta.EstadoCuenta;
import pe.qruta.trazabilidad.dominio.cuenta.Mercado;
import pe.qruta.trazabilidad.dominio.cuenta.Productor;
import pe.qruta.trazabilidad.dominio.cuenta.TipoDocumento;
import pe.qruta.trazabilidad.dominio.cuenta.TipoProductor;
import pe.qruta.trazabilidad.dominio.cuenta.Transportista;
import pe.qruta.trazabilidad.repositorio.UsuarioRepositorio;


/**
 * Carga las cuentas iniciales al arrancar, si todavía no existen (sección 9.5).
 * Todas están ACTIVAS y tienen la contraseña clave123, guardada con BCrypt.
 */
@Component
public class CargaUsuariosDePrueba
        implements CommandLineRunner {

    private static final String CONTRASENA = "clave123";

    private final UsuarioRepositorio usuarios;

    private final CifradorDeContrasenas cifrador;


    public CargaUsuariosDePrueba(
            UsuarioRepositorio usuarios,
            CifradorDeContrasenas cifrador
    ) {
        this.usuarios = usuarios;
        this.cifrador = cifrador;
    }


    @Override
    public void run(String... args) {

        String clave = cifrador.cifrar(CONTRASENA);

        registrarSiNoExiste(new Usuario(
            "regulador1", "Inspector SENASA", "regulador1@senasa.gob.pe", clave, Rol.REGULADOR, EstadoCuenta.ACTIVA
        ));

        registrarSiNoExiste(new Productor(
            "productor1", "Juan Mamani Quispe", "productor1@qruta.pe", clave, EstadoCuenta.ACTIVA,
            "Granja Mamani", TipoDocumento.DNI, "45678912", "984111222", TipoProductor.AGROPECUARIO, "Cusco"
        ));

        registrarSiNoExiste(new Transportista(
            "transportista1", "Luis Huamán Torres", "transportista1@qruta.pe", clave, EstadoCuenta.ACTIVA,
            "Transportes Andinos SAC", "20601234567", "987654321", "Lima", "ABC-123"
        ));

        registrarSiNoExiste(new Transportista(
            "transportista2", "Ana Flores Ríos", "transportista2@qruta.pe", clave, EstadoCuenta.ACTIVA,
            "Frío Express EIRL", "20607654321", "987123456", "Lima", "DEF-456"
        ));

        registrarSiNoExiste(new Mercado(
            "mercado1", "Carmen Rojas Villa", "mercado1@qruta.pe", clave, EstadoCuenta.ACTIVA,
            "Mercado Mayorista de Lima", "20509876543", "014567890", "Lima"
        ));

        registrarSiNoExiste(new CentroDeAcopio(
            "acopio1", "Pedro Salazar Cruz", "acopio1@qruta.pe", clave, EstadoCuenta.ACTIVA,
            "Centro de Acopio Huancayo", "20512345678", "064223344", "Junín"
        ));

        // Usuarios de la demostración de la fase 1 (asignación directa, D-04).
        registrarSiNoExiste(new Usuario(
            "intermediario1", "Intermediario de prueba 1", "intermediario1@qruta.pe", clave, Rol.INTERMEDIARIO, EstadoCuenta.ACTIVA
        ));

        registrarSiNoExiste(new Usuario(
            "intermediario2", "Intermediario de prueba 2", "intermediario2@qruta.pe", clave, Rol.INTERMEDIARIO, EstadoCuenta.ACTIVA
        ));
    }


    private void registrarSiNoExiste(Usuario usuario) {

        if (usuarios.buscarPorNombreUsuario(usuario.getNombreUsuario()).isEmpty()) {
            usuarios.guardar(usuario);
        }
    }
}
