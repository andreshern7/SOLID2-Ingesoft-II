public class Cuenta extends CuentaBase {
    public Cuenta(String numero, String titular, double saldoInicial) {
        super(numero, titular, saldoInicial);
    }

    public void retirar(double monto) {
        debitar(monto);
    }
}
