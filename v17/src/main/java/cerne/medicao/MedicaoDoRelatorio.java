package cerne.medicao;

import java.time.LocalDate;
import java.util.List;

import cerne.lancamentos.LinhaDoRelatorio;
import cerne.lancamentos.RelatorioDeLancamentos;
import cerne.lancamentos.RelatorioDeLancamentosLegado;

/**
 * Encontro 2: mede laços aninhados × Streams com a mesma massa e prova que o resultado é idêntico.
 * Uso: MedicaoDoRelatorio [cooperados=5000] [lancamentos=50000]. O padrão cabe na aula (legado na casa dos
 * segundos). Com 20000 200000 o legado leva cerca de 70 s por rodada; com 2 milhões não termina.
 */
public final class MedicaoDoRelatorio {

    private MedicaoDoRelatorio() {
    }

    public static void main(String[] args) {
        int quantidadeDeCooperados = args.length > 0 ? Integer.parseInt(args[0]) : 5_000;
        int quantidadeDeLancamentos = args.length > 1 ? Integer.parseInt(args[1]) : 50_000;

        var cooperados = GeradorDeMassa.cooperados(quantidadeDeCooperados);
        var lancamentos = GeradorDeMassa.lancamentos(cooperados, quantidadeDeLancamentos);
        var inicio = LocalDate.of(2026, 8, 1);
        var fim = LocalDate.of(2026, 8, 31);
        System.out.printf("massa: %d cooperados, %d lançamentos%n", cooperados.size(), lancamentos.size());

        var legado = new RelatorioDeLancamentosLegado();
        var novo = new RelatorioDeLancamentos();

        List<LinhaDoRelatorio> a = Cronometro.medir("laços aninhados", 2, 5,
                () -> legado.gerar(cooperados, lancamentos, inicio, fim));
        List<LinhaDoRelatorio> b = Cronometro.medir("streams", 2, 5,
                () -> novo.gerar(cooperados, lancamentos, inicio, fim));

        if (!a.equals(b)) {
            throw new AssertionError("As duas versões divergem; a medição não vale nada");
        }
        System.out.printf("resultado idêntico: %d linhas%n", a.size());
    }
}
