package cerne.lancamentos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepositorioDeLancamentosEmMemoria implements RepositorioDeLancamentos {

    private final Map<String, List<Lancamento>> porConta = new HashMap<>();

    @Override
    public void salvar(Lancamento lancamento) {
        porConta.computeIfAbsent(lancamento.conta(), numero -> new ArrayList<>()).add(lancamento);
    }

    @Override
    public List<Lancamento> buscarPorConta(String numeroDaConta, LocalDate inicio, LocalDate fim) {
        return porConta.getOrDefault(numeroDaConta, List.of()).stream()
                .filter(lancamento -> lancamento.estaEntre(inicio, fim))
                .toList();
    }
}
