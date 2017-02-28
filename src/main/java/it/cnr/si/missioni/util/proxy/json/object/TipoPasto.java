
package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "cd_ti_pasto",
    "inquadramento",
    "ti_area_geografica",
    "pg_nazione",
    "ds_nazione",
    "cd_divisa",
    "limite_max_pasto",
    "dt_inizio_validita",
    "dt_fine_validita",
    "dt_cancellazione",
    "cd_area_estera",
    "ds_area_estera"
})
public class TipoPasto extends RestServiceBean implements Serializable {

    @JsonProperty("cd_ti_pasto")
    private String cdTiPasto;
    @JsonProperty("inquadramento")
    private Integer inquadramento;
    @JsonProperty("ti_area_geografica")
    private String tiAreaGeografica;
    @JsonProperty("pg_nazione")
    private Integer pgNazione;
    @JsonProperty("ds_nazione")
    private String dsNazione;
    @JsonProperty("cd_divisa")
    private String cdDivisa;
    @JsonProperty("limite_max_pasto")
    private Double limiteMaxPasto;
    @JsonProperty("dt_inizio_validita")
    private Integer dtInizioValidita;
    @JsonProperty("dt_fine_validita")
    private Integer dtFineValidita;
    @JsonProperty("dt_cancellazione")
    private Serializable dtCancellazione;
    @JsonProperty("cd_area_estera")
    private String cdAreaEstera;
    @JsonProperty("ds_area_estera")
    private String dsAreaEstera;
    @JsonIgnore
    private Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

    /**
     * 
     * @return
     *     The cdTiPasto
     */
    @JsonProperty("cd_ti_pasto")
    public String getCdTiPasto() {
        return cdTiPasto;
    }

    /**
     * 
     * @param cdTiPasto
     *     The cd_ti_pasto
     */
    @JsonProperty("cd_ti_pasto")
    public void setCdTiPasto(String cdTiPasto) {
        this.cdTiPasto = cdTiPasto;
    }

    /**
     * 
     * @return
     *     The inquadramento
     */
    @JsonProperty("inquadramento")
    public Integer getInquadramento() {
        return inquadramento;
    }

    /**
     * 
     * @param inquadramento
     *     The inquadramento
     */
    @JsonProperty("inquadramento")
    public void setInquadramento(Integer inquadramento) {
        this.inquadramento = inquadramento;
    }

    /**
     * 
     * @return
     *     The tiAreaGeografica
     */
    @JsonProperty("ti_area_geografica")
    public String getTiAreaGeografica() {
        return tiAreaGeografica;
    }

    /**
     * 
     * @param tiAreaGeografica
     *     The ti_area_geografica
     */
    @JsonProperty("ti_area_geografica")
    public void setTiAreaGeografica(String tiAreaGeografica) {
        this.tiAreaGeografica = tiAreaGeografica;
    }

    /**
     * 
     * @return
     *     The pgNazione
     */
    @JsonProperty("pg_nazione")
    public Integer getPgNazione() {
        return pgNazione;
    }

    /**
     * 
     * @param pgNazione
     *     The pg_nazione
     */
    @JsonProperty("pg_nazione")
    public void setPgNazione(Integer pgNazione) {
        this.pgNazione = pgNazione;
    }

    /**
     * 
     * @return
     *     The dsNazione
     */
    @JsonProperty("ds_nazione")
    public String getDsNazione() {
        return dsNazione;
    }

    /**
     * 
     * @param dsNazione
     *     The ds_nazione
     */
    @JsonProperty("ds_nazione")
    public void setDsNazione(String dsNazione) {
        this.dsNazione = dsNazione;
    }

    /**
     * 
     * @return
     *     The cdDivisa
     */
    @JsonProperty("cd_divisa")
    public String getCdDivisa() {
        return cdDivisa;
    }

    /**
     * 
     * @param cdDivisa
     *     The cd_divisa
     */
    @JsonProperty("cd_divisa")
    public void setCdDivisa(String cdDivisa) {
        this.cdDivisa = cdDivisa;
    }

    /**
     * 
     * @return
     *     The limiteMaxPasto
     */
    @JsonProperty("limite_max_pasto")
    public Double getLimiteMaxPasto() {
        return limiteMaxPasto;
    }

    /**
     * 
     * @param limiteMaxPasto
     *     The limite_max_pasto
     */
    @JsonProperty("limite_max_pasto")
    public void setLimiteMaxPasto(Double limiteMaxPasto) {
        this.limiteMaxPasto = limiteMaxPasto;
    }

    /**
     * 
     * @return
     *     The dtInizioValidita
     */
    @JsonProperty("dt_inizio_validita")
    public Integer getDtInizioValidita() {
        return dtInizioValidita;
    }

    /**
     * 
     * @param dtInizioValidita
     *     The dt_inizio_validita
     */
    @JsonProperty("dt_inizio_validita")
    public void setDtInizioValidita(Integer dtInizioValidita) {
        this.dtInizioValidita = dtInizioValidita;
    }

    /**
     * 
     * @return
     *     The dtFineValidita
     */
    @JsonProperty("dt_fine_validita")
    public Integer getDtFineValidita() {
        return dtFineValidita;
    }

    /**
     * 
     * @param dtFineValidita
     *     The dt_fine_validita
     */
    @JsonProperty("dt_fine_validita")
    public void setDtFineValidita(Integer dtFineValidita) {
        this.dtFineValidita = dtFineValidita;
    }

    /**
     * 
     * @return
     *     The dtCancellazione
     */
    @JsonProperty("dt_cancellazione")
    public Serializable getDtCancellazione() {
        return dtCancellazione;
    }

    /**
     * 
     * @param dtCancellazione
     *     The dt_cancellazione
     */
    @JsonProperty("dt_cancellazione")
    public void setDtCancellazione(Serializable dtCancellazione) {
        this.dtCancellazione = dtCancellazione;
    }

    /**
     * 
     * @return
     *     The cdAreaEstera
     */
    @JsonProperty("cd_area_estera")
    public String getCdAreaEstera() {
        return cdAreaEstera;
    }

    /**
     * 
     * @param cdAreaEstera
     *     The cd_area_estera
     */
    @JsonProperty("cd_area_estera")
    public void setCdAreaEstera(String cdAreaEstera) {
        this.cdAreaEstera = cdAreaEstera;
    }

    /**
     * 
     * @return
     *     The dsAreaEstera
     */
    @JsonProperty("ds_area_estera")
    public String getDsAreaEstera() {
        return dsAreaEstera;
    }

    /**
     * 
     * @param dsAreaEstera
     *     The ds_area_estera
     */
    @JsonProperty("ds_area_estera")
    public void setDsAreaEstera(String dsAreaEstera) {
        this.dsAreaEstera = dsAreaEstera;
    }

    @JsonAnyGetter
    public Map<String, Serializable> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Serializable value) {
        this.additionalProperties.put(name, value);
    }

}
