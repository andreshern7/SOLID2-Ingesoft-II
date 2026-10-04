import java.time.LocalDate;

public class CDT extends CuentaBase {
    private final LocalDate vencimiento;

    public CDT(String numero, String titular, double monto, LocalDate vencimiento) {
        super(numero, titular, monto);
        this.vencimiento = vencimiento;
    }

    public void retirarAlVencimiento(double monto) {
        if (LocalDate.now().isBefore(vencimiento)) {
            throw new IllegalStateException(
                "Un CDT no permite retiros antes del vencimiento");
        }
        debitar(monto);
    }
}
