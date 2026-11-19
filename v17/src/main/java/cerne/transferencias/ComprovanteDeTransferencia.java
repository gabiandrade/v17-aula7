package cerne.transferencias;

import java.math.BigDecimal;
import java.time.Instant;

public record ComprovanteDeTransferencia(String identificador, SolicitacaoDeTransferencia solicitacao,
                                         BigDecimal tarifa, Instant momento) {
}
