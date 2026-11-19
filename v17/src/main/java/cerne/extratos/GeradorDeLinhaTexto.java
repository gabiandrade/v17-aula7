package cerne.extratos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Estorno;
import cerne.lancamentos.Tarifa;

/** Linha de texto para impressão na agência (Visitor, versão v17). */
public class GeradorDeLinhaTexto implements VisitanteDeLancamento<String> {

    private static final DateTimeFormatter DIA_MES_ANO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String visitarCredito(Credito c) {
        return DIA_MES_ANO.format(c.data()) + "  CREDITO  +" + emReais(c.valor());
    }

    @Override
    public String visitarDebito(Debito d) {
        return DIA_MES_ANO.format(d.data()) + "  DEBITO   -" + emReais(d.valor());
    }

    @Override
    public String visitarTarifa(Tarifa t) {
        return DIA_MES_ANO.format(t.data()) + "  TARIFA   -" + emReais(t.valor());
    }

    @Override
    public String visitarEstorno(Estorno e) {
        return DIA_MES_ANO.format(e.data()) + "  ESTORNO  +" + emReais(e.valor());
    }

    private static String emReais(BigDecimal valor) {
        return "R$ " + valor.setScale(2, RoundingMode.HALF_EVEN).toPlainString().replace('.', ',');
    }
}
