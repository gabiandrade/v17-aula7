package cerne.contas;

import java.util.Optional;

/** Um único método abstrato: uma lambda serve de repositório falso nos testes. */
@FunctionalInterface
public interface RepositorioDeContas {

    Optional<Conta> buscarPorNumero(String numero);
}
