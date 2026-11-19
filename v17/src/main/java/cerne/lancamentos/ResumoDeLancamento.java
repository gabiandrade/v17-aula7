package cerne.lancamentos;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Eram 72 linhas com o campo tipo esquecido no equals; agora é impossível esquecer. */
public record ResumoDeLancamento(long id, LocalDate data, BigDecimal valor, TipoDeLancamento tipo) {
}
