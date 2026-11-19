package cerne.auditoria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.contas.Agencia;
import cerne.contas.Conta;
import cerne.cooperados.Cooperado;
import cerne.cooperados.TipoCooperado;
import cerne.transferencias.ChaveDeIdempotencia;
import cerne.transferencias.SolicitacaoDeTransferencia;
import cerne.transferencias.TransferenciaInterna;

/** Exercita o XStream 1.4.9: sem os --add-opens do pom, falha com InaccessibleObjectException. */
class ServicoDeAuditoriaTest {

    @Test
    @DisplayName("grava a tentativa em XML")
    void gravaTentativaEmXml() {
        var titular = new Cooperado("52998224725", "Ana", TipoCooperado.PESSOA_FISICA);
        var agencia = new Agencia("0001", "Centro");
        var solicitacao = new SolicitacaoDeTransferencia(new TransferenciaInterna(
                new Conta("0001-1", agencia, titular, BigDecimal.TEN), new Conta("0002-2", agencia, titular, BigDecimal.ZERO),
                BigDecimal.ONE), new ChaveDeIdempotencia("pedido-7"));

        var auditoria = new ServicoDeAuditoria();
        auditoria.registrarTentativa(solicitacao);

        assertEquals(1, auditoria.registros().size());
        assertTrue(auditoria.registros().get(0).contains("<chave>pedido-7</chave>"));
    }
}
