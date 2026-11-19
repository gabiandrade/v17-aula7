package cerne.conciliacao;

public record Divergente(LancamentoInterno lancamento, RegistroExterno registroExterno)
        implements ResultadoConciliacao {
}
