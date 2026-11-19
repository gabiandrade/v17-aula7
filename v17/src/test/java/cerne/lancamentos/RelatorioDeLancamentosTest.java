package cerne.lancamentos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.contas.Agencia;
import cerne.contas.Conta;
import cerne.cooperados.Cooperado;
import cerne.cooperados.TipoCooperado;
import cerne.medicao.GeradorDeMassa;

class RelatorioDeLancamentosTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 8, 1);
    private static final LocalDate FIM = LocalDate.of(2026, 8, 31);

    private final RelatorioDeLancamentos relatorio = new RelatorioDeLancamentos();
    private Cooperado ana;
    private Cooperado bruno;
    private Cooperado carla;

    @BeforeEach
    void preparar() {
        ana = cooperado("Ana", "0001-1", "0001-2");
        bruno = cooperado("Bruno", "0002-1");
        carla = cooperado("Carla", "0003-1");
    }

    private static Cooperado cooperado(String nome, String... contas) {
        var cooperado = new Cooperado(nome, nome, TipoCooperado.PESSOA_FISICA);
        for (String numero : contas) {
            cooperado.adicionarConta(new Conta(numero, new Agencia("0001", "Centro"), cooperado, BigDecimal.ZERO));
        }
        return cooperado;
    }

    private static Lancamento credito(String conta, int dia, String valor) {
        return new Credito(conta, new BigDecimal(valor), LocalDate.of(2026, 8, dia), "");
    }

    @Test
    @DisplayName("soma créditos, débitos e tarifas de todas as contas do cooperado")
    void somaTodasAsContas() {
        var lancamentos = List.of(
                credito("0001-1", 5, "100.00"), credito("0001-2", 6, "50.00"),
                new Debito("0001-1", new BigDecimal("30.00"), LocalDate.of(2026, 8, 7)),
                new Tarifa("0001-2", new BigDecimal("5.00"), LocalDate.of(2026, 8, 8), "TED"));

        var linhas = relatorio.gerar(List.of(ana), lancamentos, INICIO, FIM);

        assertEquals(List.of(new LinhaDoRelatorio("Ana", 4, new BigDecimal("150.00"), new BigDecimal("35.00"))), linhas);
    }

    @Test
    @DisplayName("estorno conta como entrada")
    void estornoContaComoEntrada() {
        var estorno = new Estorno("0001-1", new BigDecimal("20.00"), LocalDate.of(2026, 8, 9),
                java.util.UUID.randomUUID());
        var linhas = relatorio.gerar(List.of(ana), List.of(estorno), INICIO, FIM);
        assertEquals(new BigDecimal("20.00"), linhas.get(0).totalCredito());
    }

    @Test
    @DisplayName("ordena por crédito decrescente e descarta quem não movimentou")
    void ordenaEDescarta() {
        var lancamentos = List.of(credito("0002-1", 5, "300.00"), credito("0003-1", 5, "20.00"));
        var linhas = relatorio.gerar(List.of(ana, bruno, carla), lancamentos, INICIO, FIM);
        assertEquals(List.of("Bruno", "Carla"), linhas.stream().map(LinhaDoRelatorio::nomeDoCooperado).toList());
    }

    @Test
    @DisplayName("versão legada e versão com Streams dão o mesmo resultado")
    void legadoEStreamsIguais() {
        var cooperados = GeradorDeMassa.cooperados(300);
        var lancamentos = GeradorDeMassa.lancamentos(cooperados, 5_000);
        assertEquals(new RelatorioDeLancamentosLegado().gerar(cooperados, lancamentos, INICIO, FIM),
                relatorio.gerar(cooperados, lancamentos, INICIO, FIM));
    }
}
