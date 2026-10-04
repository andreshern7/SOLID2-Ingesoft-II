import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class TransferenciaLlaveTest {
    @Test
    void transferenciaPorLlaveDe50000DescuentaExactamente50000() {
        CalculadoraComision calculadora = new CalculadoraComision(Map.of("LLAVE", new ComisionLlave()));
        TransaccionService servicio = new TransaccionService(
            new ValidadorMonto(), calculadora, new RepositorioEnMemoria(), new GeneradorComprobante(),
            new NotificadorTransferencia(new CanalFalso()), new RegistroAuditoria());
        Cuenta ana = new CuentaAhorros("001-1", "Ana", 1_000_000);
        Cuenta luis = new CuentaAhorros("001-2", "Luis", 0);

        servicio.transferir(ana, luis, 50_000, "LLAVE");

        assertEquals(950_000, ana.getSaldo());
        assertEquals(50_000, luis.getSaldo());
    }
}
