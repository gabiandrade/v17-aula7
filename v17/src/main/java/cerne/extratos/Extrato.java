package cerne.extratos;

import java.time.LocalDate;
import java.util.List;

import cerne.lancamentos.Lancamento;

/** Extrato de uma conta: período coberto e lançamentos do mais recente para o mais antigo. */
public record Extrato(String numeroDaConta, LocalDate inicio, LocalDate fim, List<Lancamento> lancamentos) {

    public static Extrato vazio(String numeroDaConta) {
        return new Extrato(numeroDaConta, null, null, List.of());
    }
}
