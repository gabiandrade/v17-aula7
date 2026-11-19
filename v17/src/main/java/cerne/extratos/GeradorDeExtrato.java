package cerne.extratos;

import java.util.List;
import java.util.stream.Collectors;

import cerne.lancamentos.Lancamento;

/** Monta o extrato no formato pedido; o formato é um visitante. */
public class GeradorDeExtrato {

    public String gerar(List<Lancamento> lancamentos, VisitanteDeLancamento<String> formato) {
        return lancamentos.stream()
                .map(lancamento -> lancamento.aceitar(formato) + "\n")
                .collect(Collectors.joining());
    }

    public String gerarCsv(List<Lancamento> lancamentos) {
        return gerar(lancamentos, new GeradorDeLinhaCsv());
    }

    public String gerarTexto(List<Lancamento> lancamentos) {
        return gerar(lancamentos, new GeradorDeLinhaTexto());
    }
}
