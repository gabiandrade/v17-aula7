package cerne.transferencias;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import cerne.contas.Conta;

/** O serviço real: valida, tarifa e movimenta saldos. Auditoria e deduplicação entram por decoradores. */
@ApplicationScoped
public class ServicoDeTransferenciaPadrao implements ServicoDeTransferencia {

    private ValidadorDeTransferencia validador;
    private CalculadoraDeTarifa calculadora;
    private RepositorioDeTransferencias transferencias;
    private Clock relogio;

    protected ServicoDeTransferenciaPadrao() {
    }

    @Inject
    public ServicoDeTransferenciaPadrao(ValidadorDeTransferencia validador,
                                        CalculadoraDeTarifa calculadora, RepositorioDeTransferencias transferencias,
                                        Clock relogio) {
        this.validador = validador;
        this.calculadora = calculadora;
        this.transferencias = transferencias;
        this.relogio = relogio;
    }

    @Override
    @Transactional
    public ComprovanteDeTransferencia transferir(SolicitacaoDeTransferencia solicitacao) {
        validador.validar(solicitacao);
        BigDecimal tarifa = calculadora.calcular(solicitacao.transferencia());
        Conta origem = solicitacao.transferencia().origem();
        origem.debitar(solicitacao.valor().add(tarifa));
        if (solicitacao.transferencia() instanceof TransferenciaInterna interna) {
            interna.destino().creditar(interna.valor());
        }
        transferencias.registrar(origem.numero(), LocalDate.now(relogio), solicitacao.valor());
        return new ComprovanteDeTransferencia(identificador(), solicitacao, tarifa, Instant.now(relogio));
    }

    private static String identificador() {
        return "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
