# Plan de desarrollo QRUTA, fase 2

## Contexto
La plataforma QRUTA tiene que estar desplegada el sábado y mostrar el flujo completo: un productor crea un lote, un transportista lo traslada, el destino lo recibe y lo evalúa, y el consumidor lo consulta con un código QR.

- **Punto de partida:** el código de `lab4`, ahora en `PROYECTO/qruta` (https://github.com/fabio2152/qruta), con 21 pruebas en verde.
- **Guía principal:** `docs/especificacion-fase2-qruta.md`.
- **Reglas de trabajo:** las de `qruta/CLAUDE.md`. Una prueba a la vez, RED → GREEN → REFACTOR, un commit por fase y push, salida de cada ejecución en `docs/evidencias/`, sin amend, rebase ni push forzado, y sin línea de coautoría.

### Decisiones tomadas por el usuario (14/10)
- **A.** Pasa el producto a ENTREGADO **el transportista**, con `cambiarEstado`. Así lo indican T51 y T52, y no hace falta pasarlo automáticamente.
- **B. Requisito nuevo, RE-41:** si un producto se bloquea después de registrar su recepción, por evaluación o por el regulador, su transportista vuelve a LIBRE. Se agrega la prueba **T75** y la bitácora la registra como "requisito nuevo".
- **C.** El despliegue se hace en **Render**: servicio web con Docker y PostgreSQL gratuito, con despliegue automático después de que pase GitHub Actions.

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
- **Productos antiguos sin `TipoProducto`** (como "Mango Kent" en las pruebas): no se pueden recibir ni evaluar.

---

## Día 1 (miércoles 14/10): despliegue, ciclo 8 y ciclo 9

### 1. Desplegar lo que ya existe (sin TDD)
- `Dockerfile` en dos etapas: compila con `maven:3.9-eclipse-temurin-21` y `-DskipTests`, y ejecuta con `eclipse-temurin:21-jre`.
- `.dockerignore` con `target/` y `.git/`.
- `application.properties`:
  - `server.port=${PORT:8080}`;
  - la conexión a la base usa `${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5433/qruta_db}`, y lo mismo el usuario y la contraseña.
- `application-prod.properties` con `ddl-auto=update`.
- `render.yaml`, opcional: servicio web Docker con la base `qruta-db`.
- **Lo hace el usuario:** crear la cuenta en Render, la base PostgreSQL y el servicio web desde el repositorio, cargar las variables de entorno y activar "Auto-Deploy: After CI checks pass".
- Comprobar el guion actual (productor1, intermediario1, regulador1) en la URL pública. Agregar el enlace al README.
- Commits: `chore: dockerfile y configuracion por variables de entorno` y `docs: enlace publico en el README`.

### 2. Ciclo 8: cuentas y sesión (T22 a T32)
Clases nuevas en `dominio/cuenta/`: `EstadoCuenta`, `Productor`, `Transportista`, `Intermediario`, `PuntoDeRecepcion`, `Mercado`, `CentroDeAcopio` y `Regulador`. Solo se agregan cuando una prueba las necesita.

| Prueba | Dónde |
|---|---|
| T22 a T28 | `servicio/RegistroService` (registro público). La prueba usa `UsuarioRepositorioEnMemoria`, que gana `buscarPorCorreo` |
| T29 | `LoginService`: rechaza las cuentas PENDIENTE o SUSPENDIDA con un mensaje claro |
| T30 y T31 | `RegistroService.aprobar(idUsuario, aprobador)`: valida si aprueba el regulador o el administrador, según RE-15 |
| T32 | `servicio/SesionService` con `Clock`: un token UUID válido durante 8 horas |

- Refactor: `dominio/cuenta/ValidadorDeFormato` (correo, DNI y RUC).
- Luego, sin TDD:
  - endpoints `POST /api/auth/registro`, `/login` y `/logout`;
  - `InterceptorDeSesion` con `Authorization: Bearer`, que reemplaza a `SesionUsuario` y a la cabecera `X-Usuario`;
  - `app.js` guarda el token, con una línea de cambio en `peticion()` (`app.js:38`);
  - `UsuarioEntidad` pasa a `SINGLE_TABLE` con columna discriminadora y estado de la cuenta;
  - `CargaUsuariosDePrueba` carga los usuarios de la sección 9.5 con BCrypt, más los usuarios antiguos;
  - pantalla de registro y manejo de errores 401 y 409.

### 3. Ciclo 9: creación, publicación y aceptación (T33 a T43)
- `dominio/producto/`: `TipoProducto` (rango, vida útil base, factor y categoría), `CategoriaProducto`, `ProductoPecuario` y `ProductoAgro`.
- Al crearse, el producto recibe un código UUID.
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

---

## Día 2 (jueves 15/10): ciclos 10 y 11, y pantallas operativas
- **Ciclo 10 (T44 a T53):**
  - el destino lo elige el transportista en `TransporteService.elegirDestino`;
  - `Medicion` y `servicio/RecepcionService.registrarRecepcion`;
  - RE-27 y RE-28 dentro de `Producto.cambiarEstado` (hora de entrega y transportista LIBRE);
  - el punto de recepción se agrega a `esVisiblePara` (T53).
  - Refactor: separar `RecepcionService`.
- **Ciclo 11 (T54 a T59 y T75):**
  - `dominio/evaluacion/`: `Evaluacion`, `NivelRiesgo` y `EvaluadorDeProducto` (RE-29 a RE-31);
  - el bloqueo automático, RE-32, se agrega a `Producto.bloquear`;
  - **T75:** después del bloqueo automático en T56, el transportista queda LIBRE (RE-41).
  - Refactor: constantes con nombre para 3 °C y 20 %. Las temperaturas usan `BigDecimal` o se comparan con cuidado (7,1 − 4 > 3).
- **Sin TDD:**
  - endpoints de destino, recepción, ubicación, `GET /api/destinos?tipo=` y `PUT /api/destinos/yo`;
  - paneles del productor (con el QR en `qrcodejs` desde CDN), del transportista y del punto de recepción;
  - redesplegar.

## Día 3 (viernes 16/10): ciclos 12 y 13, y la demostración completa
- **Ciclo 12 (T60 a T66):** `servicio/RegulacionService` con bloquear, desbloquear (con `estadoAntesDelBloqueo`), suspender (RE-35) y el reporte CSV (T66).
- **Ciclo 13 (T67 a T70):** `servicio/ConsultaPublicaService` con una ficha pública propia, `FichaPublica`, solo con los campos permitidos (RE-37 a RE-39).
- **Sin TDD:**
  - `GET /api/publico/productos/{codigo}`, que el interceptor deja pasar sin sesión;
  - `consulta.html` con la marca grande APTO o NO APTO;
  - panel del regulador y descarga del CSV.
- **Redespliegue y ensayo completo** del guion de la sección 14 en la URL de Render: la palta del paso 8 ya puede llevarla el mismo transportista, gracias a RE-41.

## Día 4 (sábado 17/10)
- Ciclo 14 (T71 a T74, administración), solo si sobra tiempo.
- Congelar el código, hacer el ensayo final y abrir Render unos minutos antes, porque el plan gratuito se apaga sin uso.

---

## Documentación en cada ciclo
- `docs/bitacora-tdd.md`: una fila por prueba en el resumen, una sección por ciclo y una línea "Repositorio" que apunta a qruta. Ahí se registran RE-41 y T75 como "requisito nuevo", y las pruebas que pasan sin código como "prueba de confirmación".
- README: el enlace público, las cuentas de prueba de la sección 9.5 y la API nueva.
- Especificación: se agregan RE-41, T75 y la aclaración de la decisión A, con un commit `docs:`.

## Verificación
- Cada RED y cada GREEN: `./mvnw test`, con la salida en `docs/evidencias/ciclo-N-TXX-{red,green}.txt`. Las 21 pruebas originales siempre en verde.
- Al cerrar cada ciclo: revisar la cobertura de JaCoCo (`target/site/jacoco/index.html`, al menos 90 %) y que GitHub Actions esté en verde.
- En local: `docker compose up -d`, `./mvnw spring-boot:run` y recorrer el flujo en el navegador integrado.
- En Render, al final de los días 1, 2 y 3: recorrer el guion de la sección 14 en la URL pública, incluido escanear el QR con un celular.
