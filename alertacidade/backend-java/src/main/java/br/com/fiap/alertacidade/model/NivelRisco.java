package br.com.fiap.alertacidade.model;

/**
 * Estados de atencao usados pelo AlertaCidade.
 * Os limites que levam a cada estado sao valores de demonstracao, nao oficiais.
 */
public enum NivelRisco {
    NORMAL,
    ATENCAO,
    CRITICO;

    public boolean geraAlerta() {
        return this != NORMAL;
    }

    public boolean maisGraveQue(NivelRisco outro) {
        return this.ordinal() > outro.ordinal();
    }
}
