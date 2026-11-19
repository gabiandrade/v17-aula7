package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import cerne.extratos.VisitanteDeLancamento;

public record Debito(String conta, BigDecimal valor, LocalDate data) implements Lancamento {

    public Debito {
        Objects.requireNonNull(conta, "conta");
        Objects.requireNonNull(data, "data");
        valor = ValorMonetario.positivo(valor);
    }

    @Override
    public TipoDeLancamento tipo() {
        return TipoDeLancamento.DEBITO;
    }

    @Override
    public <R> R aceitar(VisitanteDeLancamento<R> visitante) {
        return visitante.visitarDebito(this);
    }
}
