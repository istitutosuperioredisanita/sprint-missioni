
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
    "pg_rif_inquadramento",
    "cd_ti_spesa",
    "ds_ti_spesa",
    "ti_area_geografica",
    "pg_nazione",
    "ds_nazione",
    "dt_inizio_validita",
    "dataFineValidita",
    "fl_giustificativo_richiesto",
    "fl_pasto",
    "fl_trasporto",
    "fl_rimborso_km",
    "fl_alloggio",
    "cd_divisa",
    "ds_divisa",
    "percentuale_maggiorazione",
    "limite_max_spesa",
    "dt_cancellazione",
    "fl_ammissibile_con_rimborso"
})
public class TipoSpesa  extends RestServiceBean implements Serializable {

    @JsonProperty("pg_rif_inquadramento")
    private Integer pgRifInquadramento;
    @JsonProperty("cd_ti_spesa")
    private String cdTiSpesa;
    @JsonProperty("ds_ti_spesa")
    private String dsTiSpesa;
    @JsonProperty("ti_area_geografica")
    private String tiAreaGeografica;
    @JsonProperty("pg_nazione")
    private Integer pgNazione;
    @JsonProperty("ds_nazione")
    private String dsNazione;
    @JsonProperty("dt_inizio_validita")
    private Integer dtInizioValidita;
    @JsonProperty("dataFineValidita")
    private Serializable dataFineValidita;
    @JsonProperty("fl_giustificativo_richiesto")
    private Boolean flGiustificativoRichiesto;
    @JsonProperty("fl_pasto")
    private Boolean flPasto;
    @JsonProperty("fl_trasporto")
    private Boolean flTrasporto;
    @JsonProperty("fl_rimborso_km")
    private Boolean flRimborsoKm;
    @JsonProperty("fl_alloggio")
    private Boolean flAlloggio;
    @JsonProperty("cd_divisa")
    private String cdDivisa;
    @JsonProperty("ds_divisa")
    private String dsDivisa;
    @JsonProperty("percentuale_maggiorazione")
    private Integer percentualeMaggiorazione;
    @JsonProperty("limite_max_spesa")
    private Integer limiteMaxSpesa;
    @JsonProperty("dt_cancellazione")
    private Serializable dtCancellazione;
    @JsonProperty("fl_ammissibile_con_rimborso")
    private Boolean flAmmissibileConRimborso;
    @JsonIgnore
    private Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

    /**
     * 
     * @return
     *     The pgRifInquadramento
     */
    @JsonProperty("pg_rif_inquadramento")
    public Integer getPgRifInquadramento() {
        return pgRifInquadramento;
    }

    /**
     * 
     * @param pgRifInquadramento
     *     The pg_rif_inquadramento
     */
    @JsonProperty("pg_rif_inquadramento")
    public void setPgRifInquadramento(Integer pgRifInquadramento) {
        this.pgRifInquadramento = pgRifInquadramento;
    }

    /**
     * 
     * @return
     *     The cdTiSpesa
     */
    @JsonProperty("cd_ti_spesa")
    public String getCdTiSpesa() {
        return cdTiSpesa;
    }

    /**
     * 
     * @param cdTiSpesa
     *     The cd_ti_spesa
     */
    @JsonProperty("cd_ti_spesa")
    public void setCdTiSpesa(String cdTiSpesa) {
        this.cdTiSpesa = cdTiSpesa;
    }

    /**
     * 
     * @return
     *     The dsTiSpesa
     */
    @JsonProperty("ds_ti_spesa")
    public String getDsTiSpesa() {
        return dsTiSpesa;
    }

