package cerne.conciliacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Casa os lançamentos do Cerne com os registros da outra cooperativa. */
public class ConciliadorDeLancamentos {

    public List<ResultadoConciliacao> conciliar(List<LancamentoInterno> internos, List<RegistroExterno> externos,
                                                LocalDate hoje) {
        // toMap com mesclagem: registro repetido no arquivo externo vale uma vez
        Map<String, RegistroExterno> porIdentificador = externos.stream()
                .collect(Collectors.toMap(RegistroExterno::identificador, Function.identity(), (a, b) -> a));
        return internos.stream()
                .map(lancamento -> Optional.ofNullable(porIdentificador.get(lancamento.identificador()))
                        .<ResultadoConciliacao>map(externo -> externo.valor().compareTo(lancamento.valor()) == 0
                                ? new Conciliado(lancamento, externo)
                                : new Divergente(lancamento, externo))
                        .orElseGet(() -> new Pendente(lancamento,
                                (int) ChronoUnit.DAYS.between(lancamento.data(), hoje))))
                .toList();
    }

    public long quantidadeDePendentes(List<ResultadoConciliacao> resultados) {
        return resultados.stream().filter(resultado -> resultado instanceof Pendente).count();
    }

    public BigDecimal diferencaTotal(List<ResultadoConciliacao> resultados) {
        var total = BigDecimal.ZERO;
        for (var resultado : resultados) {
            if (resultado instanceof Divergente d) {
                total = total.add(d.lancamento().valor().subtract(d.registroExterno().valor()).abs());
            }
        }
        return total;
    }
}
