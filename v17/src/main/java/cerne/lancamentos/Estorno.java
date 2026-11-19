package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import cerne.extratos.VisitanteDeLancamento;

/** Devolve ao cooperado o valor de um lançamento anterior. */
public record Estorno(String conta, BigDecimal valor, LocalDate data, UUID lancamentoOriginal) implements Lancamento {

    public Estorno {
        Objects.requireNonNull(conta, "conta");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(lancamentoOriginal, "lancamentoOriginal");
        valor = ValorMonetario.positivo(valor);
    }

    @Override
    public TipoDeLancamento tipo() {
        return TipoDeLancamento.ESTORNO;
    }

    @Override
    public <R> R aceitar(VisitanteDeLancamento<R> visitante) {
        return visitante.visitarEstorno(this);
    }
}
