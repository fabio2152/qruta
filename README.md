# QRuta – Trazabilidad

![CI](https://github.com/fabio2152/qruta/actions/workflows/ci.yml/badge.svg)

## Nombre del proyecto

**QRuta – Trazabilidad** (`qruta-trazabilidad`)

## Integrantes

- Fabio Ezequiel Malpartida Lema
- Piero Anghelo Pittman Tolentino

## Metodología asignada

**TDD – Test-Driven Development**, aplicando el ciclo RED → GREEN → REFACTOR.
El registro de cada ciclo se encuentra en [docs/bitacora-tdd.md](docs/bitacora-tdd.md) y las capturas en [docs/evidencias/](docs/evidencias/).

## Cómo aplicamos TDD

Cada comportamiento se construyó con el ciclo **RED → GREEN → REFACTOR**: primero se escribió una sola prueba y se comprobó que fallaba (RED), luego se escribió el código mínimo para que pasara (GREEN) y, con todas las pruebas del ciclo en verde, se mejoró el diseño sin cambiar el comportamiento (REFACTOR).
En total son **94 pruebas automatizadas en 18 ciclos** (1 a 14, más el 8b, 9b, 10b y 12b). Cada prueba tiene su commit `test` (RED) y su commit `feat` (GREEN), salvo T73, que es una prueba de confirmación: pasó sin código nuevo. Cada ciclo cierra con un commit `refactor`.
El estado del avance del sábado está en [docs/estado-avance-sabado.md](docs/estado-avance-sabado.md).
La fase 2 (desde el ciclo 8) sigue la [especificación de la fase 2](docs/especificacion-fase2-qruta.md) y el [plan de desarrollo](docs/plan-desarrollo-fase2.md).
Cada fase guarda la salida de `./mvnw test` en [docs/evidencias/](https://github.com/fabio2152/qruta/tree/main/docs/evidencias) (50 archivos `.txt`).
El detalle de cada prueba (qué falló, qué código mínimo se escribió y qué se refactorizó) está en la [bitácora TDD](https://github.com/fabio2152/qruta/blob/main/docs/bitacora-tdd.md).

## Entregables

| # | Entregable | Dónde encontrarlo |
|---|---|---|
| 1 | Presentación | Se entrega aparte, en PowerPoint. |
| 2 | Código fuente | [src/](https://github.com/fabio2152/qruta/tree/main/src) |
| 3 | Repositorio Git | [Historial de commits](https://github.com/fabio2152/qruta/commits/main): muestra, para cada prueba, el commit `test` (RED) antes del commit `feat` (GREEN), y el commit `refactor` que cierra cada ciclo. |
| 4 | README del proyecto | [README.md](https://github.com/fabio2152/qruta/blob/main/README.md) (este archivo). |
| 5 | Documentación de la metodología | [docs/bitacora-tdd.md](https://github.com/fabio2152/qruta/blob/main/docs/bitacora-tdd.md) |
| 6 | Evidencias de implementación | [docs/evidencias/](https://github.com/fabio2152/qruta/tree/main/docs/evidencias) (salidas de `./mvnw test` y capturas) y la pestaña [Actions](https://github.com/fabio2152/qruta/actions), donde se ve la ejecución de las pruebas de cada push: fallida (❌) en los commits RED y exitosa (✅) en los commits GREEN y REFACTOR. |
| 7 | Demostración funcional | Se realiza en vivo durante la exposición. |
| 8 | Conclusiones del equipo | Están en la presentación (PowerPoint). |

**Demostración funcional:** Exposición.

**Resumen de conclusiones** (según la [bitácora](https://github.com/fabio2152/qruta/blob/main/docs/bitacora-tdd.md)):

- Las pruebas guiaron el diseño: cada atajo mínimo (la constante de T01, la lista sin filtrar de T10, el login sin contraseña de T16) fue roto por la prueba siguiente, y ningún parámetro se usó antes de que una prueba lo pidiera.
- El orden de las pruebas importó: T05 antes que T06 y T07 antes que T08 permitieron que cada prueba fallara primero, y los refactors llevaron cada regla a su lugar (`EstadoProducto` conoce la siguiente etapa, `Producto.esVisiblePara` decide la visibilidad) sin cambiar el comportamiento.
- El resultado es verificable: 21 pruebas en verde y una cobertura del 96 % de líneas y 97 % de ramas en el dominio y el servicio; al pasar a PostgreSQL, el ciclo 7 detectó con pruebas que comparar usuarios por referencia dejaba de funcionar.

## Problema seleccionado

**Funcionalidad: actualizar el estado de un producto en la cadena de suministro.**

En una cadena de suministro de alimentos es necesario saber en qué etapa está cada producto, quién lo movió y cuánto tardó el transporte. Si esos datos los ingresa el usuario a mano, se pueden alterar, y si cualquiera puede cambiar el estado, se pierde la confianza en la trazabilidad.

Corresponde al caso de uso de negocio **CUN-05 — Registrar evento de trazabilidad**, en el que participan:

- **Productor:** registra el producto y lo asigna a un intermediario. No puede cambiar su estado.
- **Intermediario (operador logístico):** es el único que puede hacer avanzar el producto por las etapas, y solo los productos que tiene asignados.
- **Regulador (entidad sanitaria):** consulta todos los productos y su historial. No cambia estados.

Cada producto recorre estas etapas, siempre en orden:

```text
EN_ORIGEN  →  EN_TRANSITO  →  EN_DESTINO  →  ENTREGADO
```

**Reglas principales:**

- Todo producto nuevo empieza en EN_ORIGEN.
- El estado avanza a la etapa siguiente, sin saltar etapas ni retroceder; un producto ENTREGADO ya no puede cambiar.
- Solo el intermediario asignado puede cambiar el estado del producto.
- Las horas las registra el sistema, no el usuario: al pasar a EN_TRANSITO se guarda la hora de inicio y al pasar a EN_DESTINO la hora de llegada.
- La duración del transporte (llegada − inicio) solo se puede calcular cuando existen ambas horas.

Cada regla incumplida se rechaza con una excepción propia y un mensaje claro (por ejemplo, *"El usuario no está asignado a este producto"*).

Además, cada producto guarda un **historial** de cambios (estado anterior, estado nuevo, hora y usuario), y cada rol ve solo lo que le corresponde: el productor sus productos, el intermediario los asignados y el regulador todos.

**Alcance actual:** el dominio y el servicio están construidos con TDD en 6 ciclos y 17 pruebas (ver [docs/bitacora-tdd.md](docs/bitacora-tdd.md)), y la plataforma se puede usar desde el navegador. Quedan fuera de esta entrega: temperaturas y alertas, código QR, base de datos en la nube (se usa PostgreSQL local en Docker) y seguridad real (cifrado de contraseñas, tokens).

## Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot 4.1 | Framework de la aplicación |
| Spring Web (`spring-boot-starter-web`) | API REST y servidor de la página web |
| Spring Data JPA + PostgreSQL 17 | Persistencia de usuarios, productos e historial |
| Docker Compose | Levanta PostgreSQL en local (`docker-compose.yml`) |
| HTML + CSS + JavaScript | Página web de la plataforma (sin librerías externas) |
| JUnit 5 + AssertJ + Mockito (`spring-boot-starter-test`) | Pruebas automatizadas |
| Maven + Maven Wrapper | Construcción y gestión de dependencias |
| JaCoCo | Reporte de cobertura de pruebas |
| GitHub Actions | Integración continua (`./mvnw test` en cada push) |

## Instrucciones de instalación

Requisitos previos:

- JDK 21 (verificar con `java -version`).
- Docker Desktop (para la base de datos PostgreSQL).
- Git.
- No es necesario instalar Maven: el proyecto incluye el Maven Wrapper (`mvnw` / `mvnw.cmd`).

Clonar el repositorio:

```bash
git clone https://github.com/fabio2152/qruta.git
cd qruta
```

## Instrucciones de ejecución

1. Levantar la base de datos PostgreSQL (contenedor `qruta-fase2-postgres`, puerto **5434**, base `qruta_db`, usuario y contraseña `qruta`):

```bash
docker compose up -d
```

Los datos se guardan en el volumen `qruta-fase2-datos` y sobreviven a los reinicios. Para borrarlos: `docker compose down -v`.

2. Iniciar la aplicación (las tablas se crean solas la primera vez):

Linux / macOS / Git Bash:

```bash
./mvnw spring-boot:run
```

Windows (CMD / PowerShell):

```bash
mvnw.cmd spring-boot:run
```

Abrir **http://localhost:8080** en el navegador.

**Para escanear el QR con el celular** (avance del sábado, en local):
- abrir la aplicación con la IP de la laptop en la red Wi-Fi, por ejemplo `http://192.168.100.86:8080` (la IP se ve con `ipconfig`, en "Adaptador de LAN inalámbrica Wi-Fi"; cambia según la red);
- conectar el celular a la misma red;
- permitir el puerto 8080 en el firewall de Windows.

La aplicación ya escucha en todas las interfaces (`server.address=0.0.0.0`).

### Cuentas de prueba

Se cargan al iniciar la aplicación si todavía no existen. Todas están ACTIVAS y usan la contraseña `clave123`, guardada cifrada con BCrypt.

| Usuario | Tipo de cuenta | Qué puede hacer |
|---|---|---|
| `productor1` | Productor agropecuario (Cusco) | Crear pollo, palta o banano y publicarlos |
| `transportista1`, `transportista2` | Transportista | Ver publicaciones mientras está LIBRE, aceptar una y avanzar sus etapas |
| `mercado1` | Mercado (Mercado Mayorista de Lima) | Destino de pollo, palta y banano: registra la recepción con la temperatura |
| `acopio1` | Centro de acopio (Huancayo) | Destino de palta y banano |
| `regulador1` | Regulador (SENASA, código SENASA-LIM-0001) | Aprobar productores y transportistas, bloquear y desbloquear, suspender, ver historiales y descargar el CSV |
| `admin1` | Administrador | Crear reguladores, aprobar mercados y centros de acopio, y ver el resumen. No tiene poder sanitario (D-07) |
| `intermediario1`, `intermediario2` | Intermediario de la fase 1 | Asignación directa de la fase 1 (D-04) |

### Guion de la demostración (avance actual)

1. **Registro:** en **Registrarse**, crear un transportista nuevo. Al intentar ingresar: *"Su cuenta está pendiente de aprobación"*.
2. **Aprobación:** ingresar como `regulador1` y pulsar **Aprobar**. Ahora el transportista ya puede ingresar.
3. **Producción:** ingresar como `productor1`, crear un pollo con su certificado SENASA y pulsar **Publicar**. Con **Ver QR** aparece su código.
4. **Aceptación:** con el transportista, pulsar **Aceptar**: pasa a OCUPADO. `transportista2` ya no ve la publicación.
5. **Destino:** elegir *Mercado Mayorista de Lima* y pulsar **Pasar a EN_TRANSITO** y luego **Pasar a EN_DESTINO**.
6. **Recepción:** ingresar como `mercado1` y registrar **7.1** °C. La evaluación da *NO APTO PARA CONSUMO · riesgo CRITICO* y el lote queda **BLOQUEADO**. El transportista vuelve a LIBRE (RE-41).
7. **Consumidor:** con **Ver QR**, escanear el código con el celular, conectado a la misma red Wi-Fi. Aparece **NO APTO PARA CONSUMO**, con el motivo y el recorrido. Si la red no lo permite, se abre el enlace del QR en la laptop.
8. **Contraste:** repetir con una palta recibida a **6** °C: queda **APTA**. El mismo transportista puede llevarla porque volvió a LIBRE (RE-41), y la pasa a ENTREGADO.
9. **Control:** como `regulador1`, pulsar **Ver historial** del lote bloqueado (con el motivo) y **Descargar reporte CSV**. El regulador también puede **Bloquear**, **Desbloquear** y **Suspender**.
10. **Cierre:** `./mvnw test` con todas las pruebas en verde, y la pestaña Actions de GitHub.

### API REST

Salvo el registro, el login y la consulta pública, todas las peticiones exigen la cabecera `Authorization: Bearer <token>`. El token lo devuelve el login y vale 8 horas.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/registro` | Registro público: productor, transportista, mercado o centro de acopio (queda PENDIENTE) |
| `POST` | `/api/auth/login` | Iniciar sesión: devuelve `token` y `usuario` |
| `POST` | `/api/auth/logout` | Cerrar sesión: el token deja de valer |
| `GET` | `/api/usuarios` | Cuentas con su estado (solo el regulador) |
| `GET` | `/api/usuarios/yo` | Datos de la sesión actual (por ejemplo, el estado de carga) |
| `POST` | `/api/usuarios/{nombreUsuario}/aprobar` | Aprobar una cuenta (RE-15) |
| `POST` | `/api/productos` | El productor crea un producto (`DatosProducto`) |
| `GET` | `/api/productos` | Productos según el rol (RE-10) |
| `POST` | `/api/productos/{id}/publicar` | Publicar el producto |
| `POST` | `/api/productos/{id}/retirar-publicacion` | Retirar la publicación |
| `GET` | `/api/publicaciones` | Publicaciones disponibles (transportista LIBRE) |
| `POST` | `/api/productos/{id}/aceptar` | El transportista acepta el producto |
| `PUT` | `/api/productos/{id}/estado` | Cambiar la etapa (`{"estado": "EN_TRANSITO"}`) |
| `GET` | `/api/destinos?tipo=PALTA` | Mercados y centros de acopio activos que necesitan ese tipo |
| `PUT` | `/api/productos/{id}/destino` | El transportista elige el destino (`{"destino": "mercado1"}`) |
| `PUT` | `/api/transportistas/yo/ubicacion` | El transportista publica su ubicación (`{"ubicacion": "..."}`) |
| `PUT` | `/api/destinos/yo` | El punto de recepción configura sus necesidades (`{"necesidades": ["POLLO"]}`) |
| `POST` | `/api/productos/{id}/recepcion` | El destino registra la recepción (`{"temperatura": 7.1, "humedad": 80}`) y recibe la evaluación |
| `POST` | `/api/productos/{id}/bloquear` | El regulador bloquea (`{"motivo": "..."}`) |
| `POST` | `/api/productos/{id}/desbloquear` | El regulador desbloquea: vuelve a la etapa que tenía (`{"motivo": "..."}`) |
| `POST` | `/api/usuarios/{nombreUsuario}/suspender` | El regulador suspende una cuenta (no a un transportista OCUPADO) |
| `GET` | `/api/reportes/salubridad.csv` | Reporte sanitario en CSV (solo el regulador) |
| `POST` | `/api/reguladores` | El administrador crea un regulador con código institucional |
| `GET` | `/api/admin/resumen` | Usuarios por rol y productos por etapa (solo el administrador) |
| `GET` | `/api/publico/productos/{codigo}` | **Consulta pública sin sesión**: la ficha que ve el consumidor (página `consulta.html?codigo=...`) |
| `GET` | `/api/productos/{id}/historial` | Historial de cambios del producto |

Los errores responden `{"mensaje": "..."}` con estos códigos:
- `400`: datos inválidos;
- `401`: sin sesión o con el token vencido;
- `403`: sin permiso o con la cuenta no activa;
- `404`: no existe;
- `409`: conflicto (nombre repetido, producto ya aceptado, etapa no permitida).

## Procedimiento para ejecutar las pruebas

Linux / macOS / Git Bash:

```bash
./mvnw test
```

Windows (CMD / PowerShell):

```bash
mvnw.cmd test
```

Al terminar, el reporte de cobertura de JaCoCo se genera en `target/site/jacoco/index.html`.

Las pruebas también se ejecutan automáticamente en GitHub Actions con cada `push` (ver [.github/workflows/ci.yml](.github/workflows/ci.yml)).

## Estructura del proyecto

```text
qruta/
├── .github/workflows/ci.yml   # Integración continua
├── docs/
│   ├── bitacora-tdd.md        # Registro de ciclos RED-GREEN-REFACTOR
│   └── evidencias/            # Salidas de ./mvnw test de cada fase y capturas
├── src/
│   ├── main/java/pe/qruta/trazabilidad/
│   │   ├── dominio/           # Producto, EstadoProducto, Usuario, Rol, CambioEstado, excepciones
│   │   │   ├── cuenta/        # Productor, Transportista, Mercado, CentroDeAcopio, CifradorDeContrasenas, ValidadorDeFormato
│   │   │   ├── producto/      # ProductoPecuario, ProductoAgro, TipoProducto, DatosProducto
│   │   │   └── evaluacion/    # Medicion, Evaluacion, NivelRiesgo, EvaluadorDeProducto
│   │   ├── servicio/          # ProductoService, LoginService, RegistroService, SesionService, TransporteService,
│   │   │                      # RecepcionService, RegulacionService, ConsultaPublicaService
│   │   ├── repositorio/       # Interfaces + implementación en memoria (pruebas)
│   │   │   └── jpa/           # Entidades y repositorios PostgreSQL (aplicación)
│   │   ├── controlador/       # API REST
│   │   └── configuracion/     # Beans de Spring, reloj del sistema y usuarios de prueba
│   ├── main/resources/static/ # Web: index.html y app.js (paneles), consulta.html y consulta.js (QR), estilos.css
│   └── test/java/pe/qruta/trazabilidad/
│       ├── dominio/           # ProductoTest (T01–T08)
│       └── servicio/          # ProductoServiceTest (T09–T15, T19, T21), LoginServiceTest (T16, T17, T29),
│                              # RegistroServiceTest (T22–T28, T30, T31, T76–T79), SesionServiceTest (T32, T80),
│                              # CrearProductoTest (T33–T37, T81–T84), PublicacionYAceptacionTest (T38–T43),
│                              # DestinoYRecepcionTest (T44–T53, T85–T89), EvaluacionTest (T54–T59, T75),
│                              # RegulacionTest (T60–T66, T90, T92–T94), ConsultaPublicaTest (T67–T70, T91),
│                              # AdministracionTest (T71–T74)
├── docker-compose.yml         # PostgreSQL 17
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

## Convención de commits

El historial de Git se usa como evidencia del proceso TDD:

| Prefijo | Fase | Ejemplo |
|---|---|---|
| `test:` | RED – prueba que falla | `test: crear prueba para calcular total` |
| `feat:` | GREEN – implementación mínima | `feat: implementar calculo de total` |
| `refactor:` | REFACTOR – mejora sin cambiar comportamiento | `refactor: separar calculadora de descuentos` |
| `docs:` | Documentación y evidencias | `docs: registrar ciclo 1 en bitacora` |
| `chore:` | Configuración del entorno | `chore: configurar GitHub Actions` |
