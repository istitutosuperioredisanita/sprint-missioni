/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.util.proxy.json.object;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "codice_sede",
        "sigla_sede",
        "citta_sede",
        "codice_uo",
        "livello_profilo",
        "matricola",
        "uid",
        "nome",
        "cognome",
        "codice_fiscale",
        "sesso",
        "data_nascita",
        "comune_nascita",
        "provincia_nascita",
        "nazione_nascita",
        "fl_cittadino_italiano",
        "indirizzo_residenza",
        "num_civico_residenza",
        "cap_residenza",
        "comune_residenza",
        "provincia_residenza",
        "nazione_residenza",
        "indirizzo_comunicazioni",
        "num_civico_comunicazioni",
        "cap_comunicazioni",
        "comune_comunicazioni",
        "provincia_comunicazioni",
        "nazione_comunicazioni",
        "email_comunicazioni",
        "profilo",
        "struttura_appartenenza",
        "settore_scientifico_tecnologico",
        "area_scientifica"
})

public class DatiDirettore {

    @JsonProperty("codice_sede")
    private String codiceSede;
    @JsonProperty("sigla_sede")
    private String siglaSede;
    @JsonProperty("citta_sede")
    private String cittaSede;
    @JsonProperty("codice_uo")
    private String codiceUo;
    @JsonProperty("livello_profilo")
    private String livelloProfilo;
    @JsonProperty("matricola")
    private Integer matricola;
    @JsonProperty("uid")
    private String uid;
    @JsonProperty("nome")
    private String nome;
    @JsonProperty("cognome")
    private String cognome;
    @JsonProperty("codice_fiscale")
    private String codiceFiscale;
    @JsonProperty("sesso")
    private String sesso;
    @JsonProperty("data_nascita")
    private String dataNascita;
    @JsonProperty("comune_nascita")
    private String comuneNascita;
    @JsonProperty("provincia_nascita")
    private String provinciaNascita;
    @JsonProperty("nazione_nascita")
    private String nazioneNascita;
    @JsonProperty("fl_cittadino_italiano")
    private Boolean flCittadinoItaliano;
    @JsonProperty("indirizzo_residenza")
    private String indirizzoResidenza;
    @JsonProperty("num_civico_residenza")
    private String numCivicoResidenza;
    @JsonProperty("cap_residenza")
    private String capResidenza;
    @JsonProperty("comune_residenza")
    private String comuneResidenza;
    @JsonProperty("provincia_residenza")
    private String provinciaResidenza;
    @JsonProperty("nazione_residenza")
    private String nazioneResidenza;
    @JsonProperty("indirizzo_comunicazioni")
    private String indirizzoComunicazioni;
    @JsonProperty("num_civico_comunicazioni")
    private String numCivicoComunicazioni;
    @JsonProperty("cap_comunicazioni")
    private String capComunicazioni;
    @JsonProperty("comune_comunicazioni")
    private String comuneComunicazioni;
    @JsonProperty("provincia_comunicazioni")
    private String provinciaComunicazioni;
    @JsonProperty("nazione_comunicazioni")
    private String nazioneComunicazioni;
    @JsonProperty("email_comunicazioni")
    private String emailComunicazioni;
    @JsonProperty("profilo")
    private String profilo;
    @JsonProperty("struttura_appartenenza")
    private String strutturaAppartenenza;
    @JsonProperty("settore_scientifico_tecnologico")
    private List<String> settoreScientificoTecnologico = new ArrayList<String>();
    @JsonProperty("area_scientifica")
    private List<String> areaScientifica = new ArrayList<String>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The codiceSede
     */
    @JsonProperty("codice_sede")
    public String getCodice_sede() {
        return codiceSede;
    }

    /**
     * @param codiceSede The codice_sede
     */
    @JsonProperty("codice_sede")
    public void setCodiceSede(String codiceSede) {
        this.codiceSede = codiceSede;
    }

    /**
     * @return The siglaSede
     */
    @JsonProperty("sigla_sede")
    public String getSiglaSede() {
        return siglaSede;
    }

