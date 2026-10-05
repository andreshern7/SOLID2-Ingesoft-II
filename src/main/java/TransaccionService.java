public class TransaccionService {
    private final ValidadorMonto validador;
    private final CalculadoraComision calculadora;
    private final RepositorioTransacciones repositorio;
    private final GeneradorComprobante comprobante;
    private final NotificadorTransferencia notificador;
    private final RegistroTransaccion auditoria;

    public TransaccionService(ValidadorMonto validador, CalculadoraComision calculadora,
                              RepositorioTransacciones repositorio, GeneradorComprobante comprobante,
                              NotificadorTransferencia notificador, RegistroTransaccion auditoria) {
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

        completarTransaccion(tipo, origen, destino.getNumero(), monto, comision);
    }

    public void pagarServicio(Cuenta origen, String referenciaFactura, double monto) {
        validador.validar(monto);
        double comision = calculadora.calcular(monto, "PAGO_SERVICIO");

        origen.retirar(monto + comision);

        completarTransaccion("PAGO_SERVICIO", origen, referenciaFactura, monto, comision);
    }

    public void pagarServicio(CuentaBase origen, String referenciaFactura, double monto) {
        if (!(origen instanceof Cuenta)) {
            throw new IllegalArgumentException("Un CDT no puede pagar servicios");
        }
        pagarServicio((Cuenta) origen, referenciaFactura, monto);
    }

    private void completarTransaccion(String tipo, Cuenta origen, String destino,
                                      double monto, double comision) {
        repositorio.guardarTransaccion(origen.getNumero(), destino, monto, comision);
        comprobante.imprimir(origen.getNumero(), destino, monto, comision);
        notificador.notificar(origen, destino, monto);
        auditoria.registrar(tipo, origen.getNumero(), destino, monto);
    }
}
