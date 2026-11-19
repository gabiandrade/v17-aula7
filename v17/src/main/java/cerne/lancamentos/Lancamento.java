package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;

import cerne.extratos.VisitanteDeLancamento;

/** Hierarquia fechada: no Cerne existem exatamente quatro tipos de lançamento. */
public sealed interface Lancamento permits Credito, Debito, Tarifa, Estorno {

    String conta();

    BigDecimal valor();

    LocalDate data();

    TipoDeLancamento tipo();

    /** No 17 o Visitor ainda é o despacho exaustivo; no 21 o switch com padrões o substitui. */
    <R> R aceitar(VisitanteDeLancamento<R> visitante);

    default boolean ehCredito() {
        return tipo().ehEntrada();
    }

    /** Entre inicio e fim, inclusive. */
    default boolean estaEntre(LocalDate inicio, LocalDate fim) {
        return !data().isBefore(inicio) && !data().isAfter(fim);
    }
}
