package cerne.consultaexterna;

import cerne.cooperados.Cpf;

/** Um único método: nos testes, uma lambda serve de dublê sem biblioteca de mocks. */
@FunctionalInterface
public interface ConsultorDeCpf {

    SituacaoCpf consultar(Cpf cpf);
}
