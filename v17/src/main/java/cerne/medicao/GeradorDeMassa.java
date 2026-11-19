package cerne.medicao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import cerne.contas.Agencia;
import cerne.contas.Conta;
import cerne.cooperados.Cooperado;
import cerne.cooperados.TipoCooperado;
import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Lancamento;
import cerne.lancamentos.Tarifa;
import cerne.transferencias.RegistroDeTransferencia;

/** Massa sintética com semente fixa: a mesma execução gera sempre os mesmos dados. */
public final class GeradorDeMassa {

    private GeradorDeMassa() {
    }

    /** Cooperados com uma ou duas contas cada (em média 1,5); um em cada cinquenta tem convênio especial. */
    public static List<Cooperado> cooperados(int quantidade) {
        Random aleatorio = new Random(42);
        Agencia agencia = new Agencia("0001", "Agência Centro");
        TipoCooperado[] tipos = TipoCooperado.values();
        List<Cooperado> cooperados = new ArrayList<Cooperado>(quantidade);
        for (int i = 0; i < quantidade; i++) {
            Cooperado cooperado = new Cooperado(String.format("%011d", i + 1), "Cooperado " + (i + 1),
                    tipos[i % tipos.length], i % 50 == 49);
            int contas = aleatorio.nextBoolean() ? 1 : 2;
            for (int k = 0; k < contas; k++) {
                cooperado.adicionarConta(new Conta(String.format("%06d-%d", i + 1, k), agencia, cooperado,
                        new BigDecimal("1000.00")));
            }
            cooperados.add(cooperado);
        }
        return cooperados;
    }

    /** Lançamentos entre 25/07/2026 e 07/09/2026: parte cai fora de agosto. */
    public static List<Lancamento> lancamentos(List<Cooperado> cooperados, int quantidade) {
        Random aleatorio = new Random(7);
        List<Conta> contas = new ArrayList<>();
        for (Cooperado cooperado : cooperados) {
            contas.addAll(cooperado.getContas());
        }
        LocalDate primeiroDia = LocalDate.of(2026, 7, 25);
        List<Lancamento> lancamentos = new ArrayList<>(quantidade);
        for (int i = 0; i < quantidade; i++) {
            String conta = contas.get(aleatorio.nextInt(contas.size())).numero();
            LocalDate data = primeiroDia.plusDays(aleatorio.nextInt(45));
            BigDecimal valor = BigDecimal.valueOf(100 + aleatorio.nextInt(100_000), 2);
            int sorteio = aleatorio.nextInt(10);
            if (sorteio < 5) {
                lancamentos.add(new Credito(conta, valor, data, ""));
            } else if (sorteio < 9) {
                lancamentos.add(new Debito(conta, valor, data));
            } else {
                lancamentos.add(new Tarifa(conta, valor, data, ""));
            }
        }
        return lancamentos;
    }

    /**
     * Dois lados da conciliação: ~90% casam, ~4% só no Cerne, ~4% só no arquivo e ~2% com valor divergente.
     * O arquivo chega embaralhado.
     */
    public static MassaDeConciliacao conciliacao(int quantidade) {
        Random aleatorio = new Random(11);
        LocalDate inicio = LocalDate.of(2026, 8, 1);
        List<RegistroDeTransferencia> doCerne = new ArrayList<RegistroDeTransferencia>(quantidade);
        List<RegistroDeTransferencia> doArquivo = new ArrayList<RegistroDeTransferencia>(quantidade);
        for (int i = 0; i < quantidade; i++) {
            String identificador = "TRF-" + String.format("%08d", i + 1);
            BigDecimal valor = BigDecimal.valueOf(100 + aleatorio.nextInt(500_000), 2);
            LocalDate data = inicio.plusDays(aleatorio.nextInt(28));
            int sorteio = aleatorio.nextInt(100);
            RegistroDeTransferencia doSistema = new RegistroDeTransferencia(identificador, valor, data);
            if (sorteio < 90) {
                doCerne.add(doSistema);
                doArquivo.add(new RegistroDeTransferencia(identificador.toLowerCase() + " ", valor, data));
            } else if (sorteio < 94) {
                doCerne.add(doSistema);
            } else if (sorteio < 98) {
                doArquivo.add(doSistema);
            } else {
                doCerne.add(doSistema);
                doArquivo.add(new RegistroDeTransferencia(identificador, valor.add(BigDecimal.ONE), data));
            }
        }
        Collections.shuffle(doArquivo, new Random(13));
        return new MassaDeConciliacao(doCerne, doArquivo);
    }

    /** Linhas "conta;data;tipo;valor;descricao" para o ProcessamentoDeLancamentos (idêntico nos marcos). */
    public static List<String> linhasDeLancamentos(int quantidade, int quantidadeDeContas) {
        Random aleatorio = new Random(2026);
        List<String> linhas = new ArrayList<String>(quantidade);
        StringBuilder linha = new StringBuilder(96);
        for (int i = 0; i < quantidade; i++) {
            int conta = aleatorio.nextInt(quantidadeDeContas);
            int dia = 1 + aleatorio.nextInt(28);
            int sorteio = aleatorio.nextInt(10);
            int centavos = 100 + aleatorio.nextInt(500_000);
            linha.setLength(0);
            linha.append("CONTA-").append(conta).append(';')
                    .append("2026-08-").append(dia < 10 ? "0" : "").append(dia).append(';')
                    .append(sorteio < 5 ? 'C' : sorteio < 9 ? 'D' : 'T').append(';')
                    .append(centavos / 100).append('.').append(centavos % 100 < 10 ? "0" : "").append(centavos % 100)
                    .append(';').append("Lançamento sintético número ").append(i);
            linhas.add(linha.toString());
        }
        return linhas;
    }
}