    /**
     * @param siglaSede The sigla_sede
     */
    @JsonProperty("sigla_sede")
    public void setSiglaSede(String siglaSede) {
        this.siglaSede = siglaSede;
    }

    /**
     * @return The cittaSede
     */
    @JsonProperty("citta_sede")
    public String getCittaSede() {
        return cittaSede;
    }

    /**
     * @param cittaSede The citta_sede
     */
    @JsonProperty("citta_sede")
    public void setCittaSede(String cittaSede) {
        this.cittaSede = cittaSede;
    }

    /**
     * @return The codiceUo
     */
    @JsonProperty("codice_uo")
    public String getCodiceUo() {
        return codiceUo;
    }

    /**
     * @param codiceUo The codice_uo
     */
    @JsonProperty("codice_uo")
    public void setCodiceUo(String codiceUo) {
        this.codiceUo = codiceUo;
    }

    /**
     * @return The livelloProfilo
     */
    @JsonProperty("livello_profilo")
    public String getLivelloProfilo() {
        return livelloProfilo;
    }

    /**
     * @param livelloProfilo The livello_profilo
     */
    @JsonProperty("livello_profilo")
    public void setLivelloProfilo(String livelloProfilo) {
        this.livelloProfilo = livelloProfilo;
    }

    /**
     * @return The matricola
     */
    @JsonProperty("matricola")
    public Integer getMatricola() {
        return matricola;
    }

    /**
     * @param matricola The matricola
     */
    @JsonProperty("matricola")
    public void setMatricola(Integer matricola) {
        this.matricola = matricola;
    }

    /**
     * @return The uid
     */
    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return The nome
     */
    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    /**
     * @param nome The nome
     */
    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return The cognome
     */
    @JsonProperty("cognome")
    public String getCognome() {
        return cognome;
    }

    /**
     * @param cognome The cognome
     */
    @JsonProperty("cognome")
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @return The codiceFiscale
     */
    @JsonProperty("codice_fiscale")
    public String getCodice_fiscale() {
        return codiceFiscale;
    }

    /**
     * @param codiceFiscale The codice_fiscale
     */
    @JsonProperty("codice_fiscale")
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    /**
     * @return The sesso
     */
    @JsonProperty("sesso")
    public String getSesso() {
        return sesso;
    }

    /**
     * @param sesso The sesso
     */
    @JsonProperty("sesso")
    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    /**
     * @return The dataNascita
     */
    @JsonProperty("data_nascita")
    public String getData_nascita() {
        return dataNascita;
    }

    /**
     * @param dataNascita The data_nascita
     */
    @JsonProperty("data_nascita")
    public void setDataNascita(String dataNascita) {
        this.dataNascita = dataNascita;
    }

    /**
     * @return The comuneNascita
     */
    @JsonProperty("comune_nascita")
    public String getComune_nascita() {
        return comuneNascita;
    }

    /**
     * @param comuneNascita The comune_nascita
     */
    @JsonProperty("comune_nascita")
    public void setComuneNascita(String comuneNascita) {
        this.comuneNascita = comuneNascita;
    }

    /**
     * @return The provinciaNascita
     */
    @JsonProperty("provincia_nascita")
    public String getProvinciaNascita() {
        return provinciaNascita;
    }

    /**
     * @param provinciaNascita The provincia_nascita
     */
    @JsonProperty("provincia_nascita")
    public void setProvinciaNascita(String provinciaNascita) {
        this.provinciaNascita = provinciaNascita;
    }

    /**
     * @return The nazioneNascita
     */
    @JsonProperty("nazione_nascita")
    public String getNazioneNascita() {
        return nazioneNascita;
    }

    /**
     * @param nazioneNascita The nazione_nascita
     */
    @JsonProperty("nazione_nascita")
    public void setNazioneNascita(String nazioneNascita) {
        this.nazioneNascita = nazioneNascita;
    }

