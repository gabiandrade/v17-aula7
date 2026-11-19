package cerne.util;

import cerne.lancamentos.Lancamento;
import cerne.lancamentos.LancamentoDeCredito;
import cerne.lancamentos.LancamentoDeDebito;
import cerne.lancamentos.LancamentoDeTarifa;

/** "Visitante" antigo: é uma cascata de instanceof, sem garantia de cobertura. */
public class VisitanteDeExtrato {

    public String visitar(Lancamento l) {
        if (l instanceof LancamentoDeCredito) {
            LancamentoDeCredito c = (LancamentoDeCredito) l;
            return "C " + c.getValor() + " " + c.getOrigem();
        } else if (l instanceof LancamentoDeDebito) {
            LancamentoDeDebito d = (LancamentoDeDebito) l;
            return "D " + d.getValor();
        } else if (l instanceof LancamentoDeTarifa) {
            LancamentoDeTarifa t = (LancamentoDeTarifa) l;
            return "T " + t.getValor() + " " + t.getCodigoDaTarifa();
        }
        return "";
    }
}
