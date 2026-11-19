package cerne.lancamentos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import cerne.lancamentos.importacao.LinhaDoArquivo;

class FabricaDeLancamentosTest {

    @ParameterizedTest(name = "código {0} cria {1}")
    @CsvSource({"C, Credito", "D, Debito", "T, Tarifa", "E, Estorno"})
    @DisplayName("cria o tipo concreto certo a partir do código do arquivo")
    void criaOTipoConcretoCerto(String codigo, String classeEsperada) {
        var linha = LinhaDoArquivo.deTeste(codigo);
        assertEquals(classeEsperada, FabricaDeLancamentos.aPartirDe(linha).getClass().getSimpleName());
    }

    @Test
    @DisplayName("rejeita código desconhecido")
    void rejeitaCodigoDesconhecido() {
        assertThrows(TipoDeLancamentoDesconhecidoException.class,
                () -> FabricaDeLancamentos.aPartirDe(LinhaDoArquivo.deTeste("X")));
    }
}
