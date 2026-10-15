# Bitácora TDD

Registro de cada ciclo **RED → GREEN → REFACTOR** aplicado en el proyecto.
Funcionalidad: **Actualizar el estado de un producto en la cadena** (CUN-05 — Registrar evento de trazabilidad).
Fase 2 (desde el ciclo 8): **plataforma QRUTA completa**: cuentas, publicación y aceptación, recepción con evaluación, regulación y consulta pública por QR.

Forma de trabajo: dentro de cada ciclo las pruebas se escriben **de a una** (RED → GREEN) y, cuando todas las pruebas del ciclo están en verde, se hace **un solo REFACTOR**. Cada paso queda respaldado por su commit y por la salida de `./mvnw test` guardada en [evidencias/](evidencias/).

Repositorio: https://github.com/fabio2152/qruta (continúa el historial de [lab4](https://github.com/fabio2152/lab4); la fase 2 está descrita en [especificacion-fase2-qruta.md](especificacion-fase2-qruta.md))

## Resumen de pruebas

| Prueba | Ciclo | Tipo | Regla | Comportamiento | RED | GREEN | Estado |
|---|---|---|---|---|---|---|---|
| T01 | 1 | Normal | RE-01 | Un producto recién creado está en EN_ORIGEN | [`50337de`](https://github.com/fabio2152/qruta/commit/50337de) | [`f833eb0`](https://github.com/fabio2152/qruta/commit/f833eb0) | ✅ |
| T02 | 1 | Normal | RE-07 | Al pasar a EN_TRANSITO se guarda la hora de inicio | [`e276c29`](https://github.com/fabio2152/qruta/commit/e276c29) | [`61ef540`](https://github.com/fabio2152/qruta/commit/61ef540) | ✅ |
| T03 | 1 | Normal | RE-07, RE-08 | Al pasar a EN_DESTINO se guarda la llegada y la duración es 6 h 30 min | [`e40e9cd`](https://github.com/fabio2152/qruta/commit/e40e9cd) | [`a2bdd9f`](https://github.com/fabio2152/qruta/commit/a2bdd9f) | ✅ |
| T04 | 2 | Error | RE-08 | Pedir la duración estando EN_TRANSITO se rechaza | [`6d5bdf6`](https://github.com/fabio2152/qruta/commit/6d5bdf6) | [`dda7055`](https://github.com/fabio2152/qruta/commit/dda7055) | ✅ |
| T05 | 2 | Límite | RE-06 | Un producto ENTREGADO no puede cambiar de estado | [`0452480`](https://github.com/fabio2152/qruta/commit/0452480) | [`dd8ccc4`](https://github.com/fabio2152/qruta/commit/dd8ccc4) | ✅ |
| T06 | 2 | Error | RE-05 | Pasar de EN_ORIGEN a EN_DESTINO se rechaza | [`f76fa76`](https://github.com/fabio2152/qruta/commit/f76fa76) | [`dc8fa59`](https://github.com/fabio2152/qruta/commit/dc8fa59) | ✅ |
| T07 | 3 | Error | RE-04 | El productor no puede cambiar el estado | [`50e78ea`](https://github.com/fabio2152/qruta/commit/50e78ea) | [`c97cd14`](https://github.com/fabio2152/qruta/commit/c97cd14) | ✅ |
| T08 | 3 | Error | RE-04 | Un intermediario no asignado no puede cambiar el estado | [`eb65b6f`](https://github.com/fabio2152/qruta/commit/eb65b6f) | [`9a040c6`](https://github.com/fabio2152/qruta/commit/9a040c6) | ✅ |
| T09 | 4 | Normal | RE-01 | El productor crea un producto: queda guardado con id y en EN_ORIGEN | [`8e04615`](https://github.com/fabio2152/qruta/commit/8e04615) | [`5c045fd`](https://github.com/fabio2152/qruta/commit/5c045fd) | ✅ |
| T10 | 4 | Normal | RE-10 | El regulador ve todos los productos | [`b26b24a`](https://github.com/fabio2152/qruta/commit/b26b24a) | [`389a438`](https://github.com/fabio2152/qruta/commit/389a438) | ✅ |
| T11 | 4 | Normal | RE-10 | El productor ve solo los productos que creó | [`cec61fb`](https://github.com/fabio2152/qruta/commit/cec61fb) | [`3274ff4`](https://github.com/fabio2152/qruta/commit/3274ff4) | ✅ |
| T12 | 4 | Normal | RE-10 | El intermediario ve solo sus productos asignados | [`a6eb858`](https://github.com/fabio2152/qruta/commit/a6eb858) | [`bc56885`](https://github.com/fabio2152/qruta/commit/bc56885) | ✅ |
| T13 | 5 | Normal | RE-07 | Cambiar el estado por id usa el reloj del sistema | [`002de72`](https://github.com/fabio2152/qruta/commit/002de72) | [`0f3c69b`](https://github.com/fabio2152/qruta/commit/0f3c69b) | ✅ |
| T14 | 5 | Error | — | Cambiar el estado de un producto inexistente se rechaza | [`238ed3e`](https://github.com/fabio2152/qruta/commit/238ed3e) | [`50edcd4`](https://github.com/fabio2152/qruta/commit/50edcd4) | ✅ |
| T15 | 5 | Normal | RE-09 | Tras dos cambios, el historial tiene dos registros | [`7c9d445`](https://github.com/fabio2152/qruta/commit/7c9d445) | [`06169b8`](https://github.com/fabio2152/qruta/commit/06169b8) | ✅ |
| T16 | 6 | Normal | Login | Usuario y contraseña correctos devuelven el usuario | [`4cff306`](https://github.com/fabio2152/qruta/commit/4cff306) | [`6b4187a`](https://github.com/fabio2152/qruta/commit/6b4187a) | ✅ |
| T17 | 6 | Error | Login | Una contraseña incorrecta se rechaza | [`75eb7cd`](https://github.com/fabio2152/qruta/commit/75eb7cd) | [`162c9c8`](https://github.com/fabio2152/qruta/commit/162c9c8) | ✅ |
| T18 | 7 | Normal | RE-04 | El intermediario asignado leído de nuevo (otra instancia) puede cambiar el estado | [`ca5002b`](https://github.com/fabio2152/qruta/commit/ca5002b) | [`dd0851c`](https://github.com/fabio2152/qruta/commit/dd0851c) | ✅ |
| T19 | 7 | Normal | RE-10 | El intermediario ve sus productos aunque el usuario sea otra instancia | [`612e011`](https://github.com/fabio2152/qruta/commit/612e011) | [`d7d1eb8`](https://github.com/fabio2152/qruta/commit/d7d1eb8) | ✅ |
| T20 | 7 | Normal | RE-08 | Un producto reconstruido en EN_TRANSITO calcula 6 h 30 min al llegar | [`257bf81`](https://github.com/fabio2152/qruta/commit/257bf81) | [`5537cbc`](https://github.com/fabio2152/qruta/commit/5537cbc) | ✅ |
| T21 | 7 | Normal | RE-09 | Cambiar el estado guarda el producto actualizado en el repositorio | [`f54e713`](https://github.com/fabio2152/qruta/commit/f54e713) | [`4516ad2`](https://github.com/fabio2152/qruta/commit/4516ad2) | ✅ |
| T22 | 8 | Normal | RE-13, RE-15 | Un productor se registra con contraseña de 8 caracteres y queda PENDIENTE | [`6a6c01f`](https://github.com/fabio2152/qruta/commit/6a6c01f) | [`a69c12e`](https://github.com/fabio2152/qruta/commit/a69c12e) | ✅ |
| T23 | 8 | Límite | RE-13 | Un registro con contraseña de 7 caracteres se rechaza | [`32bf005`](https://github.com/fabio2152/qruta/commit/32bf005) | [`95c7407`](https://github.com/fabio2152/qruta/commit/95c7407) | ✅ |
| T24 | 8 | Normal | RE-13 | La contraseña se guarda cifrada (BCrypt) y se puede verificar | [`00f106d`](https://github.com/fabio2152/qruta/commit/00f106d) | [`77ea0a6`](https://github.com/fabio2152/qruta/commit/77ea0a6) | ✅ |
| T25 | 8 | Error | RE-12 | Un nombre de usuario ya existente se rechaza | [`deb48b7`](https://github.com/fabio2152/qruta/commit/deb48b7) | [`deb3b9a`](https://github.com/fabio2152/qruta/commit/deb3b9a) | ✅ |
| T26 | 8 | Error | RE-12 | Un correo sin formato válido se rechaza | [`b5f73a5`](https://github.com/fabio2152/qruta/commit/b5f73a5) | [`8727c8d`](https://github.com/fabio2152/qruta/commit/8727c8d) | ✅ |
| T27 | 8 | Límite | RE-14 | Un transportista con RUC de 10 dígitos se rechaza | [`59d4d8a`](https://github.com/fabio2152/qruta/commit/59d4d8a) | [`b29a8c3`](https://github.com/fabio2152/qruta/commit/b29a8c3) | ✅ |
| T28 | 8 | Error | RE-11 | Un registro público con rol de regulador se rechaza | [`d74e744`](https://github.com/fabio2152/qruta/commit/d74e744) | [`ebb8450`](https://github.com/fabio2152/qruta/commit/ebb8450) | ✅ |
| T29 | 8 | Error | RE-16 | Una cuenta PENDIENTE no puede iniciar sesión | [`298015c`](https://github.com/fabio2152/qruta/commit/298015c) | [`4bbb098`](https://github.com/fabio2152/qruta/commit/4bbb098) | ✅ |
| T30 | 8 | Normal | RE-15 | El regulador aprueba al productor: queda ACTIVA y ya puede iniciar sesión | [`3f22d71`](https://github.com/fabio2152/qruta/commit/3f22d71) | [`b5743fc`](https://github.com/fabio2152/qruta/commit/b5743fc) | ✅ |
| T31 | 8 | Error | RE-15 | El regulador intenta aprobar un mercado y se rechaza | [`fc4f88a`](https://github.com/fabio2152/qruta/commit/fc4f88a) | [`a4f2962`](https://github.com/fabio2152/qruta/commit/a4f2962) | ✅ |
| T32 | 8 | Límite | RE-17 | Un token emitido a las 08:00 es válido a las 15:59 y se rechaza a las 16:00 | [`aaef3f2`](https://github.com/fabio2152/qruta/commit/aaef3f2) | [`1db4235`](https://github.com/fabio2152/qruta/commit/1db4235) | ✅ |
| T78 | 8b | Límite | RE-14 | Un productor con DNI de 7 dígitos se rechaza *(prueba agregada)* | [`81c79b7`](https://github.com/fabio2152/qruta/commit/81c79b7) | [`aab3860`](https://github.com/fabio2152/qruta/commit/aab3860) | ✅ |
| T79 | 8b | Error | RE-12 | Un correo ya registrado se rechaza *(prueba agregada)* | [`20c54c8`](https://github.com/fabio2152/qruta/commit/20c54c8) | [`91c845a`](https://github.com/fabio2152/qruta/commit/91c845a) | ✅ |
| T80 | 8b | Normal | RF-03 | Al cerrar sesión el token deja de ser válido *(prueba agregada)* | [`e6cbdac`](https://github.com/fabio2152/qruta/commit/e6cbdac) | [`93c1497`](https://github.com/fabio2152/qruta/commit/93c1497) | ✅ |
| T76 | 9 | Normal | RE-15, D-02, D-03 | Un transportista y un centro de acopio se registran con su propia clase *(prueba agregada)* | [`bc3a8dc`](https://github.com/fabio2152/qruta/commit/bc3a8dc) | [`70c0a99`](https://github.com/fabio2152/qruta/commit/70c0a99) | ✅ |
| T77 | 9 | Error | RE-15 | Un productor intenta aprobar a un transportista y se rechaza *(prueba agregada)* | [`10d9ff7`](https://github.com/fabio2152/qruta/commit/10d9ff7) | [`fd3d3a1`](https://github.com/fabio2152/qruta/commit/fd3d3a1) | ✅ |
| T33 | 9 | Normal | RE-18 | Un productor activo crea un pollo: EN_ORIGEN, sin transportista, sin publicar y con código QR único | [`b6ef621`](https://github.com/fabio2152/qruta/commit/b6ef621) | [`e29923f`](https://github.com/fabio2152/qruta/commit/e29923f) | ✅ |
| T34 | 9 | Error | RE-02 | Un transportista intenta crear un producto y se rechaza | [`59fcd7b`](https://github.com/fabio2152/qruta/commit/59fcd7b) | [`f7bd26f`](https://github.com/fabio2152/qruta/commit/f7bd26f) | ✅ |
| T35 | 9 | Error | RE-19 | Un producto con fecha de producción en el futuro se rechaza | [`c966b9d`](https://github.com/fabio2152/qruta/commit/c966b9d) | [`ca4cdbe`](https://github.com/fabio2152/qruta/commit/ca4cdbe) | ✅ |
| T36 | 9 | Límite | RE-19 | Un pollo producido hace exactamente 168 h se rechaza (vida útil agotada) | [`d59d9c5`](https://github.com/fabio2152/qruta/commit/d59d9c5) | [`434046f`](https://github.com/fabio2152/qruta/commit/434046f) | ✅ |
| T37 | 9 | Error | RE-19 | Un productor agrícola intenta crear un pollo y se rechaza | [`d2ee1cd`](https://github.com/fabio2152/qruta/commit/d2ee1cd) | [`3d9dcb7`](https://github.com/fabio2152/qruta/commit/3d9dcb7) | ✅ |
| T81 | 9 | Normal | RF-05, RE-19 | Un productor crea una palta: queda como ProductoAgro con su variedad y calidad *(prueba agregada)* | [`7ff5696`](https://github.com/fabio2152/qruta/commit/7ff5696) | [`3669215`](https://github.com/fabio2152/qruta/commit/3669215) | ✅ |
| T38 | 9 | Normal | RE-20 | El productor publica su producto y aparece para un transportista LIBRE | [`857c9b1`](https://github.com/fabio2152/qruta/commit/857c9b1) | [`4f2157d`](https://github.com/fabio2152/qruta/commit/4f2157d) | ✅ |
| T39 | 9 | Error | RE-20 | Un productor intenta publicar el producto de otro y se rechaza | [`389c5e3`](https://github.com/fabio2152/qruta/commit/389c5e3) | [`2c4821b`](https://github.com/fabio2152/qruta/commit/2c4821b) | ✅ |
| T40 | 9 | Normal | RE-22 | Un transportista LIBRE acepta: queda asignado, pasa a OCUPADO y la publicación desaparece para los demás | [`df24077`](https://github.com/fabio2152/qruta/commit/df24077) | [`08ab141`](https://github.com/fabio2152/qruta/commit/08ab141) | ✅ |
| T41 | 9 | Error | RE-22 | Un transportista OCUPADO intenta aceptar otro producto y se rechaza | [`aee46eb`](https://github.com/fabio2152/qruta/commit/aee46eb) | [`a023c23`](https://github.com/fabio2152/qruta/commit/a023c23) | ✅ |
| T42 | 9 | Error | RE-23 | Un segundo transportista intenta aceptar un producto ya aceptado y se rechaza | [`25a7652`](https://github.com/fabio2152/qruta/commit/25a7652) | [`19c4432`](https://github.com/fabio2152/qruta/commit/19c4432) | ✅ |
| T43 | 9 | Error | RE-21 | El productor intenta retirar una publicación ya aceptada y se rechaza | [`5d7f2bc`](https://github.com/fabio2152/qruta/commit/5d7f2bc) | [`074fde9`](https://github.com/fabio2152/qruta/commit/074fde9) | ✅ |
| T82 | 9b | Error | RE-19 | Un pollo sin certificado sanitario SENASA se rechaza *(prueba agregada)* | [`047bfda`](https://github.com/fabio2152/qruta/commit/047bfda) | [`9484623`](https://github.com/fabio2152/qruta/commit/9484623) | ✅ |
| T83 | 9b | Límite | RE-19 | Una palta con calidad 4 se rechaza *(prueba agregada)* | [`fe20cda`](https://github.com/fabio2152/qruta/commit/fe20cda) | [`d585d8d`](https://github.com/fabio2152/qruta/commit/d585d8d) | ✅ |
| T84 | 9b | Error | RE-19 | Una palta sin certificado fitosanitario se rechaza *(prueba agregada)* | [`974d047`](https://github.com/fabio2152/qruta/commit/974d047) | [`8fc9a81`](https://github.com/fabio2152/qruta/commit/8fc9a81) | ✅ |
| T44 | 10 | Normal | RE-24, RE-25 | El transportista asignado elige como destino un mercado que necesita palta | [`8f40d4a`](https://github.com/fabio2152/qruta/commit/8f40d4a) | [`a2ae9ab`](https://github.com/fabio2152/qruta/commit/a2ae9ab) | ✅ |
| T45 | 10 | Error | RE-24 | Elegir como destino a otro transportista se rechaza | [`1d45b8d`](https://github.com/fabio2152/qruta/commit/1d45b8d) | [`9f0086a`](https://github.com/fabio2152/qruta/commit/9f0086a) | ✅ |
| T46 | 10 | Error | RE-25 | Elegir un mercado que no necesita ese tipo de producto se rechaza | [`7e73ee5`](https://github.com/fabio2152/qruta/commit/7e73ee5) | [`c113a19`](https://github.com/fabio2152/qruta/commit/c113a19) | ✅ |
| T47 | 10 | Normal | RE-26 | El destino registra la recepción a 5 °C: la medición queda con la hora del sistema | [`115dcbd`](https://github.com/fabio2152/qruta/commit/115dcbd) | [`a2fe7aa`](https://github.com/fabio2152/qruta/commit/a2fe7aa) | ✅ |
| T48 | 10 | Error | RE-26 | Un mercado que no es el destino intenta registrar la recepción y se rechaza | [`a329304`](https://github.com/fabio2152/qruta/commit/a329304) | [`122a9a1`](https://github.com/fabio2152/qruta/commit/122a9a1) | ✅ |
| T49 | 10 | Error | RE-26 | Registrar la recepción de un producto EN_TRANSITO se rechaza | [`cc94875`](https://github.com/fabio2152/qruta/commit/cc94875) | [`d9376b6`](https://github.com/fabio2152/qruta/commit/d9376b6) | ✅ |
| T50 | 10 | Error | RE-26 | Una recepción con 85 °C se rechaza por lectura inválida | [`a329e4f`](https://github.com/fabio2152/qruta/commit/a329e4f) | [`b953ccd`](https://github.com/fabio2152/qruta/commit/b953ccd) | ✅ |
| T51 | 10 | Error | RE-27 | Un producto con destino no puede pasar a ENTREGADO sin recepción | [`bd2e37e`](https://github.com/fabio2152/qruta/commit/bd2e37e) | [`a571ee3`](https://github.com/fabio2152/qruta/commit/a571ee3) | ✅ |
| T52 | 10 | Normal | RE-28 | Al pasar a ENTREGADO se guarda la hora de entrega y el transportista queda LIBRE | [`89d75ca`](https://github.com/fabio2152/qruta/commit/89d75ca) | [`27177aa`](https://github.com/fabio2152/qruta/commit/27177aa) | ✅ |
| T53 | 10 | Normal | RE-10 | El mercado ve los productos cuyo destino es él | [`62390cd`](https://github.com/fabio2152/qruta/commit/62390cd) | [`7e89707`](https://github.com/fabio2152/qruta/commit/7e89707) | ✅ |

| Ciclo | Tema | REFACTOR | Estado |
|---|---|---|---|
| 1 | Recorrido normal del producto | [`33b4d0a`](https://github.com/fabio2152/qruta/commit/33b4d0a) | ✅ Completo |
| 2 | Orden de las etapas | [`15062f8`](https://github.com/fabio2152/qruta/commit/15062f8) | ✅ Completo |
| 3 | Permisos | [`d3cd1f9`](https://github.com/fabio2152/qruta/commit/d3cd1f9) | ✅ Completo |
| 4 | Servicio: crear y listar productos | [`8bc058e`](https://github.com/fabio2152/qruta/commit/8bc058e) | ✅ Completo |
| 5 | Servicio: cambiar estado e historial | [`c72044d`](https://github.com/fabio2152/qruta/commit/c72044d) | ✅ Completo |
| 6 | Servicio: login | [`4176317`](https://github.com/fabio2152/qruta/commit/4176317) + [`573bb78`](https://github.com/fabio2152/qruta/commit/573bb78) | ✅ Completo |
| 7 | Persistencia en PostgreSQL | [`8d8281f`](https://github.com/fabio2152/qruta/commit/8d8281f) | ✅ Completo |
| 8 | Cuentas y sesión | [`404bb0a`](https://github.com/fabio2152/qruta/commit/404bb0a) | ✅ Completo |
| 8b | Cuentas: pruebas agregadas por reglas sin cobertura | [`28182da`](https://github.com/fabio2152/qruta/commit/28182da) | ✅ Completo |
| 9 | Creación, publicación y aceptación | [`3114843`](https://github.com/fabio2152/qruta/commit/3114843) | ✅ Completo |
| 9b | Certificados y calidad: pruebas agregadas | [`2f32ec0`](https://github.com/fabio2152/qruta/commit/2f32ec0) | ✅ Completo |
| 10 | Destino y recepción | [`73849a7`](https://github.com/fabio2152/qruta/commit/73849a7) | ✅ Completo |

## Control de requisitos mínimos

- [x] Al menos 5 pruebas automatizadas (49: 10 del dominio y 39 del servicio).
- [x] Al menos 3 ciclos RED-GREEN-REFACTOR completos (10: del 1 al 9, más el 8b).
- [x] Casos normales (T01, T02, T03, T09–T13, T15, T16, T22, T24, T30, T33, T38, T40, T76, T80, T81).
- [x] Casos límite (T05, T23, T27, T32, T36, T78).
- [x] Casos de error (T04, T06, T07, T08, T14, T17, T25, T26, T28, T29, T31, T34, T35, T37, T39, T41–T43, T77, T79).
- [x] Ejecución completa de todas las pruebas (`./mvnw test`) en verde al cerrar cada ciclo.
- [x] Reporte de cobertura JaCoCo (`target/site/jacoco/index.html`): ver [Resultados](#resultados).

---

## Ciclo 1 – Recorrido normal del producto

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-01, RE-07, RE-08
**Tipo de casos:** Normales

### T01 – Un producto recién creado está en EN_ORIGEN (RE-01)

```text
Dado un productor y un intermediario
Cuando el productor crea el producto "Palta Hass" asignado al intermediario
Entonces el producto está en EN_ORIGEN
```

**🔴 RED** – `ProductoTest#unProductoRecienCreadoEstaEnOrigen`

- Se escribió la prueba primero. Para que compile se crearon solo esqueletos: los enums `Rol` (PRODUCTOR, INTERMEDIARIO) y `EstadoProducto` (EN_ORIGEN), `Usuario` con constructor vacío y `Producto` con constructor vacío y `getEstado()` que devuelve `null`.
- **Qué falló:** la verificación, no la compilación.

```text
Tests run: 1, Failures: 1, Errors: 0
expected: EN_ORIGEN
 but was: null
```

- Commit: `test(ciclo-1): T01 producto recien creado esta en EN_ORIGEN [RED]` ([`50337de`](https://github.com/fabio2152/qruta/commit/50337de))
- Evidencia: [ciclo-1-T01-red.txt](evidencias/ciclo-1-T01-red.txt) · Captura: [1rojo.png](evidencias/1rojo.png)

**🟢 GREEN**

- **Código mínimo:** `getEstado()` devuelve la constante `EstadoProducto.EN_ORIGEN` (técnica *fake it*). No se creó un campo `estado` porque ninguna prueba necesitaba todavía que el estado cambiara.

```java
public EstadoProducto getEstado() {
    return EstadoProducto.EN_ORIGEN;
}
```

- Resultado: `Tests run: 1, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-1): T01 producto recien creado esta en EN_ORIGEN [GREEN]` ([`f833eb0`](https://github.com/fabio2152/qruta/commit/f833eb0))
- Evidencia: [ciclo-1-T01-green.txt](evidencias/ciclo-1-T01-green.txt) · Captura: [1verde.png](evidencias/1verde.png)

### T02 – Al pasar a EN_TRANSITO se guarda la hora de inicio (RE-07)

```text
Dado un producto en EN_ORIGEN y un reloj fijo a las 08:00 del 01/09/2026
Cuando el intermediario asignado lo cambia a EN_TRANSITO
Entonces el producto está en EN_TRANSITO y su hora de inicio del transporte es las 08:00
```

**🔴 RED** – `ProductoTest#alPasarAEnTransitoSeGuardaLaHoraDeInicioDelTransporte`

- Esqueletos agregados: valor `EN_TRANSITO`, método `cambiarEstado(nuevoEstado, usuario, reloj)` vacío y `getHoraInicioTransporte()` que devuelve `null`.
- **Qué falló:** el *fake it* de T01 ya no alcanza; el estado seguía siendo la constante.

```text
Tests run: 2, Failures: 1, Errors: 0
expected: EN_TRANSITO
 but was: EN_ORIGEN
```

- Commit: `test(ciclo-1): T02 al pasar a EN_TRANSITO se guarda la hora de inicio del transporte [RED]` ([`e276c29`](https://github.com/fabio2152/qruta/commit/e276c29))
- Evidencia: [ciclo-1-T02-red.txt](evidencias/ciclo-1-T02-red.txt) · Captura: [2rojo.png](evidencias/2rojo.png)

**🟢 GREEN**

- **Código mínimo:** campo `estado` inicializado en EN_ORIGEN, campo `horaInicioTransporte`, y `cambiarEstado` que asigna el estado y guarda la hora del reloj. No se distingue a qué etapa se pasa ni se valida al usuario: ninguna prueba lo exigía aún.

```java
public void cambiarEstado(EstadoProducto nuevoEstado, Usuario usuario, Clock reloj) {
    estado = nuevoEstado;
    horaInicioTransporte = LocalDateTime.now(reloj);
}
```

- Resultado: `Tests run: 2, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-1): T02 al pasar a EN_TRANSITO se guarda la hora de inicio del transporte [GREEN]` ([`61ef540`](https://github.com/fabio2152/qruta/commit/61ef540))
- Evidencia: [ciclo-1-T02-green.txt](evidencias/ciclo-1-T02-green.txt) · Captura: [2verde.png](evidencias/2verde.png)

### T03 – Al pasar a EN_DESTINO se guarda la llegada y se calcula la duración (RE-07, RE-08)

```text
Dado un producto que pasó a EN_TRANSITO a las 08:00
Cuando el intermediario lo cambia a EN_DESTINO a las 14:30
Entonces la hora de llegada es las 14:30 y la duración del transporte es 6 h 30 min
```

**🔴 RED** – `ProductoTest#alPasarAEnDestinoSeGuardaLaHoraDeLlegadaYSeCalculaLaDuracionDelTransporte`

- Esqueletos agregados: valor `EN_DESTINO`, `getHoraLlegada()` y `getDuracionTransporte()` que devuelven `null`.
- **Qué falló:** la hora de llegada no se guardaba (y el atajo de T02 habría pisado la hora de inicio con las 14:30).

```text
Tests run: 3, Failures: 1, Errors: 0
expected: 2026-09-01T14:30 (java.time.LocalDateTime)
 but was: null
```

- Commit: `test(ciclo-1): T03 al pasar a EN_DESTINO se guarda la hora de llegada y la duracion del transporte [RED]` ([`e40e9cd`](https://github.com/fabio2152/qruta/commit/e40e9cd))
- Evidencia: [ciclo-1-T03-red.txt](evidencias/ciclo-1-T03-red.txt) · Captura: [3rojo.png](evidencias/3rojo.png)

**🟢 GREEN**

- **Código mínimo:** campo `horaLlegada`; `cambiarEstado` guarda la hora de inicio solo con EN_TRANSITO y la de llegada solo con EN_DESTINO; la duración se calcula al pedirla con `Duration.between`, sin guardarla en un campo (así nunca queda desfasada de las horas).

```java
if (nuevoEstado == EstadoProducto.EN_TRANSITO) {
    horaInicioTransporte = LocalDateTime.now(reloj);
}

if (nuevoEstado == EstadoProducto.EN_DESTINO) {
    horaLlegada = LocalDateTime.now(reloj);
}

public Duration getDuracionTransporte() {
    return Duration.between(horaInicioTransporte, horaLlegada);
}
```

- Resultado: `Tests run: 3, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-1): T03 al pasar a EN_DESTINO se guarda la hora de llegada y la duracion del transporte [GREEN]` ([`a2bdd9f`](https://github.com/fabio2152/qruta/commit/a2bdd9f))
- Evidencia: [ciclo-1-T03-green.txt](evidencias/ciclo-1-T03-green.txt) · Captura: [3verde.png](evidencias/3verde.png)

### 🔵 REFACTOR del ciclo 1

**Sugerencia del documento:** separar el cálculo de la duración en un método propio usando `Duration`. Esto ya quedó resuelto en el GREEN de T03 (`getDuracionTransporte()` con `Duration.between`), así que el refactor se centró en la duplicación que quedó.

**Qué se mejoró y por qué:**

1. **Producción – `Producto`:** `LocalDateTime.now(reloj)` se repetía en cada condición. Ahora la hora se toma **una sola vez** y el registro de horas pasa a un método privado `registrarHora(...)`. `cambiarEstado` queda más corto y con una sola responsabilidad visible: cambiar el estado y delegar el registro de la hora.
2. **Pruebas – `ProductoTest`:** la creación de usuarios y de relojes se repetía en las tres pruebas. Se agregó `@BeforeEach prepararUsuarios()` y los métodos auxiliares `horaDelDia(hora, minuto)` y `relojFijoEn(fechaHora)`. Las verificaciones (`assertThat`) de cada prueba **no cambiaron**.

**Código antes:**

```java
public void cambiarEstado(EstadoProducto nuevoEstado, Usuario usuario, Clock reloj) {

    estado = nuevoEstado;

    if (nuevoEstado == EstadoProducto.EN_TRANSITO) {
        horaInicioTransporte = LocalDateTime.now(reloj);
    }

    if (nuevoEstado == EstadoProducto.EN_DESTINO) {
        horaLlegada = LocalDateTime.now(reloj);
    }
}
```

**Código después:**

```java
public void cambiarEstado(EstadoProducto nuevoEstado, Usuario usuario, Clock reloj) {

    estado = nuevoEstado;

    registrarHora(nuevoEstado, LocalDateTime.now(reloj));
}


private void registrarHora(EstadoProducto nuevoEstado, LocalDateTime ahora) {

    if (nuevoEstado == EstadoProducto.EN_TRANSITO) {
        horaInicioTransporte = ahora;
    }

    if (nuevoEstado == EstadoProducto.EN_DESTINO) {
        horaLlegada = ahora;
    }
}
```

- Resultado: `Tests run: 3, Failures: 0, Errors: 0` – BUILD SUCCESS (el comportamiento no cambió).
- Commit: `refactor(ciclo-1): extraer registro de horas y reloj de prueba reutilizable [REFACTOR]` ([`33b4d0a`](https://github.com/fabio2152/qruta/commit/33b4d0a))
- Evidencia: [ciclo-1-refactor.txt](evidencias/ciclo-1-refactor.txt)

### Decisiones de diseño y aprendizajes del ciclo 1

- **El reloj se recibe en `cambiarEstado`** (inyección por parámetro) en lugar de en el constructor. Así cada cambio de estado puede usar su propio reloj fijo (08:00 y 14:30 en T03) sin necesitar un reloj de prueba modificable, y las horas las pone el sistema, no el usuario.
- **El constructor pide productor e intermediario desde T01**, porque así se crea un producto según RE-03. Esto evitó tener que modificar T01 en las pruebas siguientes.
- **Las pruebas guiaron el crecimiento del código:** T01 se resolvió con una constante, T02 obligó a crear el campo `estado`, y T03 obligó a distinguir la etapa para no pisar la hora de inicio. Cada línea de `Producto` apareció porque una prueba la pidió.
- **Pendiente para el ciclo 2:** hoy `getDuracionTransporte()` fallaría con un `NullPointerException` si falta la hora de llegada, y `cambiarEstado` acepta cualquier etapa. Esas reglas (RE-05, RE-06, RE-08) las exigirán T04, T05 y T06.

---

## Ciclo 2 – Orden de las etapas

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-05, RE-06, RE-08
**Tipo de casos:** Error (T04, T06) y límite (T05)

### T04 – Pedir la duración estando EN_TRANSITO se rechaza (RE-08)

```text
Dado un producto que pasó a EN_TRANSITO a las 08:00
Cuando se pide la duración del transporte
Entonces se rechaza con DuracionNoDisponibleException:
  "La duración del transporte aún no está disponible: falta la hora de llegada"
```

**🔴 RED** – `ProductoTest#pedirLaDuracionDeUnProductoEnTransitoSeRechazaPorqueAunNoTieneHoraDeLlegada`

- Esqueleto agregado: clase vacía `DuracionNoDisponibleException extends RuntimeException`. `Producto` no cambió.
- **Qué falló:** se lanzaba un `NullPointerException` genérico desde `Duration.between`, no la excepción del dominio. La prueba destapó un hueco del código de T03.

```text
Tests run: 4, Failures: 1, Errors: 0
Expecting actual throwable to be an instance of:
  pe.qruta.trazabilidad.dominio.DuracionNoDisponibleException
but was:
  java.lang.NullPointerException: temporal
```

- Commit: `test(ciclo-2): T04 pedir la duracion de un producto EN_TRANSITO se rechaza [RED]` ([`6d5bdf6`](https://github.com/fabio2152/qruta/commit/6d5bdf6))
- Evidencia: [ciclo-2-T04-red.txt](evidencias/ciclo-2-T04-red.txt) · Captura: [4rojo.png](evidencias/4rojo.png)

**🟢 GREEN**

- **Código mínimo:** constructor con mensaje en la excepción y una guarda en `getDuracionTransporte()` que solo revisa la hora de llegada (no la de inicio: ninguna prueba lo exige y, con RE-05, no se puede llegar a EN_DESTINO sin pasar por EN_TRANSITO).

```java
public Duration getDuracionTransporte() {

    if (horaLlegada == null) {
        throw new DuracionNoDisponibleException(
            "La duración del transporte aún no está disponible: falta la hora de llegada"
        );
    }

    return Duration.between(horaInicioTransporte, horaLlegada);
}
```

- Resultado: `Tests run: 4, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-2): T04 pedir la duracion de un producto EN_TRANSITO se rechaza [GREEN]` ([`dda7055`](https://github.com/fabio2152/qruta/commit/dda7055))
- Evidencia: [ciclo-2-T04-green.txt](evidencias/ciclo-2-T04-green.txt) · Captura: [4verde.png](evidencias/4verde.png)

### T05 – Un producto ENTREGADO no puede cambiar de estado (RE-06)

```text
Dado un producto que recorrió EN_TRANSITO (08:00), EN_DESTINO (14:30) y ENTREGADO (16:00)
Cuando se intenta pasarlo a EN_DESTINO
Entonces se rechaza con CambioDeEstadoNoPermitidoException:
  "El producto ya fue entregado y no puede cambiar de estado"
```

**🔴 RED** – `ProductoTest#unProductoEntregadoNoPuedeCambiarDeEstado`

- Esqueletos agregados: valor `ENTREGADO` en `EstadoProducto` y clase vacía `CambioDeEstadoNoPermitidoException`. `Producto` no cambió.
- **Qué falló:** no se lanzaba ninguna excepción; el producto entregado cambiaba de estado sin control.

```text
Tests run: 5, Failures: 1, Errors: 0
Expecting code to raise a throwable.
```

- Commit: `test(ciclo-2): T05 un producto ENTREGADO no puede cambiar de estado [RED]` ([`0452480`](https://github.com/fabio2152/qruta/commit/0452480))
- Evidencia: [ciclo-2-T05-red.txt](evidencias/ciclo-2-T05-red.txt) · Captura: [5rojo.png](evidencias/5rojo.png)

**🟢 GREEN**

- **Código mínimo:** constructor con mensaje en la excepción y una guarda al inicio de `cambiarEstado` solo para ENTREGADO. Al estar al inicio, un intento rechazado no deja el producto a medio cambiar. Los saltos de etapa se seguían aceptando.

```java
if (estado == EstadoProducto.ENTREGADO) {
    throw new CambioDeEstadoNoPermitidoException(
        "El producto ya fue entregado y no puede cambiar de estado"
    );
}
```

- Resultado: `Tests run: 5, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-2): T05 un producto ENTREGADO no puede cambiar de estado [GREEN]` ([`dd8ccc4`](https://github.com/fabio2152/qruta/commit/dd8ccc4))
- Evidencia: [ciclo-2-T05-green.txt](evidencias/ciclo-2-T05-green.txt) · Captura: [5verde.png](evidencias/5verde.png)

### T06 – Pasar de EN_ORIGEN directamente a EN_DESTINO se rechaza (RE-05)

```text
Dado un producto recién creado (EN_ORIGEN)
Cuando se intenta pasarlo directamente a EN_DESTINO
Entonces se rechaza con CambioDeEstadoNoPermitidoException:
  "No se puede pasar de EN_ORIGEN a EN_DESTINO"
```

**🔴 RED** – `ProductoTest#pasarDeEnOrigenDirectamenteAEnDestinoSeRechaza`

- No hizo falta ningún esqueleto: la prueba reutiliza la excepción de T05.
- **Qué falló:** el salto de etapa se aceptaba sin control.

```text
Tests run: 6, Failures: 1, Errors: 0
Expecting code to raise a throwable.
```

- Commit: `test(ciclo-2): T06 pasar de EN_ORIGEN directamente a EN_DESTINO se rechaza [RED]` ([`f76fa76`](https://github.com/fabio2152/qruta/commit/f76fa76))
- Evidencia: [ciclo-2-T06-red.txt](evidencias/ciclo-2-T06-red.txt) · Captura: [6rojo.png](evidencias/6rojo.png)

**🟢 GREEN**

- **Código mínimo:** una condición que enumera a mano los tres pasos válidos. Se descartó rechazar solo el caso literal EN_ORIGEN → EN_DESTINO: con eso, el refactor sugerido (que cada etapa sepa su siguiente) habría empezado a rechazar otros saltos, y **un refactor no puede cambiar el comportamiento**. Por eso GREEN implementa RE-05 completa, pero con condiciones repetidas.

```java
boolean esLaSiguienteEtapa =
    (estado == EstadoProducto.EN_ORIGEN
        && nuevoEstado == EstadoProducto.EN_TRANSITO)
    || (estado == EstadoProducto.EN_TRANSITO
        && nuevoEstado == EstadoProducto.EN_DESTINO)
    || (estado == EstadoProducto.EN_DESTINO
        && nuevoEstado == EstadoProducto.ENTREGADO);

if (!esLaSiguienteEtapa) {
    throw new CambioDeEstadoNoPermitidoException(
        "No se puede pasar de " + estado + " a " + nuevoEstado
    );
}
```

- Resultado: `Tests run: 6, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-2): T06 pasar de EN_ORIGEN directamente a EN_DESTINO se rechaza [GREEN]` ([`dc8fa59`](https://github.com/fabio2152/qruta/commit/dc8fa59))
- Evidencia: [ciclo-2-T06-green.txt](evidencias/ciclo-2-T06-green.txt) · Captura: [6verde.png](evidencias/6verde.png)

### 🔵 REFACTOR del ciclo 2

**Sugerencia del documento:** que cada etapa del enum `EstadoProducto` sepa cuál es la siguiente, en lugar de repetir condiciones.

**Qué se mejoró y por qué:**

1. **`EstadoProducto`** ahora conoce el orden de la cadena: `siguiente()`, `esFinal()` y `puedePasarA(nuevoEstado)`. El conocimiento sobre las etapas vive en el enum de las etapas (alta cohesión), y si mañana se agrega una etapa se cambia en un solo lugar.
2. **`Producto`** ya no enumera combinaciones de estados: delega en el enum dentro de un método privado `validarCambioDeEtapa(...)`, y `cambiarEstado` queda en tres pasos legibles: validar, cambiar, registrar la hora.
3. Los mensajes de T05 y T06 no cambiaron; las 6 pruebas siguen en verde sin tocarlas.

**Código antes (`Producto.cambiarEstado`):**

```java
if (estado == EstadoProducto.ENTREGADO) {
    throw new CambioDeEstadoNoPermitidoException(
        "El producto ya fue entregado y no puede cambiar de estado"
    );
}

boolean esLaSiguienteEtapa =
    (estado == EstadoProducto.EN_ORIGEN
        && nuevoEstado == EstadoProducto.EN_TRANSITO)
    || (estado == EstadoProducto.EN_TRANSITO
        && nuevoEstado == EstadoProducto.EN_DESTINO)
    || (estado == EstadoProducto.EN_DESTINO
        && nuevoEstado == EstadoProducto.ENTREGADO);

if (!esLaSiguienteEtapa) {
    throw new CambioDeEstadoNoPermitidoException(
        "No se puede pasar de " + estado + " a " + nuevoEstado
    );
}

estado = nuevoEstado;
```

**Código después:**

```java
// EstadoProducto
public EstadoProducto siguiente() {

    return switch (this) {
        case EN_ORIGEN -> EN_TRANSITO;
        case EN_TRANSITO -> EN_DESTINO;
        case EN_DESTINO -> ENTREGADO;
        case ENTREGADO -> null;
    };
}

public boolean esFinal() {
    return siguiente() == null;
}

public boolean puedePasarA(EstadoProducto nuevoEstado) {
    return siguiente() == nuevoEstado;
}


// Producto
public void cambiarEstado(EstadoProducto nuevoEstado, Usuario usuario, Clock reloj) {

    validarCambioDeEtapa(nuevoEstado);

    estado = nuevoEstado;

    registrarHora(nuevoEstado, LocalDateTime.now(reloj));
}

private void validarCambioDeEtapa(EstadoProducto nuevoEstado) {

    if (estado.esFinal()) {
        throw new CambioDeEstadoNoPermitidoException(
            "El producto ya fue entregado y no puede cambiar de estado"
        );
    }

    if (!estado.puedePasarA(nuevoEstado)) {
        throw new CambioDeEstadoNoPermitidoException(
            "No se puede pasar de " + estado + " a " + nuevoEstado
        );
    }
}
```

- Resultado: `Tests run: 6, Failures: 0, Errors: 0` – BUILD SUCCESS (el comportamiento no cambió).
- Commit: `refactor(ciclo-2): cada etapa de EstadoProducto conoce su siguiente [REFACTOR]` ([`15062f8`](https://github.com/fabio2152/qruta/commit/15062f8))
- Evidencia: [ciclo-2-refactor.txt](evidencias/ciclo-2-refactor.txt)

### Decisiones de diseño y aprendizajes del ciclo 2

- **El orden de las pruebas importa:** T05 (ENTREGADO) fue antes que T06 (saltos). Si la regla general de "solo la siguiente etapa" hubiera existido primero, T05 habría pasado sin fallar y no habría RED que mostrar.
- **Las pruebas de error destaparon huecos reales:** T04 mostró que el código de T03 reventaba con un `NullPointerException` sin explicación; ahora hay una excepción del dominio con un mensaje claro.
- **Guardas al inicio de `cambiarEstado`:** una regla incumplida se detecta antes de modificar datos, así el producto nunca queda en un estado intermedio.
- **Límite de "lo mínimo":** en T06 el mínimo no podía ser un caso literal, porque eso habría obligado a que el refactor cambiara comportamiento. Se eligió el mínimo que el refactor pudiera reorganizar sin alterar resultados.
- **Pendiente para el ciclo 3:** el parámetro `usuario` de `cambiarEstado` todavía no se usa; cualquiera puede cambiar el estado. T07 y T08 (RE-04) lo exigirán.

---

## Ciclo 3 – Permisos

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-04
**Tipo de casos:** Error (T07, T08)

### T07 – El productor no puede cambiar el estado de su producto (RE-04)

```text
Dado un producto en EN_ORIGEN
Cuando su propio productor intenta pasarlo a EN_TRANSITO
Entonces se rechaza con OperacionNoAutorizadaException:
  "Solo un intermediario puede cambiar el estado de un producto"
```

**🔴 RED** – `ProductoTest#elProductorNoPuedeCambiarElEstadoDeSuProducto`

- Esqueleto agregado: clase vacía `OperacionNoAutorizadaException`. `Producto` y `Usuario` no cambiaron.
- Se pidió un cambio de etapa **válido** (EN_ORIGEN → EN_TRANSITO) para que el único motivo de rechazo fuera el permiso, no el orden de las etapas.
- **Qué falló:** el productor pudo cambiar el estado; el parámetro `usuario` se recibía pero no se usaba.

```text
Tests run: 7, Failures: 1, Errors: 0
Expecting code to raise a throwable.
```

- Commit: `test(ciclo-3): T07 el productor no puede cambiar el estado de su producto [RED]` ([`50e78ea`](https://github.com/fabio2152/qruta/commit/50e78ea))
- Evidencia: [ciclo-3-T07-red.txt](evidencias/ciclo-3-T07-red.txt) · Captura: [7rojo.png](evidencias/7rojo.png)

**🟢 GREEN**

- **Código mínimo:** `Usuario` guarda su `rol` y lo expone con `getRol()` (el `nombreUsuario` sigue sin guardarse: ninguna prueba lo pide); la excepción recibe el mensaje; y `cambiarEstado` valida el rol **antes** que la etapa, para que alguien sin permiso no obtenga información del estado del producto. Solo se valida el rol, no la asignación.

```java
if (usuario.getRol() != Rol.INTERMEDIARIO) {
    throw new OperacionNoAutorizadaException(
        "Solo un intermediario puede cambiar el estado de un producto"
    );
}
```

- Resultado: `Tests run: 7, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-3): T07 el productor no puede cambiar el estado de su producto [GREEN]` ([`c97cd14`](https://github.com/fabio2152/qruta/commit/c97cd14))
- Evidencia: [ciclo-3-T07-green.txt](evidencias/ciclo-3-T07-green.txt) · Captura: [7verde.png](evidencias/7verde.png)

### T08 – Un intermediario no asignado no puede cambiar el estado (RE-04)

```text
Dado un producto asignado a intermediario1
  y otro usuario intermediario2 con rol INTERMEDIARIO
Cuando intermediario2 intenta pasarlo a EN_TRANSITO
Entonces se rechaza con OperacionNoAutorizadaException:
  "El usuario no está asignado a este producto"
```

**🔴 RED** – `ProductoTest#unIntermediarioNoAsignadoNoPuedeCambiarElEstado`

- No hizo falta ningún esqueleto: reutiliza la excepción de T07.
- El usuario tiene el rol correcto a propósito, para que lo único que falle sea la asignación.
- **Qué falló:** el intermediario no asignado superaba la validación de rol y cambiaba el estado.

```text
Tests run: 8, Failures: 1, Errors: 0
Expecting code to raise a throwable.
```

- Commit: `test(ciclo-3): T08 un intermediario no asignado no puede cambiar el estado [RED]` ([`eb65b6f`](https://github.com/fabio2152/qruta/commit/eb65b6f))
- Evidencia: [ciclo-3-T08-red.txt](evidencias/ciclo-3-T08-red.txt) · Captura: [8rojo.png](evidencias/8rojo.png)

**🟢 GREEN**

- **Código mínimo:** `Producto` guarda por fin el `intermediarioAsignado` que recibía desde T01, y `cambiarEstado` compara al usuario con él. La comparación es por referencia (`!=`) porque `Usuario` aún no tiene `id` ni `equals` y ninguna prueba los pide.

```java
public Producto(String nombre, Usuario productor, Usuario intermediarioAsignado) {
    this.intermediarioAsignado = intermediarioAsignado;
}

if (usuario != intermediarioAsignado) {
    throw new OperacionNoAutorizadaException(
        "El usuario no está asignado a este producto"
    );
}
```

- Resultado: `Tests run: 8, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: `feat(ciclo-3): T08 un intermediario no asignado no puede cambiar el estado [GREEN]` ([`9a040c6`](https://github.com/fabio2152/qruta/commit/9a040c6))
- Evidencia: [ciclo-3-T08-green.txt](evidencias/ciclo-3-T08-green.txt) · Captura: [8verde.png](evidencias/8verde.png)

### 🔵 REFACTOR del ciclo 3

**Sugerencia del documento:** separar la validación de permisos en un método propio con excepciones y mensajes claros.

**Qué se mejoró y por qué:**

1. Las dos guardas de permisos pasaron a un método privado `validarPermiso(usuario)`. Ahora `cambiarEstado` se lee como la regla de negocio completa, en cuatro pasos: **validar permiso → validar etapa → cambiar estado → registrar hora**. Cada validación tiene su propio método, su propia excepción y su propio mensaje.
2. `intermediarioAsignado` (en `Producto`) y `rol` (en `Usuario`) pasaron a ser `final`: se asignan una sola vez en el constructor y no pueden cambiar después.
3. Los mensajes y las excepciones no cambiaron; las 8 pruebas siguen en verde sin tocarlas.

**Código antes:**

```java
public void cambiarEstado(EstadoProducto nuevoEstado, Usuario usuario, Clock reloj) {

    if (usuario.getRol() != Rol.INTERMEDIARIO) {
        throw new OperacionNoAutorizadaException(
            "Solo un intermediario puede cambiar el estado de un producto"
        );
    }

    if (usuario != intermediarioAsignado) {
        throw new OperacionNoAutorizadaException(
            "El usuario no está asignado a este producto"
        );
    }

    validarCambioDeEtapa(nuevoEstado);

    estado = nuevoEstado;

    registrarHora(nuevoEstado, LocalDateTime.now(reloj));
}
```

**Código después:**

```java
public void cambiarEstado(EstadoProducto nuevoEstado, Usuario usuario, Clock reloj) {

    validarPermiso(usuario);

    validarCambioDeEtapa(nuevoEstado);

    estado = nuevoEstado;

    registrarHora(nuevoEstado, LocalDateTime.now(reloj));
}


private void validarPermiso(Usuario usuario) {

    if (usuario.getRol() != Rol.INTERMEDIARIO) {
        throw new OperacionNoAutorizadaException(
            "Solo un intermediario puede cambiar el estado de un producto"
        );
    }

    if (usuario != intermediarioAsignado) {
        throw new OperacionNoAutorizadaException(
            "El usuario no está asignado a este producto"
        );
    }
}
```

- Resultado: `Tests run: 8, Failures: 0, Errors: 0` – BUILD SUCCESS (el comportamiento no cambió).
- Commit: `refactor(ciclo-3): separar la validacion de permisos en su propio metodo [REFACTOR]` ([`d3cd1f9`](https://github.com/fabio2152/qruta/commit/d3cd1f9))
- Evidencia: [ciclo-3-refactor.txt](evidencias/ciclo-3-refactor.txt)

### Decisiones de diseño y aprendizajes del ciclo 3

- **Primero el rol, después la asignación:** si se hubiera validado primero la asignación, el productor también habría quedado rechazado (no es el asignado) y T07 habría pasado sin fallar.
- **Se mantienen las dos validaciones** aunque la de asignación ya rechazaría al productor: cada una da un mensaje distinto y preciso, y T07 exige el suyo.
- **Los permisos van antes que las etapas:** quien no tiene permiso no recibe información sobre el estado del producto.
- **Parámetros que esperaron a su prueba:** `usuario` (recibido desde T02) recién se usó en T07, e `intermediarioAsignado` (recibido desde T01) recién se guardó en T08. Nada se implementó antes de que una prueba lo pidiera.
- **Pendiente para ciclos opcionales:** comparar usuarios por `id` cuando exista el repositorio en memoria, validar RE-02/RE-03 al crear el producto, el historial (RE-09) y la visibilidad por rol (RE-10). *(RE-09 y RE-10 se cubrieron después en los ciclos 4 y 5; RE-02 y RE-03 quedaron fuera del alcance.)*

---

## Ciclo 4 – Servicio: crear y listar productos

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-01, RE-10
**Tipo de casos:** Normales
**Clase de prueba:** `ProductoServiceTest` (servicio con repositorio en memoria y un reloj fijo a las 08:00 del 01/09/2026, preparados en `@BeforeEach`)

Desde este ciclo el servicio recibe el `Clock` en su constructor: es la "clase que recibe el reloj del sistema" de la sección 7 del documento. Spring le pasa la hora real y las pruebas le pasan una fija.

### T09 – El productor crea un producto y queda guardado con id en EN_ORIGEN (RE-01)

```text
Dado un productor, un intermediario y el servicio
Cuando el productor crea "Palta Hass" asignada al intermediario
Entonces el producto tiene id, buscarPorId(id) devuelve ese mismo producto y está en EN_ORIGEN
```

**🔴 RED** – `ProductoServiceTest#elProductorCreaUnProductoYQuedaGuardadoConIdEnOrigen`

- Esqueletos: `ProductoService` (constructor vacío; `crearProducto` y `buscarPorId` devuelven `null`), `ProductoRepositorioEnMemoria` vacío y `Producto.getId()` que devuelve `null`.
- **Qué falló:** `Expecting actual not to be null` (el servicio no creaba nada).
- Commit: [`8e04615`](https://github.com/fabio2152/qruta/commit/8e04615) · Evidencia: [ciclo-4-T09-red.txt](evidencias/ciclo-4-T09-red.txt)

**🟢 GREEN**

- **Código mínimo:** el repositorio guarda en una lista y asigna un id secuencial (`guardar`, `buscarPorId` con `Optional`); `Producto` tiene `id` con `getId`/`setId`; el servicio crea el producto y lo guarda. El reloj todavía no se guarda: ninguna prueba lo usa.
- Commit: [`5c045fd`](https://github.com/fabio2152/qruta/commit/5c045fd) · Evidencia: [ciclo-4-T09-green.txt](evidencias/ciclo-4-T09-green.txt)

### T10 – El regulador ve todos los productos (RE-10)

**🔴 RED** – `ProductoServiceTest#elReguladorVeTodosLosProductos`

- Dos productos asignados a intermediarios distintos; `listarProductos(regulador)` debe devolver ambos.
- Esqueletos: valor `REGULADOR` en `Rol` y `listarProductos` que devuelve `null`.
- **Qué falló:** `Expecting actual not to be null`.
- Commit: [`b26b24a`](https://github.com/fabio2152/qruta/commit/b26b24a) · Evidencia: [ciclo-4-T10-red.txt](evidencias/ciclo-4-T10-red.txt)

**🟢 GREEN**

- **Código mínimo:** `listarProductos` devuelve `repositorio.listarTodos()` sin filtrar. Es correcto para el regulador y todavía nadie exige filtros.
- Commit: [`389a438`](https://github.com/fabio2152/qruta/commit/389a438) · Evidencia: [ciclo-4-T10-green.txt](evidencias/ciclo-4-T10-green.txt)

### T11 – El productor ve solo los productos que creó (RE-10)

**🔴 RED** – `ProductoServiceTest#elProductorVeSoloLosProductosQueCreo`

- Un producto de `productor1` y otro de `productor2`; `productor1` debe ver solo el suyo.
- **Qué falló:** se devolvían los dos productos (el GREEN de T10 no filtraba).
- Commit: [`cec61fb`](https://github.com/fabio2152/qruta/commit/cec61fb) · Evidencia: [ciclo-4-T11-red.txt](evidencias/ciclo-4-T11-red.txt)

**🟢 GREEN**

- **Código mínimo:** `Producto` guarda por fin el `productor` que recibía desde T01 (`getProductor()`), y el servicio filtra cuando el rol es PRODUCTOR.
- Commit: [`3274ff4`](https://github.com/fabio2152/qruta/commit/3274ff4) · Evidencia: [ciclo-4-T11-green.txt](evidencias/ciclo-4-T11-green.txt)

### T12 – El intermediario ve solo sus productos asignados (RE-10)

**🔴 RED** – `ProductoServiceTest#elIntermediarioVeSoloSusProductosAsignados`

- Un producto para `intermediario1` y otro para `intermediario2`; `intermediario1` debe ver solo el suyo.
- **Qué falló:** se devolvían los dos productos.
- Commit: [`a6eb858`](https://github.com/fabio2152/qruta/commit/a6eb858) · Evidencia: [ciclo-4-T12-red.txt](evidencias/ciclo-4-T12-red.txt)

**🟢 GREEN**

- **Código mínimo:** `getIntermediarioAsignado()` en `Producto` y un segundo `if` en el servicio para el rol INTERMEDIARIO.
- Commit: [`bc56885`](https://github.com/fabio2152/qruta/commit/bc56885) · Evidencia: [ciclo-4-T12-green.txt](evidencias/ciclo-4-T12-green.txt)

### 🔵 REFACTOR del ciclo 4

**Qué se mejoró y por qué:** el servicio tenía un `if` por rol con el mismo `stream().filter(...)` repetido. La regla de "quién puede ver qué" es conocimiento del producto, así que pasó a `Producto.esVisiblePara(usuario)` con un `switch` sobre el rol. El servicio queda con una sola línea de filtrado. En las pruebas, `otroIntermediario` pasó al `@BeforeEach`.

**Código antes (`ProductoService`):**

```java
List<Producto> todos = repositorio.listarTodos();

if (usuario.getRol() == Rol.PRODUCTOR) {
    return todos.stream()
            .filter(producto -> producto.getProductor() == usuario)
            .toList();
}

if (usuario.getRol() == Rol.INTERMEDIARIO) {
    return todos.stream()
            .filter(producto -> producto.getIntermediarioAsignado() == usuario)
            .toList();
}

return todos;
```

**Código después:**

```java
// ProductoService
return repositorio.listarTodos()
        .stream()
        .filter(producto -> producto.esVisiblePara(usuario))
        .toList();

// Producto
public boolean esVisiblePara(Usuario usuario) {
    return switch (usuario.getRol()) {
        case PRODUCTOR -> productor == usuario;
        case INTERMEDIARIO -> intermediarioAsignado == usuario;
        case REGULADOR -> true;
    };
}
```

- Resultado: `Tests run: 12, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: [`8bc058e`](https://github.com/fabio2152/qruta/commit/8bc058e) · Evidencia: [ciclo-4-refactor.txt](evidencias/ciclo-4-refactor.txt)

---

## Ciclo 5 – Servicio: cambiar estado e historial

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-07, RE-09
**Tipo de casos:** Normales (T13, T15) y error (T14)

### T13 – Cambiar el estado por id usa el reloj del sistema (RE-07)

**🔴 RED** – `ProductoServiceTest#cambiarElEstadoPorIdUsaElRelojDelSistema`

- `servicio.cambiarEstado(id, EN_TRANSITO, intermediario)`: el producto debe quedar EN_TRANSITO con hora de inicio igual a la del reloj del servicio (08:00).
- Esqueleto: `cambiarEstado(idProducto, nuevoEstado, usuario)` vacío.
- **Qué falló:** `expected: EN_TRANSITO but was: EN_ORIGEN`.
- Commit: [`002de72`](https://github.com/fabio2152/qruta/commit/002de72) · Evidencia: [ciclo-5-T13-red.txt](evidencias/ciclo-5-T13-red.txt)

**🟢 GREEN**

- **Código mínimo:** el servicio guarda el `Clock` que recibía desde T09, busca el producto y delega en `producto.cambiarEstado(nuevoEstado, usuario, reloj)`. Todas las reglas del dominio (ciclos 1 a 3) se reutilizan sin duplicarlas.
- Commit: [`0f3c69b`](https://github.com/fabio2152/qruta/commit/0f3c69b) · Evidencia: [ciclo-5-T13-green.txt](evidencias/ciclo-5-T13-green.txt)

### T14 – Cambiar el estado de un producto inexistente se rechaza

**🔴 RED** – `ProductoServiceTest#cambiarElEstadoDeUnProductoInexistenteSeRechaza`

- Se espera `ProductoNoEncontradoException` con *"No existe un producto con id 99"*.
- Esqueleto: clase vacía `ProductoNoEncontradoException`.
- **Qué falló:** se lanzaba un `NullPointerException` (el GREEN de T13 usaba `orElse(null)`).
- Commit: [`238ed3e`](https://github.com/fabio2152/qruta/commit/238ed3e) · Evidencia: [ciclo-5-T14-red.txt](evidencias/ciclo-5-T14-red.txt)

**🟢 GREEN**

- **Código mínimo:** `orElseThrow(...)` con la excepción propia y su mensaje.
- Commit: [`50edcd4`](https://github.com/fabio2152/qruta/commit/50edcd4) · Evidencia: [ciclo-5-T14-green.txt](evidencias/ciclo-5-T14-green.txt)

### T15 – Tras dos cambios, el historial tiene dos registros (RE-09)

```text
Dado un producto que pasa a EN_TRANSITO y luego a EN_DESTINO (reloj a las 08:00)
Cuando se pide su historial
Entonces hay dos registros, en orden:
  EN_ORIGEN → EN_TRANSITO, 08:00, intermediario1
  EN_TRANSITO → EN_DESTINO, 08:00, intermediario1
```

**🔴 RED** – `ProductoServiceTest#trasDosCambiosElHistorialTieneDosRegistros`

- Esqueletos: el `record CambioEstado(estadoAnterior, estadoNuevo, fechaHora, usuario)` y `obtenerHistorial` que devuelve `null`.
- **Qué falló:** `Expecting actual not to be null`.
- Commit: [`7c9d445`](https://github.com/fabio2152/qruta/commit/7c9d445) · Evidencia: [ciclo-5-T15-red.txt](evidencias/ciclo-5-T15-red.txt)

**🟢 GREEN**

- **Código mínimo:** `Producto` agrega un `CambioEstado` a su historial en cada cambio válido (después de las validaciones, así un intento rechazado no queda registrado) y lo expone con `getHistorial()`; el servicio lo devuelve.
- Commit: [`06169b8`](https://github.com/fabio2152/qruta/commit/06169b8) · Evidencia: [ciclo-5-T15-green.txt](evidencias/ciclo-5-T15-green.txt)

### 🔵 REFACTOR del ciclo 5

**Qué se mejoró y por qué:**

1. La búsqueda con su `orElseThrow` estaba dentro de `cambiarEstado`; pasó a un método privado `buscarProductoExistente(id)`, así `cambiarEstado` solo expresa la acción.
2. `getHistorial()` devuelve una copia inmodificable (`List.copyOf`): desde fuera nadie puede agregar ni borrar registros. Es la misma idea de "nadie puede alterar los tiempos" aplicada al historial.

**Código después (`ProductoService`):**

```java
public void cambiarEstado(Long idProducto, EstadoProducto nuevoEstado, Usuario usuario) {

    Producto producto = buscarProductoExistente(idProducto);

    producto.cambiarEstado(nuevoEstado, usuario, reloj);
}


private Producto buscarProductoExistente(Long idProducto) {

    return repositorio
            .buscarPorId(idProducto)
            .orElseThrow(
                () -> new ProductoNoEncontradoException(
                    "No existe un producto con id " + idProducto
                )
            );
}
```

- Resultado: `Tests run: 15, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commit: [`c72044d`](https://github.com/fabio2152/qruta/commit/c72044d) · Evidencia: [ciclo-5-refactor.txt](evidencias/ciclo-5-refactor.txt)

---

## Ciclo 6 – Servicio: login

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** Login simple (sección 8 del documento)
**Tipo de casos:** Normal (T16) y error (T17)
**Clase de prueba:** `LoginServiceTest` (repositorio de usuarios con `productor1/clave123` e `intermediario1/clave456`)

### T16 – Usuario y contraseña correctos devuelven el usuario

**🔴 RED** – `LoginServiceTest#usuarioYContrasenaCorrectosDevuelvenElUsuario`

- Esqueletos: `LoginService` (`iniciarSesion` devuelve `null`), `UsuarioRepositorioEnMemoria` con `guardar` vacío, y un constructor `Usuario(nombreUsuario, contrasena, rol)` que delega en el existente (las pruebas del dominio no se tocaron).
- **Qué falló:** `Expecting actual: null` — se esperaba el mismo objeto `productor1`.
- Commit: [`4cff306`](https://github.com/fabio2152/qruta/commit/4cff306) · Evidencia: [ciclo-6-T16-red.txt](evidencias/ciclo-6-T16-red.txt)

**🟢 GREEN**

- **Código mínimo:** `Usuario` guarda su `nombreUsuario`; el repositorio busca por nombre; el login devuelve el usuario encontrado. **Todavía no revisa la contraseña**: ninguna prueba lo exigía.
- Commit: [`6b4187a`](https://github.com/fabio2152/qruta/commit/6b4187a) · Evidencia: [ciclo-6-T16-green.txt](evidencias/ciclo-6-T16-green.txt)

### T17 – Una contraseña incorrecta se rechaza

**🔴 RED** – `LoginServiceTest#unaContrasenaIncorrectaSeRechaza`

- Se espera `CredencialesInvalidasException` con *"Usuario o contraseña incorrectos"*.
- **Qué falló:** `Expecting code to raise a throwable.` — el atajo de T16 aceptaba cualquier contraseña.
- Commit: [`75eb7cd`](https://github.com/fabio2152/qruta/commit/75eb7cd) · Evidencia: [ciclo-6-T17-red.txt](evidencias/ciclo-6-T17-red.txt)

**🟢 GREEN**

- **Código mínimo:** `Usuario` guarda la contraseña y responde `tieneContrasena(...)`; el login encadena `filter(...)` y `orElseThrow(...)`. El mismo camino rechaza también a un usuario inexistente, sin una condición extra, y con el mismo mensaje (no revela si el usuario existe).

```java
return usuarios
        .buscarPorNombreUsuario(nombreUsuario)
        .filter(usuario -> usuario.tieneContrasena(contrasena))
        .orElseThrow(
            () -> new CredencialesInvalidasException(
                "Usuario o contraseña incorrectos"
            )
        );
```

- Commit: [`162c9c8`](https://github.com/fabio2152/qruta/commit/162c9c8) · Evidencia: [ciclo-6-T17-green.txt](evidencias/ciclo-6-T17-green.txt)

### 🔵 REFACTOR del ciclo 6

**Qué se mejoró y por qué:** `Usuario` tenía la delegación al revés (el constructor completo llamaba al corto y luego asignaba la contraseña), lo que obligaba a que `contrasena` no fuera `final`. Ahora el constructor completo asigna los tres campos, el corto delega en él con `this(nombreUsuario, null, rol)`, y todos los campos son `final`.

**Nota sobre los commits:** el primer commit de refactor ([`4176317`](https://github.com/fabio2152/qruta/commit/4176317)) subió por error solo la evidencia, sin el cambio de código. Como las reglas del proyecto prohíben reescribir el historial (`amend`, `rebase`, `push --force`), se corrigió con un segundo commit ([`573bb78`](https://github.com/fabio2152/qruta/commit/573bb78)) que contiene el cambio real. Ambos quedan visibles en el historial.

**Código antes:**

```java
private final String nombreUsuario;
private final Rol rol;
private String contrasena;

public Usuario(String nombreUsuario, Rol rol) {
    this.nombreUsuario = nombreUsuario;
    this.rol = rol;
}

public Usuario(String nombreUsuario, String contrasena, Rol rol) {
    this(nombreUsuario, rol);
    this.contrasena = contrasena;
}
```

**Código después:**

```java
private final String nombreUsuario;
private final String contrasena;
private final Rol rol;

public Usuario(String nombreUsuario, Rol rol) {
    this(nombreUsuario, null, rol);
}

public Usuario(String nombreUsuario, String contrasena, Rol rol) {
    this.nombreUsuario = nombreUsuario;
    this.contrasena = contrasena;
    this.rol = rol;
}
```

- Resultado: `Tests run: 17, Failures: 0, Errors: 0` – BUILD SUCCESS.
- Commits: [`4176317`](https://github.com/fabio2152/qruta/commit/4176317) (solo evidencia) y [`573bb78`](https://github.com/fabio2152/qruta/commit/573bb78) (cambio de código) · Evidencia: [ciclo-6-refactor.txt](evidencias/ciclo-6-refactor.txt)

### Decisiones de diseño y aprendizajes de los ciclos 4 a 6

- **El servicio no repite reglas:** todas las validaciones de etapas y permisos siguen en el dominio; el servicio solo busca, delega y guarda. Por eso T13 pasó con una sola línea de delegación.
- **Parámetros que esperaron a su prueba:** `productor` (recibido desde T01) recién se guardó en T11, y el reloj del servicio (recibido desde T09) recién se guardó en T13.
- **Atajos que la siguiente prueba rompió:** T10 listaba todo sin filtrar (T11 lo rompió), T13 usaba `orElse(null)` (T14 lo rompió) y T16 no revisaba la contraseña (T17 lo rompió).
- **Comparación por referencia:** los filtros y permisos comparan usuarios con `==`. Funciona porque el repositorio en memoria devuelve siempre los mismos objetos; con una base de datos real habría que comparar por `id`.

---

## Ciclo 7 – Persistencia en PostgreSQL

**Fecha:** 2026-09-26
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-04, RE-08, RE-09, RE-10 (mantenerlas cuando los datos vienen de una base de datos)
**Tipo de casos:** Normales

**Motivación:** al guardar los datos en PostgreSQL, cada lectura crea objetos nuevos. Un `Usuario` leído de la base ya no es *el mismo objeto* que el asignado al producto, y un `Producto` leído tiene que reconstruirse con su estado, horas e historial. Antes del ciclo se hizo un refactor preparatorio: `ProductoRepositorio` y `UsuarioRepositorio` pasaron a ser interfaces ([`f09ff22`](https://github.com/fabio2152/qruta/commit/f09ff22)), así las pruebas siguen usando la versión en memoria.

### T18 – El intermediario asignado leído de nuevo puede cambiar el estado (RE-04)

- **🔴 RED** – `ProductoTest#elIntermediarioAsignadoLeidoDeNuevoPuedeCambiarElEstado`: se pasa otra instancia de `intermediario1`. **Falló** con `OperacionNoAutorizadaException: El usuario no está asignado a este producto`, porque se comparaba con `!=` (referencia). Commit [`ca5002b`](https://github.com/fabio2152/qruta/commit/ca5002b) · [ciclo-7-T18-red.txt](evidencias/ciclo-7-T18-red.txt)
- **🟢 GREEN** – `Usuario` define `equals`/`hashCode` por `nombreUsuario` y `validarPermiso` usa `equals`. Commit [`dd0851c`](https://github.com/fabio2152/qruta/commit/dd0851c) · [ciclo-7-T18-green.txt](evidencias/ciclo-7-T18-green.txt)

### T19 – El intermediario ve sus productos aunque el usuario sea otra instancia (RE-10)

- **🔴 RED** – `ProductoServiceTest#elIntermediarioVeSusProductosAunqueElUsuarioSeaOtraInstancia`. **Falló**: `Expecting actual: []` (el filtro de visibilidad comparaba con `==`). Commit [`612e011`](https://github.com/fabio2152/qruta/commit/612e011) · [ciclo-7-T19-red.txt](evidencias/ciclo-7-T19-red.txt)
- **🟢 GREEN** – `esVisiblePara` usa `equals` en el caso INTERMEDIARIO. Commit [`d7d1eb8`](https://github.com/fabio2152/qruta/commit/d7d1eb8) · [ciclo-7-T19-green.txt](evidencias/ciclo-7-T19-green.txt)

### T20 – Un producto reconstruido en EN_TRANSITO calcula la duración al llegar (RE-08)

- **🔴 RED** – `ProductoTest#unProductoReconstruidoEnTransitoCalculaLaDuracionAlLlegar`. Esqueleto: `Producto.reconstruir(...)` que devolvía un producto nuevo. **Falló**: `expected: EN_TRANSITO but was: EN_ORIGEN`. Commit [`257bf81`](https://github.com/fabio2152/qruta/commit/257bf81) · [ciclo-7-T20-red.txt](evidencias/ciclo-7-T20-red.txt)
- **🟢 GREEN** – `reconstruir` restaura id, estado, horas e historial. Después, las reglas del dominio siguen funcionando: pasa a EN_DESTINO y la duración es 6 h 30 min. Commit [`5537cbc`](https://github.com/fabio2152/qruta/commit/5537cbc) · [ciclo-7-T20-green.txt](evidencias/ciclo-7-T20-green.txt)

### T21 – Cambiar el estado guarda el producto actualizado (RE-09)

- **🔴 RED** – `ProductoServiceTest#cambiarElEstadoGuardaElProductoActualizadoEnElRepositorio`, con un repositorio de prueba que cuenta los `guardar`. **Falló**: `expected: 2 but was: 1` (en memoria no hacía falta volver a guardar; con una base de datos el cambio se perdería). Commit [`f54e713`](https://github.com/fabio2152/qruta/commit/f54e713) · [ciclo-7-T21-red.txt](evidencias/ciclo-7-T21-red.txt)
- **🟢 GREEN** – el servicio llama a `repositorio.guardar(producto)` después de `cambiarEstado`, y el repositorio en memoria no duplica un producto que ya tiene id. Commit [`4516ad2`](https://github.com/fabio2152/qruta/commit/4516ad2) · [ciclo-7-T21-green.txt](evidencias/ciclo-7-T21-green.txt)

### 🔵 REFACTOR del ciclo 7

El caso PRODUCTOR de `esVisiblePara` todavía comparaba con `==`. Se cambió a `equals`, así el dominio **nunca** compara usuarios por referencia. Resultado: `Tests run: 21, Failures: 0, Errors: 0`. Commit [`8d8281f`](https://github.com/fabio2152/qruta/commit/8d8281f) · [ciclo-7-refactor.txt](evidencias/ciclo-7-refactor.txt)

### Decisiones de diseño del ciclo 7

- **El dominio sigue sin Spring ni JPA:** las anotaciones de base de datos están en clases aparte (`ProductoEntidad`, `UsuarioEntidad`, `CambioEstadoEntidad`, paquete `repositorio.jpa`), que traducen hacia y desde el dominio con `Producto.reconstruir(...)`.
- **Inversión de dependencias:** el servicio depende de las interfaces `ProductoRepositorio` y `UsuarioRepositorio`. Las pruebas usan la implementación en memoria y la aplicación usa la de PostgreSQL, sin cambiar el servicio.
- **Las pruebas no necesitan la base de datos:** siguen corriendo en milisegundos y en GitHub Actions sin Docker.

---

## Ciclo 8 – Cuentas y sesión (fase 2)

**Fecha:** 2026-10-14
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-11, RE-12, RE-13, RE-14, RE-15, RE-16, RE-17
**Tipo de casos:** Normales (T22, T24, T30), límite (T23, T27, T32) y de error (T25, T26, T28, T29, T31)

**Motivación:** en la fase 1 los usuarios eran de prueba y sin contraseña cifrada. La plataforma ahora necesita que productores, transportistas y mercados se registren solos, que alguien con autoridad los apruebe y que cada sesión tenga un token con vencimiento.

**Cómo se respetaron T01–T21 sin modificarlas:**
- Los constructores actuales de `Usuario` siguen existiendo y crean cuentas ACTIVAS (decisión D-05).
- `LoginService` conserva su constructor de un solo parámetro, que compara la contraseña tal cual, como esperan T16 y T17. La aplicación real usa el constructor que recibe el cifrador BCrypt.

### T22 – Un productor se registra y queda PENDIENTE (RE-13, RE-15)

- **🔴 RED** – `RegistroServiceTest#unProductorSeRegistraConUnaContrasenaDeOchoCaracteresYQuedaPendiente`.
  - Esqueletos: `RegistroService.registrar` que devolvía `null`, `SolicitudRegistro`, los enums `EstadoCuenta`, `TipoCuenta`, `TipoDocumento` y `TipoProductor`, y `Productor`.
  - **Falló:** `Expecting actual not to be null`.
  - Commit [`6a6c01f`](https://github.com/fabio2152/qruta/commit/6a6c01f) · [ciclo-8-T22-red.txt](evidencias/ciclo-8-T22-red.txt)
- **🟢 GREEN** – `Usuario` gana `nombreCompleto`, `correo` y `estadoCuenta`. `Productor` guarda sus datos y `registrar` crea al productor PENDIENTE y lo guarda. Commit [`a69c12e`](https://github.com/fabio2152/qruta/commit/a69c12e) · [ciclo-8-T22-green.txt](evidencias/ciclo-8-T22-green.txt)

### T23 – Una contraseña de 7 caracteres se rechaza (RE-13, límite)

- **🔴 RED** – `unRegistroConUnaContrasenaDeSieteCaracteresSeRechaza`. Esqueleto: `DatoInvalidoException`. **Falló:** `Expecting code to raise a throwable`. Commit [`32bf005`](https://github.com/fabio2152/qruta/commit/32bf005) · [ciclo-8-T23-red.txt](evidencias/ciclo-8-T23-red.txt)
- **🟢 GREEN** – si la contraseña tiene menos de 8 caracteres se lanza `DatoInvalidoException`. T22 (8 caracteres) sigue pasando: el límite queda probado por los dos lados. Commit [`95c7407`](https://github.com/fabio2152/qruta/commit/95c7407) · [ciclo-8-T23-green.txt](evidencias/ciclo-8-T23-green.txt)

### T24 – La contraseña se guarda cifrada y se puede verificar (RE-13)

- **🔴 RED** – `laContrasenaSeGuardaCifradaYSePuedeVerificar`.
  - Esqueletos: la interfaz `CifradorDeContrasenas` (dominio, Java puro) y `CifradorBCrypt` vacío.
  - **Falló:** `Expecting actual: "clave123" not to be equal to: "clave123"`.
  - Commit [`00f106d`](https://github.com/fabio2152/qruta/commit/00f106d) · [ciclo-8-T24-red.txt](evidencias/ciclo-8-T24-red.txt)
- **🟢 GREEN** – se agrega la dependencia `spring-security-crypto`. `CifradorBCrypt` usa `BCryptPasswordEncoder` y `registrar` guarda `cifrador.cifrar(contrasena)`. El constructor simple de `RegistroService` usa `CifradorTextoPlano`. Commit [`77ea0a6`](https://github.com/fabio2152/qruta/commit/77ea0a6) · [ciclo-8-T24-green.txt](evidencias/ciclo-8-T24-green.txt)

### T25 – Un nombre de usuario repetido se rechaza (RE-12)

- **🔴 RED** – `unRegistroConUnNombreDeUsuarioYaExistenteSeRechaza`. Esqueleto: `UsuarioYaExisteException`. **Falló:** `Expecting code to raise a throwable`. Commit [`deb48b7`](https://github.com/fabio2152/qruta/commit/deb48b7) · [ciclo-8-T25-red.txt](evidencias/ciclo-8-T25-red.txt)
- **🟢 GREEN** – antes de crear la cuenta se busca el nombre de usuario en el repositorio. Commit [`deb3b9a`](https://github.com/fabio2152/qruta/commit/deb3b9a) · [ciclo-8-T25-green.txt](evidencias/ciclo-8-T25-green.txt)

### T26 – Un correo sin formato válido se rechaza (RE-12)

- **🔴 RED** – `unRegistroConUnCorreoSinFormatoValidoSeRechaza` con `rosa.quispe.correo.pe`. **Falló:** `Expecting code to raise a throwable`. Commit [`b5f73a5`](https://github.com/fabio2152/qruta/commit/b5f73a5) · [ciclo-8-T26-red.txt](evidencias/ciclo-8-T26-red.txt)
- **🟢 GREEN** – el correo debe cumplir el patrón `texto@texto.texto`. Commit [`8727c8d`](https://github.com/fabio2152/qruta/commit/8727c8d) · [ciclo-8-T26-green.txt](evidencias/ciclo-8-T26-green.txt)

### T27 – Un transportista con RUC de 10 dígitos se rechaza (RE-14, límite)

- **🔴 RED** – `unTransportistaConUnRucDeDiezDigitosSeRechaza` con `2060123456`. **Falló:** `Expecting code to raise a throwable`. Commit [`59d4d8a`](https://github.com/fabio2152/qruta/commit/59d4d8a) · [ciclo-8-T27-red.txt](evidencias/ciclo-8-T27-red.txt)
- **🟢 GREEN** – si el documento es RUC, debe tener exactamente 11 dígitos. Commit [`b29a8c3`](https://github.com/fabio2152/qruta/commit/b29a8c3) · [ciclo-8-T27-green.txt](evidencias/ciclo-8-T27-green.txt)

### T28 – El registro público no admite reguladores (RE-11)

- **🔴 RED** – `unRegistroPublicoConRolDeReguladorSeRechaza`. **Falló:** `Expecting code to raise a throwable`. Commit [`d74e744`](https://github.com/fabio2152/qruta/commit/d74e744) · [ciclo-8-T28-red.txt](evidencias/ciclo-8-T28-red.txt)
- **🟢 GREEN** – una solicitud con `TipoCuenta.REGULADOR` lanza `OperacionNoAutorizadaException`. Commit [`ebb8450`](https://github.com/fabio2152/qruta/commit/ebb8450) · [ciclo-8-T28-green.txt](evidencias/ciclo-8-T28-green.txt)

### T29 – Una cuenta PENDIENTE no puede iniciar sesión (RE-16)

- **🔴 RED** – `LoginServiceTest#unaCuentaPendienteNoPuedeIniciarSesion`. La prueba arma su propio repositorio, sin tocar la preparación de T16 y T17. Esqueleto: `CuentaNoActivaException`. **Falló:** `Expecting code to raise a throwable`. Commit [`298015c`](https://github.com/fabio2152/qruta/commit/298015c) · [ciclo-8-T29-red.txt](evidencias/ciclo-8-T29-red.txt)
- **🟢 GREEN** – después de verificar la contraseña, `LoginService` rechaza toda cuenta que no esté ACTIVA y explica el motivo: "pendiente de aprobación" o "suspendida". Commit [`4bbb098`](https://github.com/fabio2152/qruta/commit/4bbb098) · [ciclo-8-T29-green.txt](evidencias/ciclo-8-T29-green.txt)

### T30 – El regulador aprueba al productor y ya puede iniciar sesión (RE-15)

- **🔴 RED** – `elReguladorApruebaAlProductorYYaPuedeIniciarSesion`. Usa BCrypt de punta a punta: registro cifrado y login que verifica con el mismo cifrador. Esqueletos: `RegistroService.aprobar` vacío y el constructor `LoginService(usuarios, cifrador)`. **Falló:** `expected: ACTIVA but was: PENDIENTE`. Commit [`3f22d71`](https://github.com/fabio2152/qruta/commit/3f22d71) · [ciclo-8-T30-red.txt](evidencias/ciclo-8-T30-red.txt)
- **🟢 GREEN** – Commit [`b5743fc`](https://github.com/fabio2152/qruta/commit/b5743fc) · [ciclo-8-T30-green.txt](evidencias/ciclo-8-T30-green.txt)
  - `Usuario.activar()`.
  - `aprobar` busca la cuenta, la activa y la vuelve a guardar.
  - `LoginService` compara con `cifrador.coincide(...)`.
  - El repositorio en memoria reemplaza a un usuario que ya existe en lugar de duplicarlo.

### T31 – El regulador no puede aprobar un mercado (RE-15)

- **🔴 RED** – `elReguladorIntentaAprobarUnMercadoYSeRechaza`. **Falló:** `Expecting code to raise a throwable`, porque el mercado se registraba como si fuera un productor. Commit [`fc4f88a`](https://github.com/fabio2152/qruta/commit/fc4f88a) · [ciclo-8-T31-red.txt](evidencias/ciclo-8-T31-red.txt)
- **🟢 GREEN** – nace `Mercado`, con rol INTERMEDIARIO (D-02). `registrar` lo crea cuando la cuenta es de mercado, y `aprobar` rechaza que un regulador apruebe un mercado. Commit [`a4f2962`](https://github.com/fabio2152/qruta/commit/a4f2962) · [ciclo-8-T31-green.txt](evidencias/ciclo-8-T31-green.txt)

### T32 – El token vence a las 8 horas (RE-17, límite)

- **🔴 RED** – `SesionServiceTest#unTokenEmitidoALasOchoEsValidoALas1559YSeRechazaALas1600`. Usa un `RelojAjustable`, un `Clock` de prueba al que se le cambia la hora. Esqueletos: `SesionService` (`iniciar` y `validar` devolvían `null`) y `SesionInvalidaException`. **Falló:** `expected: Usuario@… but was: null`. Commit [`aaef3f2`](https://github.com/fabio2152/qruta/commit/aaef3f2) · [ciclo-8-T32-red.txt](evidencias/ciclo-8-T32-red.txt)
- **🟢 GREEN** – Commit [`1db4235`](https://github.com/fabio2152/qruta/commit/1db4235) · [ciclo-8-T32-green.txt](evidencias/ciclo-8-T32-green.txt)
  - `iniciar` genera un UUID y guarda el usuario con la hora de emisión, tomada del reloj inyectado.
  - `validar` rechaza el token cuando `ahora` ya no es anterior a `emitida + 8 h`. Así las 15:59 pasan y las 16:00 no.

### 🔵 REFACTOR del ciclo 8

**Qué se mejoró y por qué:**
- `registrar` había crecido con cinco validaciones y la creación de la cuenta en un solo método.
- Las validaciones de formato pasan a `ValidadorDeFormato` (dominio), como sugiere la especificación. Se pueden reutilizar en otros registros, por ejemplo el del regulador del ciclo 14.
- `registrar` queda en tres pasos: `validar`, `crearCuenta` y `guardar`.
- Los números 8 (largo de la contraseña y horas de validez) pasan a constantes con nombre.
- Se elimina `Usuario.tieneContrasena`, que quedó sin uso cuando el login empezó a verificar con el cifrador.

**Código antes:**

```java
if (!solicitud.getCorreo().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) { ... }
if (solicitud.getTipoDocumento() == TipoDocumento.RUC
        && !solicitud.getNumeroDocumento().matches("\\d{11}")) { ... }
// ... y la creación del Productor o del Mercado en el mismo método
```

**Código después:**

```java
public Usuario registrar(SolicitudRegistro solicitud) {
    validar(solicitud);
    Usuario nuevo = crearCuenta(solicitud);
    usuarios.guardar(nuevo);
    return nuevo;
}
// validar(...) usa ValidadorDeFormato.esCorreoValido(...) y esRucValido(...)
```

- Resultado: `Tests run: 32, Failures: 0, Errors: 0`. Commit [`404bb0a`](https://github.com/fabio2152/qruta/commit/404bb0a) · [ciclo-8-refactor.txt](evidencias/ciclo-8-refactor.txt)

### Decisiones de diseño del ciclo 8

- **El dominio no depende de BCrypt.** Solo conoce la interfaz `CifradorDeContrasenas`; `CifradorBCrypt` vive en `configuracion`. Es inversión de dependencias, como con los repositorios del ciclo 7.
- **Herencia para los datos y rol para los permisos (D-02).** `Productor` y `Mercado` heredan de `Usuario`. El mercado tiene rol INTERMEDIARIO, pero la regla de aprobación pregunta por su clase (`instanceof Mercado`).
- **Sesiones en memoria.** Un token es un UUID guardado en `SesionService`, con la hora tomada del `Clock` inyectado. Si la aplicación se reinicia, hay que volver a iniciar sesión, lo que es aceptable para este alcance.
- **Cobertura del dominio y los servicios al cerrar el ciclo:** 282 de 301 líneas (93,7 %).

---

## Ciclo 8b – Cuentas: pruebas agregadas

**Fecha:** 2026-10-14
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-12, RE-14, RF-03
**Tipo de casos:** Límite (T78), error (T79) y normal (T80)

**Motivación (pruebas agregadas):** al cerrar el ciclo 8 se revisó qué reglas de la especificación no tenían ninguna prueba en la lista T22–T74. Por TDD, ese código no se había escrito: el DNI no se validaba, el correo podía repetirse y no existía cerrar sesión. El equipo decidió agregar las pruebas T76 a T80. Estas tres son de cuentas; T76 y T77 van al inicio del ciclo 9.

### T78 – Un DNI de 7 dígitos se rechaza (RE-14, límite)

- **🔴 RED** – `RegistroServiceTest#unProductorConUnDniDeSieteDigitosSeRechaza`. **Falló:** `Expecting code to raise a throwable`. Commit [`81c79b7`](https://github.com/fabio2152/qruta/commit/81c79b7) · [ciclo-8b-T78-red.txt](evidencias/ciclo-8b-T78-red.txt)
- **🟢 GREEN** – `ValidadorDeFormato.esDniValido` (8 dígitos). El servicio lo aplica cuando el documento es DNI. T22, con un DNI de 8 dígitos, sigue en verde. Commit [`aab3860`](https://github.com/fabio2152/qruta/commit/aab3860) · [ciclo-8b-T78-green.txt](evidencias/ciclo-8b-T78-green.txt)

### T79 – Un correo ya registrado se rechaza (RE-12)

- **🔴 RED** – `unRegistroConUnCorreoYaRegistradoSeRechaza`: un transportista intenta usar el correo de un productor. **Falló:** `Expecting code to raise a throwable`. Commit [`20c54c8`](https://github.com/fabio2152/qruta/commit/20c54c8) · [ciclo-8b-T79-red.txt](evidencias/ciclo-8b-T79-red.txt)
- **🟢 GREEN** – se recorre `usuarios.listarTodos()` comparando el correo sin distinguir mayúsculas. No hizo falta un método nuevo en el repositorio: con menos de 100 usuarios es suficiente (RNF-08). Commit [`91c845a`](https://github.com/fabio2152/qruta/commit/91c845a) · [ciclo-8b-T79-green.txt](evidencias/ciclo-8b-T79-green.txt)

### T80 – Al cerrar sesión el token deja de ser válido (RF-03)

- **🔴 RED** – `SesionServiceTest#alCerrarSesionElTokenDejaDeSerValido`. Esqueleto: `SesionService.cerrar` vacío. **Falló:** `Expecting code to raise a throwable`. Commit [`e6cbdac`](https://github.com/fabio2152/qruta/commit/e6cbdac) · [ciclo-8b-T80-red.txt](evidencias/ciclo-8b-T80-red.txt)
- **🟢 GREEN** – `cerrar` elimina el token del mapa de sesiones. Así `validar` responde "Debe iniciar sesión". Commit [`93c1497`](https://github.com/fabio2152/qruta/commit/93c1497) · [ciclo-8b-T80-green.txt](evidencias/ciclo-8b-T80-green.txt)

### 🔵 REFACTOR del ciclo 8b

`validar` había llegado a siete reglas seguidas. Se dividió en tres métodos con nombre y con la regla que cumplen: `validarDatosDeAcceso` (RE-12, RE-13), `validarDocumento` (RE-14) y `validarQueNoEsteRegistrado` (RE-12). Así cada regla del registro se encuentra rápido durante la defensa. Resultado: `Tests run: 35, Failures: 0, Errors: 0`. Commit [`28182da`](https://github.com/fabio2152/qruta/commit/28182da) · [ciclo-8b-refactor.txt](evidencias/ciclo-8b-refactor.txt)

---

## Ciclo 9 – Creación, publicación y aceptación

**Fecha:** 2026-10-14
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-02, RE-15, RE-18, RE-19, RE-20, RE-21, RE-22, RE-23 (y D-02, D-03, D-06)
**Tipo de casos:** Normales (T76, T33, T81, T38, T40), límite (T36) y de error (T77, T34, T35, T37, T39, T41, T42, T43)

**Motivación:** este es el flujo principal de la plataforma. El productor crea un lote con su código QR y lo publica, y un transportista libre lo toma. Hasta ahora, la única forma de asignar un transportista era la directa de la fase 1 (D-04), que se conserva.

**Pruebas agregadas:**
- **T76 y T77** se acordaron al cerrar el ciclo 8.
- **T81** apareció durante este ciclo: toda creación producía un `ProductoPecuario`, incluso para una palta, y ninguna prueba de la especificación lo detectaba.

Todas las pruebas de creación están en `CrearProductoTest` y las de publicación y aceptación en `PublicacionYAceptacionTest`. Todas usan un reloj fijo el 01/09/2026 a las 08:00. Cada RED falló por su verificación, nunca por un error de compilación: `Expecting code to raise a throwable`, `Expecting actual not to be null` o `expected: OCUPADO but was: LIBRE`. La salida completa está en `evidencias/ciclo-9-TXX-red.txt`.

| Prueba | 🔴 RED: qué faltaba | 🟢 GREEN: código mínimo |
|---|---|---|
| T76 | El transportista se creaba como `Productor` | Jerarquía del modelo: `Intermediario` → `Transportista` (nace LIBRE) y `PuntoDeRecepcion` → `Mercado`, `CentroDeAcopio`. `registrar` elige la clase con un `switch` sobre `TipoCuenta`. La regla de T31 pasa de `instanceof Mercado` a `instanceof PuntoDeRecepcion`, para incluir al centro de acopio (D-03) |
| T77 | Cualquier usuario aprobaba a un productor o transportista | `aprobar` exige rol REGULADOR para las cuentas que no son puntos de recepción |
| T33 | `crearProducto(DatosProducto, Usuario)` devolvía `null` | `Producto` gana código, tipo, lote, fecha de producción y `publicado`, más un constructor para el flujo nuevo, sin transportista. Nace `ProductoPecuario` y el código es un UUID corto de 8 caracteres |
| T34 | Cualquier rol creaba productos | Si el rol no es PRODUCTOR, se lanza `OperacionNoAutorizadaException` |
| T35 | Se aceptaba una fecha futura | Se compara con `LocalDateTime.now(reloj)` |
| T36 | Se aceptaba un pollo de 168 h | `TipoProducto` conoce su vida útil base (168, 504 y 336 h). Si `horas ≥ vida útil`, se rechaza |
| T37 | Un agrícola creaba pollo | `TipoProducto` conoce su `CategoriaProducto`, y se compara con el tipo del productor |
| T81 | La palta salía como `ProductoPecuario` | `ProductoAgro` (variedad, maduración, calidad y certificado). Se elige la subclase según la categoría |
| T38 | `listarPublicaciones` devolvía una lista vacía | `Producto.publicar()` y un filtro por `isPublicado` |
| T39 | Cualquier productor publicaba | Se compara con `producto.getProductor()` |
| T40 | `aceptar` no hacía nada | `TransporteService.aceptar` asigna, ocupa y guarda el producto y el transportista. Las publicaciones excluyen los productos con transportista. **`esVisiblePara` compara `usuario.equals(intermediarioAsignado)`**: con productos sin transportista, la forma anterior fallaba por un valor vacío. La prueba lo cubre porque lista los productos del transportista mientras hay otro producto sin asignar |
| T41 | Un transportista OCUPADO aceptaba | Se rechaza si el estado de carga es OCUPADO |
| T42 | Un segundo transportista aceptaba | `ProductoNoDisponibleException` (409) si el producto ya tiene transportista |
| T43 | Se retiraba una publicación aceptada | `retirarPublicacion` la rechaza si el producto tiene transportista; si no, desmarca `publicado` |

Commits RED → GREEN:

| Prueba | Commits | Prueba | Commits |
|---|---|---|---|
| T76 | [`bc3a8dc`](https://github.com/fabio2152/qruta/commit/bc3a8dc) → [`70c0a99`](https://github.com/fabio2152/qruta/commit/70c0a99) | T81 | [`7ff5696`](https://github.com/fabio2152/qruta/commit/7ff5696) → [`3669215`](https://github.com/fabio2152/qruta/commit/3669215) |
| T77 | [`10d9ff7`](https://github.com/fabio2152/qruta/commit/10d9ff7) → [`fd3d3a1`](https://github.com/fabio2152/qruta/commit/fd3d3a1) | T38 | [`857c9b1`](https://github.com/fabio2152/qruta/commit/857c9b1) → [`4f2157d`](https://github.com/fabio2152/qruta/commit/4f2157d) |
| T33 | [`b6ef621`](https://github.com/fabio2152/qruta/commit/b6ef621) → [`e29923f`](https://github.com/fabio2152/qruta/commit/e29923f) | T39 | [`389c5e3`](https://github.com/fabio2152/qruta/commit/389c5e3) → [`2c4821b`](https://github.com/fabio2152/qruta/commit/2c4821b) |
| T34 | [`59fcd7b`](https://github.com/fabio2152/qruta/commit/59fcd7b) → [`f7bd26f`](https://github.com/fabio2152/qruta/commit/f7bd26f) | T40 | [`df24077`](https://github.com/fabio2152/qruta/commit/df24077) → [`08ab141`](https://github.com/fabio2152/qruta/commit/08ab141) |
| T35 | [`c966b9d`](https://github.com/fabio2152/qruta/commit/c966b9d) → [`ca4cdbe`](https://github.com/fabio2152/qruta/commit/ca4cdbe) | T41 | [`aee46eb`](https://github.com/fabio2152/qruta/commit/aee46eb) → [`a023c23`](https://github.com/fabio2152/qruta/commit/a023c23) |
| T36 | [`d59d9c5`](https://github.com/fabio2152/qruta/commit/d59d9c5) → [`434046f`](https://github.com/fabio2152/qruta/commit/434046f) | T42 | [`25a7652`](https://github.com/fabio2152/qruta/commit/25a7652) → [`19c4432`](https://github.com/fabio2152/qruta/commit/19c4432) |
| T37 | [`d2ee1cd`](https://github.com/fabio2152/qruta/commit/d2ee1cd) → [`3d9dcb7`](https://github.com/fabio2152/qruta/commit/3d9dcb7) | T43 | [`5d7f2bc`](https://github.com/fabio2152/qruta/commit/5d7f2bc) → [`074fde9`](https://github.com/fabio2152/qruta/commit/074fde9) |

### 🔵 REFACTOR del ciclo 9

**Qué se mejoró y por qué:** la especificación pide que el servicio solo coordine. Las reglas se movieron al objeto que las conoce:

- `Producto.publicar(quienPublica)` valida que sea su productor (RE-20).
- `Producto.tieneTransportista()` y `estaDisponibleParaTransporte()` reemplazan las comparaciones con `null` repartidas en dos servicios.
- `Transportista.validarQueEstaLibre()` y `Transportista.cargar(producto)` (RE-22): el transportista se asigna y se ocupa en un solo paso, sin poder hacer uno sin el otro.
- `TipoProductor.puedeProducir(categoria)` (RE-19) reemplaza una condición de tres partes.
- `crearProducto` queda en "validar → elegir subclase → guardar", con `validarNuevoProducto` aparte.

**Código antes** (`TransporteService.aceptar`):

```java
if (transportista.getEstadoCarga() == EstadoCarga.OCUPADO) { throw ... }
if (producto.getIntermediarioAsignado() != null) { throw ... }
producto.asignarIntermediario(transportista);
transportista.ocupar();
```

**Código después:**

```java
transportista.validarQueEstaLibre();
if (producto.tieneTransportista()) { throw new ProductoNoDisponibleException(...); }
transportista.cargar(producto);
```

- Resultado: `Tests run: 49, Failures: 0, Errors: 0`. Commit [`3114843`](https://github.com/fabio2152/qruta/commit/3114843) · [ciclo-9-refactor.txt](evidencias/ciclo-9-refactor.txt)

### Decisiones de diseño del ciclo 9

- **`DatosProducto` vive en el dominio** y no en el servicio, porque los constructores de `ProductoPecuario` y `ProductoAgro` lo reciben. Así el dominio no depende de la capa de servicio.
- **Las dos formas de asignar un transportista conviven (D-04).** El constructor de la fase 1 sigue intacto para T01–T21, y el flujo nuevo es publicar y luego aceptar.
- **Pendiente para la capa web:** el control de concurrencia con `@Version` (D-13) para dos aceptaciones simultáneas. La regla de negocio ya está probada en T42.
- **Cobertura del dominio y los servicios:** 481 de 510 líneas (94,3 %).

---

## Ciclo 9b – Certificados y calidad: pruebas agregadas

**Fecha:** 2026-10-15
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-19
**Tipo de casos:** Error (T82, T84) y límite (T83)

**Motivación (pruebas agregadas):** RE-19 exige certificado SENASA para los pecuarios, y certificado fitosanitario y calidad de 1 a 3 para los agrícolas. Ninguna prueba de la especificación lo cubría, así que un producto podía crearse sin certificado. El equipo aprobó T82 y T83. T84 se agregó para cubrir la tercera parte de la regla, el certificado fitosanitario.

| Prueba | 🔴 RED (`CrearProductoTest`) | 🟢 GREEN |
|---|---|---|
| T82 | Pollo con certificado vacío. **Falló:** `Expecting code to raise a throwable` · [red](evidencias/ciclo-9b-T82-red.txt) | Si la categoría es PECUARIO y el certificado SENASA está vacío, se lanza `DatoInvalidoException` · [green](evidencias/ciclo-9b-T82-green.txt) |
| T83 | Palta con calidad 4. **Falló:** `Expecting code to raise a throwable` · [red](evidencias/ciclo-9b-T83-red.txt) | Si la categoría es AGRICOLA, la calidad debe estar entre 1 y 3. T81 (calidad 1) sigue en verde · [green](evidencias/ciclo-9b-T83-green.txt) |
| T84 | Palta sin certificado fitosanitario. **Falló:** `Expecting code to raise a throwable` · [red](evidencias/ciclo-9b-T84-red.txt) | Si la categoría es AGRICOLA y el certificado fitosanitario está vacío, se rechaza · [green](evidencias/ciclo-9b-T84-green.txt) |

### 🔵 REFACTOR del ciclo 9b

`validarNuevoProducto` había llegado a siete reglas. Quedó en tres métodos, cada uno con la regla que cumple:
- `validarQuienCrea` (RE-02 y la categoría del productor);
- `validarFechaYVidaUtil` (RE-19);
- `validarCertificados` (RE-19).

Resultado: `Tests run: 52, Failures: 0, Errors: 0`. Commit [`2f32ec0`](https://github.com/fabio2152/qruta/commit/2f32ec0) · [ciclo-9b-refactor.txt](evidencias/ciclo-9b-refactor.txt)

---

## Ciclo 10 – Destino y recepción

**Fecha:** 2026-10-15
**Responsables:** Fabio Ezequiel Malpartida Lema, Piero Anghelo Pittman Tolentino
**Reglas que se abordan:** RE-10, RE-24, RE-25, RE-26, RE-27, RE-28
**Tipo de casos:** Normales (T44, T47, T52, T53) y de error (T45, T46, T48, T49, T50, T51)

**Motivación:** el transportista elige a qué mercado o centro de acopio lleva el producto, y ese destino deja constancia de cómo llegó (temperatura y humedad). Sin esa medición no hay evaluación (ciclo 11).

Las pruebas están en `DestinoYRecepcionTest`. Usan `RelojAjustable`, un `Clock` de prueba compartido, para que el transporte vaya de 08:00 a 14:00 del 01/09/2026 y la entrega sea a las 16:00.

| Prueba | 🔴 RED: qué faltaba | 🟢 GREEN: código mínimo |
|---|---|---|
| T44 | `getDestino()` devolvía `null` (`expected: Mercado but was: null`) | `Producto.asignarDestino`. `TransporteService.elegirDestino` busca al usuario y lo asigna |
| T45 | Lanzaba `ClassCastException` en vez de un rechazo claro | Si el destino no es `PuntoDeRecepcion`, se lanza `DatoInvalidoException` |
| T46 | Se aceptaba un mercado que no necesitaba palta | `PuntoDeRecepcion` guarda sus necesidades (`EnumSet<TipoProducto>`) y responde `necesita(tipo)` |
| T47 | `getMediciones()` estaba vacío | `Medicion(fechaHora, temperatura, humedad, registradaPor)` con `BigDecimal`. `RecepcionService` la registra con `LocalDateTime.now(reloj)` |
| T48 | Cualquier mercado registraba | Se compara con `producto.getDestino()` |
| T49 | Se registraba estando EN_TRANSITO | Se exige el estado EN_DESTINO |
| T50 | Se aceptaban 85 °C | Rango del instrumento de -40 °C a 60 °C |
| T51 | Se pasaba a ENTREGADO sin recepción | En `Producto.cambiarEstado`: si hay destino y no hay medición, se rechaza. T05 sigue en verde porque ese producto no tiene destino (D-04) |
| T52 | `getHoraEntrega()` devolvía `null` | `registrarHora` guarda `horaEntrega` y llama a `Transportista.liberar()` |
| T53 | El mercado no veía nada | `esVisiblePara`: un INTERMEDIARIO ve lo que tiene asignado **o** lo que tiene como destino |

Las evidencias de cada fase están en `evidencias/ciclo-10-TXX-{red,green}.txt`.

### 🔵 REFACTOR del ciclo 10

La especificación sugería separar la recepción en `RecepcionService`. Eso ya se hizo desde T47, así que el refactor llevó las reglas al dominio:
- **`Medicion` valida el rango del instrumento** en su constructor compacto, con constantes con nombre. No puede existir una medición imposible.
- **`Producto.registrarRecepcion(medicion)`** valida que la registre el destino y que el producto esté EN_DESTINO. `RecepcionService` queda en tres pasos: buscar, crear la medición y registrar.
- `TransporteService` tenía la búsqueda del producto repetida; queda en `buscarProducto`. Un destino inexistente ahora responde con un mensaje claro.
- Se corrigió una errata en el nombre del método de prueba de T47.

Resultado: `Tests run: 62, Failures: 0, Errors: 0`. Commit [`73849a7`](https://github.com/fabio2152/qruta/commit/73849a7) · [ciclo-10-refactor.txt](evidencias/ciclo-10-refactor.txt)

### Decisiones de diseño del ciclo 10

- **Temperaturas con `BigDecimal`**, para que 7,1 − 4 sea exactamente 3,1 al comparar en el ciclo 11.
- **Quién pasa a ENTREGADO:** el transportista, con `cambiarEstado` (decisión A del 14/10). RE-27 lo condiciona a que el destino haya registrado la recepción.
- **Reglas de RE-24 a RE-26 sin prueba en la especificación:** que solo el transportista asignado elija el destino, que el destino esté ACTIVO, la humedad entre 0 y 100 % y una sola recepción. Quedan anotadas como pendientes.

---

## Resultados

**Ejecución final (`./mvnw test`):** `Tests run: 21, Failures: 0, Errors: 0, Skipped: 0` – BUILD SUCCESS (10 pruebas en `ProductoTest`, 9 en `ProductoServiceTest` y 2 en `LoginServiceTest`).

**Cobertura JaCoCo de las clases de dominio y servicio:**

| Paquete | Clase | Líneas cubiertas | Ramas cubiertas |
|---|---|---|---|
| `dominio` | `Producto` | 50 / 53 | 17 / 17 |
| `dominio` | `EstadoProducto` | 12 / 12 | 8 / 8 |
| `dominio` | `Usuario` | 12 / 14 | 3 / 4 |
| `dominio` | `Rol`, `CambioEstado` y las 3 excepciones | 11 / 11 | – |
| `servicio` | `ProductoService` | 24 / 24 | – |
| `servicio` | `LoginService` | 8 / 8 | – |
| `servicio` | `ProductoNoEncontradoException`, `CredencialesInvalidasException` | 4 / 4 | – |
| **Total** | | **121 / 126 (96 %)** | **28 / 29 (97 %)** |

Sin cubrir: en `Producto`, los *getters* `getNombre()`, `getProductor()` y `getIntermediarioAsignado()`, que solo se usan para mostrar datos y guardarlos en la base; en `Usuario`, `getContrasena()` y `hashCode()`, que usa solo la capa de base de datos, y la rama de `equals` que compara un usuario con un objeto de otro tipo. Todas las decisiones de negocio (etapas, permisos, visibilidad, duración) tienen su rama cubierta por una prueba que antes falló.

**Historial de commits del proceso TDD:** 21 commits `test` (RED), 21 commits `feat(ciclo-N)` (GREEN) y 8 commits `refactor(ciclo-N)` (7 ciclos; el ciclo 6 tiene dos), verificables con:

```bash
git log --oneline --reverse
```

---

<!--
  Plantilla de ciclo: copiar desde "## Ciclo N" hasta la línea "---" final
  por cada nuevo ciclo. Repetir el bloque "### TXX" por cada prueba del ciclo.
-->

## Ciclo N – <tema del ciclo>

**Fecha:** AAAA-MM-DD
**Responsables:**
**Reglas que se abordan:**
**Tipo de casos:** Normal / Límite / Error

### TXX – <comportamiento> (RE-XX)

```text
Dado ...
Cuando ...
Entonces ...
```

**🔴 RED** – `ClaseTest#metodo`

- Esqueletos agregados:
- **Qué falló:**

```text
// Salida de ./mvnw test
```

- Commit: `test(ciclo-N): TXX ... [RED]` (`xxxxxxx`)
- Evidencia: `evidencias/ciclo-N-TXX-red.txt`

**🟢 GREEN**

- **Código mínimo:**

```java
```

- Resultado:
- Commit: `feat(ciclo-N): TXX ... [GREEN]` (`xxxxxxx`)
- Evidencia: `evidencias/ciclo-N-TXX-green.txt`

### 🔵 REFACTOR del ciclo N

**Qué se mejoró y por qué:**

**Código antes:**

```java
```

**Código después:**

```java
```

- Resultado:
- Commit: `refactor(ciclo-N): ... [REFACTOR]` (`xxxxxxx`)

### Decisiones de diseño y aprendizajes del ciclo N

-

---
