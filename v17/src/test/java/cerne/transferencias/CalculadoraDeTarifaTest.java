package cerne.transferencias;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CalculadoraDeTarifaTest {

    private final CalculadoraDeTarifa calculadora = new CalculadoraDeTarifa();

    private static Transferencia transferencia(TipoDeTransferencia tipo, String valor) {
        var origem = Cenario.contaPf("0001-1", "10000.00");
        var quantia = new BigDecimal(valor);
        return switch (tipo) {
            case INTERNA -> new TransferenciaInterna(origem, Cenario.contaPf("0002-2", "0.00"), quantia);
            case ENTRE_COOPERATIVAS -> new TransferenciaEntreCooperativas(origem, new ContaCooperativa("0102", "7788-1"), quantia);
            case PARA_OUTRO_BANCO -> new TransferenciaParaOutroBanco(origem, Cenario.CONTA_EXTERNA, quantia);
        };
    }

    @ParameterizedTest(name = "{0} de {1} paga {2}")
    @CsvSource({
            "INTERNA,             500.00,  0.00",
            "ENTRE_COOPERATIVAS,  500.00,  1.50",
            "PARA_OUTRO_BANCO,    100.00,  0.00",
            "PARA_OUTRO_BANCO,    100.01,  3.00",
            "PARA_OUTRO_BANCO,    500.00,  3.00",
            "PARA_OUTRO_BANCO,   2000.00, 10.00"
    })
    @DisplayName("switch expression sobre o discriminador")
    void calculaPorSwitchExpression(TipoDeTransferencia tipo, BigDecimal valor, BigDecimal esperada) {
        assertEquals(esperada, calculadora.calcular(transferencia(tipo, valor.toPlainString())), "tarifa errada");
    }

    @ParameterizedTest(name = "{0} de {1} paga {2}")
    @CsvSource({
            "INTERNA,             500.00,  0.00",
            "ENTRE_COOPERATIVAS,  500.00,  1.50",
            "PARA_OUTRO_BANCO,    100.00,  0.00",
            "PARA_OUTRO_BANCO,   2000.00, 10.00"
    })
    @DisplayName("escada de instanceof com pattern matching dá o mesmo resultado")
    void calculaPorInstanceof(TipoDeTransferencia tipo, BigDecimal valor, BigDecimal esperada) {
        assertEquals(esperada, calculadora.calcularTarifa(transferencia(tipo, valor.toPlainString())));
    }
}
