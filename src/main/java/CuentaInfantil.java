import java.time.LocalDate;

public class CuentaInfantil extends Cuenta {
    private static final double TOPE_RETIRO_DIARIO = 200_000;

    private LocalDate diaDelAcumulado = LocalDate.now();
    private double retiradoHoy = 0;

    public CuentaInfantil(String numero, String titular, double saldoInicial) {
        super(numero, titular, saldoInicial);
    }

    @Override
    public void retirar(double monto) {
        LocalDate hoy = LocalDate.now();
        if (!hoy.equals(diaDelAcumulado)) {
            diaDelAcumulado = hoy;
            retiradoHoy = 0;
        }
        if (retiradoHoy + monto > TOPE_RETIRO_DIARIO) {
            throw new IllegalStateException("Supera el tope diario de retiros de la cuenta infantil");
        }
        super.retirar(monto);
        retiradoHoy += monto;
    }
}
