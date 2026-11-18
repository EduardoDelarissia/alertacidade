package br.com.fiap.alertacidade.model;

/** Tipo do sensor; a unidade de medida e definida pelo tipo. */
public enum TipoSensor {
    NIVEL_AGUA("cm"),
    PLUVIOMETRO("mm");

    private final String unidade;

    TipoSensor(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidade() {
        return unidade;
    }
}
