package cerne.extratos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.contas.Agencia;
import cerne.contas.Conta;
import cerne.cooperados.Cooperado;
import cerne.cooperados.RepositorioDeCooperadosEmMemoria;
import cerne.cooperados.TipoCooperado;
import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Lancamento;
import cerne.lancamentos.RepositorioDeLancamentosEmMemoria;

class ServicoDeExtratoTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 8, 1);
    private static final LocalDate FIM = LocalDate.of(2026, 8, 31);

    private RepositorioDeLancamentosEmMemoria lancamentos;
    private ServicoDeExtrato servico;
    private Conta conta;

    @BeforeEach
    void preparar() {
        var cooperados = new RepositorioDeCooperadosEmMemoria();
        lancamentos = new RepositorioDeLancamentosEmMemoria();
        servico = new ServicoDeExtrato(cooperados, lancamentos);
        var ana = new Cooperado("52998224725", "Ana", TipoCooperado.PESSOA_FISICA);
        conta = new Conta("0001-1", new Agencia("0001", "Agência Centro"), ana, BigDecimal.ZERO);
        ana.adicionarConta(conta);
        cooperados.salvar(ana);
    }

    private static Lancamento credito(String valor, LocalDate data) {
        return new Credito("0001-1", new BigDecimal(valor), data, "");
    }

    @Test
    void cabecalhoMostraAgenciaECooperado() {
        assertEquals("EXTRATO - Agência Centro - Ana", servico.cabecalhoDoExtrato("52998224725"));
    }

    @Test
    void cabecalhoSemAgenciaUsaValorPadrao() {
        conta.alterarAgencia(null);
        assertEquals("EXTRATO - Agência não informada - Ana", servico.cabecalhoDoExtrato("52998224725"));
    }

    @Test
    void cooperadoInexistenteDevolveListaVazia() {
        assertTrue(servico.lancamentosDoPeriodo("00000000000", INICIO, FIM).isEmpty());
    }

    @Test
    @DisplayName("extrato: primeiro e último pelo índice, cópia invertida (o 21 troca por getFirst/getLast/reversed)")
    void extratoMostraOMaisRecentePrimeiro() {
        List<Lancamento> cronologicos = List.of(
                credito("10.00", LocalDate.of(2026, 8, 1)),
                new Debito("0001-1", new BigDecimal("5.00"), LocalDate.of(2026, 8, 2)),
                credito("7.00", LocalDate.of(2026, 8, 3)));
        var extrato = servico.montarExtrato(conta, cronologicos);
        assertEquals(LocalDate.of(2026, 8, 1), extrato.inicio());
        assertEquals(LocalDate.of(2026, 8, 3), extrato.fim());
        assertEquals(new BigDecimal("7.00"), extrato.lancamentos().get(0).valor());
    }
}
