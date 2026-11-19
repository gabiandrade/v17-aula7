package cerne.transferencias;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RepositorioDeTransferencias {

    void registrar(String numeroDaConta, LocalDate dia, BigDecimal valor);

    BigDecimal totalDoDia(String numeroDaConta, LocalDate dia);
}
