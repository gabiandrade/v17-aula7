package cerne.cooperados;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Repositório para testes e medições; sem banco. */
public class RepositorioDeCooperadosEmMemoria implements RepositorioDeCooperados {

    private final Map<String, Cooperado> porCpf = new LinkedHashMap<>();

    @Override
    public Optional<Cooperado> buscarPorCpf(String cpf) {
        return Optional.ofNullable(porCpf.get(cpf));
    }

    @Override
    public List<Cooperado> listarTodos() {
        return List.copyOf(porCpf.values());
    }

    @Override
    public void salvar(Cooperado cooperado) {
        porCpf.put(cooperado.getCpf(), cooperado);
    }
}
