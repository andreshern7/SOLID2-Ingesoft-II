public class NotificadorTransferencia {
    private final SmsGateway sms = new SmsGateway();

    public void notificar(Cuenta origen, Cuenta destino, double monto) {
        sms.enviar(origen.getTitular(),
            "Transferiste $" + monto + " a la cuenta " + destino.getNumero());
    }
}
