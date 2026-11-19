package cerne.transferencias;

import cerne.contas.Conta;

import java.math.BigDecimal;

public final class TransferenciaInternacional implements Transferencia {
    @Override
    public Conta origem() {
        return null;
    }

    @Override
    public BigDecimal valor() {
        return null;
    }

    @Override
    public String numeroDaContaDeDestino() {
        return "";
    }

    @Override
    public TipoDeTransferencia tipo() {
        return null;
    }
}
