package cerne.conciliacao;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/** Arquivo XML enviado à central (JAXB exige construtor vazio: não vira record). */
@XmlRootElement(name = "conciliacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class ArquivoDeConciliacao {

    @XmlAttribute
    private String conta;
    @XmlElement(name = "lancamento")
    private List<ItemDeConciliacao> itens = new ArrayList<ItemDeConciliacao>();
    @XmlElement
    private String total;
    @XmlElement
    private String assinatura;

    public ArquivoDeConciliacao() {
    }

    public ArquivoDeConciliacao(String conta, List<ItemDeConciliacao> itens, String total, String assinatura) {
        this.conta = conta;
        this.itens = itens;
        this.total = total;
        this.assinatura = assinatura;
    }

    public String getConta() {
        return conta;
    }

    public List<ItemDeConciliacao> getItens() {
        return itens;
    }

    public String getTotal() {
        return total;
    }

    public String getAssinatura() {
        return assinatura;
    }
}
