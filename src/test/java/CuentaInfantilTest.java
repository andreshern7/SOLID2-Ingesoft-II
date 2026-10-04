import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CuentaInfantilTest {
    @Test
    void siYaRetiro150000HoyUnRetiroDe60000SeRechazaYElSaldoNoCambia() {
        CuentaInfantil cuenta = new CuentaInfantil("INF-1", "Sofi", 500_000);
        cuenta.retirar(150_000);

        assertThrows(IllegalStateException.class, () -> cuenta.retirar(60_000));

        assertEquals(350_000, cuenta.getSaldo());
    }

    @Test
    void sePuedeUsarComoOrigenDeUnaTransferencia() {
        CalculadoraComision calculadora =
            new CalculadoraComision(Map.of("MISMO_BANCO", new ComisionMismoBanco()));
        TransaccionService servicio = new TransaccionService(
            new ValidadorMonto(), calculadora, new RepositorioEnMemoria(), new GeneradorComprobante(),
            new NotificadorTransferencia(new CanalFalso()), new RegistroAuditoria());
        Cuenta infantil = new CuentaInfantil("INF-1", "Sofi", 500_000);
        Cuenta ana = new CuentaAhorros("001-1", "Ana", 0);

        servicio.transferir(infantil, ana, 100_000, "MISMO_BANCO");

        assertEquals(400_000, infantil.getSaldo());
        assertEquals(100_000, ana.getSaldo());
    }

    @Test
    void seLeCobraLaCuotaDeManejoComoACualquierCuenta() {
        Cuenta infantil = new CuentaInfantil("INF-1", "Sofi", 500_000);

        new CobroCuotaManejo().cobrarMensual(List.of(infantil));

        assertEquals(500_000 - 12_900, infantil.getSaldo());
    }
}
