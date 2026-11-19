package cerne.transferencias;

import java.math.BigDecimal;

import cerne.contas.Conta;

/** Hierarquia fechada: ninguém fora deste pacote cria um quarto tipo de transferência. */
public sealed interface Transferencia
        permits TransferenciaInterna, TransferenciaEntreCooperativas, TransferenciaParaOutroBanco, TransferenciaInternacional {

    Conta origem();

    BigDecimal valor();

    /** Número da conta de destino, qualquer que seja a instituição. */
    String numeroDaContaDeDestino();

    TipoDeTransferencia tipo();
}
