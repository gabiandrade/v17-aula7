package cerne.transferencias;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RepositorioDeTransferenciasEmMemoria implements RepositorioDeTransferencias {

    private record ContaNoDia(String numeroDaConta, LocalDate dia) {
    }

    private final Map<ContaNoDia, BigDecimal> totais = new HashMap<>();

    @Override
    public void registrar(String numeroDaConta, LocalDate dia, BigDecimal valor) {
        totais.merge(new ContaNoDia(numeroDaConta, dia), valor, BigDecimal::add);
    }

    @Override
    public BigDecimal totalDoDia(String numeroDaConta, LocalDate dia) {
        return totais.getOrDefault(new ContaNoDia(numeroDaConta, dia), BigDecimal.ZERO);
    }
}
