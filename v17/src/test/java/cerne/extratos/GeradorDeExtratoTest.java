package cerne.extratos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Estorno;
import cerne.lancamentos.Lancamento;
import cerne.lancamentos.Tarifa;

class GeradorDeExtratoTest {

    private final GeradorDeExtrato gerador = new GeradorDeExtrato();

    private static List<Lancamento> quatroLancamentos() {
        return List.of(
                new Credito("0001-1", new BigDecimal("150.00"), LocalDate.of(2026, 8, 5), "DEPOSITO_CAIXA"),
                new Debito("0001-1", new BigDecimal("40.00"), LocalDate.of(2026, 8, 6)),
                new Tarifa("0001-1", new BigDecimal("3.00"), LocalDate.of(2026, 8, 7), "TED"),
                new Estorno("0001-1", new BigDecimal("40.00"), LocalDate.of(2026, 8, 8),
                        UUID.fromString("00000000-0000-0000-0000-000000000001")));
    }

    @Test
    @DisplayName("CSV: uma linha por lançamento, débitos com sinal negativo")
    void csvComSinal() {
        assertEquals("""
                05/08/2026;CREDITO;150.00;DEPOSITO_CAIXA
                06/08/2026;DEBITO;-40.00;
                07/08/2026;TARIFA;-3.00;TED
                08/08/2026;ESTORNO;40.00;00000000-0000-0000-0000-000000000001
                """, gerador.gerarCsv(quatroLancamentos()));
    }

    @Test
    @DisplayName("texto usa valor em reais")
    void textoEmReais() {
        assertEquals("05/08/2026  CREDITO  +R$ 150,00", gerador.gerarTexto(quatroLancamentos()).lines().findFirst().orElseThrow());
    }
}
