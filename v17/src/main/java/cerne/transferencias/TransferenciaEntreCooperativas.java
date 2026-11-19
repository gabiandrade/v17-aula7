package cerne.transferencias;

import java.math.BigDecimal;
import java.util.Objects;

import cerne.contas.Conta;

public record TransferenciaEntreCooperativas(Conta origem, ContaCooperativa destino, BigDecimal valor)
        implements Transferencia {

    public TransferenciaEntreCooperativas {
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
        return TipoDeTransferencia.ENTRE_COOPERATIVAS;
    }
}
