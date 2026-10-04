import java.util.ArrayList;
import java.util.List;

public class CanalFalso implements CanalNotificacion {
    public final List<String> mensajes = new ArrayList<>();

    public void enviar(String destinatario, String mensaje) {
        mensajes.add(destinatario + ": " + mensaje);
    }
}
