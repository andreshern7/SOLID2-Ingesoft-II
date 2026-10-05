# Revisión cruzada: R6, pago de servicios públicos

Revisaron: Manuel Federico Castro Suarez y Andrés Felipe Arias González

## Lista de revisión

| Lista de revisión | Sí | No |
|---|:---:|:---:|
| Entendimos qué hace cada clase leyendo solo su nombre y sus métodos públicos. | X | |
| Pudimos reutilizar piezas existentes sin copiar y pegar código. | X | |
| Implementamos el requerimiento sin modificar la lógica de clases existentes. | | X |
| No encontramos métodos vacíos ni que lancen “no aplica”. | X | |
| No encontramos `if`/`switch` por tipo que tuvimos que extender. | X | |
| Las pruebas existentes siguieron pasando después de nuestro cambio. | | X |
| No encontramos abstracciones innecesarias (interfaces que no aportan). | X | |

La tercera respuesta es **No** porque fue necesario modificar
`TransaccionService` para extraer `completarTransaccion` y ampliar
`NotificadorTransferencia` con el caso en que el destino es una referencia de
factura. Son cambios acotados y conservan el comportamiento de las
transferencias, pero sí modifican clases que ya existían.

La sexta respuesta queda en **No** únicamente porque no pudimos ejecutar
`mvn test` en el entorno de revisión: Maven no estaba instalado. La suite
incluye pruebas para transferencias, cuentas, canales, repositorio y el nuevo
flujo de pago, pero no afirmamos que pasaron sin poder ejecutar el comando.

## Lo mejor del diseño

La estructura de `TransaccionService` permitió ubicar rápidamente las piezas
que debía reutilizar el pago de servicios:

- `ValidadorMonto` conserva las mismas validaciones que usan las
  transferencias.
- `CalculadoraComision` permite registrar
  `ComisionPagoServicios` mediante el mapa de reglas, sin agregar un
  `if` o un `switch` por tipo.
- `RepositorioTransacciones`, `GeneradorComprobante`,
  `NotificadorTransferencia` y `RegistroTransaccion` ya estaban abstraídos
  mediante dependencias inyectadas.
- El método privado `completarTransaccion` concentra el guardado, el
  comprobante, la notificación y la auditoría. Así el nuevo pago reutiliza el
  mismo flujo y no duplica la lógica de `transferir`.

También fue claro comprobar el criterio de aceptación: un pago de `$184.300`
calcula una comisión fija de `$1.500`, descuenta `$185.800` y usa la
referencia de factura como destino en el repositorio, comprobante, notificación
y auditoría. El registro compuesto existente permite que el pago siga pasando
por auditoría y antifraude.

## Lo que nos costó entender o extender

- El nombre `CalculadoraComision.calcular` y el mensaje de error
  “Tipo de transferencia desconocido” siguen hablando de transferencias,
  aunque la calculadora ahora también maneja pagos de servicios. No afecta el
  caso válido, pero puede confundir al diagnosticar un tipo de pago inválido.
- La restricción del CDT se resuelve en la sobrecarga
  `pagarServicio(CuentaBase, ...)` mediante `instanceof`. Funciona y evita que
  el saldo del CDT cambie, pero hace que el servicio conozca la jerarquía
  concreta de cuentas. Una abstracción de capacidades, por ejemplo una
  operación de retiro disponible solo para cuentas retirables, podría eliminar
  esa comprobación explícita.
- `NotificadorTransferencia` conserva su nombre aunque ahora también notifica
  pagos de servicios. Además, el mensaje reutilizado dice “Transferiste” para
  una factura. Sería más expresivo separar la responsabilidad de notificación
  o generalizar el nombre y el mensaje para cualquier transacción.
- `Main` crea el `PostgresRepositorio`, los canales de notificación y los
  registros concretos directamente. Para probar el nuevo servicio tuvimos que
  construir manualmente dobles de prueba, aunque el constructor de
  `TransaccionService` sí permite inyectarlos.

En conjunto, el diseño soportó bien el requerimiento: solo se agregó la regla
de comisión, se extendió la notificación y se reutilizó el flujo común. Las
observaciones anteriores son oportunidades de mejora para que el siguiente
requerimiento no tenga que distinguir entre transferencias y otros tipos de
transacción.
