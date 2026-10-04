import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistroTransaccionTest {
    private RegistroFalso auditoria;
    private RegistroFalso antifraude;
    private TransaccionService servicio;

    @BeforeEach
    void armarServicioConDosRegistros() {
        auditoria = new RegistroFalso();
        antifraude = new RegistroFalso();
        CalculadoraComision calculadora =
            new CalculadoraComision(Map.of("MISMO_BANCO", new ComisionMismoBanco()));
        servicio = new TransaccionService(
            new ValidadorMonto(), calculadora, new RepositorioEnMemoria(), new GeneradorComprobante(),
            new NotificadorTransferencia(new CanalFalso()),
            new RegistroMultiple(List.of(auditoria, antifraude)));
    }

    @Test
    void transferenciaExitosaLlegaUnaVezAAuditoriaYUnaVezAAntifraude() {
        Cuenta ana = new CuentaAhorros("001-1", "Ana", 1_000_000);
        Cuenta luis = new CuentaAhorros("001-2", "Luis", 0);

        servicio.transferir(ana, luis, 100_000, "MISMO_BANCO");

        assertEquals(1, auditoria.registros.size());
        assertEquals(1, antifraude.registros.size());
    }

    @Test
    void transferenciaRechazadaNoLlegaANinguno() {
        Cuenta sinSaldo = new CuentaAhorros("001-3", "Luis", 0);
        Cuenta ana = new CuentaAhorros("001-1", "Ana", 1_000_000);

        assertThrows(IllegalStateException.class,
            () -> servicio.transferir(sinSaldo, ana, 50_000, "MISMO_BANCO"));

        assertEquals(0, auditoria.registros.size());
        assertEquals(0, antifraude.registros.size());
    }
}
