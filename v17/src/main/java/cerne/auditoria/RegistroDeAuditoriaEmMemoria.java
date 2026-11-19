package cerne.auditoria;

import java.util.ArrayList;
import java.util.List;

import cerne.transferencias.ComprovanteDeTransferencia;
import cerne.transferencias.SolicitacaoDeTransferencia;

/** Registro para testes: guarda só o nome de cada evento, na ordem. */
public class RegistroDeAuditoriaEmMemoria implements RegistroDeAuditoria {

    private final List<String> eventos = new ArrayList<>();

    @Override
    public synchronized void registrarTentativa(SolicitacaoDeTransferencia solicitacao) {
        eventos.add("TENTATIVA");
    }

    @Override
    public synchronized void registrarSucesso(ComprovanteDeTransferencia comprovante) {
        eventos.add("SUCESSO");
    }

    @Override
    public synchronized void registrarFalha(SolicitacaoDeTransferencia solicitacao, RuntimeException erro) {
        eventos.add("FALHA");
    }

    public synchronized List<String> eventos() {
        return List.copyOf(eventos);
    }
}
