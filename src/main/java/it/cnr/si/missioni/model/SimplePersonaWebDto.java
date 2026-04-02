package it.cnr.si.missioni.model;

import java.io.Serializable;
import java.util.Date;

public class SimplePersonaWebDto implements Serializable {

    private Integer id;

    private String nome;

    private String cognome;

    private String codiceFiscale;

    private Integer matricola;

    private Date dataCessazione;

    private Date dataPrevistaCessazione;

    private TipoContratto tipoContratto;

    private SimpleEntitaOrganizzativaWebDto sede;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public Integer getMatricola() {
        return matricola;
    }

    public void setMatricola(Integer matricola) {
        this.matricola = matricola;
    }

    public Date getDataCessazione() {
        return dataCessazione;
    }

    public void setDataCessazione(Date dataCessazione) {
        this.dataCessazione = dataCessazione;
    }

    public Date getDataPrevistaCessazione() {
        return dataPrevistaCessazione;
    }

    public void setDataPrevistaCessazione(Date dataPrevistaCessazione) {
        this.dataPrevistaCessazione = dataPrevistaCessazione;
    }

    public TipoContratto getTipoContratto() {
        return tipoContratto;
    }

    public void setTipoContratto(TipoContratto tipoContratto) {
        this.tipoContratto = tipoContratto;
    }

    public SimpleEntitaOrganizzativaWebDto getSede() {
        return sede;
    }

    public void setSede(SimpleEntitaOrganizzativaWebDto sede) {
        this.sede = sede;
    }
}
