import java.util.List;

public class RegistroMultiple implements RegistroTransaccion {
    private final List<RegistroTransaccion> registros;

    public RegistroMultiple(List<RegistroTransaccion> registros) {
        this.registros = registros;
    }

    public void registrar(String tipo, String origen, String destino, double monto) {
        for (RegistroTransaccion registro : registros) registro.registrar(tipo, origen, destino, monto);
    }
}
