package cerne.conciliacao;

public record Pendente(LancamentoInterno lancamento, int diasEmAberto) implements ResultadoConciliacao {
}
