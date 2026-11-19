package cerne.conciliacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Lancamento;
import cerne.lancamentos.Tarifa;

/** JAXB de verdade, em memória. O arquivo de referência prova que a reposição do JAXB não mudou a saída. */
class GeradorDeArquivoDeConciliacaoTest {

    private final GeradorDeArquivoDeConciliacao gerador = new GeradorDeArquivoDeConciliacao();

    private List<Lancamento> tresLancamentos() {
        return List.of(
                new Credito("0001-1", new BigDecimal("150.00"), LocalDate.of(2026, 8, 5), ""),
                new Debito("0001-1", new BigDecimal("40.00"), LocalDate.of(2026, 8, 6)),
                new Tarifa("0001-1", new BigDecimal("3.00"), LocalDate.of(2026, 8, 7), "TED"));
    }

    @Test
    void geraXmlComUmElementoPorLancamento() {
        String xml = gerador.gerar("0001-1", tresLancamentos());
        assertTrue(xml.contains("<conciliacao conta=\"0001-1\">"));
        assertTrue(xml.contains("<lancamento data=\"05/08/2026\" tipo=\"CREDITO\" valor=\"150.00\"/>"));
    }

    @Test
    void idaEVoltaPreservaOConteudo() {
        ArquivoDeConciliacao lido = gerador.ler(gerador.gerar("0001-1", tresLancamentos()));
        assertEquals("0001-1", lido.getConta());
        assertEquals(3, lido.getItens().size());
        assertEquals("MDAwMS0xfDEwNy4wMHwz", lido.getAssinatura());
    }

    @Test
    @DisplayName("termômetro da troca de namespace: saída idêntica ao arquivo de referência do v11")
    void saidaIgualAoArquivoDeReferencia() throws IOException {
        // quebras de linha normalizadas; ordem de atributos e indentação precisam bater
        String esperado = recurso("/conciliacao/arquivo-de-referencia.xml");
        assertEquals(normalizar(esperado), normalizar(gerador.gerar("0001-1", tresLancamentos())));
    }

    private static String recurso(String caminho) throws IOException {
        try (InputStream entrada = GeradorDeArquivoDeConciliacaoTest.class.getResourceAsStream(caminho)) {
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String normalizar(String xml) {
        return xml.replace("\r\n", "\n").strip();
    }
}
