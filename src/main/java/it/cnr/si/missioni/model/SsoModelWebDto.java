package it.cnr.si.missioni.model;

import java.io.Serializable;
import java.util.Set;

public class SsoModelWebDto implements Serializable {

    private String siglaRuolo;
    private String siglaContesto;
    private Set<SsoEntitaOrganizzativaWebDto> entitaOrganizzative;

    public String getSiglaRuolo() {
        return siglaRuolo;
    }
    public void setSiglaRuolo(String siglaRuolo) {
        this.siglaRuolo = siglaRuolo;
    }
    public String getSiglaContesto() {
        return siglaContesto;
    }
    public void setSiglaContesto(String siglaContesto) {
        this.siglaContesto = siglaContesto;
    }
    public Set<SsoEntitaOrganizzativaWebDto> getEntitaOrganizzative() {
        return entitaOrganizzative;
    }
    public void setEntitaOrganizzative(Set<SsoEntitaOrganizzativaWebDto> entitaOrganizzative) {
        this.entitaOrganizzative = entitaOrganizzative;
    }

}