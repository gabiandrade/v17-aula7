package cerne.transferencias;

import java.util.Map;

/**
 * Decorator (encontro 7): transferir não é idempotente, então não é cache de resultado, é detecção de
 * reenvio. A mesma chave devolve o mesmo comprovante em vez de debitar de novo.
 */
public class ServicoDeTransferenciaComDeduplicacao implements ServicoDeTransferencia {

    private final ServicoDeTransferencia delegado;
    private final Map<ChaveDeIdempotencia, ComprovanteDeTransferencia> recentes;

    public ServicoDeTransferenciaComDeduplicacao(ServicoDeTransferencia delegado,
                                                 Map<ChaveDeIdempotencia, ComprovanteDeTransferencia> recentes) {
        this.delegado = delegado;
        this.recentes = recentes;
    }

    @Override
    public ComprovanteDeTransferencia transferir(SolicitacaoDeTransferencia solicitacao) {
        return recentes.computeIfAbsent(solicitacao.chaveDeIdempotencia(), chave -> delegado.transferir(solicitacao));
    }
}
