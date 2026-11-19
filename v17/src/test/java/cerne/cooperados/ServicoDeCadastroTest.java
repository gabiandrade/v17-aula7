package cerne.cooperados;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ServicoDeCadastroTest {

    private RepositorioDeCooperadosEmMemoria repositorio;
    private ServicoDeCadastro servico;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioDeCooperadosEmMemoria();
        servico = new ServicoDeCadastro(repositorio);
    }

    private void assertRejeita(String cpf, String nome, TipoCooperado tipo, String mensagem) {
        var erro = assertThrows(CadastroInvalidoException.class, () -> servico.cadastrar(cpf, nome, tipo));
        assertEquals(mensagem, erro.getMessage());
    }

    @Test
    @DisplayName("cadastra cooperado válido com nome sem espaços nas pontas")
    void cadastraCooperadoValido() {
        servico.cadastrar("52998224725", "  Ana Souza ", TipoCooperado.PESSOA_FISICA);
        assertEquals("Ana Souza", repositorio.buscarPorCpf("52998224725").orElseThrow().getNome());
    }

    @Test
    void rejeitaCpfInvalido() {
        assertRejeita("52998224700", "Ana", TipoCooperado.PESSOA_FISICA, "CPF inválido: 52998224700");
    }

    @Test
    void rejeitaCpfDuplicado() {
        servico.cadastrar("52998224725", "Ana", TipoCooperado.PESSOA_FISICA);
        assertRejeita("52998224725", "Outra Ana", TipoCooperado.PESSOA_FISICA, "CPF já cadastrado: 52998224725");
    }

    @Test
    void rejeitaNomeCurto() {
        assertRejeita("52998224725", "Al", TipoCooperado.PESSOA_FISICA, "Nome muito curto");
    }

    @Test
    void rejeitaTipoAusente() {
        assertRejeita("52998224725", "Ana", null, "Tipo de cooperado obrigatório");
    }
}
