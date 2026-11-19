package cerne.medicao;

import java.util.Arrays;
import java.util.function.Supplier;

/** Cronômetro mínimo: aquecimento, N repetições, mediana. Para decisão de verdade, use JMH. */
public final class Cronometro {

    private Cronometro() {
    }

    public static <T> T medir(String rotulo, int aquecimento, int repeticoes, Supplier<T> acao) {
        for (int i = 0; i < aquecimento; i++) {
            acao.get();
        }
        long[] duracoes = new long[repeticoes];
        T resultado = null;
        for (int i = 0; i < repeticoes; i++) {
            long inicio = System.nanoTime();
            resultado = acao.get();
            duracoes[i] = (System.nanoTime() - inicio) / 1_000_000;
        }
        Arrays.sort(duracoes);
        System.out.printf("%s: mediana %d ms (min %d, max %d)%n",
                rotulo, duracoes[repeticoes / 2], duracoes[0], duracoes[repeticoes - 1]);
        return resultado;
    }
}
