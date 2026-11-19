package cerne.auditoria;

import cerne.transferencias.ComprovanteDeTransferencia;
import cerne.transferencias.ServicoDeTransferencia;
import cerne.transferencias.SolicitacaoDeTransferencia;

/** Decorator manual (encontro 7): mesma interface, delegado recebido de fora, auditoria antes e depois. */
public class ServicoDeTransferenciaAuditado implements ServicoDeTransferencia {

    private final ServicoDeTransferencia delegado;
    private final RegistroDeAuditoria auditoria;

    public ServicoDeTransferenciaAuditado(ServicoDeTransferencia delegado, RegistroDeAuditoria auditoria) {
        this.delegado = delegado;
        this.auditoria = auditoria;
    }

    @Override
    public ComprovanteDeTransferencia transferir(SolicitacaoDeTransferencia solicitacao) {
        auditoria.registrarTentativa(solicitacao);
        try {
            var comprovante = delegado.transferir(solicitacao);
            auditoria.registrarSucesso(comprovante);
            return comprovante;
        } catch (RuntimeException e) {
            auditoria.registrarFalha(solicitacao, e);
            throw e;
        }
    }
}
