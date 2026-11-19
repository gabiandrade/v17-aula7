package cerne.util;

import cerne.lancamentos.LancamentoDeCredito;
import cerne.lancamentos.LancamentoDeDebito;
import cerne.lancamentos.LancamentoDeTarifa;

/** "Fábrica" antiga: devolve Object e obriga o cast; ninguém a usa. */
public class FabricaDeLancamentos {

    public Object criar(String tipo) {
        if ("C".equals(tipo)) {
            return new LancamentoDeCredito();
        } else if ("D".equals(tipo)) {
            return new LancamentoDeDebito();
        } else if ("T".equals(tipo)) {
            return new LancamentoDeTarifa();
        }
        return null;
    }
}
