package cerne.conciliacao;

/** Resultado da conciliação de um lançamento: hierarquia fechada. */
public sealed interface ResultadoConciliacao permits Conciliado, Pendente, Divergente {
}
