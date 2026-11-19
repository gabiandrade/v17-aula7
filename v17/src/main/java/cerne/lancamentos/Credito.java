package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import cerne.extratos.VisitanteDeLancamento;

public record Credito(String conta, BigDecimal valor, LocalDate data, String origem) implements Lancamento {

    public Credito {
        Objects.requireNonNull(conta, "conta");
        Objects.requireNonNull(data, "data");
        valor = ValorMonetario.positivo(valor);
        origem = origem == null ? "" : origem.strip();
    }

    @Override
    public TipoDeLancamento tipo() {
        return TipoDeLancamento.CREDITO;
    }

    @Override
    public <R> R aceitar(VisitanteDeLancamento<R> visitante) {
        return visitante.visitarCredito(this);
    }
}
