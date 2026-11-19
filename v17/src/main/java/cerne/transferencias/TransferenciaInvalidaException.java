package cerne.transferencias;

public class TransferenciaInvalidaException extends RuntimeException {

    public TransferenciaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
