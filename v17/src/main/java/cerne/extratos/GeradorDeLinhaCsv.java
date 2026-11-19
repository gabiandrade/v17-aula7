package cerne.extratos;

import java.time.format.DateTimeFormatter;

import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Estorno;
import cerne.lancamentos.Tarifa;

/** Linha CSV para o contador do cooperado: data;tipo;valor;complemento (Visitor, versão v17). */
public class GeradorDeLinhaCsv implements VisitanteDeLancamento<String> {

    private static final DateTimeFormatter DIA_MES_ANO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String visitarCredito(Credito c) {
        return String.join(";", DIA_MES_ANO.format(c.data()), "CREDITO", c.valor().toPlainString(), c.origem());
    }

    @Override
    public String visitarDebito(Debito d) {
        return String.join(";", DIA_MES_ANO.format(d.data()), "DEBITO", d.valor().negate().toPlainString(), "");
    }

    @Override
    public String visitarTarifa(Tarifa t) {
        return String.join(";", DIA_MES_ANO.format(t.data()), "TARIFA", t.valor().negate().toPlainString(),
                t.codigoDaTarifa());
    }

    @Override
    public String visitarEstorno(Estorno e) {
        return String.join(";", DIA_MES_ANO.format(e.data()), "ESTORNO", e.valor().toPlainString(),
                e.lancamentoOriginal().toString());
    }
}
