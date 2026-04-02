package it.cnr.si.missioni.model;

import java.io.Serializable;
import java.util.Set;

public class UserInfoDto implements Serializable {

    private String nome;
    private String cognome;
    private String codice_fiscale;
    private String sesso;
    private String data_nascita;
    private String comune_nascita;
    private String provincia_nascita;
    private String nazione_nascita;
    private Boolean fl_cittadino_italiano;
    private String indirizzo_residenza;
    private String num_civico_residenza;
    private String cap_residenza;
    private String comune_residenza;
    private String provincia_residenza;
    private String nazione_residenza;
    private String indirizzo_comunicazioni;
    private String num_civico_comunicazioni;
    private String cap_comunicazioni;
    private String comune_comunicazioni;
    private String provincia_comunicazioni;
    private String nazione_comunicazioni;
    private String telefono_comunicazioni;
    private String email_comunicazioni;
    private String profilo;
    private String struttura_appartenenza;
    private String[] settore_scientifico_tecnologico;
    private String[] area_scientifica;
    private String codice_sede;
    private String sigla_sede;
    private String citta_sede;
    private String codice_uo;
    private String livello_profilo;
    private Integer matricola;
    private String uid;
    private String data_cessazione;
    private String scadenza_account;
    private Boolean dipendente;
    private SimpleUtenteWebDto boss;
    private Set<SsoModelWebDto> roles;

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

    public String getCodice_fiscale() {
        return codice_fiscale;
    }