    /**
     * 
     * @param dsTiSpesa
     *     The ds_ti_spesa
     */
    @JsonProperty("ds_ti_spesa")
    public void setDsTiSpesa(String dsTiSpesa) {
        this.dsTiSpesa = dsTiSpesa;
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
     *     The dataFineValidita
     */
    @JsonProperty("dataFineValidita")
    public Serializable getDataFineValidita() {
        return dataFineValidita;
    }

    /**
     * 
     * @param dataFineValidita
     *     The dataFineValidita
     */
    @JsonProperty("dataFineValidita")
    public void setDataFineValidita(Serializable dataFineValidita) {
        this.dataFineValidita = dataFineValidita;
    }

    /**
     * 
     * @return
     *     The flGiustificativoRichiesto
     */
    @JsonProperty("fl_giustificativo_richiesto")
    public Boolean getFlGiustificativoRichiesto() {
        return flGiustificativoRichiesto;
    }

    /**
     * 
     * @param flGiustificativoRichiesto
     *     The fl_giustificativo_richiesto
     */
    @JsonProperty("fl_giustificativo_richiesto")
    public void setFlGiustificativoRichiesto(Boolean flGiustificativoRichiesto) {
        this.flGiustificativoRichiesto = flGiustificativoRichiesto;
    }

    /**
     * 
     * @return
     *     The flPasto
     */
    @JsonProperty("fl_pasto")
    public Boolean getFlPasto() {
        return flPasto;
    }

    /**
     * 
     * @param flPasto
     *     The fl_pasto
     */
    @JsonProperty("fl_pasto")
    public void setFlPasto(Boolean flPasto) {
        this.flPasto = flPasto;
    }

    /**
     * 
     * @return
     *     The flTrasporto
     */
    @JsonProperty("fl_trasporto")
    public Boolean getFlTrasporto() {
        return flTrasporto;
    }

    /**
     * 
     * @param flTrasporto
     *     The fl_trasporto
     */
    @JsonProperty("fl_trasporto")
    public void setFlTrasporto(Boolean flTrasporto) {
        this.flTrasporto = flTrasporto;
    }

    /**
     * 
     * @return
     *     The flRimborsoKm
     */
    @JsonProperty("fl_rimborso_km")
    public Boolean getFlRimborsoKm() {
        return flRimborsoKm;
    }

    /**
     * 
     * @param flRimborsoKm
     *     The fl_rimborso_km
     */
    @JsonProperty("fl_rimborso_km")
    public void setFlRimborsoKm(Boolean flRimborsoKm) {
        this.flRimborsoKm = flRimborsoKm;
    }

    /**
     * 
     * @return
     *     The flAlloggio
     */
    @JsonProperty("fl_alloggio")
    public Boolean getFlAlloggio() {
        return flAlloggio;
    }

    /**
     * 
     * @param flAlloggio
     *     The fl_alloggio
     */
    @JsonProperty("fl_alloggio")
    public void setFlAlloggio(Boolean flAlloggio) {
        this.flAlloggio = flAlloggio;
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
     *     The dsDivisa
     */
    @JsonProperty("ds_divisa")
    public String getDsDivisa() {
        return dsDivisa;
    }

    /**
     * 
     * @param dsDivisa
     *     The ds_divisa
     */
    @JsonProperty("ds_divisa")
    public void setDsDivisa(String dsDivisa) {
        this.dsDivisa = dsDivisa;
    }

    /**
     * 
     * @return
     *     The percentualeMaggiorazione
     */
    @JsonProperty("percentuale_maggiorazione")
    public Integer getPercentualeMaggiorazione() {
        return percentualeMaggiorazione;
    }

    /**
     * 
     * @param percentualeMaggiorazione
     *     The percentuale_maggiorazione
     */
    @JsonProperty("percentuale_maggiorazione")
    public void setPercentualeMaggiorazione(Integer percentualeMaggiorazione) {
        this.percentualeMaggiorazione = percentualeMaggiorazione;
    }

    /**
     * 
     * @return
     *     The limiteMaxSpesa
     */
    @JsonProperty("limite_max_spesa")
    public Integer getLimiteMaxSpesa() {
        return limiteMaxSpesa;
    }

    /**
     * 
     * @param limiteMaxSpesa
     *     The limite_max_spesa
     */
    @JsonProperty("limite_max_spesa")
    public void setLimiteMaxSpesa(Integer limiteMaxSpesa) {
        this.limiteMaxSpesa = limiteMaxSpesa;
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
     *     The flAmmissibileConRimborso
     */
    @JsonProperty("fl_ammissibile_con_rimborso")
    public Boolean getFlAmmissibileConRimborso() {
        return flAmmissibileConRimborso;
    }

    /**
     * 
     * @param flAmmissibileConRimborso
     *     The fl_ammissibile_con_rimborso
     */
    @JsonProperty("fl_ammissibile_con_rimborso")
    public void setFlAmmissibileConRimborso(Boolean flAmmissibileConRimborso) {
        this.flAmmissibileConRimborso = flAmmissibileConRimborso;
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
