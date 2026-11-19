package cerne.extratos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import cerne.contas.Agencia;
import cerne.contas.Conta;
import cerne.cooperados.Cooperado;
import cerne.cooperados.RepositorioDeCooperados;
import cerne.lancamentos.Lancamento;
import cerne.lancamentos.RepositorioDeLancamentos;

@Stateless
public class ServicoDeExtrato {

    @Inject
    private RepositorioDeCooperados cooperados;

    @Inject
    private RepositorioDeLancamentos lancamentos;

    public ServicoDeExtrato() {
    }

    public ServicoDeExtrato(RepositorioDeCooperados cooperados, RepositorioDeLancamentos lancamentos) {
        this.cooperados = cooperados;
        this.lancamentos = lancamentos;
    }

    public String cabecalhoDoExtrato(String cpf) {
        Optional<Cooperado> cooperado = cooperados.buscarPorCpf(cpf);
        String nomeDaAgencia = cooperado
                .map(Cooperado::getContaPrincipal)
                .map(Conta::agencia)
                .map(Agencia::nome)
                .filter(nome -> !nome.isBlank())
                .orElse("Agência não informada");
        String nomeDoCooperado = cooperado.map(Cooperado::getNome).orElse("");
        return "EXTRATO - " + nomeDaAgencia + " - " + nomeDoCooperado;
    }

    public List<Lancamento> lancamentosDoPeriodo(String cpf, LocalDate inicio, LocalDate fim) {
        return cooperados.buscarPorCpf(cpf)
                .map(Cooperado::getContaPrincipal)
                .map(conta -> lancamentos.buscarPorConta(conta.numero(), inicio, fim))
                .orElse(List.of());
    }

    /** Recebe os lançamentos em ordem cronológica; o extrato mostra o mais recente primeiro. */
    public Extrato montarExtrato(Conta conta, List<Lancamento> lancamentosCronologicos) {
        if (lancamentosCronologicos.isEmpty()) {
            return Extrato.vazio(conta.numero());
        }

        //getFirst, getLast
        List<Lancamento> maisRecentesPrimeiro =
                Collections.unmodifiableList(lancamentosCronologicos).reversed();
        return new Extrato(conta.numero(), lancamentosCronologicos.getFirst().data(), lancamentosCronologicos.getLast().data(), maisRecentesPrimeiro);
    }
}
