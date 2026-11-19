package cerne.transferencias;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Validação comum aos construtores compactos das transferências. */
final class ValoresDeTransferencia {

    private ValoresDeTransferencia() {
    }

    static BigDecimal positivo(BigDecimal valor) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("valor da transferência deve ser positivo: " + valor);
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    static String preenchido(String texto, String campo) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode estar em branco");
        }
        return texto.strip();
    }
}
