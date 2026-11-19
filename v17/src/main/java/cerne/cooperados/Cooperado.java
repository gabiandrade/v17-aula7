package cerne.cooperados;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cerne.contas.Conta;

/** Cooperado é entidade (identidade pelo CPF): continua classe, sem Lombok, com getters explícitos. */
public class Cooperado {

    private final String cpf;
    private String nome;
    private TipoCooperado tipo;
    private final boolean convenioEspecial;
    private final List<Conta> contas = new ArrayList<>();
    private Conta contaPrincipal;

    public Cooperado(String cpf, String nome, TipoCooperado tipo) {
        this(cpf, nome, tipo, false);
    }

    public Cooperado(String cpf, String nome, TipoCooperado tipo, boolean convenioEspecial) {
        this.cpf = cpf;
        this.nome = nome;
        this.tipo = tipo;
        this.convenioEspecial = convenioEspecial;
    }

    /** A primeira conta aberta vira a principal. */
    public void adicionarConta(Conta conta) {
        contas.add(conta);
        if (contaPrincipal == null) {
            contaPrincipal = conta;
        }
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public TipoCooperado getTipo() {
        return tipo;
    }

    /** Cooperado com convênio especial não tem limite por transferência. */
    public boolean possuiConvenioEspecial() {
        return convenioEspecial;
    }

    public List<Conta> getContas() {
        return List.copyOf(contas);
    }

    public Conta getContaPrincipal() {
        return contaPrincipal;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Cooperado outro && cpf.equals(outro.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }
}
