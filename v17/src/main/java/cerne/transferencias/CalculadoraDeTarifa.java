package cerne.transferencias;

import cerne.cooperados.TipoCooperado;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

/**
 * Tarifa por tipo de transferência, nas duas formas honestas do Java 17 (desafio do encontro 4):
 * calcular = switch expression sobre o discriminador enum, sem default;
 * calcularTarifa = escada de instanceof com pattern matching, sem cast, com o throw que só o 21 elimina.
 */
public class CalculadoraDeTarifa {

    static final BigDecimal ISENTA = new BigDecimal("0.00");
    static final BigDecimal TARIFA_ENTRE_COOPERATIVAS = new BigDecimal("1.50");
    static final BigDecimal LIMITE_ISENCAO = new BigDecimal("100.00");
    static final BigDecimal PERCENTUAL_OUTRO_BANCO = new BigDecimal("0.005");
    static final BigDecimal TARIFA_MINIMA = new BigDecimal("3.00");
    static final Set<String> BANCO_COOPERATIVOS = Set.of("02038232", "01181521");

    public BigDecimal calcular(Transferencia transferencia) {
        return switch (transferencia) {
            case TransferenciaParaOutroBanco outroBanco
                    when outroBanco.valor().compareTo(LIMITE_ISENCAO) <= 0 ->
                    ISENTA;
            case TransferenciaParaOutroBanco outroBanco
                    when outroBanco.origem().tipoDoTitular() == TipoCooperado.PRODUTOR_RURAL
                    && BANCO_COOPERATIVOS.contains(outroBanco.destino().ispb()) -> ISENTA;

            case TransferenciaParaOutroBanco outro -> tarifaPercentual(outro.valor());

            case TransferenciaInterna interna -> ISENTA;
            case TransferenciaEntreCooperativas entreCooperativas -> TARIFA_ENTRE_COOPERATIVAS;
            case TransferenciaInternacional internacional -> TARIFA_ENTRE_COOPERATIVAS;
        };
    }


    private static BigDecimal tarifaPercentual(BigDecimal valor) {
        return valor.multiply(PERCENTUAL_OUTRO_BANCO).setScale(2, RoundingMode.HALF_EVEN).max(TARIFA_MINIMA);
    }
}