    public void setCodice_fiscale(String codice_fiscale) {
        this.codice_fiscale = codice_fiscale;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getData_nascita() {
        return data_nascita;
    }

    public void setData_nascita(String data_nascita) {
        this.data_nascita = data_nascita;
    }

    public String getComune_nascita() {
        return comune_nascita;
    }

    public void setComune_nascita(String comune_nascita) {
        this.comune_nascita = comune_nascita;
    }

    public String getProvincia_nascita() {
        return provincia_nascita;
    }

    public void setProvincia_nascita(String provincia_nascita) {
        this.provincia_nascita = provincia_nascita;
    }

    public String getNazione_nascita() {
        return nazione_nascita;
    }

    public void setNazione_nascita(String nazione_nascita) {
        this.nazione_nascita = nazione_nascita;
    }

    public Boolean getFl_cittadino_italiano() {
        return fl_cittadino_italiano;
    }

    public void setFl_cittadino_italiano(Boolean fl_cittadino_italiano) {
        this.fl_cittadino_italiano = fl_cittadino_italiano;
    }

    public String getIndirizzo_residenza() {
        return indirizzo_residenza;
    }

    public void setIndirizzo_residenza(String indirizzo_residenza) {
        this.indirizzo_residenza = indirizzo_residenza;
    }

    public String getNum_civico_residenza() {
        return num_civico_residenza;
    }

    public void setNum_civico_residenza(String num_civico_residenza) {
        this.num_civico_residenza = num_civico_residenza;
    }

    public String getCap_residenza() {
        return cap_residenza;
    }

    public void setCap_residenza(String cap_residenza) {
        this.cap_residenza = cap_residenza;
    }

    public String getComune_residenza() {
        return comune_residenza;
    }

    public void setComune_residenza(String comune_residenza) {
        this.comune_residenza = comune_residenza;
    }

    public String getProvincia_residenza() {
        return provincia_residenza;
    }

    public void setProvincia_residenza(String provincia_residenza) {
        this.provincia_residenza = provincia_residenza;
    }

    public String getNazione_residenza() {
        return nazione_residenza;
    }

    public void setNazione_residenza(String nazione_residenza) {
        this.nazione_residenza = nazione_residenza;
    }

    public String getIndirizzo_comunicazioni() {
        return indirizzo_comunicazioni;
    }

    public void setIndirizzo_comunicazioni(String indirizzo_comunicazioni) {
        this.indirizzo_comunicazioni = indirizzo_comunicazioni;
    }

    public String getNum_civico_comunicazioni() {
        return num_civico_comunicazioni;
    }

    public void setNum_civico_comunicazioni(String num_civico_comunicazioni) {
        this.num_civico_comunicazioni = num_civico_comunicazioni;
    }

    public String getCap_comunicazioni() {
        return cap_comunicazioni;
    }

    public void setCap_comunicazioni(String cap_comunicazioni) {
        this.cap_comunicazioni = cap_comunicazioni;
    }

    public String getComune_comunicazioni() {
        return comune_comunicazioni;
    }

    public void setComune_comunicazioni(String comune_comunicazioni) {
        this.comune_comunicazioni = comune_comunicazioni;
    }

    public String getProvincia_comunicazioni() {
        return provincia_comunicazioni;
    }

    public void setProvincia_comunicazioni(String provincia_comunicazioni) {
        this.provincia_comunicazioni = provincia_comunicazioni;
    }

    public String getNazione_comunicazioni() {
        return nazione_comunicazioni;
    }

    public void setNazione_comunicazioni(String nazione_comunicazioni) {
        this.nazione_comunicazioni = nazione_comunicazioni;
    }

    public String getTelefono_comunicazioni() {
        return telefono_comunicazioni;
    }

    public void setTelefono_comunicazioni(String telefono_comunicazioni) {
        this.telefono_comunicazioni = telefono_comunicazioni;
    }

    public String getEmail_comunicazioni() {
        return email_comunicazioni;
    }

    public void setEmail_comunicazioni(String email_comunicazioni) {
        this.email_comunicazioni = email_comunicazioni;
    }

    public String getProfilo() {
        return profilo;
    }

    public void setProfilo(String profilo) {
        this.profilo = profilo;
    }

    public String getStruttura_appartenenza() {
        return struttura_appartenenza;
    }

    public void setStruttura_appartenenza(String struttura_appartenenza) {
        this.struttura_appartenenza = struttura_appartenenza;
    }

    public String[] getSettore_scientifico_tecnologico() {
        return settore_scientifico_tecnologico;
    }

    public void setSettore_scientifico_tecnologico(String[] settore_scientifico_tecnologico) {
        this.settore_scientifico_tecnologico = settore_scientifico_tecnologico;
    }

    public String[] getArea_scientifica() {
        return area_scientifica;
    }

    public void setArea_scientifica(String[] area_scientifica) {
        this.area_scientifica = area_scientifica;
    }

    public String getCodice_sede() {
        return codice_sede;
    }

    public void setCodice_sede(String codice_sede) {
        this.codice_sede = codice_sede;
    }

    public String getSigla_sede() {
        return sigla_sede;
    }

    public void setSigla_sede(String sigla_sede) {
        this.sigla_sede = sigla_sede;
    }

    public String getCitta_sede() {
        return citta_sede;
    }

    public void setCitta_sede(String citta_sede) {
        this.citta_sede = citta_sede;
    }

    public String getCodice_uo() {
        return codice_uo;
    }

    public void setCodice_uo(String codice_uo) {
        this.codice_uo = codice_uo;
    }

    public String getLivello_profilo() {
        return livello_profilo;
    }

    public void setLivello_profilo(String livello_profilo) {
        this.livello_profilo = livello_profilo;
    }

    public Integer getMatricola() {
        return matricola;
    }

    public void setMatricola(Integer matricola) {
        this.matricola = matricola;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getData_cessazione() {
        return data_cessazione;
    }

    public void setData_cessazione(String data_cessazione) {
        this.data_cessazione = data_cessazione;
    }

    public String getScadenza_account() {
        return scadenza_account;
    }

    public void setScadenza_account(String scadenza_account) {
        this.scadenza_account = scadenza_account;
    }

    public Boolean getDipendente() {
        return dipendente;
    }

    public void setDipendente(Boolean dipendente) {
        this.dipendente = dipendente;
    }

    public SimpleUtenteWebDto getBoss() {
        return boss;
    }

    public void setBoss(SimpleUtenteWebDto boss) {
        this.boss = boss;
    }

    public Set<SsoModelWebDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<SsoModelWebDto> roles) {
        this.roles = roles;
    }

}
