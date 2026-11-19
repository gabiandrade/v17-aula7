package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cerne.contas.Conta;
import cerne.cooperados.Cooperado;

/** Relatório por cooperado com Streams: índice, linha por cooperado, pipeline final. */
public class RelatorioDeLancamentos {

    public List<LinhaDoRelatorio> gerar(List<Cooperado> cooperados, List<Lancamento> lancamentos,
                                        LocalDate inicio, LocalDate fim) {
        var lancamentosPorConta = lancamentos.stream()
                .filter(lancamento -> lancamento.estaEntre(inicio, fim))
                .collect(Collectors.groupingBy(Lancamento::conta));

        return cooperados.stream()
                .map(cooperado -> linhaDe(cooperado, lancamentosPorConta))
                .filter(linha -> linha.quantidade() > 0)
                .sorted(Comparator.comparing(LinhaDoRelatorio::totalCredito).reversed())
                .toList();
    }

    private LinhaDoRelatorio linhaDe(Cooperado cooperado, Map<String, List<Lancamento>> lancamentosPorConta) {
        List<Lancamento> doCooperado = cooperado.getContas().stream()
                .map(Conta::numero)
                .flatMap(numero -> lancamentosPorConta.getOrDefault(numero, List.of()).stream())
                .toList();

        Map<Boolean, BigDecimal> totais = doCooperado.stream()
                .collect(Collectors.partitioningBy(Lancamento::ehCredito,
                        Collectors.reducing(BigDecimal.ZERO, Lancamento::valor, BigDecimal::add)));

        return new LinhaDoRelatorio(cooperado.getNome(), doCooperado.size(), totais.get(true), totais.get(false));
    }

    public String formatar(List<LinhaDoRelatorio> linhas) {
        return linhas.stream()
                .map(linha -> String.join(";", linha.nomeDoCooperado(), String.valueOf(linha.quantidade()),
                        linha.totalCredito().toPlainString(), linha.totalDebito().toPlainString()) + "\n")
                .collect(Collectors.joining());
    }
}
