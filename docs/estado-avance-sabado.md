# QRUTA – Estado del avance del sábado 17/10/2026

Código congelado con la etiqueta `avance-sabado`. Después del ensayo final no se hacen más cambios (sección 12 de la especificación).

## Resumen

| Indicador | Valor |
|---|---|
| Pruebas automatizadas | **94 en verde** (`./mvnw test`): las 21 de la fase 1 sin cambios y 73 nuevas |
| Ciclos RED → GREEN → REFACTOR | 18: del 1 al 14, más el 8b, 9b, 10b y 12b |
| Pruebas de confirmación | 1 (T73: pasó sin código nuevo y no se cuenta como ciclo) |
| Cobertura de líneas del dominio y los servicios | **94,3 %** (810 de 859, JaCoCo). Lo exigido por RNF-06 es 90 % |
| Integración continua | GitHub Actions en verde en `main` |
| Ensayo final por API (guion de la sección 14 y administración) | 23 de 23 comprobaciones correctas |

## Qué cubre cada ciclo de la fase 2

| Ciclo | Tema | Pruebas |
|---|---|---|
| 8 | Cuentas y sesión | T22–T32 |
| 8b | Cuentas: pruebas agregadas | T78–T80 |
| 9 | Creación, publicación y aceptación | T33–T43, T76, T77, T81 |
| 9b | Certificados y calidad | T82–T84 |
| 10 | Destino y recepción | T44–T53 |
| 10b | Destino y recepción: pruebas agregadas | T85–T89 |
| 11 | Evaluación (motor de reglas) | T54–T59, T75 (RE-41) |
| 12 | Regulación | T60–T66, T90 |
| 12b | Bloqueo: pruebas agregadas | T92–T94 |
| 13 | Consulta pública por QR | T67–T70, T91 |
| 14 | Administración | T71, T72, T74 (y T73 de confirmación) |

Decisiones tomadas durante el desarrollo, todas registradas en la especificación:
- **A.** ENTREGADO lo marca el transportista.
- **B.** RE-41, el transportista queda libre tras un bloqueo posterior a la recepción.
- **C.** El avance del sábado se presenta en local; el despliegue en Render queda para después.
- Pruebas T76 a T94, agregadas para reglas que ya estaban en la especificación pero no tenían prueba.

## Antes de exponer (checklist)

1. Abrir Docker Desktop y ejecutar `docker compose up -d`.
2. `./mvnw spring-boot:run` (o `mvnw.cmd spring-boot:run` en Windows) y esperar a que diga "Started".
3. Con `ipconfig`, ver la IP del **Wi-Fi** del salón y abrir `http://<IP>:8080` en la laptop, no `localhost`, para que el QR lleve esa dirección.
4. Comprobar que el puerto 8080 esté permitido en el firewall de Windows (regla "QRuta 8080").
5. Probar con el celular un QR cualquiera. Si la red bloquea la conexión, usar el **plan B**: abrir el enlace del QR en la laptop.
6. Para empezar con datos limpios: `docker compose down -v` y luego `docker compose up -d`. Se pierden los productos de prueba y se vuelven a crear las cuentas iniciales.
7. Tener abierta la pestaña **Actions** del repositorio y una terminal para correr `./mvnw test` en vivo.

## Guion (10 pasos, sección 14)

Está detallado en el [README](../README.md#guion-de-la-demostración-avance-actual): registro → aprobación → pollo con QR → aceptación → destino y etapas → recepción a 7,1 °C (bloqueo automático) → QR en el celular (NO APTO) → palta a 6 °C (APTA) → historial y CSV → `./mvnw test` y Actions.

## Pendientes conocidos (fuera del avance del sábado)

| # | Pendiente | Regla | Comentario |
|---|---|---|---|
| 1 | Hoy la regla dice "el regulador no aprueba mercados", no "solo el administrador los aprueba", así que un productor también podría aprobar un mercado | RE-15 | Lo descubrió la prueba de confirmación T73. Requiere una prueba nueva |
| 2 | Que solo el regulador suspenda y descargue el CSV, y que solo el administrador cree reguladores y vea el resumen, se valida en los controladores, no en los servicios | RE-35, RE-36, RE-11, RE-40 | Funciona y se comprobó en el ensayo (403), pero no salió de una prueba de TDD |
| 3 | Despliegue en Render con HTTPS | RNF-01, sección 13 | Decisión C: después del sábado |
| 4 | El horario y la dirección del punto de recepción todavía no se configuran (solo los tipos de producto que necesita) | RF-12 | La restricción por horario está fuera del alcance (sección 15) |
| 5 | Los valores de conservación (rangos, vida útil, factor) son referencias del prototipo | 6.3 | Deben validarse con fuentes técnicas antes de producción |
