package cerne.cooperados;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CpfTest {

    @ParameterizedTest(name = "{0} válido? {1}")
    @CsvSource({
            "529.982.247-25, true",
            "11144477735,    true",
            "12345678909,    true",
            "52998224700,    false",
            "11111111111,    false"
    })
    @DisplayName("confere os dígitos verificadores")
    void confereDigitos(String numero, boolean esperado) {
        assertEquals(esperado, Cpf.ehValido(numero));
    }

    @Test
    @DisplayName("normaliza pontuação e formata")
    void normalizaEFormata() {
        var cpf = new Cpf("529.982.247-25");
        assertEquals("52998224725", cpf.numero());
        assertEquals("529.982.247-25", cpf.formatado());
    }

    @Test
    @DisplayName("rejeita tamanho errado no construtor compacto")
    void rejeitaTamanhoErrado() {
        assertThrows(IllegalArgumentException.class, () -> new Cpf("123"));
    }
}
