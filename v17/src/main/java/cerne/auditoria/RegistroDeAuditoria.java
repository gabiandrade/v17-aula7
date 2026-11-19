package cerne.auditoria;

import cerne.transferencias.ComprovanteDeTransferencia;
import cerne.transferencias.SolicitacaoDeTransferencia;

/** Onde a trilha de auditoria é gravada. */
public interface RegistroDeAuditoria {

    void registrarTentativa(SolicitacaoDeTransferencia solicitacao);

    void registrarSucesso(ComprovanteDeTransferencia comprovante);

    void registrarFalha(SolicitacaoDeTransferencia solicitacao, RuntimeException erro);
}
