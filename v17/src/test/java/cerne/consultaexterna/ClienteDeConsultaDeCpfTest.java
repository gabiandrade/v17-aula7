package cerne.consultaexterna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.cooperados.Cpf;

/** Sem rede: testa só o que não depende do serviço externo. */
class ClienteDeConsultaDeCpfTest {

    @Test
    @DisplayName("cabeçalho Basic com java.util.Base64")
    void montaCabecalhoBasic() {
        assertEquals("Basic dXN1YXJpbzpzZW5oYQ==", ClienteDeConsultaDeCpf.cabecalhoDeAutenticacao("usuario", "senha"));
        assertEquals("usuario", ClienteDeConsultaDeCpf.usuarioDoCabecalho("Basic dXN1YXJpbzpzZW5oYQ=="));
    }

    @Test
    @DisplayName("stub devolve REGULAR por padrão")
    void stubDevolveRegular() {
        ConsultorDeCpf consultor = new ConsultorDeCpfFalso().com(new Cpf("11144477735"), SituacaoCpf.SUSPENSA);
        assertEquals(SituacaoCpf.REGULAR, consultor.consultar(new Cpf("52998224725")));
        assertEquals(SituacaoCpf.SUSPENSA, consultor.consultar(new Cpf("111.444.777-35")));
    }
}
