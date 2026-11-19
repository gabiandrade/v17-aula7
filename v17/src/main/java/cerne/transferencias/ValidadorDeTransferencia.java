package cerne.transferencias;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import cerne.contas.Conta;
import cerne.contas.RepositorioDeContas;
import cerne.cooperados.Cooperado;
import cerne.cooperados.Cpf;
import cerne.cooperados.TipoCooperado;

/**
 * Ainda o método de ifs encadeados (alvo do encontro 8): a ordem é implícita, a primeira falha interrompe
 * e cada regra conhece as outras. Os records já garantem valor positivo e destino preenchido.
 */
@ApplicationScoped
public class ValidadorDeTransferencia {

    private static final BigDecimal LIMITE_PF_OUTRO_BANCO = new BigDecimal("5000.00");
    private static final BigDecimal LIMITE_PJ_OUTRO_BANCO = new BigDecimal("50000.00");
    private static final LocalTime ABERTURA = LocalTime.of(8, 0);
    private static final LocalTime FECHAMENTO = LocalTime.of(18, 0);

    private RepositorioDeContas contas;
    private RepositorioDeTransferencias transferencias;
    private Clock relogio;

    protected ValidadorDeTransferencia() {
    }

    @Inject
    public ValidadorDeTransferencia(RepositorioDeContas contas, RepositorioDeTransferencias transferencias,
                                    Clock relogio) {
        this.contas = contas;
        this.transferencias = transferencias;
        this.relogio = relogio;
    }

    public void validar(SolicitacaoDeTransferencia s) {
        if (s == null) {
            throw new TransferenciaInvalidaException("Solicitação nula");
        }
        if (s.valor().signum() <= 0) {
            throw new TransferenciaInvalidaException("Valor deve ser positivo");
        }
        Conta origem = contas.buscarPorNumero(s.contaDeOrigem())
                .orElseThrow(() -> new TransferenciaInvalidaException("Conta de origem inexistente"));
        if (!origem.ativa()) {
            throw new TransferenciaInvalidaException("Conta de origem bloqueada");
        }
        Cooperado titular = origem.titular();
        if (titular == null) {
            throw new TransferenciaInvalidaException("Conta de origem sem titular");
        }
        if (!Cpf.ehValido(titular.getCpf())) {
            throw new TransferenciaInvalidaException("CPF do titular inválido");
        }
        TipoDeTransferencia tipo = s.transferencia().tipo();
        if (tipo == TipoDeTransferencia.INTERNA) {
            Conta destino = contas.buscarPorNumero(s.contaDeDestino()).orElse(null);
            if (destino == null) {
                throw new TransferenciaInvalidaException("Conta de destino inexistente");
            } else {
                if (!destino.ativa()) {
                    throw new TransferenciaInvalidaException("Conta de destino bloqueada");
                }
                if (destino.numero().equals(origem.numero())) {
                    throw new TransferenciaInvalidaException("Origem e destino são a mesma conta");
                }
            }
        } else if (tipo == TipoDeTransferencia.ENTRE_COOPERATIVAS) {
            if (s.contaDeDestino().equals(origem.numero())) {
                throw new TransferenciaInvalidaException("Conta de destino inválida");
            }
        } else {
            LocalTime agora = LocalTime.now(relogio);
            if (agora.isBefore(ABERTURA) || !agora.isBefore(FECHAMENTO)) {
                throw new TransferenciaInvalidaException("Transferência para outro banco fora do horário comercial");
            }
        }
        if (origem.saldo().compareTo(s.valor()) < 0) {
            if (titular.getTipo() == TipoCooperado.PESSOA_JURIDICA
                    && origem.limiteDeChequeEspecial().isPresent()
                    && origem.saldo().add(origem.limiteDeChequeEspecial().get()).compareTo(s.valor()) >= 0) {
                // permitido: pessoa jurídica usa o cheque especial
            } else {
                throw new TransferenciaInvalidaException("Saldo insuficiente");
            }
        }
        BigDecimal totalDoDia = transferencias.totalDoDia(origem.numero(), LocalDate.now(relogio));
        if (totalDoDia.add(s.valor()).compareTo(origem.limiteDiario()) > 0) {
            throw new TransferenciaInvalidaException("Limite diário excedido");
        }
        if (tipo == TipoDeTransferencia.PARA_OUTRO_BANCO && !titular.possuiConvenioEspecial()) {
            if (titular.getTipo() == TipoCooperado.PESSOA_FISICA
                    || titular.getTipo() == TipoCooperado.PRODUTOR_RURAL) {
                if (s.valor().compareTo(LIMITE_PF_OUTRO_BANCO) > 0) {
                    throw new TransferenciaInvalidaException("Limite por transferência para pessoa física excedido");
                }
            } else if (titular.getTipo() == TipoCooperado.PESSOA_JURIDICA
                    || titular.getTipo() == TipoCooperado.ASSOCIACAO) {
                if (s.valor().compareTo(LIMITE_PJ_OUTRO_BANCO) > 0) {
                    throw new TransferenciaInvalidaException("Limite por transferência para pessoa jurídica excedido");
                }
            }
        }
    }
}
