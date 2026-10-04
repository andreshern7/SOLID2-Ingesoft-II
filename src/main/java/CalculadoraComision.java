import java.util.Map;

public class CalculadoraComision {
    private final Map<String, ReglaComision> reglas;

    public CalculadoraComision(Map<String, ReglaComision> reglas) {
        this.reglas = reglas;
    }

    public double calcular(double monto, String tipo) {
        ReglaComision regla = reglas.get(tipo);
        if (regla == null) throw new IllegalArgumentException("Tipo de transferencia desconocido");
        return regla.calcular(monto);
    }
}
