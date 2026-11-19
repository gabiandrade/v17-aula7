package cerne.medicao;

import java.util.List;

import cerne.transferencias.RegistroDeTransferencia;

/** Os dois lados da conciliação gerados com a mesma semente. */
public record MassaDeConciliacao(List<RegistroDeTransferencia> doCerne, List<RegistroDeTransferencia> doArquivo) {
}
