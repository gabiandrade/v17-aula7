package cerne.transferencias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Validação nos construtores compactos (desafio do encontro 4). */
class TransferenciaTest {

    @ParameterizedTest(name = "valor {0}")
    @CsvSource({"0", "-10.00"})
    @DisplayName("nenhum dos três tipos aceita valor zero ou negativo")
    void rejeitaValorNaoPositivo(BigDecimal valor) {
        var origem = Cenario.contaPf("0001-1", "100.00");
        assertThrows(IllegalArgumentException.class,
                () -> new TransferenciaInterna(origem, Cenario.contaPf("0002-2", "0.00"), valor));
        assertThrows(IllegalArgumentException.class,
                () -> new TransferenciaEntreCooperativas(origem, new ContaCooperativa("0102", "1"), valor));
        assertThrows(IllegalArgumentException.class,
                () -> new TransferenciaParaOutroBanco(origem, Cenario.CONTA_EXTERNA, valor));
    }

    @ParameterizedTest(name = "ispb=''{0}'' agencia=''{1}'' conta=''{2}''")
    @CsvSource({"'', 1234, 99", "00000208, ' ', 99", "00000208, 1234, ''", "208, 1234, 99"})
    @DisplayName("identificadores de destino não podem estar em branco")
    void rejeitaDestinoEmBranco(String ispb, String agencia, String numero) {
        assertThrows(IllegalArgumentException.class, () -> new ContaExterna(ispb, agencia, numero));
    }

    @Test
    @DisplayName("solicitação expõe valor, origem e destino de qualquer tipo")
    void solicitacaoExpoeOsDadosDoPedido() {
        var origem = Cenario.contaPf("0001-1", "100.00");
        var solicitacao = SolicitacaoDeTransferencia.de(
                new TransferenciaEntreCooperativas(origem, new ContaCooperativa("0102", "7788-1"), new BigDecimal("5")));
        assertEquals(new BigDecimal("5.00"), solicitacao.valor());
        assertEquals("0001-1", solicitacao.contaDeOrigem());
        assertEquals("7788-1", solicitacao.contaDeDestino());
        assertInstanceOf(TransferenciaEntreCooperativas.class, solicitacao.transferencia());
    }
}
