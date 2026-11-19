package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import cerne.extratos.VisitanteDeLancamento;

public record Tarifa(String conta, BigDecimal valor, LocalDate data, String codigoDaTarifa) implements Lancamento {

    public Tarifa {
        Objects.requireNonNull(conta, "conta");
        Objects.requireNonNull(data, "data");
        valor = ValorMonetario.positivo(valor);
        codigoDaTarifa = codigoDaTarifa == null ? "" : codigoDaTarifa;
    }

    @Override
    public TipoDeLancamento tipo() {
        return TipoDeLancamento.TARIFA;
    }

    @Override
    public <R> R aceitar(VisitanteDeLancamento<R> visitante) {
        return visitante.visitarTarifa(this);
    }
}
