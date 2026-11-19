package cerne.lancamentos;

import java.time.LocalDate;
import java.util.List;

public interface RepositorioDeLancamentos {

    void salvar(Lancamento lancamento);

    /** Lista vazia quando a conta não tem lançamento no período. */
    List<Lancamento> buscarPorConta(String numeroDaConta, LocalDate inicio, LocalDate fim);
}
