# Laboratorio L2: SOLID — Backend de Banco Andino

Ingeniería de Software II · Universidad Nacional de Colombia · Sede Bogotá · 2026

**Integrantes:**
- Jorge Andrés Hernández Garcia
- Juan Camilo Vergara Teo

**Lenguaje elegido:** Java 21

## Cómo ejecutar

Con Maven:

```bash
mvn compile exec:java
```

Sin Maven (solo JDK):

```bash
javac -encoding UTF-8 -d out src/main/java/*.java
java -Dstdout.encoding=UTF-8 -cp out Main
```

## Bloque 0 — Arranque

Se copió el código base entregado por el docente (11 archivos) sin modificar su diseño.
La salida del programa principal quedó guardada en `salida_original.txt` y servirá como
prueba de caracterización durante la refactorización.

## Bloque 1 — Diagnóstico

### 1.1 Tabla de hallazgos

| Clase / método | Letra | Evidencia en el código | Consecuencia para el banco o el cliente |
|---|---|---|---|
| `TransaccionService.transferir` | S | El método valida montos, calcula la comisión, mueve el dinero, guarda en Oracle, imprime el comprobante, envía el SMS y escribe la auditoría. Son siete tareas distintas en 36 líneas. | Si el área legal pide cambiar el texto del comprobante, hay que editar el mismo método que descuenta el dinero. Un error al tocar el formato puede terminar cobrando mal una transferencia, y cualquier cambio obliga a volver a probar todo el flujo. |
| `TransaccionService.transferir` (comprobante y auditoría) | S | El comprobante y la auditoría se arman con `System.out.println` directamente dentro del servicio, mezclados con la lógica del dinero. | No se puede reutilizar el comprobante para otros movimientos (por ejemplo, un pago) sin copiar y pegar esas líneas. Si mañana la auditoría debe ir a un archivo o a otro sistema, se toca de nuevo la clase más crítica. |
| `TransaccionService.transferir` (`switch (tipo)`) | O | La comisión se decide con un `switch` sobre un `String`. Agregar un tipo de transferencia exige editar ese `switch`. | Cada producto nuevo del área de negocio (por ejemplo, transferencias por llave) obliga a modificar código que ya funciona y que cobra a todos los clientes. Además, un tipo mal escrito (`"OTRO_BANCo"`) no lo detecta el compilador: el cliente solo ve el error cuando ya intentó transferir. |
| `TransaccionService.transferir` (notificación) | O | El único canal de aviso es `sms.enviar(...)`, escrito dentro del método. | Si el banco quiere avisar también por push o por correo, hay que abrir y modificar el método de transferencias. Lo mismo pasa con cualquier sistema que deba enterarse de cada transacción (por ejemplo, un control antifraude). |
| `TransaccionService` (atributo `repositorio`) | D | `private final OracleRepositorio repositorio = new OracleRepositorio();` La lógica de negocio depende directamente de la clase concreta de Oracle. | Cambiar de motor de base de datos (por ejemplo, dejar de pagar la licencia de Oracle) obliga a editar el servicio de transferencias. Además, cualquier prueba del servicio se conecta a la base de datos de producción. |
| `TransaccionService` (atributo `sms`) | D | `private final SmsGateway sms = new SmsGateway();` El servicio crea su propio proveedor de mensajería y no hay forma de reemplazarlo desde afuera. | Probar una transferencia le envía un SMS real a un cliente. Cambiar de proveedor de mensajería obliga a tocar la clase que mueve el dinero. |
| _Pendiente (compañero)_ | L | | |
| _Pendiente (compañero)_ | I | | |

### 1.2 Experimentos

**Experimento 1 — El CDT.** 

**Experimento 2 — La prueba imposible.**

Intentamos verificar que una transferencia a otro banco cobra $7.500 de comisión sin
conectarse a Oracle ni enviar un SMS. Este fue el intento:

```java
Cuenta origen = new CuentaAhorros("T-1", "Prueba", 1_000_000);
Cuenta destino = new CuentaAhorros("T-2", "Prueba2", 0);
new TransaccionService().transferir(origen, destino, 100_000, "OTRO_BANCO");
double esperado = 1_000_000 - 100_000 - 7_500;
// verificar que origen.getSaldo() == esperado
```

**Resultado:** no se logró cumplir la condición. La verificación del saldo sí pasa, pero
al ejecutarla la consola muestra `[ORACLE] INSERT INTO transacciones ...` y
`[SMS] Para Prueba: Transferiste ...`. Es decir, la "prueba" escribió en la base de datos
de producción y le mandó un mensaje a un cliente.

**Qué lo impide:** `TransaccionService` crea `OracleRepositorio` y `SmsGateway` con `new`
en sus propios atributos, que además son `private final`. No hay constructor ni método que
permita pasarle otra implementación (por ejemplo, un repositorio en memoria o un
notificador falso). Tampoco existen interfaces para esas dependencias, así que aunque
quisiéramos, no hay un "tipo" con el que crear un reemplazo. La única forma de probar la
comisión es ejecutar todo el flujo real.

### 1.3 Medición "antes"

| Métrica | Antes |
|---|---|
| Líneas del método `transferir` | 36 (de la firma a la llave de cierre, según la numeración del código entregado) |
| Número de razones distintas por las que `TransaccionService` podría cambiar | 7: validación de montos, reglas de comisión, movimiento del dinero, persistencia, formato del comprobante, notificación al cliente y auditoría |
| Clases concretas que `TransaccionService` crea con `new` | 2: `OracleRepositorio` y `SmsGateway` (sin contar las excepciones) |
| Métodos vacíos o que lanzan excepción por "no aplica" | 4: `TarjetaCredito.depositar`, `CreditoVivienda.depositar` y `CreditoVivienda.retirar` (vacíos), y `CDT.retirar` (lanza `UnsupportedOperationException`) |
| ¿Se puede probar `transferir` sin Oracle ni SMS? | No |

### 1.4 Diagrama de clases del código original



## Bloque 2 — Refactorización

### Punto de control S

Se separaron las responsabilidades de `TransaccionService.transferir` en clases propias:
`ValidadorMonto`, `CalculadoraComision`, `GeneradorComprobante`, `NotificadorTransferencia`
y `RegistroAuditoria`. La persistencia ya estaba aislada en `OracleRepositorio`.

**¿Qué hace `TransaccionService`, en una frase?** Coordina los pasos de una transferencia.
Ya no aparece la "y": validar, calcular, guardar, imprimir, notificar y auditar los hace
cada clase; el servicio solo decide el orden y mueve el dinero entre las cuentas.

**Si el área legal pide cambiar el formato del comprobante, ¿qué archivo se toca?**
Solo `GeneradorComprobante.java`. El método que mueve el dinero no se abre.

**Nota:** el servicio todavía crea sus dependencias con `new`. Eso se corrige en el
punto de control D; en este punto solo se separaron responsabilidades.