package cerne.transferencias;

import java.math.BigDecimal;
import java.util.Objects;

import cerne.contas.Conta;

public record TransferenciaInterna(Conta origem, Conta destino, BigDecimal valor) implements Transferencia {

    public TransferenciaInterna {
        Objects.requireNonNull(origem, "origem");
        Objects.requireNonNull(destino, "destino");
        valor = ValoresDeTransferencia.positivo(valor);
    }

    @Override
    public String numeroDaContaDeDestino() {
        return destino.numero();
    }

    @Override
    public TipoDeTransferencia tipo() {
        return TipoDeTransferencia.INTERNA;
    }
}
