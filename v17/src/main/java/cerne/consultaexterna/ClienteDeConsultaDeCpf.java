package cerne.consultaexterna;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import jakarta.enterprise.context.ApplicationScoped;

import cerne.cooperados.Cpf;

/** Cliente do serviço externo de CPF com o HTTP Client do Java 11. Nunca é chamado nos testes. */
@ApplicationScoped
public class ClienteDeConsultaDeCpf implements ConsultorDeCpf {

    private static final URI URL_BASE = URI.create("https://consulta-cpf.exemplo.coop/v1/cpf/");

    // um cliente por aplicação: ele mantém o pool de conexões
    private final HttpClient cliente = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private final String cabecalhoDeAutenticacao;

    public ClienteDeConsultaDeCpf() {
        this("cerne", "trocar-na-configuracao");
    }

    public ClienteDeConsultaDeCpf(String usuario, String senha) {
        this.cabecalhoDeAutenticacao = cabecalhoDeAutenticacao(usuario, senha);
    }

    @Override
    public SituacaoCpf consultar(Cpf cpf) {
        var requisicao = HttpRequest.newBuilder(URL_BASE.resolve(cpf.numero()))
                .header("Accept", "application/json")
                .header("Authorization", cabecalhoDeAutenticacao)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
        try {
            HttpResponse<String> resposta = cliente.send(requisicao, BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                throw new IOException("Consulta de CPF falhou com status " + resposta.statusCode());
            }
            return SituacaoCpf.deJson(resposta.body());
        } catch (IOException e) {
            throw new UncheckedIOException("Falha na consulta de CPF " + cpf.formatado(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // nunca engolir a interrupção
            throw new IllegalStateException("Consulta de CPF interrompida", e);
        }
    }

    static String cabecalhoDeAutenticacao(String usuario, String senha) {
        var credenciais = usuario + ":" + senha;
        return "Basic " + Base64.getEncoder().encodeToString(credenciais.getBytes(StandardCharsets.UTF_8));
    }

    static String usuarioDoCabecalho(String cabecalho) {
        var credenciais = new String(Base64.getDecoder().decode(cabecalho.substring("Basic ".length())),
                StandardCharsets.UTF_8);
        return credenciais.substring(0, credenciais.indexOf(':'));
    }
}
