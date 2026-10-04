public class TransaccionService {
    private final ValidadorMonto validador = new ValidadorMonto();
    private final CalculadoraComision calculadora;
    private final OracleRepositorio repositorio = new OracleRepositorio();
    private final GeneradorComprobante comprobante = new GeneradorComprobante();
    private final NotificadorTransferencia notificador = new NotificadorTransferencia();
    private final RegistroAuditoria auditoria = new RegistroAuditoria();

    public TransaccionService(CalculadoraComision calculadora) {
        this.calculadora = calculadora;
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