    /**
     * @return The flCittadinoItaliano
     */
    @JsonProperty("fl_cittadino_italiano")
    public Boolean getFlCittadinoItaliano() {
        return flCittadinoItaliano;
    }

    /**
     * @param flCittadinoItaliano The fl_cittadino_italiano
     */
    @JsonProperty("fl_cittadino_italiano")
    public void setFlCittadinoItaliano(Boolean flCittadinoItaliano) {
        this.flCittadinoItaliano = flCittadinoItaliano;
    }

    /**
     * @return The indirizzoResidenza
     */
    @JsonProperty("indirizzo_residenza")
    public String getIndirizzoResidenza() {
        return indirizzoResidenza;
    }

    /**
     * @param indirizzoResidenza The indirizzo_residenza
     */
    @JsonProperty("indirizzo_residenza")
    public void setIndirizzoResidenza(String indirizzoResidenza) {
        this.indirizzoResidenza = indirizzoResidenza;
    }

    /**
     * @return The numCivicoResidenza
     */
    @JsonProperty("num_civico_residenza")
    public String getNumCivicoResidenza() {
        return numCivicoResidenza;
    }

    /**
     * @param numCivicoResidenza The num_civico_residenza
     */
    @JsonProperty("num_civico_residenza")
    public void setNumCivicoResidenza(String numCivicoResidenza) {
        this.numCivicoResidenza = numCivicoResidenza;
    }

    /**
     * @return The capResidenza
     */
    @JsonProperty("cap_residenza")
    public String getCapResidenza() {
        return capResidenza;
    }

    /**
     * @param capResidenza The cap_residenza
     */
    @JsonProperty("cap_residenza")
    public void setCapResidenza(String capResidenza) {
        this.capResidenza = capResidenza;
    }

    /**
     * @return The comuneResidenza
     */
    @JsonProperty("comune_residenza")
    public String getComuneResidenza() {
        return comuneResidenza;
    }

    /**
     * @param comuneResidenza The comune_residenza
     */
    @JsonProperty("comune_residenza")
    public void setComuneResidenza(String comuneResidenza) {
        this.comuneResidenza = comuneResidenza;
    }

    /**
     * @return The provinciaResidenza
     */
    @JsonProperty("provincia_residenza")
    public String getProvinciaResidenza() {
        return provinciaResidenza;
    }

    /**
     * @param provinciaResidenza The provincia_residenza
     */
    @JsonProperty("provincia_residenza")
    public void setProvinciaResidenza(String provinciaResidenza) {
        this.provinciaResidenza = provinciaResidenza;
    }

    /**
     * @return The nazioneResidenza
     */
    @JsonProperty("nazione_residenza")
    public String getNazioneResidenza() {
        return nazioneResidenza;
    }

    /**
     * @param nazioneResidenza The nazione_residenza
     */
    @JsonProperty("nazione_residenza")
    public void setNazioneResidenza(String nazioneResidenza) {
        this.nazioneResidenza = nazioneResidenza;
    }

    /**
     * @return The indirizzoComunicazioni
     */
    @JsonProperty("indirizzo_comunicazioni")
    public String getIndirizzoComunicazioni() {
        return indirizzoComunicazioni;
    }

    /**
     * @param indirizzoComunicazioni The indirizzo_comunicazioni
     */
    @JsonProperty("indirizzo_comunicazioni")
    public void setIndirizzoComunicazioni(String indirizzoComunicazioni) {
        this.indirizzoComunicazioni = indirizzoComunicazioni;
    }

    /**
     * @return The numCivicoComunicazioni
     */
    @JsonProperty("num_civico_comunicazioni")
    public String getNumCivicoComunicazioni() {
        return numCivicoComunicazioni;
    }

    /**
     * @param numCivicoComunicazioni The num_civico_comunicazioni
     */
    @JsonProperty("num_civico_comunicazioni")
    public void setNumCivicoComunicazioni(String numCivicoComunicazioni) {
        this.numCivicoComunicazioni = numCivicoComunicazioni;
    }

