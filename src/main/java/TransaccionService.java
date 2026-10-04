public class TransaccionService {
    private final ValidadorMonto validador;
    private final CalculadoraComision calculadora;
    private final RepositorioTransacciones repositorio;
    private final GeneradorComprobante comprobante;
    private final NotificadorTransferencia notificador;
    private final RegistroAuditoria auditoria;

    public TransaccionService(ValidadorMonto validador, CalculadoraComision calculadora,
                              RepositorioTransacciones repositorio, GeneradorComprobante comprobante,
                              NotificadorTransferencia notificador, RegistroAuditoria auditoria) {
        this.validador = validador;
        this.calculadora = calculadora;
        this.repositorio = repositorio;
        this.comprobante = comprobante;
        this.notificador = notificador;
        this.auditoria = auditoria;
    }

    public void transferir(Cuenta origen, Cuenta destino, double monto, String tipo) {
        validador.validar(monto);
        double comision = calculadora.calcular(monto, tipo);

        origen.retirar(monto + comision);
        destino.depositar(monto);

        repositorio.guardarTransaccion(origen.getNumero(), destino.getNumero(), monto, comision);
        comprobante.imprimir(origen.getNumero(), destino.getNumero(), monto, comision);
        notificador.notificar(origen, destino, monto);
        auditoria.registrar(tipo, origen.getNumero(), destino.getNumero(), monto);
    }
}
