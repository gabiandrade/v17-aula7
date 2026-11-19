package cerne.transferencias;

import java.math.BigDecimal;
import java.util.Objects;

import cerne.contas.Conta;

public record TransferenciaParaOutroBanco(Conta origem, ContaExterna destino, BigDecimal valor)
        implements Transferencia {

    public TransferenciaParaOutroBanco {
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
        return TipoDeTransferencia.PARA_OUTRO_BANCO;
    }
}
