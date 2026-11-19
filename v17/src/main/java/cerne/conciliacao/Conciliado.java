package cerne.conciliacao;

public record Conciliado(LancamentoInterno lancamento, RegistroExterno registroExterno)
        implements ResultadoConciliacao {
}
