public class ComisionInternacional implements ReglaComision {
    private static final double PORCENTAJE = 0.03;
    private static final double CARGO_FIJO = 25_000;

    @Override
    public double calcular(double monto) {
        return monto * PORCENTAJE + CARGO_FIJO;
    }
}
