package cerne.auditoria;

import java.math.BigDecimal;

/** Evento gravado na trilha (classe comum: o XStream 1.4.9 não conhece records). */
public class EventoDeAuditoria {

    private final String tipo;
    private final String chave;
    private final String conta;
    private final BigDecimal valor;
    private final String detalhe;

    public EventoDeAuditoria(String tipo, String chave, String conta, BigDecimal valor, String detalhe) {
        this.tipo = tipo;
        this.chave = chave;
        this.conta = conta;
        this.valor = valor;
        this.detalhe = detalhe;
    }

    public String getTipo() {
        return tipo;
    }

    public String getChave() {
        return chave;
    }

    public String getConta() {
        return conta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getDetalhe() {
        return detalhe;
    }
}
