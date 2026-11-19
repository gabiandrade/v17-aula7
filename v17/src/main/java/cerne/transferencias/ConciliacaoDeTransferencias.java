package cerne.transferencias;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Compara as transferências registradas no Cerne com o arquivo diário da central (desafio do encontro 2).
 * Casa por identificador (sem espaços, maiúsculo) e valor; cada linha do arquivo casa uma única vez.
 */
public class ConciliacaoDeTransferencias {

    public ResultadoDaConciliacao conciliar(List<RegistroDeTransferencia> doCerne,
                                            List<RegistroDeTransferencia> doArquivo) {
        // groupingBy e não toMap: o arquivo pode repetir identificador e toMap lançaria "Duplicate key"
        Map<Chave, Deque<RegistroDeTransferencia>> indice = doArquivo.stream()
                .collect(Collectors.groupingBy(Chave::de, HashMap::new, Collectors.toCollection(ArrayDeque::new)));

        // stream sequencial de propósito: o predicado consome o índice na ordem do Cerne
        Set<RegistroDeTransferencia> casadosNoArquivo = Collections.newSetFromMap(new IdentityHashMap<>());
        Map<Boolean, List<RegistroDeTransferencia>> porCasamento = doCerne.stream()
                .collect(Collectors.partitioningBy(doSistema -> retirarDoIndice(indice, doSistema)
                        .map(casadosNoArquivo::add)
                        .orElse(false)));

        var conciliadas = porCasamento.get(true);
        var somenteNoArquivo = doArquivo.stream()
                .filter(registro -> !casadosNoArquivo.contains(registro))
                .toList();
        var total = conciliadas.stream()
                .map(RegistroDeTransferencia::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        var quantidadePorData = conciliadas.stream()
                .collect(Collectors.groupingBy(RegistroDeTransferencia::data, Collectors.summingInt(registro -> 1)));

        return new ResultadoDaConciliacao(conciliadas, porCasamento.get(false), somenteNoArquivo, total,
                quantidadePorData);
    }

    /** O índice é consumido de propósito: a mesma linha do arquivo não casa duas vezes. */
    private Optional<RegistroDeTransferencia> retirarDoIndice(Map<Chave, Deque<RegistroDeTransferencia>> indice,
                                                             RegistroDeTransferencia doSistema) {
        return Optional.ofNullable(indice.get(Chave.de(doSistema))).map(Deque::pollFirst);
    }

    /** Chave de casamento: identificador normalizado e valor sem zeros à direita (10.0 == 10.00). */
    private record Chave(String identificador, BigDecimal valor) {

        static Chave de(RegistroDeTransferencia registro) {
            return new Chave(registro.identificador().strip().toUpperCase(), registro.valor().stripTrailingZeros());
        }
    }
}
