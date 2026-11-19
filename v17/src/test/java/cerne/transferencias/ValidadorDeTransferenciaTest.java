package cerne.transferencias;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.contas.Conta;

class ValidadorDeTransferenciaTest {

    private RepositorioDeTransferenciasEmMemoria historico;
    private Conta origem;
    private Conta destino;

    @BeforeEach
    void preparar() {
        historico = new RepositorioDeTransferenciasEmMemoria();
        origem = Cenario.contaPf("0001-1", "1000.00");
        destino = Cenario.contaPj("0002-2", "0.00");
    }

    private ValidadorDeTransferencia validador(Clock relogio, Conta... contas) {
        return new ValidadorDeTransferencia(Cenario.repositorio(contas), historico, relogio);
    }

    private void assertRejeita(Transferencia t, String mensagem) {
        var erro = assertThrows(TransferenciaInvalidaException.class,
                () -> validador(Cenario.SEGUNDA_10H, origem, destino).validar(Cenario.solicitacao(t)));
        assertEquals(mensagem, erro.getMessage());
    }

    @Test
    @DisplayName("aceita transferência interna válida")
    void aceitaInternaValida() {
        assertDoesNotThrow(() -> validador(Cenario.SEGUNDA_10H, origem, destino)
                .validar(Cenario.solicitacao(new TransferenciaInterna(origem, destino, new BigDecimal("100.00")))));
    }

    @Test
    void rejeitaOrigemBloqueada() {
        origem.bloquear();
        assertRejeita(new TransferenciaInterna(origem, destino, BigDecimal.TEN), "Conta de origem bloqueada");
    }

    @Test
    void rejeitaDestinoBloqueado() {
        destino.bloquear();
        assertRejeita(new TransferenciaInterna(origem, destino, BigDecimal.TEN), "Conta de destino bloqueada");
    }

    @Test
    void rejeitaSaldoInsuficiente() {
        assertRejeita(new TransferenciaInterna(origem, destino, new BigDecimal("1500.00")), "Saldo insuficiente");
    }

    @Test
    @DisplayName("cheque especial vale só para pessoa jurídica")
    void chequeEspecialSoParaPessoaJuridica() {
        var empresa = Cenario.contaPj("0003-3", "100.00");
        empresa.alterarChequeEspecial(new BigDecimal("1000.00"));
        origem.alterarChequeEspecial(new BigDecimal("1000.00"));
        var validador = validador(Cenario.SEGUNDA_10H, origem, destino, empresa);
        assertDoesNotThrow(() -> validador.validar(Cenario.solicitacao(
                new TransferenciaInterna(empresa, destino, new BigDecimal("900.00")))));
        assertThrows(TransferenciaInvalidaException.class, () -> validador.validar(Cenario.solicitacao(
                new TransferenciaInterna(origem, destino, new BigDecimal("1500.00")))));
    }

    @Test
    void rejeitaLimiteDiarioExcedido() {
        origem.alterarLimiteDiario(new BigDecimal("300.00"));
        historico.registrar("0001-1", LocalDate.of(2026, 8, 3), new BigDecimal("250.00"));
        assertRejeita(new TransferenciaInterna(origem, destino, new BigDecimal("100.00")), "Limite diário excedido");
    }

    @Test
    void rejeitaOutroBancoForaDoHorarioComercial() {
        var erro = assertThrows(TransferenciaInvalidaException.class,
                () -> validador(Cenario.SEGUNDA_20H, origem).validar(Cenario.solicitacao(
                        new TransferenciaParaOutroBanco(origem, Cenario.CONTA_EXTERNA, BigDecimal.TEN))));
        assertEquals("Transferência para outro banco fora do horário comercial", erro.getMessage());
    }

    @Test
    @DisplayName("pessoa física tem limite por transferência para outro banco; convênio especial não tem")
    void limitePorTipoDeCooperado() {
        origem.alterarLimiteDiario(new BigDecimal("100000.00"));
        var rica = Cenario.contaComConvenio("0004-4", "100000.00");
        rica.alterarLimiteDiario(new BigDecimal("100000.00"));
        origem.creditar(new BigDecimal("9000.00"));
        var validador = validador(Cenario.SEGUNDA_10H, origem, rica);
        var erro = assertThrows(TransferenciaInvalidaException.class, () -> validador.validar(Cenario.solicitacao(
                new TransferenciaParaOutroBanco(origem, Cenario.CONTA_EXTERNA, new BigDecimal("6000.00")))));
        assertEquals("Limite por transferência para pessoa física excedido", erro.getMessage());
        assertDoesNotThrow(() -> validador.validar(Cenario.solicitacao(
                new TransferenciaParaOutroBanco(rica, Cenario.CONTA_EXTERNA, new BigDecimal("6000.00")))));
    }
}
