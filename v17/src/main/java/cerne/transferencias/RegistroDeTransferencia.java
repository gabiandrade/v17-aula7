package cerne.transferencias;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Uma transferência como aparece no Cerne ou no arquivo diário da central. */
public record RegistroDeTransferencia(String identificador, BigDecimal valor, LocalDate data) {
}
