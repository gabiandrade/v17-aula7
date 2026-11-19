package cerne.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

import cerne.lancamentos.Credito;
import cerne.lancamentos.Lancamento;
import cerne.lancamentos.TipoDeLancamento;

/**
 * Lançamento como sai na API. Record: equals cobre todos os componentes (inclusive origem), sem setters e
 * sem Lombok. O construtor compacto roda também na desserialização JSON.
 */
public record LancamentoDTO(String contaId, LocalDate data, BigDecimal valor,
                            TipoDeLancamento tipo,
                            String origem) {

    public LancamentoDTO {
        Objects.requireNonNull(contaId, "contaId");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(tipo, "tipo");
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException("valor deve ser positivo: " + valor);
        }
        valor = valor.setScale(2, RoundingMode.HALF_EVEN);
        origem = origem == null ? "" : origem.strip();
    }

    public static LancamentoDTO de(Lancamento lancamento) {
        // pattern matching para instanceof: sem cast
        String origem = lancamento instanceof Credito credito ? credito.origem() : "";
        return new LancamentoDTO(lancamento.conta(), lancamento.data(), lancamento.valor(), lancamento.tipo(), origem);
    }
}
