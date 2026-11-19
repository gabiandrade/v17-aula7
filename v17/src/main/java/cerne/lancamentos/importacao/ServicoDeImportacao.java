package cerne.lancamentos.importacao;

import cerne.lancamentos.*;

import java.util.ArrayList;
import java.util.List;


public class ServicoDeImportacao {

    public List<Lancamento> importar(List<LinhaDoArquivo> linhas) {
        List<Lancamento> lancamentos = new ArrayList<>();

        for (LinhaDoArquivo linha : linhas) {
            lancamentos.add(montarLancamento(linha));
        }

        return lancamentos;
    }

    public Lancamento montarLancamento(LinhaDoArquivo linha) {
        Lancamento lancamento;

        if ("C".equals(linha.tipo())) {
            lancamento = new Credito(
                    linha.conta(),
                    linha.valor(),
                    linha.data(),
                    linha.origem());

        } else if ("D".equals(linha.tipo())) {
            lancamento = new Debito(
                    linha.conta(),
                    linha.valor(),
                    linha.data());

        } else if ("T".equals(linha.tipo())) {
            lancamento = new Tarifa(
                    linha.conta(),
                    linha.valor(),
                    linha.data(),
                    linha.codigoDaTarifa());

        } else if ("E".equals(linha.tipo())) {
            lancamento = new Estorno(
                    linha.conta(),
                    linha.valor(),
                    linha.data(),
                    linha.lancamentoOriginal());

        } else {
            throw new TipoDeLancamentoDesconhecidoException(
                    linha.tipo());
        }

        return lancamento;
    }
}