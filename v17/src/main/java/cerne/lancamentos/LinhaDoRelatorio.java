package cerne.lancamentos;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Uma linha do relatório por cooperado; o equals do record cobre os quatro componentes. */
public record LinhaDoRelatorio(String nomeDoCooperado, int quantidade, BigDecimal totalCredito,
                               BigDecimal totalDebito) {

    public LinhaDoRelatorio {
        totalCredito = totalCredito.setScale(2, RoundingMode.HALF_EVEN);
        totalDebito = totalDebito.setScale(2, RoundingMode.HALF_EVEN);
    }
}