    /**
     * @return The capComunicazioni
     */
    @JsonProperty("cap_comunicazioni")
    public String getCapComunicazioni() {
        return capComunicazioni;
    }

    /**
     * @param capComunicazioni The cap_comunicazioni
     */
    @JsonProperty("cap_comunicazioni")
    public void setCapComunicazioni(String capComunicazioni) {
        this.capComunicazioni = capComunicazioni;
    }

    /**
     * @return The comuneComunicazioni
     */
    @JsonProperty("comune_comunicazioni")
    public String getComuneComunicazioni() {
        return comuneComunicazioni;
    }

    /**
     * @param comuneComunicazioni The comune_comunicazioni
     */
    @JsonProperty("comune_comunicazioni")
    public void setComuneComunicazioni(String comuneComunicazioni) {
        this.comuneComunicazioni = comuneComunicazioni;
    }

    /**
     * @return The provinciaComunicazioni
     */
    @JsonProperty("provincia_comunicazioni")
    public String getProvinciaComunicazioni() {
        return provinciaComunicazioni;
    }

    /**
     * @param provinciaComunicazioni The provincia_comunicazioni
     */
    @JsonProperty("provincia_comunicazioni")
    public void setProvinciaComunicazioni(String provinciaComunicazioni) {
        this.provinciaComunicazioni = provinciaComunicazioni;
    }

    /**
     * @return The nazioneComunicazioni
     */
    @JsonProperty("nazione_comunicazioni")
    public String getNazioneComunicazioni() {
        return nazioneComunicazioni;
    }

    /**
     * @param nazioneComunicazioni The nazione_comunicazioni
     */
    @JsonProperty("nazione_comunicazioni")
    public void setNazioneComunicazioni(String nazioneComunicazioni) {
        this.nazioneComunicazioni = nazioneComunicazioni;
    }

    /**
     * @return The emailComunicazioni
     */
    @JsonProperty("email_comunicazioni")
    public String getEmail_comunicazioni() {
        return emailComunicazioni;
    }

    /**
     * @param emailComunicazioni The email_comunicazioni
     */
    @JsonProperty("email_comunicazioni")
    public void setEmailComunicazioni(String emailComunicazioni) {
        this.emailComunicazioni = emailComunicazioni;
    }

    /**
     * @return The profilo
     */
    @JsonProperty("profilo")
    public String getProfilo() {
        return profilo;
    }

    /**
     * @param profilo The profilo
     */
    @JsonProperty("profilo")
    public void setProfilo(String profilo) {
        this.profilo = profilo;
    }

    /**
     * @return The strutturaAppartenenza
     */
    @JsonProperty("struttura_appartenenza")
    public String getStrutturaAppartenenza() {
        return strutturaAppartenenza;
    }

    /**
     * @param strutturaAppartenenza The struttura_appartenenza
     */
    @JsonProperty("struttura_appartenenza")
    public void setStrutturaAppartenenza(String strutturaAppartenenza) {
        this.strutturaAppartenenza = strutturaAppartenenza;
    }

    /**
     * @return The settoreScientificoTecnologico
     */
    @JsonProperty("settore_scientifico_tecnologico")
    public List<String> getSettoreScientificoTecnologico() {
        return settoreScientificoTecnologico;
    }

    /**
     * @param settoreScientificoTecnologico The settore_scientifico_tecnologico
     */
    @JsonProperty("settore_scientifico_tecnologico")
    public void setSettoreScientificoTecnologico(List<String> settoreScientificoTecnologico) {
        this.settoreScientificoTecnologico = settoreScientificoTecnologico;
    }

    /**
     * @return The areaScientifica
     */
    @JsonProperty("area_scientifica")
    public List<String> getAreaScientifica() {
        return areaScientifica;
    }

    /**
     * @param areaScientifica The area_scientifica
     */
    @JsonProperty("area_scientifica")
    public void setAreaScientifica(List<String> areaScientifica) {
        this.areaScientifica = areaScientifica;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
