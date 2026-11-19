package cerne.contas;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import cerne.cooperados.Cooperado;
import cerne.cooperados.TipoCooperado;

/**
 * Conta é entidade (identidade e saldo mutável): não vira record. Os acessores seguem o estilo dos records
 * (numero(), saldo()) para ler igual ao resto do domínio.
 */
@Entity
public class Conta {

    @Id
    private String numero;
    @Transient
    private Agencia agencia;
    @ManyToOne
    private Cooperado titular;
    private BigDecimal saldo = BigDecimal.ZERO;
    private boolean ativa = true;
    private BigDecimal limiteDiario = new BigDecimal("10000.00");
    private BigDecimal limiteDeChequeEspecial;

    protected Conta() {
    }

    public Conta(String numero, Agencia agencia, Cooperado titular, BigDecimal saldo) {
        this.numero = numero;
        this.agencia = agencia;
        this.titular = titular;
        this.saldo = saldo;
    }

    public void debitar(BigDecimal valor) {
        saldo = saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor) {
        saldo = saldo.add(valor);
    }

    public void bloquear() {
        ativa = false;
    }

    public void alterarAgencia(Agencia agencia) {
        this.agencia = agencia;
    }

    public void alterarLimiteDiario(BigDecimal limiteDiario) {
        this.limiteDiario = limiteDiario;
    }

    public void alterarChequeEspecial(BigDecimal limite) {
        this.limiteDeChequeEspecial = limite;
    }

    public String numero() {
        return numero;
    }

    public Agencia agencia() {
        return agencia;
    }

    public Cooperado titular() {
        return titular;
    }

    public TipoCooperado tipoDoTitular() {
        return titular.getTipo();
    }

    public BigDecimal saldo() {
        return saldo;
    }

    public boolean ativa() {
        return ativa;
    }

    public BigDecimal limiteDiario() {
        return limiteDiario;
    }

    /** Optional como retorno, não como atributo: o campo continua podendo ser null. */
    public Optional<BigDecimal> limiteDeChequeEspecial() {
        return Optional.ofNullable(limiteDeChequeEspecial);
    }
}
