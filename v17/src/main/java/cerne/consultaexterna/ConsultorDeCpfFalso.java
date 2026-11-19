package cerne.consultaexterna;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cerne.cooperados.Cpf;

/** Simulador local do serviço de CPF: nunca acessa a rede. Usado nos testes e nas medições. */
public class ConsultorDeCpfFalso implements ConsultorDeCpf {

    private final long latenciaEmMilis;
    private final Map<Cpf, SituacaoCpf> situacoes = new ConcurrentHashMap<>();

    public ConsultorDeCpfFalso() {
        this(0);
    }

    /** A latência simula a espera de rede com Thread.sleep (que libera a thread como I/O real). */
    public ConsultorDeCpfFalso(long latenciaEmMilis) {
        this.latenciaEmMilis = latenciaEmMilis;
    }

    public ConsultorDeCpfFalso com(Cpf cpf, SituacaoCpf situacao) {
        situacoes.put(cpf, situacao);
        return this;
    }

    @Override
    public SituacaoCpf consultar(Cpf cpf) {
        if (latenciaEmMilis > 0) {
            try {
                Thread.sleep(latenciaEmMilis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Consulta interrompida", e);
            }
        }
        return situacoes.getOrDefault(cpf, SituacaoCpf.REGULAR);
    }
}
