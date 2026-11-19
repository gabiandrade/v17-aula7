package cerne.medicao;

import java.math.BigDecimal;

/** O que o processamento produziu; os totais provam que as versões fizeram o mesmo trabalho. */
public final class ResumoDeProcessamento {

    private final int totalDeLancamentos;
    private final int totalDeContas;
    private final BigDecimal saldoConsolidado;

    public ResumoDeProcessamento(int totalDeLancamentos, int totalDeContas, BigDecimal saldoConsolidado) {
        this.totalDeLancamentos = totalDeLancamentos;
        this.totalDeContas = totalDeContas;
        this.saldoConsolidado = saldoConsolidado;
    }

    public int totalDeLancamentos() {
        return totalDeLancamentos;
    }

    public int totalDeContas() {
        return totalDeContas;
    }

    public BigDecimal saldoConsolidado() {
        return saldoConsolidado;
    }
}
