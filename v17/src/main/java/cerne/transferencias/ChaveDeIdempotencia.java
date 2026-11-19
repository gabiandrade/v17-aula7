package cerne.transferencias;

import java.util.UUID;

/** Identifica um pedido do cliente: o mesmo pedido reenviado tem a mesma chave. */
public record ChaveDeIdempotencia(String valor) {

    public ChaveDeIdempotencia {
        valor = ValoresDeTransferencia.preenchido(valor, "chave de idempotência");
    }

    public static ChaveDeIdempotencia nova() {
        return new ChaveDeIdempotencia(UUID.randomUUID().toString());
    }
}
