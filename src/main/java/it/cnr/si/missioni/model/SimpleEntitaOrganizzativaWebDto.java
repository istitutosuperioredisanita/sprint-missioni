package it.cnr.si.missioni.model;

import java.io.Serializable;

public class SimpleEntitaOrganizzativaWebDto implements Serializable {

    private Integer id;

    private String sigla;

    private String idnsip;

    private String cdsuo;

    private String denominazione;

    private SimpleTipoEntitaOrganizzativaWebDto tipo;

    private IndirizzoWebDto indirizzoPrincipale;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getIdnsip() {
        return idnsip;
    }

    public void setIdnsip(String idnsip) {
        this.idnsip = idnsip;
    }

    public String getCdsuo() {
        return cdsuo;
    }

    public void setCdsuo(String cdsuo) {
        this.cdsuo = cdsuo;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public SimpleTipoEntitaOrganizzativaWebDto getTipo() {
        return tipo;
    }

    public void setTipo(SimpleTipoEntitaOrganizzativaWebDto tipo) {
        this.tipo = tipo;
    }

    public IndirizzoWebDto getIndirizzoPrincipale() {
        return indirizzoPrincipale;
    }

    public void setIndirizzoPrincipale(IndirizzoWebDto indirizzoPrincipale) {
        this.indirizzoPrincipale = indirizzoPrincipale;
    }
}
