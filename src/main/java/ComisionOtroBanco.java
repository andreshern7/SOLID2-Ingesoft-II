public class ComisionOtroBanco implements ReglaComision {
    private static final double COMISION_FIJA = 7_500;

    @Override
    public double calcular(double monto) {
        return COMISION_FIJA;
    }
}
