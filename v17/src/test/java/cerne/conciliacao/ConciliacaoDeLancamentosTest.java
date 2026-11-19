package cerne.conciliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Cobre as quatro classes do módulo; o desafio do encontro 5 troca os instanceof sem mexer nestes testes. */
class ConciliacaoDeLancamentosTest {

    private static final LocalDate HOJE = LocalDate.of(2026, 8, 10);

    private static LancamentoInterno interno(String id, String valor) {
        return new LancamentoInterno(id, new BigDecimal(valor), LocalDate.of(2026, 8, 3));
    }

    private static RegistroExterno externo(String id, String valor) {
        return new RegistroExterno(id, new BigDecimal(valor), LocalDate.of(2026, 8, 3));
    }

    private static final Conciliado CONCILIADO = new Conciliado(interno("L1", "10.00"), externo("L1", "10.00"));
    private static final Pendente PENDENTE = new Pendente(interno("L2", "20.00"), 7);
    private static final Divergente ARREDONDAMENTO = new Divergente(interno("L3", "30.00"), externo("L3", "29.97"));
    private static final Divergente DIVERGENTE = new Divergente(interno("L4", "300.00"), externo("L4", "150.00"));

    @Test
    @DisplayName("conciliador separa conciliado, pendente e divergente")
    void conciliadorSeparaOsTresCasos() {
        var conciliador = new ConciliadorDeLancamentos();
        var resultados = conciliador.conciliar(
                List.of(interno("L1", "10.00"), interno("L2", "20.00"), interno("L4", "300.00")),
                List.of(externo("L1", "10.00"), externo("L4", "150.00")), HOJE);
        assertInstanceOf(Conciliado.class, resultados.get(0));
        assertEquals(new Pendente(interno("L2", "20.00"), 7), resultados.get(1));
        assertInstanceOf(Divergente.class, resultados.get(2));
        assertEquals(1, conciliador.quantidadeDePendentes(resultados));
        assertEquals(new BigDecimal("150.00"), conciliador.diferencaTotal(resultados));
    }

    @Test
    @DisplayName("relatório descreve cada caso, com tolerância de arredondamento")
    void relatorioDescreveCadaCaso() {
        var gerador = new GeradorDeRelatorioDeConciliacao();
        assertEquals("OK L1", gerador.descrever(CONCILIADO));
        assertEquals("PENDENTE L2 há 7 dias", gerador.descrever(PENDENTE));
        assertEquals("ARREDONDAMENTO L3", gerador.descrever(ARREDONDAMENTO));
        assertEquals("DIVERGENTE L4 diferença 150.00", gerador.descrever(DIVERGENTE));
    }

    @ParameterizedTest(name = "{0} tem gravidade {1}")
    @CsvSource({"conciliado, NENHUMA", "pendente, ALTA", "arredondamento, BAIXA", "divergente, ALTA"})
    @DisplayName("classificador de divergências")
    void classificador(String caso, GravidadeDaDivergencia esperada) {
        var resultado = switch (caso) {
            case "conciliado" -> CONCILIADO;
            case "pendente" -> PENDENTE;
            case "arredondamento" -> ARREDONDAMENTO;
            default -> DIVERGENTE;
        };
        assertEquals(esperada, new ClassificadorDeDivergencias().classificar(resultado));
    }

    @Test
    @DisplayName("exportador gera uma linha CSV por resultado")
    void exportador() {
        var exportador = new ExportadorDeConciliacao();
        assertEquals("L1;CONCILIADO;10.00;10.00", exportador.linhaCsv(CONCILIADO));
        assertEquals("L2;PENDENTE;20.00;", exportador.linhaCsv(PENDENTE));
        assertEquals("L4;DIVERGENTE;300.00;150.00", exportador.linhaCsv(DIVERGENTE));
    }
}
