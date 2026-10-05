public class ComisionPagoServicios implements ReglaComision {
    private static final double COMISION_FIJA = 1_500;

    @Override
    public double calcular(double monto) {
        return COMISION_FIJA;
    }
}
