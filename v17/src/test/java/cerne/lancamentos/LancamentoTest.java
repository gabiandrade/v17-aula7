package cerne.lancamentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LancamentoTest {

    @Test
    @DisplayName("construtor compacto normaliza o valor em duas casas: 0.1 e 0.10 são iguais")
    void normalizaEscala() {
        var dia = LocalDate.of(2026, 8, 5);
        assertEquals(new Debito("0001-1", new BigDecimal("0.10"), dia), new Debito("0001-1", new BigDecimal("0.1"), dia));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1.00"})
    @DisplayName("rejeita valor zero ou negativo")
    void rejeitaValorNaoPositivo(String valor) {
        assertThrows(IllegalArgumentException.class,
                () -> new Credito("0001-1", new BigDecimal(valor), LocalDate.of(2026, 8, 5), "x"));
    }

    @Test
    @DisplayName("resumo é record: o tipo entra no equals")
    void resumoComparaOTipo() {
        var dia = LocalDate.of(2026, 8, 5);
        var credito = new ResumoDeLancamento(1, dia, BigDecimal.TEN, TipoDeLancamento.CREDITO);
        var debito = new ResumoDeLancamento(1, dia, BigDecimal.TEN, TipoDeLancamento.DEBITO);
        assertEquals(false, credito.equals(debito));
    }
}
