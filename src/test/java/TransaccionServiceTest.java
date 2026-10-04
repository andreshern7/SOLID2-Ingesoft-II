import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransaccionServiceTest {
    private RepositorioEnMemoria repositorio;
    private CanalFalso canal;
    private TransaccionService servicio;
    private Cuenta ana;
    private Cuenta luis;

    @BeforeEach
    void armarServicioConDobles() {
        repositorio = new RepositorioEnMemoria();
        canal = new CanalFalso();
        CalculadoraComision calculadora = new CalculadoraComision(Map.of(
            "MISMO_BANCO", new ComisionMismoBanco(),
            "OTRO_BANCO", new ComisionOtroBanco()));
        servicio = new TransaccionService(
            new ValidadorMonto(), calculadora, repositorio, new GeneradorComprobante(),
            new NotificadorTransferencia(canal), new RegistroAuditoria());
        ana = new CuentaAhorros("001-1", "Ana", 1_000_000);
        luis = new CuentaAhorros("001-2", "Luis", 0);
    }

    @Test
    void transferenciaAlMismoBancoNoCobraComisionYMueveElMontoExacto() {
        servicio.transferir(ana, luis, 100_000, "MISMO_BANCO");

        assertEquals(900_000, ana.getSaldo());
        assertEquals(100_000, luis.getSaldo());
    }

    @Test
    void transferenciaAOtroBancoCobra7500YDescuentaMontoMasComision() {
        servicio.transferir(ana, luis, 100_000, "OTRO_BANCO");

        assertEquals(1_000_000 - 100_000 - 7_500, ana.getSaldo());
        assertEquals(100_000, luis.getSaldo());
    }

    @Test
    void saldoInsuficienteRechazaLaTransferenciaYNoGuardaNiNotifica() {
        assertThrows(IllegalStateException.class,
            () -> servicio.transferir(luis, ana, 50_000, "MISMO_BANCO"));

        assertEquals(0, luis.getSaldo());
        assertEquals(1_000_000, ana.getSaldo());
        assertEquals(0, repositorio.guardadas.size());
        assertEquals(0, canal.mensajes.size());
    }

    @Test
    void transferenciaExitosaSeGuardaUnaVezYGeneraUnaSolaNotificacion() {
        servicio.transferir(ana, luis, 100_000, "OTRO_BANCO");

        assertEquals(1, repositorio.guardadas.size());
        assertEquals(1, canal.mensajes.size());
    }

    @Test
    void tipoDesconocidoSeRechazaYElSaldoDeOrigenNoCambia() {
        assertThrows(IllegalArgumentException.class,
            () -> servicio.transferir(ana, luis, 100_000, "TIPO_QUE_NO_EXISTE"));

        assertEquals(1_000_000, ana.getSaldo());
        assertEquals(0, repositorio.guardadas.size());
        assertEquals(0, canal.mensajes.size());
    }
}
