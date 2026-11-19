package cerne.consultaexterna;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cerne.cooperados.Cpf;

/**
 * Decorator de cache (encontro 7): a consulta é lenta e cobrada. O Clock injetado permite testar a
 * expiração sem esperar uma hora.
 */
public class ConsultorDeCpfComCache implements ConsultorDeCpf {

    private record Entrada(SituacaoCpf situacao, Instant expiraEm) {
    }

    private final ConsultorDeCpf delegado;
    private final Duration validade;
    private final Clock relogio;
    private final ConcurrentMap<Cpf, Entrada> cache = new ConcurrentHashMap<>();

    public ConsultorDeCpfComCache(ConsultorDeCpf delegado, Duration validade, Clock relogio) {
        this.delegado = delegado;
        this.validade = validade;
        this.relogio = relogio;
    }

    @Override
    public SituacaoCpf consultar(Cpf cpf) {
        var agora = relogio.instant();
        var entrada = cache.compute(cpf, (chave, atual) ->
                atual != null && atual.expiraEm().isAfter(agora)
                        ? atual
                        : new Entrada(delegado.consultar(chave), agora.plus(validade)));
        return entrada.situacao();
    }
}
