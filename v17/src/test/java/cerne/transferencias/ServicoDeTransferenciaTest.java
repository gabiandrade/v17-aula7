package cerne.transferencias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import cerne.auditoria.RegistroDeAuditoriaEmMemoria;
import cerne.auditoria.ServicoDeTransferenciaAuditado;
import cerne.contas.Conta;

class ServicoDeTransferenciaTest {

    @Nested
    @DisplayName("serviço padrão")
    class Padrao {

        private RepositorioDeTransferenciasEmMemoria historico;
        private Conta origem;
        private Conta destino;
        private ServicoDeTransferencia servico;

        @BeforeEach
        void preparar() {
            historico = new RepositorioDeTransferenciasEmMemoria();
            origem = Cenario.contaPf("0001-1", "1000.00");
            destino = Cenario.contaPj("0002-2", "0.00");
            var validador = new ValidadorDeTransferencia(Cenario.repositorio(origem, destino), historico,
                    Cenario.SEGUNDA_10H);
            servico = new ServicoDeTransferenciaPadrao(validador, new CalculadoraDeTarifa(), historico,
                    Cenario.SEGUNDA_10H);
        }

        @Test
        @DisplayName("interna movimenta os dois saldos e registra no histórico do dia")
        void internaMovimentaOsDoisSaldos() {
            servico.transferir(Cenario.solicitacao(new TransferenciaInterna(origem, destino, new BigDecimal("150.00"))));
            assertEquals(new BigDecimal("850.00"), origem.saldo());
            assertEquals(new BigDecimal("150.00"), destino.saldo());
            assertEquals(new BigDecimal("150.00"), historico.totalDoDia("0001-1", LocalDate.of(2026, 8, 3)));
        }

        @Test
        @DisplayName("outro banco debita valor mais tarifa")
        void outroBancoDebitaValorMaisTarifa() {
            var comprovante = servico.transferir(Cenario.solicitacao(
                    new TransferenciaParaOutroBanco(origem, Cenario.CONTA_EXTERNA, new BigDecimal("500.00"))));
            assertEquals(new BigDecimal("497.00"), origem.saldo());
            assertEquals(new BigDecimal("3.00"), comprovante.tarifa());
        }

        @Test
        @DisplayName("transferência rejeitada não mexe no saldo")
        void rejeitadaNaoMexeNoSaldo() {
            assertThrows(TransferenciaInvalidaException.class, () -> servico.transferir(
                    Cenario.solicitacao(new TransferenciaInterna(origem, destino, new BigDecimal("5000.00")))));
            assertEquals(new BigDecimal("1000.00"), origem.saldo());
        }
    }

    @Nested
    @DisplayName("decoradores")
    class Decoradores {

        private final SolicitacaoDeTransferencia solicitacao = Cenario.solicitacao(new TransferenciaInterna(
                Cenario.contaPf("0001-1", "100.00"), Cenario.contaPj("0002-2", "0.00"), BigDecimal.TEN));
        private final ComprovanteDeTransferencia comprovanteFixo =
                new ComprovanteDeTransferencia("TRF-FIXO", solicitacao, BigDecimal.ZERO, Instant.EPOCH);

        @Test
        @DisplayName("auditado registra tentativa e sucesso")
        void auditadoRegistraTentativaESucesso() {
            ServicoDeTransferencia falso = s -> comprovanteFixo;
            var registro = new RegistroDeAuditoriaEmMemoria();
            new ServicoDeTransferenciaAuditado(falso, registro).transferir(solicitacao);
            assertEquals(List.of("TENTATIVA", "SUCESSO"), registro.eventos());
        }

        @Test
        @DisplayName("auditado registra a falha e relança")
        void auditadoRegistraFalha() {
            ServicoDeTransferencia quebrado = s -> {
                throw new TransferenciaInvalidaException("Saldo insuficiente");
            };
            var registro = new RegistroDeAuditoriaEmMemoria();
            var auditado = new ServicoDeTransferenciaAuditado(quebrado, registro);
            assertThrows(TransferenciaInvalidaException.class, () -> auditado.transferir(solicitacao));
            assertEquals(List.of("TENTATIVA", "FALHA"), registro.eventos());
        }

        @Test
        @DisplayName("deduplicação chama o delegado uma vez para a mesma chave")
        void deduplicacaoChamaODelegadoUmaVez() {
            var chamadas = new AtomicInteger();
            ServicoDeTransferencia padrao = s -> {
                chamadas.incrementAndGet();
                return comprovanteFixo;
            };
            var servico = new ServicoDeTransferenciaComDeduplicacao(padrao, new ConcurrentHashMap<>());
            var primeiro = servico.transferir(solicitacao);
            var segundo = servico.transferir(solicitacao);
            assertEquals(1, chamadas.get());
            assertSame(primeiro, segundo);
        }

        @Test
        @DisplayName("auditoria por fora vê a tentativa duplicada")
        void auditoriaVeATentativaDuplicadaQuandoFicaPorFora() {
            var registro = new RegistroDeAuditoriaEmMemoria();
            var chamadas = new AtomicInteger();
            ServicoDeTransferencia padrao = s -> {
                chamadas.incrementAndGet();
                return comprovanteFixo;
            };

            ServicoDeTransferencia servico =
                    new ServicoDeTransferenciaAuditado(
                            new ServicoDeTransferenciaComDeduplicacao(padrao, new ConcurrentHashMap<>()),
                            registro);

            servico.transferir(solicitacao);
            servico.transferir(solicitacao);

            assertEquals(1, chamadas.get());
            assertEquals(List.of("TENTATIVA", "SUCESSO", "TENTATIVA", "SUCESSO"), registro.eventos());
        }

        @Test
        @DisplayName("auditoria por dentro não vê a tentativa barrada")
        void auditoriaPorDentroNaoVeADuplicada() {
            var registro = new RegistroDeAuditoriaEmMemoria();
            ServicoDeTransferencia padrao = s -> comprovanteFixo;

            ServicoDeTransferencia servico =
                    new ServicoDeTransferenciaComDeduplicacao(
                            new ServicoDeTransferenciaAuditado(padrao, registro),
                            new ConcurrentHashMap<>());

            servico.transferir(solicitacao);
            servico.transferir(solicitacao);

            assertEquals(List.of("TENTATIVA", "SUCESSO"), registro.eventos());
        }
    }
}
