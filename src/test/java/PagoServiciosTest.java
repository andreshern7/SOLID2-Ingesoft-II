import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PagoServiciosTest {
    private RepositorioEnMemoria repositorio;
    private CanalFalso canal;
    private RegistroFalso registro;
    private TransaccionService servicio;
    private CuentaAhorros cuenta;

    @BeforeEach
    void armarServicioConDobles() {
        repositorio = new RepositorioEnMemoria();
        canal = new CanalFalso();
        registro = new RegistroFalso();
        CalculadoraComision calculadora = new CalculadoraComision(
            Map.of("PAGO_SERVICIO", new ComisionPagoServicios()));
        servicio = new TransaccionService(
            new ValidadorMonto(), calculadora, repositorio, new GeneradorComprobante(),
            new NotificadorTransferencia(canal), registro);
        cuenta = new CuentaAhorros("001-1", "Ana", 1_000_000);
    }

    @Test
    void pagoDescuentaMontoMasComisionYGuardaLaReferencia() {
        servicio.pagarServicio(cuenta, "FACTURA-123", 184_300);

        assertEquals(1_000_000 - 184_300 - 1_500, cuenta.getSaldo());
        assertEquals(1, repositorio.guardadas.size());
        assertEquals("001-1 -> FACTURA-123 $184300.0 comision $1500.0",
            repositorio.guardadas.get(0));
        assertEquals(1, canal.mensajes.size());
        assertEquals(1, registro.registros.size());
    }

    @Test
    void montoInvalidoNoGeneraEfectos() {
        assertThrows(IllegalArgumentException.class,
            () -> servicio.pagarServicio(cuenta, "FACTURA-123", 0));

        assertEquals(1_000_000, cuenta.getSaldo());
        assertEquals(0, repositorio.guardadas.size());
        assertEquals(0, canal.mensajes.size());
        assertEquals(0, registro.registros.size());
    }

    @Test
    void cdtNoPuedePagarServicios() {
        CDT cdt = new CDT("CDT-1", "Ana", 1_000_000, LocalDate.now().plusMonths(1));

        assertThrows(IllegalArgumentException.class,
            () -> servicio.pagarServicio(cdt, "FACTURA-123", 100_000));
        assertEquals(1_000_000, cdt.getSaldo());
    }
}
