public class PushGateway implements CanalNotificacion {
    public void enviar(String destinatario, String mensaje) {
        System.out.println("[PUSH] Para " + destinatario + ": " + mensaje);
    }
}
