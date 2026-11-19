package cerne.util;

import java.util.Date;

import cerne.transferencias.ServicoDeTransferencia;
import cerne.transferencias.Transferencia;

/** "Decorador" antigo: é herança, não composição (não dá para empilhar dois). */
public class DecoradorDeServico extends ServicoDeTransferencia {

    @Override
    public String transferir(Transferencia transferencia, Date momento) {
        UtilCerne.log("Iniciando transferência de " + transferencia.getOrigem().getNumero());
        String identificador = super.transferir(transferencia, momento);
        UtilCerne.log("Transferência " + identificador + " concluída");
        return identificador;
    }
}
