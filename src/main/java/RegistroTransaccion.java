public interface RegistroTransaccion {
    void registrar(String tipo, String origen, String destino, double monto);
}
