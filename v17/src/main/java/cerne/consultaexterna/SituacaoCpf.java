package cerne.consultaexterna;

/** Situação cadastral devolvida pelo serviço externo. */
public enum SituacaoCpf {
    REGULAR,
    SUSPENSA,
    CANCELADA,
    NULA;

    /** Lê {"situacao":"REGULAR"}; parser mínimo, sem biblioteca de JSON. */
    public static SituacaoCpf deJson(String json) {
        int chave = json.indexOf("\"situacao\"");
        if (chave < 0) {
            throw new IllegalArgumentException("Resposta sem situação: " + json);
        }
        int inicio = json.indexOf('"', json.indexOf(':', chave) + 1) + 1;
        int fim = json.indexOf('"', inicio);
        return valueOf(json.substring(inicio, fim).trim().toUpperCase());
    }
}
