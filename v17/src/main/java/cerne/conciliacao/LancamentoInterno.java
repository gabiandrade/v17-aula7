package cerne.conciliacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Lançamento do Cerne como entra na conciliação com a outra cooperativa. */
public record LancamentoInterno(String identificador, BigDecimal valor, LocalDate data) {
}
