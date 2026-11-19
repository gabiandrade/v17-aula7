package cerne.transferencias;

import java.math.BigDecimal;
import java.util.Objects;

/** O pedido que chega ao serviço: a transferência e a chave que detecta reenvio. */
public record SolicitacaoDeTransferencia(Transferencia transferencia, ChaveDeIdempotencia chaveDeIdempotencia) {

    public SolicitacaoDeTransferencia {
        Objects.requireNonNull(transferencia, "transferencia");
        Objects.requireNonNull(chaveDeIdempotencia, "chaveDeIdempotencia");
    }

    public static SolicitacaoDeTransferencia de(Transferencia transferencia) {
        return new SolicitacaoDeTransferencia(transferencia, ChaveDeIdempotencia.nova());
    }

    public BigDecimal valor() {
        return transferencia.valor();
    }

    public String contaDeOrigem() {
        return transferencia.origem().numero();
    }

    public String contaDeDestino() {
        return transferencia.numeroDaContaDeDestino();
    }
}
