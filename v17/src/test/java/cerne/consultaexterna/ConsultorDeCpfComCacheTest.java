package cerne.consultaexterna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import cerne.cooperados.Cpf;

class ConsultorDeCpfComCacheTest {

    @Test
    @DisplayName("consulta repetida dentro da validade não chama o delegado")
    void consultaRepetidaDentroDaValidadeNaoChamaODelegado() {
        var chamadas = new AtomicInteger();
        ConsultorDeCpf lento = cpf -> {
            chamadas.incrementAndGet();
            return SituacaoCpf.REGULAR;
        };
        var relogio = Clock.fixed(Instant.parse("2026-09-25T10:00:00Z"), ZoneOffset.UTC);
        var consultor = new ConsultorDeCpfComCache(lento, Duration.ofHours(1), relogio);

        consultor.consultar(new Cpf("12345678909"));
        consultor.consultar(new Cpf("12345678909"));

        assertEquals(1, chamadas.get());
    }

    @Test
    @DisplayName("consulta de novo depois que o cache expira")
    void consultaDeNovoDepoisQueOCacheExpira() {
        var chamadas = new AtomicInteger();
        ConsultorDeCpf lento = cpf -> {
            chamadas.incrementAndGet();
            return SituacaoCpf.REGULAR;
        };
        var inicio = Instant.parse("2026-09-25T10:00:00Z");
        var relogio = new RelogioAjustavel(inicio);
        var consultor = new ConsultorDeCpfComCache(lento, Duration.ofHours(1), relogio);

        consultor.consultar(new Cpf("12345678909"));
        relogio.avancar(Duration.ofMinutes(61));
        consultor.consultar(new Cpf("12345678909"));

        assertEquals(2, chamadas.get());
    }
}
