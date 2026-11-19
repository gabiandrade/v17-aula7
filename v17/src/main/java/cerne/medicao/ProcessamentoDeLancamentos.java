package cerne.medicao;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Processamento de lançamentos do encontro 6: lê N linhas sintéticas (semente fixa), calcula saldo e
 * tarifa por conta e monta o extrato consolidado. O código é idêntico nos quatro marcos (sintaxe de
 * Java 8) para que a única variável da medição seja o JDK.
 *
 * Uso: ProcessamentoDeLancamentos [aquecimentos=3] [execucoes=5] [lancamentos=1000000]
 */
public final class ProcessamentoDeLancamentos {

    private static final int QUANTIDADE_DE_CONTAS = 100_000;
    private static final BigDecimal TARIFA_POR_LANCAMENTO_DE_TARIFA = new BigDecimal("0.35");
    private static final BigDecimal PACOTE_MENSAL = new BigDecimal("12.90");

    private ProcessamentoDeLancamentos() {
    }

    public static void main(String[] args) {
        int aquecimentos = args.length > 0 ? Integer.parseInt(args[0]) : 3;
        int execucoes = args.length > 1 ? Integer.parseInt(args[1]) : 5;
        int quantidade = args.length > 2 ? Integer.parseInt(args[2]) : 1_000_000;

        System.out.printf("JDK %s · coletores: %s · heap máximo: %d MB%n", System.getProperty("java.version"),
                coletores(), Runtime.getRuntime().maxMemory() / (1024 * 1024));
        final List<String> linhas = GeradorDeMassa.linhasDeLancamentos(quantidade, QUANTIDADE_DE_CONTAS);
        System.out.printf("massa: %d lançamentos sintéticos · aquecimento %d · execuções %d%n",
                linhas.size(), aquecimentos, execucoes);
        MedidorDeProcessamento.medir(aquecimentos, execucoes, new Supplier<ResumoDeProcessamento>() {
            @Override
            public ResumoDeProcessamento get() {
                return processar(linhas);
            }
        });
    }

    public static ResumoDeProcessamento processar(List<String> linhas) {
        Map<String, TotaisDaConta> porConta = new HashMap<String, TotaisDaConta>();
        for (String linha : linhas) {
            String[] campos = linha.split(";");
            TotaisDaConta totais = porConta.get(campos[0]);
            if (totais == null) {
                totais = new TotaisDaConta();
                porConta.put(campos[0], totais);
            }
            totais.registrar(campos[2].charAt(0), new BigDecimal(campos[3]), LocalDate.parse(campos[1]));
        }

        List<String> extratoConsolidado = new ArrayList<String>(porConta.size());
        BigDecimal saldoConsolidado = BigDecimal.ZERO;
        for (Map.Entry<String, TotaisDaConta> entrada : new TreeMap<String, TotaisDaConta>(porConta).entrySet()) {
            TotaisDaConta totais = entrada.getValue();
            BigDecimal tarifa = totais.tarifaDoMes();
            BigDecimal saldo = totais.saldo().subtract(tarifa);
            extratoConsolidado.add(entrada.getKey() + ";" + totais.quantidade + ";" + saldo.toPlainString()
                    + ";" + tarifa.toPlainString() + ";" + totais.ultimoDia);
            saldoConsolidado = saldoConsolidado.add(saldo);
        }
        return new ResumoDeProcessamento(linhas.size(), extratoConsolidado.size(), saldoConsolidado);
    }

    private static String coletores() {
        StringBuilder nomes = new StringBuilder();
        for (GarbageCollectorMXBean coletor : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (nomes.length() > 0) {
                nomes.append(", ");
            }
            nomes.append(coletor.getName());
        }
        return nomes.toString();
    }

    /** Acumulador mutável por conta. */
    private static final class TotaisDaConta {

        private BigDecimal creditos = BigDecimal.ZERO;
        private BigDecimal debitos = BigDecimal.ZERO;
        private int lancamentosDeTarifa;
        private int quantidade;
        private LocalDate ultimoDia = LocalDate.MIN;

        void registrar(char tipo, BigDecimal valor, LocalDate data) {
            quantidade++;
            if (tipo == 'C') {
                creditos = creditos.add(valor);
            } else {
                debitos = debitos.add(valor);
                if (tipo == 'T') {
                    lancamentosDeTarifa++;
                }
            }
            if (data.isAfter(ultimoDia)) {
                ultimoDia = data;
            }
        }

        BigDecimal saldo() {
            return creditos.subtract(debitos);
        }

        BigDecimal tarifaDoMes() {
            BigDecimal tarifa = TARIFA_POR_LANCAMENTO_DE_TARIFA.multiply(BigDecimal.valueOf(lancamentosDeTarifa));
            return quantidade > 15 ? tarifa.add(PACOTE_MENSAL) : tarifa;
        }
    }
}
