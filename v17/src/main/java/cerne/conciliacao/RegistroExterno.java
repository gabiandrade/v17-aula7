package cerne.conciliacao;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Registro enviado pela outra cooperativa. */
public record RegistroExterno(String identificador, BigDecimal valor, LocalDate data) {
}
