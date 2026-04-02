package it.cnr.si.missioni.model;


import java.io.Serializable;

public class SimpleTipoEntitaOrganizzativaWebDto implements Serializable {

    private String descr;

    private String sigla;

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}
