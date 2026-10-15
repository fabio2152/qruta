# Plan de desarrollo QRUTA, fase 2 (versión local para el sábado)

## Contexto
Para el sábado, QRUTA tiene que mostrar **en local** el flujo completo: un productor crea un lote, un transportista lo traslada, el destino lo recibe y lo evalúa, y el consumidor lo consulta con un código QR.

- **Punto de partida:** el código de `lab4`, ahora en `PROYECTO/qruta` (https://github.com/fabio2152/qruta), con 21 pruebas en verde.
- **Guía principal:** `docs/especificacion-fase2-qruta.md`.
- **Reglas de trabajo:** las de `qruta/CLAUDE.md`. Una prueba a la vez, RED → GREEN → REFACTOR, un commit por fase y push, salida de cada ejecución en `docs/evidencias/`, sin amend, rebase ni push forzado, y sin línea de coautoría.

### Decisiones del usuario (14/10)
- **A.** Pasa el producto a ENTREGADO **el transportista**, con `cambiarEstado` (T51, T52). Ya está registrado en la especificación.
- **B. RE-41, requisito nuevo:** si un producto se bloquea después de su recepción, su transportista vuelve a LIBRE. Se cubre con la prueba **T75**. Ya está registrado en la especificación.
- **C. Sin despliegue para este avance.** La demostración del sábado se hace en local: PostgreSQL en Docker y la aplicación con `./mvnw spring-boot:run`. Render queda para una fase posterior.
  - RNF-01 y la sección 13 de la especificación se marcan como "posterior al avance del sábado".
  - La sección 11, punto 4, pasa a decir "funciona ejecutando la aplicación localmente".

### Cómo se mantienen en verde T01 a T21
Ninguna prueba se modifica. Estas son las decisiones de diseño que lo permiten:
- **`LoginService`:** se conserva su constructor de un solo parámetro, que compara la contraseña tal cual (T16 y T17). Se agrega un constructor que recibe un `CifradorDeContrasenas`, una interfaz en Java puro. Su versión con BCrypt (`spring-security-crypto`) vive en `configuracion/`.
- **`Usuario`:** sus dos constructores actuales crean cuentas ACTIVAS (D-05). El estado de la cuenta es lo único que puede cambiar en un usuario.
- **`CambioEstado`:** gana el campo `observacion` y conserva un constructor de 4 datos que la deja vacía (T15 y T20 comparan con `equals`).
- **`Producto`:**
  - `intermediarioAsignado` deja de ser `final`, para que un transportista pueda aceptar el producto después.
  - `esVisiblePara` compara al revés (`usuario.equals(...)`), para no fallar cuando todavía no hay transportista.
  - El switch de `esVisiblePara` agrega el caso ADMINISTRADOR.
- **`EstadoProducto.BLOQUEADO`:** se valida aparte, para que nunca aparezca el mensaje "ya fue entregado".
- **Productos antiguos sin `TipoProducto`** (como "Mango Kent"): no se pueden recibir ni evaluar.

### El código QR en local
- El QR se genera en el navegador con `qrcodejs` desde CDN y usa `window.location.origin + "/consulta.html?codigo=..."`. No hay que configurar ninguna dirección.
- Para escanearlo con el celular el día de la demo:
  - se abre la aplicación en la laptop con su IP de la red Wi-Fi (por ejemplo `http://192.168.1.20:8080`), así el QR lleva esa dirección;
  - el celular se conecta a la misma red;
  - el usuario permite el puerto 8080 en el firewall de Windows.
- **Plan B, si la red del salón bloquea la conexión:** abrir el enlace del QR en la misma laptop. El paso 7 se muestra igual.

---

## Día 1 (miércoles 14/10): ciclos 8 y 9
**0. Especificación** (commit `docs:`): registrar la decisión C (RNF-01, sección 11 punto 4, sección 13 y el paso 7 del guion con su plan B).

### Ciclo 8: cuentas y sesión (T22 a T32)
Clases nuevas en `dominio/cuenta/`: `EstadoCuenta`, `Productor`, `Transportista`, `Intermediario`, `PuntoDeRecepcion`, `Mercado`, `CentroDeAcopio` y `Regulador`. Solo se agregan cuando una prueba las necesita.

| Prueba | Dónde |
|---|---|
| T22 a T28 | `servicio/RegistroService`. `UsuarioRepositorioEnMemoria` gana `buscarPorCorreo` |
| T29 | `LoginService`: rechaza cuentas PENDIENTE o SUSPENDIDA con un mensaje claro |
| T30 y T31 | `RegistroService.aprobar(idUsuario, aprobador)`: valida si aprueba el regulador o el administrador, según RE-15 |
| T32 | `servicio/SesionService` con `Clock`: token UUID válido durante 8 horas |

- Refactor: `dominio/cuenta/ValidadorDeFormato` (correo, DNI y RUC).
- Luego, sin TDD:
  - endpoints `POST /api/auth/registro`, `/login` y `/logout`;
  - `InterceptorDeSesion` con `Authorization: Bearer`, que reemplaza a `SesionUsuario` y a la cabecera `X-Usuario` (`app.js:38`);
  - `UsuarioEntidad` pasa a `SINGLE_TABLE` con columna discriminadora y estado de la cuenta;
  - `CargaUsuariosDePrueba` carga los usuarios de la sección 9.5 con BCrypt, más los usuarios antiguos;
  - pantalla de registro y manejo de errores 401 y 409.

### Ciclo 9: creación, publicación y aceptación (T33 a T43)
- `dominio/producto/`: `TipoProducto` (rango, vida útil base, factor y categoría), `CategoriaProducto`, `ProductoPecuario` y `ProductoAgro`. Al crearse, el producto recibe un código UUID.
- `ProductoService`:
  - nuevo método de creación para el `Productor` (T33 a T37);
  - `publicar` y `retirarPublicacion` (T38, T39 y T43);
  - `listarPublicaciones` (D-06).
- `servicio/TransporteService.aceptar` (T40 a T42).
- Refactor: las reglas de publicar y aceptar pasan a métodos de `Producto` y `Transportista`.
- Luego, sin TDD:
  - `ProductoEntidad` con intermediario opcional, destino, código, tipo, `@Version` (D-13) y las columnas de las subclases;
  - endpoints de publicar, retirar, aceptar y `GET /api/publicaciones`;
  - en `Dtos` se manejan los campos vacíos.
- **Cierre del día:** `docker compose up -d` y `./mvnw spring-boot:run`, y probar registro, aprobación, login, crear, publicar y aceptar en el navegador.

## Día 2 (jueves 15/10): ciclos 10 y 11, y pantallas operativas
- **Ciclo 10 (T44 a T53):**
  - el destino lo elige el transportista en `TransporteService.elegirDestino`;
  - `Medicion` y `servicio/RecepcionService.registrarRecepcion`;
  - RE-27 y RE-28 en `Producto.cambiarEstado` (hora de entrega y transportista LIBRE);
  - el punto de recepción se agrega a `esVisiblePara` (T53).
  - Refactor: separar `RecepcionService`.
- **Ciclo 11 (T54 a T59 y T75):**
  - `dominio/evaluacion/`: `Evaluacion`, `NivelRiesgo` y `EvaluadorDeProducto` (RE-29 a RE-31);
  - el bloqueo automático, RE-32, se agrega a `Producto.bloquear`;
  - T75 cubre RE-41.
  - Refactor: constantes con nombre para 3 °C y 20 %. Las temperaturas se comparan con `BigDecimal` (7,1 − 4 > 3).
- **Sin TDD:**
  - endpoints de destino, recepción, ubicación, `GET /api/destinos?tipo=` y `PUT /api/destinos/yo`;
  - paneles del productor (con su QR), del transportista y del punto de recepción.
- **Cierre del día:** recorrer en local los pasos 3 a 6 del guion.

## Día 3 (viernes 16/10): ciclos 12 y 13, y ensayo completo
- **Ciclo 12 (T60 a T66):** `servicio/RegulacionService` con bloquear, desbloquear (con `estadoAntesDelBloqueo`), suspender (RE-35) y el reporte CSV (T66).
- **Ciclo 13 (T67 a T70):** `servicio/ConsultaPublicaService` con una ficha pública propia, `FichaPublica`, solo con los campos permitidos (RE-37 a RE-39).
- **Sin TDD:**
  - `GET /api/publico/productos/{codigo}`, que el interceptor deja pasar sin sesión;
  - `consulta.html` con la marca grande APTO o NO APTO, adaptable al celular;
  - panel del regulador y descarga del CSV.
- **Ensayo completo en local** del guion de la sección 14, incluido escanear el QR con el celular por la red Wi-Fi.

## Día 4 (sábado 17/10)
- Ciclo 14 (T71 a T74, administración), solo si sobra tiempo.
- Congelar el código y hacer el ensayo final.
- Antes de exponer:
  - levantar Docker y la aplicación con anticipación;
  - revisar la IP de la laptop en la red del salón;
  - tener listo el plan B del QR.

## Después del sábado (fuera de este avance)
- Despliegue en Render:
  - `Dockerfile` en dos etapas;
  - `server.port=${PORT:8080}` y la conexión a la base por variables de entorno (`SPRING_DATASOURCE_*`);
  - perfil `prod`;
  - despliegue automático cuando GitHub Actions esté en verde;
  - enlace público en el README.

---

## Documentación en cada ciclo
- `docs/bitacora-tdd.md`:
  - una fila por prueba en el resumen y una sección por ciclo;
  - la línea "Repositorio" apunta a qruta;
  - RE-41 y T75 se registran como "requisito nuevo", y las pruebas que pasan sin código como "prueba de confirmación".
- README: cómo ejecutar en local, cómo usar la red Wi-Fi para el QR, las cuentas de prueba de la sección 9.5 y la API nueva.
- `docs/plan-desarrollo-fase2.md`: se reemplaza por esta versión.

## Verificación
- Cada RED y cada GREEN: `./mvnw test`, con la salida en `docs/evidencias/ciclo-N-TXX-{red,green}.txt`. Las 21 pruebas originales siempre en verde.
- Al cerrar cada ciclo:
  - cobertura de JaCoCo de al menos 90 % (`target/site/jacoco/index.html`);
  - GitHub Actions en verde.
- Al final de cada día: `docker compose up -d`, `./mvnw spring-boot:run` y recorrer en el navegador integrado los pasos del guion que ya existan.
- El viernes: escanear el QR desde el celular.
