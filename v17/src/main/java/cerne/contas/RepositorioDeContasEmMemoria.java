package cerne.contas;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RepositorioDeContasEmMemoria implements RepositorioDeContas {

    private final Map<String, Conta> porNumero = new HashMap<>();

    public void adicionar(Conta conta) {
        porNumero.put(conta.numero(), conta);
    }

    @Override
    public Optional<Conta> buscarPorNumero(String numero) {
        return Optional.ofNullable(porNumero.get(numero));
    }
}
