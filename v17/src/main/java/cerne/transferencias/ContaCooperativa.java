package cerne.transferencias;

/** Conta em outra cooperativa do sistema. */
public record ContaCooperativa(String codigoCooperativa, String numero) {

    public ContaCooperativa {
        codigoCooperativa = ValoresDeTransferencia.preenchido(codigoCooperativa, "código da cooperativa");
        numero = ValoresDeTransferencia.preenchido(numero, "número da conta");
    }
}
