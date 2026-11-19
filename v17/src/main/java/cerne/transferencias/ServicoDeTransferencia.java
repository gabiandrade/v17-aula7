package cerne.transferencias;

/** Interface comum ao serviço real e aos decoradores (encontro 7). Um método: uma lambda serve de dublê. */
@FunctionalInterface
public interface ServicoDeTransferencia {

    ComprovanteDeTransferencia transferir(SolicitacaoDeTransferencia solicitacao);
}
