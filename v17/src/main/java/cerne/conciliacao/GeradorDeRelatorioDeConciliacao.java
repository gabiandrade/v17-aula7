package cerne.conciliacao;

import java.math.BigDecimal;

/** Uma linha de texto por resultado, para a equipe de conciliação. */
public class GeradorDeRelatorioDeConciliacao {

    static final BigDecimal TOLERANCIA = new BigDecimal("0.05");

    public String descrever(ResultadoConciliacao resultado) {
        return switch (resultado) {
            case Conciliado conciliado -> "OK " + conciliado.lancamento().identificador();
            case Pendente(var lancamento, var dias) -> "PENDENTE " + lancamento.identificador() + " há " + dias + " dias";
            //recordPatterns
            case Divergente(var lancamento, var registroExterno)
                    when lancamento.valor()
                        .subtract(registroExterno.valor()).abs().compareTo(TOLERANCIA) <= 0 ->
                    "ARREDONDAMENTO " + lancamento.identificador();
            case Divergente(var lancamento, var externo) ->
                    "DIVERGENTE " + lancamento.identificador()
                            + " diferença "
                            + lancamento.valor().subtract(externo.valor());
        };
    }

}
