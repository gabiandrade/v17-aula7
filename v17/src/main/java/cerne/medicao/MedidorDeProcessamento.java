package cerne.medicao;

import java.util.Arrays;
import java.util.function.Supplier;

/** Harness do encontro 6: mesmo código nos quatro marcos. Aquecimento, N execuções, mediana. */
public final class MedidorDeProcessamento {

    private MedidorDeProcessamento() {
    }

    public static long medir(int aquecimento, int execucoes, Supplier<ResumoDeProcessamento> processamento) {
        for (int i = 0; i < aquecimento; i++) {
            processamento.get(); // resultado descartado
        }
        long[] tempos = new long[execucoes];
        for (int i = 0; i < execucoes; i++) {
            long inicio = System.nanoTime();
            ResumoDeProcessamento resumo = processamento.get();
            tempos[i] = (System.nanoTime() - inicio) / 1_000_000;
            System.out.printf("execução %d: %d ms, %d lançamentos, %d contas, saldo consolidado %s%n",
                    i + 1, tempos[i], resumo.totalDeLancamentos(), resumo.totalDeContas(),
                    resumo.saldoConsolidado().toPlainString());
        }
        Arrays.sort(tempos);
        long mediana = tempos[execucoes / 2];
        System.out.printf("mediana: %d ms (min %d, max %d)%n", mediana, tempos[0], tempos[execucoes - 1]);
        return mediana;
    }
}
