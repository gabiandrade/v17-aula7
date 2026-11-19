package cerne.transferencias;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** As três listas da conciliação diária, mais dois totalizadores. */
public record ResultadoDaConciliacao(List<RegistroDeTransferencia> conciliadas,
                                     List<RegistroDeTransferencia> somenteNoCerne,
                                     List<RegistroDeTransferencia> somenteNoArquivo,
                                     BigDecimal totalConciliado,
                                     Map<LocalDate, Integer> quantidadePorData) {

    public ResultadoDaConciliacao {
        conciliadas = List.copyOf(conciliadas);
        somenteNoCerne = List.copyOf(somenteNoCerne);
        somenteNoArquivo = List.copyOf(somenteNoArquivo);
        quantidadePorData = Map.copyOf(quantidadePorData);
    }

    /** Identificadores de uma lista, na ordem; facilita a comparação entre versões. */
    public static String identificadores(List<RegistroDeTransferencia> registros) {
        return registros.stream().map(RegistroDeTransferencia::identificador).collect(Collectors.joining(","));
    }
}
