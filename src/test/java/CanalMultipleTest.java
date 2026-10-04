import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CanalMultipleTest {
    @Test
    void unMensajeLlegaUnaVezACadaCanal() {
        CanalFalso sms = new CanalFalso();
        CanalFalso push = new CanalFalso();
        CanalNotificacion canal = new CanalMultiple(List.of(sms, push));

        canal.enviar("Ana", "Transferiste $100000.0 a la cuenta 001-2");

        assertEquals(1, sms.mensajes.size());
        assertEquals(1, push.mensajes.size());
    }
}
