import java.util.ArrayList;
import java.util.List;

public class RegistroFalso implements RegistroTransaccion {
    public final List<String> registros = new ArrayList<>();

    public void registrar(String tipo, String origen, String destino, double monto) {
        registros.add(tipo + " " + origen + " -> " + destino + " $" + monto);
    }
}
