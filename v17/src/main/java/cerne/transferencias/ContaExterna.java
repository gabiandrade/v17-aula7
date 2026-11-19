package cerne.transferencias;

/** Conta em outro banco; o banco é identificado pelo ISPB (8 dígitos). */
public record ContaExterna(String ispb, String agencia, String numero) {

    public ContaExterna {
        ispb = ValoresDeTransferencia.preenchido(ispb, "ISPB");
        if (!ispb.matches("\\d{8}")) {
            throw new IllegalArgumentException("ISPB do banco de destino deve ter 8 dígitos: " + ispb);
        }
        agencia = ValoresDeTransferencia.preenchido(agencia, "agência");
        numero = ValoresDeTransferencia.preenchido(numero, "número da conta");
    }
}
