package cerne.extratos;

import cerne.lancamentos.Credito;
import cerne.lancamentos.Debito;
import cerne.lancamentos.Estorno;
import cerne.lancamentos.Tarifa;

/** Visitor clássico: no 17 ainda é a forma de ter despacho por tipo com cobertura garantida. */
public interface VisitanteDeLancamento<R> {

    R visitarCredito(Credito credito);

    R visitarDebito(Debito debito);

    R visitarTarifa(Tarifa tarifa);

    R visitarEstorno(Estorno estorno);
}
