package cerne.auditoria;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;

import cerne.transferencias.ComprovanteDeTransferencia;
import cerne.transferencias.ServicoDeTransferencia;
import cerne.transferencias.SolicitacaoDeTransferencia;

/**
 * Variante CDI do mesmo Decorator: vale para todo ponto de injeção, mas a pilha não está escrita em lugar
 * nenhum. Sem @Priority, no CDI 4.0 ele simplesmente não roda. Só compila aqui: não há contêiner no projeto.
 */
@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public abstract class DecoradorDeAuditoria implements ServicoDeTransferencia {

    @Inject
    @Delegate
    @Any
    ServicoDeTransferencia delegado;

    @Inject
    RegistroDeAuditoria auditoria;

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
