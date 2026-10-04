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
            "INTERNACIONAL", new ComisionInternacional()));

        TransaccionService servicio = new TransaccionService(calculadora);
        servicio.transferir(ana, luis, 150_000, "OTRO_BANCO");

        new CobroCuotaManejo().cobrarMensual(List.of(ana, luis));

        List<ProductoBancario> productos =
            List.of(new TarjetaCredito(3_000_000), new CreditoVivienda(120_000_000));
        for (ProductoBancario p : productos) System.out.println(p.generarExtracto());
    }
}
