package cerne.transferencias;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import cerne.contas.Agencia;
import cerne.contas.Conta;
import cerne.contas.RepositorioDeContasEmMemoria;
import cerne.cooperados.Cooperado;
import cerne.cooperados.TipoCooperado;

/** Dados de teste compartilhados pelos testes de transferência. */
final class Cenario {

    static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");
    static final Clock SEGUNDA_10H = relogio(LocalDateTime.of(2026, 8, 3, 10, 0));
    static final Clock SEGUNDA_20H = relogio(LocalDateTime.of(2026, 8, 3, 20, 0));
    static final Agencia AGENCIA = new Agencia("0001", "Agência Centro");
    static final ContaExterna CONTA_EXTERNA = new ContaExterna("00000208", "1234", "99887-6");

    private Cenario() {
    }

    static Clock relogio(LocalDateTime momento) {
        return Clock.fixed(momento.atZone(FUSO).toInstant(), FUSO);
    }

    static Conta conta(String numero, Cooperado titular, String saldo) {
        var conta = new Conta(numero, AGENCIA, titular, new BigDecimal(saldo));
        titular.adicionarConta(conta);
        return conta;
    }

    static Conta contaPf(String numero, String saldo) {
        return conta(numero, new Cooperado("52998224725", "Ana", TipoCooperado.PESSOA_FISICA), saldo);
    }

    static Conta contaPj(String numero, String saldo) {
        return conta(numero, new Cooperado("11144477735", "Cooperativa Agro Ltda", TipoCooperado.PESSOA_JURIDICA), saldo);
    }

    /** Cooperado com convênio especial: sem limite por transferência para outro banco. */
    static Conta contaComConvenio(String numero, String saldo) {
        return conta(numero, new Cooperado("12345678909", "Bruno", TipoCooperado.PESSOA_FISICA, true), saldo);
    }

    static RepositorioDeContasEmMemoria repositorio(Conta... contas) {
        var repositorio = new RepositorioDeContasEmMemoria();
        for (Conta conta : contas) {
            repositorio.adicionar(conta);
        }
        return repositorio;
    }

    static SolicitacaoDeTransferencia solicitacao(Transferencia transferencia) {
        return new SolicitacaoDeTransferencia(transferencia, new ChaveDeIdempotencia("pedido-1"));
    }
}
