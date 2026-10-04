import java.util.List;

public class CanalMultiple implements CanalNotificacion {
    private final List<CanalNotificacion> canales;

    public CanalMultiple(List<CanalNotificacion> canales) {
        this.canales = canales;
    }

    public void enviar(String destinatario, String mensaje) {
        for (CanalNotificacion canal : canales) canal.enviar(destinatario, mensaje);
    }
}
