package cerne.conciliacao;

import java.math.BigDecimal;

/** Decide a gravidade de cada resultado para a fila de tratamento. */
public class ClassificadorDeDivergencias {

    private static final BigDecimal DIFERENCA_ALTA = new BigDecimal("100.00");

    public GravidadeDaDivergencia classificar(ResultadoConciliacao resultado) {
        if (resultado instanceof Conciliado) {
            return GravidadeDaDivergencia.NENHUMA;
        }
        if (resultado instanceof Pendente p) {
            return p.diasEmAberto() > 5 ? GravidadeDaDivergencia.ALTA : GravidadeDaDivergencia.MEDIA;
        }
        if (resultado instanceof Divergente d) {
            BigDecimal diferenca = d.lancamento().valor().subtract(d.registroExterno().valor()).abs();
            if (diferenca.compareTo(GeradorDeRelatorioDeConciliacao.TOLERANCIA) <= 0) {
                return GravidadeDaDivergencia.BAIXA;
            }
            return diferenca.compareTo(DIFERENCA_ALTA) > 0 ? GravidadeDaDivergencia.ALTA : GravidadeDaDivergencia.MEDIA;
        }
        throw new IllegalStateException("Resultado não tratado: " + resultado);
    }
}
