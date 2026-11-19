package cerne.lancamentos;

public class TipoDeLancamentoDesconhecidoException extends RuntimeException {

    public TipoDeLancamentoDesconhecidoException(String codigo) {
        super("Tipo de lançamento desconhecido: " + codigo);
    }
}
