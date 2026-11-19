package cerne.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import cerne.lancamentos.TipoDeLancamento;

class LancamentoDTOTest {

    private final ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    @DisplayName("equals cobre todos os componentes, inclusive origem")
    void equalsCobreOrigem() {
        var dia = LocalDate.of(2026, 8, 5);
        assertNotEquals(new LancamentoDTO("0001-7", dia, BigDecimal.TEN, TipoDeLancamento.CREDITO, "CAIXA"),
                new LancamentoDTO("0001-7", dia, BigDecimal.TEN, TipoDeLancamento.CREDITO, "PIX"));
    }

    @Test
    @DisplayName("JSON → record → JSON, sem anotação: o construtor compacto normaliza na desserialização")
    void idaEVoltaEmJson() throws Exception {
        var corpo = """
                {
                  "contaId": "0001-7",
                  "data": "2026-08-05",
                  "valor": 150.0,
                  "tipo": "CREDITO",
                  "origem": "  DEPOSITO_CAIXA  "
                }
                """;
        var dto = json.readValue(corpo, LancamentoDTO.class);
        assertEquals(new LancamentoDTO("0001-7", LocalDate.of(2026, 8, 5), new BigDecimal("150.00"),
                TipoDeLancamento.CREDITO, "DEPOSITO_CAIXA"), dto);
        assertEquals(dto, json.readValue(json.writeValueAsString(dto), LancamentoDTO.class));
    }

    @Test
    @DisplayName("a validação do construtor compacto protege a borda da API")
    void valorNegativoNaoEntra() {
        var corpo = """
                {"contaId": "0001-7", "data": "2026-08-05", "valor": -1, "tipo": "CREDITO"}
                """;
        assertThrows(ValueInstantiationException.class, () -> json.readValue(corpo, LancamentoDTO.class));
    }
}
