package cerne.conciliacao;

/** Exporta os resultados em CSV para a outra cooperativa: identificador;situacao;valor interno;valor externo. */
public class ExportadorDeConciliacao {

    public String linhaCsv(ResultadoConciliacao resultado) {
        if (resultado instanceof Conciliado c) {
            return String.join(";", c.lancamento().identificador(), "CONCILIADO",
                    c.lancamento().valor().toPlainString(), c.registroExterno().valor().toPlainString());
        } else if (resultado instanceof Pendente p) {
            return String.join(";", p.lancamento().identificador(), "PENDENTE",
                    p.lancamento().valor().toPlainString(), "");
        } else if (resultado instanceof Divergente d) {
            return String.join(";", d.lancamento().identificador(), "DIVERGENTE",
                    d.lancamento().valor().toPlainString(), d.registroExterno().valor().toPlainString());
        }
        throw new IllegalStateException("Resultado não tratado: " + resultado);
    }
}
