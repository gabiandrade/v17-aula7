package cerne.cooperados;

import java.util.List;
import java.util.Optional;

public interface RepositorioDeCooperados {

    /** A ausência está na assinatura: quem chama decide o que fazer. */
    Optional<Cooperado> buscarPorCpf(String cpf);

    List<Cooperado> listarTodos();

    void salvar(Cooperado cooperado);
}
