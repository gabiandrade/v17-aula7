package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cerne.contas.Conta;
import cerne.cooperados.Cooperado;

/** Versão com laços aninhados, mantida só para a medição antes/depois (MedicaoDoRelatorio). */
public class RelatorioDeLancamentosLegado {

    public List<LinhaDoRelatorio> gerar(List<Cooperado> cooperados, List<Lancamento> lancamentos,
                                        LocalDate inicio, LocalDate fim) {
        List<LinhaDoRelatorio> linhas = new ArrayList<LinhaDoRelatorio>();
        for (Cooperado cooperado : cooperados) {
            BigDecimal totalCredito = BigDecimal.ZERO;
            BigDecimal totalDebito = BigDecimal.ZERO;
            int quantidade = 0;
            for (Conta conta : cooperado.getContas()) {
                for (Lancamento lancamento : lancamentos) {
                    if (lancamento.conta().equals(conta.numero())) {
                        if (!lancamento.data().isBefore(inicio) && !lancamento.data().isAfter(fim)) {
                            if (lancamento.tipo().ehEntrada()) {
                                totalCredito = totalCredito.add(lancamento.valor());
                            } else {
                                totalDebito = totalDebito.add(lancamento.valor());
                            }
                            quantidade++;
                        }
                    }
                }
            }
            if (quantidade > 0) {
                linhas.add(new LinhaDoRelatorio(cooperado.getNome(), quantidade, totalCredito, totalDebito));
            }
        }
        Collections.sort(linhas, new Comparator<LinhaDoRelatorio>() {
            @Override
            public int compare(LinhaDoRelatorio a, LinhaDoRelatorio b) {
                return b.totalCredito().compareTo(a.totalCredito());
            }
        });
        return linhas;
    }
}
