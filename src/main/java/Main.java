import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Cuenta ana = new CuentaAhorros("001-1", "Ana", 2_000_000);
        Cuenta luis = new CuentaAhorros("001-2", "Luis", 500_000);
        CDT cdtAna = new CDT("CDT-9", "Ana", 10_000_000, LocalDate.now().plusMonths(6));

        CalculadoraComision calculadora = new CalculadoraComision(Map.of(
            "MISMO_BANCO", new ComisionMismoBanco(),
            "OTRO_BANCO", new ComisionOtroBanco(),
            "INTERNACIONAL", new ComisionInternacional(),
            "LLAVE", new ComisionLlave(),
            "PAGO_SERVICIO", new ComisionPagoServicios()));

        RepositorioTransacciones repositorio = new PostgresRepositorio();
        CanalNotificacion canal = new CanalMultiple(List.of(new SmsGateway(), new PushGateway()));

        RegistroTransaccion registro =
            new RegistroMultiple(List.of(new RegistroAuditoria(), new SistemaAntifraude()));

        TransaccionService servicio = new TransaccionService(
            new ValidadorMonto(), calculadora, repositorio, new GeneradorComprobante(),
            new NotificadorTransferencia(canal), registro);
        servicio.transferir(ana, luis, 150_000, "OTRO_BANCO");
        servicio.pagarServicio(ana, "AGUA-2026-001", 184_300);

        new CobroCuotaManejo().cobrarMensual(List.of(ana, luis));

        List<ProductoBancario> productos =
            List.of(new TarjetaCredito(3_000_000), new CreditoVivienda(120_000_000));
        new GeneradorExtractos().imprimir(productos);
    }
}
