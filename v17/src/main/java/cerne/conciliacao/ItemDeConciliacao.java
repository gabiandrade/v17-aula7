package cerne.conciliacao;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class ItemDeConciliacao {

    @XmlAttribute
    private String data;
    @XmlAttribute
    private String tipo;
    @XmlAttribute
    private String valor;

    public ItemDeConciliacao() {
    }

    public ItemDeConciliacao(String data, String tipo, String valor) {
        this.data = data;
        this.tipo = tipo;
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }
}
