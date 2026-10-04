public class SistemaAntifraude implements RegistroTransaccion {
    public void registrar(String tipo, String origen, String destino, double monto) {
        System.out.println("[ANTIFRAUDE] " + tipo + " " + origen + " -> " + destino + " $" + monto);
    }
}
