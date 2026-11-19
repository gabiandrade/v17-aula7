package cerne.transferencias;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Os 14 testes do desafio do encontro 2, agora em JUnit 5 (mesmas asserções, acessores de record). */
class ConciliacaoDeTransferenciasTest {

    private static final LocalDate DIA_1 = LocalDate.of(2026, 8, 3);
    private static final LocalDate DIA_2 = LocalDate.of(2026, 8, 4);

    private ConciliacaoDeTransferencias conciliacao;

    @BeforeEach
    void preparar() {
        conciliacao = new ConciliacaoDeTransferencias();
    }

    private static RegistroDeTransferencia registro(String id, String valor, LocalDate data) {
        return new RegistroDeTransferencia(id, new BigDecimal(valor), data);
    }

    private static List<RegistroDeTransferencia> lista(RegistroDeTransferencia... registros) {
        return List.of(registros);
    }

    @Test
    void semRegistrosNaoHaNadaParaConciliar() {
        ResultadoDaConciliacao r = conciliacao.conciliar(List.of(),
                List.of());
        assertTrue(r.conciliadas().isEmpty());
        assertTrue(r.somenteNoCerne().isEmpty());
        assertTrue(r.somenteNoArquivo().isEmpty());
        assertEquals(0, BigDecimal.ZERO.compareTo(r.totalConciliado()));
    }

    @Test
    void mesmaTransferenciaNosDoisLadosEhConciliada() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-1", "10.00", DIA_1)),
                lista(registro("TRF-1", "10.00", DIA_1)));
        assertEquals("TRF-1", ResultadoDaConciliacao.identificadores(r.conciliadas()));
        assertTrue(r.somenteNoCerne().isEmpty());
        assertTrue(r.somenteNoArquivo().isEmpty());
    }

    @Test
    void transferenciaAusenteNoArquivoFicaSomenteNoCerne() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-1", "10.00", DIA_1)),
                List.of());
        assertEquals("TRF-1", ResultadoDaConciliacao.identificadores(r.somenteNoCerne()));
        assertTrue(r.conciliadas().isEmpty());
    }

    @Test
    void linhaDoArquivoSemCorrespondenteFicaSomenteNoArquivo() {
        ResultadoDaConciliacao r = conciliacao.conciliar(List.of(),
                lista(registro("TRF-9", "5.00", DIA_1)));
        assertEquals("TRF-9", ResultadoDaConciliacao.identificadores(r.somenteNoArquivo()));
    }

    @Test
    void valorDivergenteApareceNosDoisLados() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-1", "10.00", DIA_1)),
                lista(registro("TRF-1", "11.00", DIA_1)));
        assertTrue(r.conciliadas().isEmpty());
        assertEquals("TRF-1", ResultadoDaConciliacao.identificadores(r.somenteNoCerne()));
        assertEquals("TRF-1", ResultadoDaConciliacao.identificadores(r.somenteNoArquivo()));
    }

    @Test
    void escalaDiferenteDoMesmoValorConcilia() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-1", "10.0", DIA_1)),
                lista(registro("TRF-1", "10.00", DIA_1)));
        assertEquals(1, r.conciliadas().size());
    }

    @Test
    void identificadorDoArquivoEhNormalizado() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-1", "10.00", DIA_1)),
                lista(registro("  trf-1 ", "10.00", DIA_1)));
        assertEquals(1, r.conciliadas().size());
        assertTrue(r.somenteNoArquivo().isEmpty());
    }

    @Test
    void conciliadasSeguemAOrdemDoCerne() {
        ResultadoDaConciliacao r = conciliacao.conciliar(
                lista(registro("TRF-2", "2.00", DIA_1), registro("TRF-1", "1.00", DIA_1), registro("TRF-3", "3.00", DIA_1)),
                lista(registro("TRF-1", "1.00", DIA_1), registro("TRF-3", "3.00", DIA_1), registro("TRF-2", "2.00", DIA_1)));
        assertEquals("TRF-2,TRF-1,TRF-3", ResultadoDaConciliacao.identificadores(r.conciliadas()));
    }

    @Test
    void somenteNoArquivoSegueAOrdemDoArquivo() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-2", "2.00", DIA_1)),
                lista(registro("TRF-5", "5.00", DIA_1), registro("TRF-2", "2.00", DIA_1), registro("TRF-4", "4.00", DIA_1)));
        assertEquals("TRF-5,TRF-4", ResultadoDaConciliacao.identificadores(r.somenteNoArquivo()));
    }

    @Test
    void cenarioMistoSeparaAsTresListas() {
        ResultadoDaConciliacao r = conciliacao.conciliar(
                lista(registro("TRF-1", "1.00", DIA_1), registro("TRF-2", "2.00", DIA_1), registro("TRF-3", "3.00", DIA_2)),
                lista(registro("TRF-3", "3.00", DIA_2), registro("TRF-4", "4.00", DIA_2), registro("TRF-1", "1.00", DIA_1)));
        assertEquals("TRF-1,TRF-3", ResultadoDaConciliacao.identificadores(r.conciliadas()));
        assertEquals("TRF-2", ResultadoDaConciliacao.identificadores(r.somenteNoCerne()));
        assertEquals("TRF-4", ResultadoDaConciliacao.identificadores(r.somenteNoArquivo()));
    }

    @Test
    void totalConciliadoSomaApenasAsConciliadas() {
        ResultadoDaConciliacao r = conciliacao.conciliar(
                lista(registro("TRF-1", "10.50", DIA_1), registro("TRF-2", "99.99", DIA_1), registro("TRF-3", "0.50", DIA_1)),
                lista(registro("TRF-1", "10.50", DIA_1), registro("TRF-3", "0.50", DIA_1)));
        assertEquals(0, new BigDecimal("11.00").compareTo(r.totalConciliado()));
    }

    @Test
    void linhaDuplicadaNoArquivoCasaUmaSoVez() {
        ResultadoDaConciliacao r = conciliacao.conciliar(lista(registro("TRF-1", "10.00", DIA_1)),
                lista(registro("TRF-1", "10.00", DIA_1), registro("TRF-1", "10.00", DIA_1)));
        assertEquals(1, r.conciliadas().size());
        assertEquals("TRF-1", ResultadoDaConciliacao.identificadores(r.somenteNoArquivo()));
    }

    @Test
    void duplicataNoCerneComUmaLinhaNoArquivoSobraNoCerne() {
        ResultadoDaConciliacao r = conciliacao.conciliar(
                lista(registro("TRF-1", "10.00", DIA_1), registro("TRF-1", "10.00", DIA_1)),
                lista(registro("TRF-1", "10.00", DIA_1)));
        assertEquals(1, r.conciliadas().size());
        assertEquals(1, r.somenteNoCerne().size());
    }

    @Test
    void quantidadePorDataContaAsConciliadasDeCadaDia() {
        ResultadoDaConciliacao r = conciliacao.conciliar(
                lista(registro("TRF-1", "1.00", DIA_1), registro("TRF-2", "2.00", DIA_1), registro("TRF-3", "3.00", DIA_2),
                        registro("TRF-4", "4.00", DIA_2)),
                lista(registro("TRF-1", "1.00", DIA_1), registro("TRF-2", "2.00", DIA_1), registro("TRF-3", "3.00", DIA_2)));
        assertEquals(Integer.valueOf(2), r.quantidadePorData().get(DIA_1));
        assertEquals(Integer.valueOf(1), r.quantidadePorData().get(DIA_2));
        assertEquals(2, r.quantidadePorData().size());
    }
}
