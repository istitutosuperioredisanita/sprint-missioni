package it.cnr.si.missioni.model;

import java.io.Serializable;

public class SsoEntitaOrganizzativaWebDto implements Serializable {

    private Integer id;
    private String sigla;
    private String idnsip;
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
}