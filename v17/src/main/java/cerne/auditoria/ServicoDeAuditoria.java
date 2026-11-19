package cerne.auditoria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import com.thoughtworks.xstream.XStream;

import cerne.transferencias.ComprovanteDeTransferencia;
import cerne.transferencias.SolicitacaoDeTransferencia;

/**
 * Trilha de auditoria em XML via XStream 1.4.9. No 17 só funciona com os --add-opens do pom
 * (InaccessibleObjectException sem eles). Substituída no encontro 8.
 */
@ApplicationScoped
public class ServicoDeAuditoria implements RegistroDeAuditoria {

    private final XStream xstream;
    private final List<String> registros = Collections.synchronizedList(new ArrayList<>());

    public ServicoDeAuditoria() {
        xstream = new XStream();
        xstream.alias("evento", EventoDeAuditoria.class);
    }

    @Override
    public void registrarTentativa(SolicitacaoDeTransferencia solicitacao) {
        gravar(new EventoDeAuditoria("TENTATIVA", solicitacao.chaveDeIdempotencia().valor(),
                solicitacao.contaDeOrigem(), solicitacao.valor(), ""));
    }

    @Override
    public void registrarSucesso(ComprovanteDeTransferencia comprovante) {
        var solicitacao = comprovante.solicitacao();
        gravar(new EventoDeAuditoria("SUCESSO", solicitacao.chaveDeIdempotencia().valor(),
                solicitacao.contaDeOrigem(), solicitacao.valor(), comprovante.identificador()));
    }

    @Override
    public void registrarFalha(SolicitacaoDeTransferencia solicitacao, RuntimeException erro) {
        gravar(new EventoDeAuditoria("FALHA", solicitacao.chaveDeIdempotencia().valor(),
                solicitacao.contaDeOrigem(), solicitacao.valor(), erro.getMessage()));
    }

    public List<String> registros() {
        return List.copyOf(registros);
    }

    private void gravar(EventoDeAuditoria evento) {
        registros.add(xstream.toXML(evento));
    }
}
