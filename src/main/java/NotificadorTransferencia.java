public class NotificadorTransferencia {
    private final CanalNotificacion canal;

    public NotificadorTransferencia(CanalNotificacion canal) {
        this.canal = canal;
    }

    public void notificar(Cuenta origen, Cuenta destino, double monto) {
        canal.enviar(origen.getTitular(),
            "Transferiste $" + monto + " a la cuenta " + destino.getNumero());
    }
}
