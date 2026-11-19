package cerne.conciliacao;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

import cerne.lancamentos.Lancamento;

/** Gera o XML de conciliação com JAXB 4 (jakarta.xml.bind). O arquivo de referência do teste não mudou. */
public class GeradorDeArquivoDeConciliacao {

    private static final DateTimeFormatter DIA_MES_ANO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final JAXBContext contexto;

    public GeradorDeArquivoDeConciliacao() {
        try {
            contexto = JAXBContext.newInstance(ArquivoDeConciliacao.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("JAXB indisponível", e);
        }
    }

    public String gerar(String numeroDaConta, List<Lancamento> lancamentos) {
        List<ItemDeConciliacao> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO.setScale(2);
        for (Lancamento lancamento : lancamentos) {
            BigDecimal valor = lancamento.valor().setScale(2, RoundingMode.HALF_EVEN);
            itens.add(new ItemDeConciliacao(DIA_MES_ANO.format(lancamento.data()), lancamento.tipo().name(),
                    valor.toPlainString()));
            total = lancamento.ehCredito() ? total.add(valor) : total.subtract(valor);
        }
        var assinatura = Base64.getEncoder().encodeToString(
                (numeroDaConta + "|" + total.toPlainString() + "|" + itens.size()).getBytes(StandardCharsets.UTF_8));
        var arquivo = new ArquivoDeConciliacao(numeroDaConta, itens, total.toPlainString(), assinatura);
        try {
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            var saida = new StringWriter();
            marshaller.marshal(arquivo, saida);
            return saida.toString();
        } catch (JAXBException e) {
            throw new IllegalStateException("Falha ao gerar arquivo de conciliação", e);
        }
    }

    public ArquivoDeConciliacao ler(String xml) {
        try {
            return (ArquivoDeConciliacao) contexto.createUnmarshaller().unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            throw new IllegalStateException("Arquivo de conciliação inválido", e);
        }
    }
}
