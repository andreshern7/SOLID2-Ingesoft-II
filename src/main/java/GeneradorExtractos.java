import java.util.List;

public class GeneradorExtractos {
    public void imprimir(List<? extends ProductoBancario> productos) {
        for (ProductoBancario p : productos) System.out.println(p.generarExtracto());
    }
}
