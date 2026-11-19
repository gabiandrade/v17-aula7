package cerne.lancamentos;

public enum TipoDeLancamento {
    CREDITO,
    DEBITO,
    TARIFA,
    ESTORNO;

    /** Crédito e estorno entram na conta; débito e tarifa saem. */
    public boolean ehEntrada() {
        return this == CREDITO || this == ESTORNO;
    }
}
