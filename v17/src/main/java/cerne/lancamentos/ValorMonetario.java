package cerne.lancamentos;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Regra comum aos construtores compactos: valor presente, positivo e com duas casas. */
final class ValorMonetario {

    private ValorMonetario() {
    }

    static BigDecimal positivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("valor do lançamento deve ser positivo: " + valor);
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}
