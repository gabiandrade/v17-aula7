package cerne.lancamentos.importacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Uma linha do arquivo de lançamentos da central; o tipo vem como código ("C", "D", "T", "E"). */
public record LinhaDoArquivo(String tipo, String conta, BigDecimal valor, LocalDate data, String origem,
                             String codigoDaTarifa, UUID lancamentoOriginal) {

    private static final UUID ORIGINAL_DE_EXEMPLO = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /** Linha completa com o código pedido; usada em testes e exemplos. */
    public static LinhaDoArquivo deTeste(String codigo) {
        return new LinhaDoArquivo(codigo, "0001-1", new BigDecimal("10.00"), LocalDate.of(2026, 8, 5),
                "DEPOSITO_CAIXA", "TED", ORIGINAL_DE_EXEMPLO);
    }
}
