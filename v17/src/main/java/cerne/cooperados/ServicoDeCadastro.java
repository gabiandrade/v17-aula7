package cerne.cooperados;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

/** Cadastro de cooperados: ifs em sequência, a primeira falha interrompe. */
@Stateless
public class ServicoDeCadastro {

    @Inject
    private RepositorioDeCooperados cooperados;

    public ServicoDeCadastro() {
    }

    public ServicoDeCadastro(RepositorioDeCooperados cooperados) {
        this.cooperados = cooperados;
    }

    public Cooperado cadastrar(String cpf, String nome, TipoCooperado tipo) {
        if (cpf == null || cpf.isBlank()) {
            throw new CadastroInvalidoException("CPF obrigatório");
        }
        if (!Cpf.ehValido(cpf)) {
            throw new CadastroInvalidoException("CPF inválido: " + cpf);
        }
        if (nome == null || nome.isBlank()) {
            throw new CadastroInvalidoException("Nome obrigatório");
        }
        if (nome.strip().length() < 3) {
            throw new CadastroInvalidoException("Nome muito curto");
        }
        if (tipo == null) {
            throw new CadastroInvalidoException("Tipo de cooperado obrigatório");
        }
        if (cooperados.buscarPorCpf(cpf).isPresent()) {
            throw new CadastroInvalidoException("CPF já cadastrado: " + cpf);
        }
        var cooperado = new Cooperado(cpf, nome.strip(), tipo);
        cooperados.salvar(cooperado);
        return cooperado;
    }
}
