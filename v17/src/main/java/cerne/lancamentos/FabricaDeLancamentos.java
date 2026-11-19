package cerne.lancamentos;

import cerne.lancamentos.importacao.LinhaDoArquivo;

/**
 * Factory (encontro 7): único ponto que traduz o código do arquivo no tipo concreto.
 * Quem chama depende só de Lancamento. O default existe porque o seletor é String, não o tipo sealed.
 */
public final class FabricaDeLancamentos {


    private FabricaDeLancamentos() {
    }

    public static Lancamento aPartirDe(LinhaDoArquivo linha) {
        throw new UnsupportedOperationException(
                "Implementar Factory no Encontro 7");
    }
}
