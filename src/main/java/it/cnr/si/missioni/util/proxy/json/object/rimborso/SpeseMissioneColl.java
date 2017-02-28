
package it.cnr.si.missioni.util.proxy.json.object.rimborso;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "pg_riga",
    "dt_inizio_tappa",
    "ti_spesa_diaria",
    "cd_ti_spesa",
    "ds_ti_spesa",
    "fl_spesa_anticipata",
    "fl_diaria_manuale",
    "percentuale_maggiorazione",
    "im_totale_spesa",
    "im_maggiorazione",
    "im_spesa_euro",
    "im_base_maggiorazione",
    "im_spesa_divisa",
    "im_diaria_lorda",
    "im_diaria_netto",
    "im_rimborso",
    "im_spesa_max",
    "im_maggiorazione_euro",
    "im_spesa_max_divisa",
    "im_quota_esente",
    "cd_ti_pasto",
    "chilometri",
    "ti_auto",
    "ds_spesa",
    "localita_spostamento",
    "indennita_chilometrica",
    "ds_giustificativo",
    "id_giustificativo",
    "ds_no_giustificativo",
    "cd_divisa_spesa",
    "cambio_spesa",
    "ti_cd_ti_spesa"
})
public class SpeseMissioneColl implements Serializable {

    @JsonProperty("pg_riga")
    private Integer pgRiga;
    @JsonProperty("dt_inizio_tappa")
    private String dtInizioTappa;
    @JsonProperty("ti_spesa_diaria")
    private String tiSpesaDiaria;
    @JsonProperty("cd_ti_spesa")
    private String cdTiSpesa;
    @JsonProperty("ds_ti_spesa")
    private String dsTiSpesa;
    @JsonProperty("fl_spesa_anticipata")
    private Boolean flSpesaAnticipata;
    @JsonProperty("fl_diaria_manuale")
    private Boolean flDiariaManuale;
    @JsonProperty("percentuale_maggiorazione")
    private BigDecimal percentualeMaggiorazione;
    @JsonProperty("im_totale_spesa")
    private BigDecimal imTotaleSpesa;
    @JsonProperty("im_maggiorazione")
    private BigDecimal imMaggiorazione;
    @JsonProperty("im_spesa_euro")
    private BigDecimal imSpesaEuro;
    @JsonProperty("im_base_maggiorazione")
    private BigDecimal imBaseMaggiorazione;
    @JsonProperty("im_spesa_divisa")
    private BigDecimal imSpesaDivisa;
    @JsonProperty("im_diaria_lorda")
    private BigDecimal imDiariaLorda;
    @JsonProperty("im_diaria_netto")
    private BigDecimal imDiariaNetto;
    @JsonProperty("im_rimborso")
    private BigDecimal imRimborso;
    @JsonProperty("im_spesa_max")
    private BigDecimal imSpesaMax;
    @JsonProperty("im_maggiorazione_euro")
    private BigDecimal imMaggiorazioneEuro;
    @JsonProperty("im_spesa_max_divisa")
    private BigDecimal imSpesaMaxDivisa;
    @JsonProperty("im_quota_esente")
    private BigDecimal imQuotaEsente;
    @JsonProperty("chilometri")
    private Long chilometri;
    @JsonProperty("indennita_chilometrica")
    private BigDecimal indennitaChilometrica;
    @JsonProperty("ds_spesa")
    private String dsSpesa;
    @JsonProperty("ti_auto")
    private String tiAuto;
    @JsonProperty("cd_ti_pasto")
    private String cdTiPasto;
    @JsonProperty("localita_spostamento")
    private String localitaSpostamento;
    @JsonProperty("ds_giustificativo")
    private String dsGiustificativo;
    @JsonProperty("ds_no_giustificativo")
    private String dsNoGiustificativo;
    @JsonProperty("id_giustificativo")
    private String idGiustificativo;
    @JsonProperty("cambio_spesa")
    private BigDecimal cambioSpesa;
    @JsonProperty("cd_divisa_spesa")
    private String cdDivisaSpesa;
    @JsonProperty("ti_cd_ti_spesa")
    private String tiCdTiSpesa;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The pgRiga
     */
    @JsonProperty("pg_riga")
    public Integer getPgRiga() {
        return pgRiga;
    }

    /**
     * 
     * @param pgRiga
     *     The pg_riga
     */
    @JsonProperty("pg_riga")
    public void setPgRiga(Integer pgRiga) {
        this.pgRiga = pgRiga;
    }

    /**
     * 
     * @return
     *     The dtInizioTappa
     */
    @JsonProperty("dt_inizio_tappa")
    public String getDtInizioTappa() {
        return dtInizioTappa;
    }

    /**
     * 
     * @param dtInizioTappa
     *     The dt_inizio_tappa
     */
    @JsonProperty("dt_inizio_tappa")
    public void setDtInizioTappa(String dtInizioTappa) {
        this.dtInizioTappa = dtInizioTappa;
    }

    /**
     * 
     * @return
     *     The tiSpesaDiaria
     */
    @JsonProperty("ti_spesa_diaria")
    public String getTiSpesaDiaria() {
        return tiSpesaDiaria;
    }

    /**
     * 
     * @param tiSpesaDiaria
     *     The ti_spesa_diaria
     */
    @JsonProperty("ti_spesa_diaria")
    public void setTiSpesaDiaria(String tiSpesaDiaria) {
        this.tiSpesaDiaria = tiSpesaDiaria;
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
     *     The flSpesaAnticipata
     */
    @JsonProperty("fl_spesa_anticipata")
    public Boolean getFlSpesaAnticipata() {
        return flSpesaAnticipata;
    }

    /**
     * 
     * @param flSpesaAnticipata
     *     The fl_spesa_anticipata
     */
    @JsonProperty("fl_spesa_anticipata")
    public void setFlSpesaAnticipata(Boolean flSpesaAnticipata) {
        this.flSpesaAnticipata = flSpesaAnticipata;
    }

    /**
     * 
     * @return
     *     The flDiariaManuale
     */
    @JsonProperty("fl_diaria_manuale")
    public Boolean getFlDiariaManuale() {
        return flDiariaManuale;
    }

    /**
     * 
     * @param flDiariaManuale
     *     The fl_diaria_manuale
     */
    @JsonProperty("fl_diaria_manuale")
    public void setFlDiariaManuale(Boolean flDiariaManuale) {
        this.flDiariaManuale = flDiariaManuale;
    }

    /**
     * 
     * @return
     *     The percentualeMaggiorazione
     */
    @JsonProperty("percentuale_maggiorazione")
    public BigDecimal getPercentualeMaggiorazione() {
        return percentualeMaggiorazione;
    }

    /**
     * 
     * @param percentualeMaggiorazione
     *     The percentuale_maggiorazione
     */
    @JsonProperty("percentuale_maggiorazione")
    public void setPercentualeMaggiorazione(BigDecimal percentualeMaggiorazione) {
        this.percentualeMaggiorazione = percentualeMaggiorazione;
    }

    /**
     * 
     * @return
     *     The imTotaleSpesa
     */
    @JsonProperty("im_totale_spesa")
    public BigDecimal getImTotaleSpesa() {
        return imTotaleSpesa;
    }

    /**
     * 
     * @param imTotaleSpesa
     *     The im_totale_spesa
     */
    @JsonProperty("im_totale_spesa")
    public void setImTotaleSpesa(BigDecimal imTotaleSpesa) {
        this.imTotaleSpesa = imTotaleSpesa;
    }

    /**
     * 
     * @return
     *     The imMaggiorazione
     */
    @JsonProperty("im_maggiorazione")
    public BigDecimal getImMaggiorazione() {
        return imMaggiorazione;
    }

    /**
     * 
     * @param imMaggiorazione
     *     The im_maggiorazione
     */
    @JsonProperty("im_maggiorazione")
    public void setImMaggiorazione(BigDecimal imMaggiorazione) {
        this.imMaggiorazione = imMaggiorazione;
    }

    /**
     * 
     * @return
     *     The imSpesaEuro
     */
    @JsonProperty("im_spesa_euro")
    public BigDecimal getImSpesaEuro() {
        return imSpesaEuro;
    }

    /**
     * 
     * @param imSpesaEuro
     *     The im_spesa_euro
     */
    @JsonProperty("im_spesa_euro")
    public void setImSpesaEuro(BigDecimal imSpesaEuro) {
        this.imSpesaEuro = imSpesaEuro;
    }

    /**
     * 
     * @return
     *     The imBaseMaggiorazione
     */
    @JsonProperty("im_base_maggiorazione")
    public BigDecimal getImBaseMaggiorazione() {
        return imBaseMaggiorazione;
    }

    /**
     * 
     * @param imBaseMaggiorazione
     *     The im_base_maggiorazione
     */
    @JsonProperty("im_base_maggiorazione")
    public void setImBaseMaggiorazione(BigDecimal imBaseMaggiorazione) {
        this.imBaseMaggiorazione = imBaseMaggiorazione;
    }

    /**
     * 
     * @return
     *     The imSpesaDivisa
     */
    @JsonProperty("im_spesa_divisa")
    public BigDecimal getImSpesaDivisa() {
        return imSpesaDivisa;
    }

    /**
     * 
     * @param imSpesaDivisa
     *     The im_spesa_divisa
     */
    @JsonProperty("im_spesa_divisa")
    public void setImSpesaDivisa(BigDecimal imSpesaDivisa) {
        this.imSpesaDivisa = imSpesaDivisa;
    }

    /**
     * 
     * @return
     *     The imDiariaLorda
     */
    @JsonProperty("im_diaria_lorda")
    public BigDecimal getImDiariaLorda() {
        return imDiariaLorda;
    }

    /**
     * 
     * @param imDiariaLorda
     *     The im_diaria_lorda
     */
    @JsonProperty("im_diaria_lorda")
    public void setImDiariaLorda(BigDecimal imDiariaLorda) {
        this.imDiariaLorda = imDiariaLorda;
    }

    /**
     * 
     * @return
     *     The imDiariaNetto
     */
    @JsonProperty("im_diaria_netto")
    public BigDecimal getImDiariaNetto() {
        return imDiariaNetto;
    }

    /**
     * 
     * @param imDiariaNetto
     *     The im_diaria_netto
     */
    @JsonProperty("im_diaria_netto")
    public void setImDiariaNetto(BigDecimal imDiariaNetto) {
        this.imDiariaNetto = imDiariaNetto;
    }

    /**
     * 
     * @return
     *     The imRimborso
     */
    @JsonProperty("im_rimborso")
    public BigDecimal getImRimborso() {
        return imRimborso;
    }

    /**
     * 
     * @param imRimborso
     *     The im_rimborso
     */
    @JsonProperty("im_rimborso")
    public void setImRimborso(BigDecimal imRimborso) {
        this.imRimborso = imRimborso;
    }

    /**
     * 
     * @return
     *     The imSpesaMax
     */
    @JsonProperty("im_spesa_max")
    public BigDecimal getImSpesaMax() {
        return imSpesaMax;
    }

    /**
     * 
     * @param imSpesaMax
     *     The im_spesa_max
     */
    @JsonProperty("im_spesa_max")
    public void setImSpesaMax(BigDecimal imSpesaMax) {
        this.imSpesaMax = imSpesaMax;
    }

    /**
     * 
     * @return
     *     The imMaggiorazioneEuro
     */
    @JsonProperty("im_maggiorazione_euro")
    public BigDecimal getImMaggiorazioneEuro() {
        return imMaggiorazioneEuro;
    }

    /**
     * 
     * @param imMaggiorazioneEuro
     *     The im_maggiorazione_euro
     */
    @JsonProperty("im_maggiorazione_euro")
    public void setImMaggiorazioneEuro(BigDecimal imMaggiorazioneEuro) {
        this.imMaggiorazioneEuro = imMaggiorazioneEuro;
    }

    /**
     * 
     * @return
     *     The imSpesaMaxDivisa
     */
    @JsonProperty("im_spesa_max_divisa")
    public BigDecimal getImSpesaMaxDivisa() {
        return imSpesaMaxDivisa;
    }

    /**
     * 
     * @param imSpesaMaxDivisa
     *     The im_spesa_max_divisa
     */
    @JsonProperty("im_spesa_max_divisa")
    public void setImSpesaMaxDivisa(BigDecimal imSpesaMaxDivisa) {
        this.imSpesaMaxDivisa = imSpesaMaxDivisa;
    }

    /**
     * 
     * @return
     *     The imQuotaEsente
     */
    @JsonProperty("im_quota_esente")
    public BigDecimal getImQuotaEsente() {
        return imQuotaEsente;
    }

    /**
     * 
     * @param imQuotaEsente
     *     The im_quota_esente
     */
    @JsonProperty("im_quota_esente")
    public void setImQuotaEsente(BigDecimal imQuotaEsente) {
        this.imQuotaEsente = imQuotaEsente;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	public Long getChilometri() {
		return chilometri;
	}

	public void setChilometri(Long chilometri) {
		this.chilometri = chilometri;
	}

	public BigDecimal getIndennitaChilometrica() {
		return indennitaChilometrica;
	}

	public void setIndennitaChilometrica(BigDecimal indennitaChilometrica) {
		this.indennitaChilometrica = indennitaChilometrica;
	}

	public String getDsSpesa() {
		return dsSpesa;
	}

	public void setDsSpesa(String dsSpesa) {
		this.dsSpesa = dsSpesa;
	}

	public String getTiAuto() {
		return tiAuto;
	}

	public void setTiAuto(String tiAuto) {
		this.tiAuto = tiAuto;
	}

	public String getCdTiPasto() {
		return cdTiPasto;
	}

	public void setCdTiPasto(String cdTiPasto) {
		this.cdTiPasto = cdTiPasto;
	}

	public String getLocalitaSpostamento() {
		return localitaSpostamento;
	}

	public void setLocalitaSpostamento(String localitaSpostamento) {
		this.localitaSpostamento = localitaSpostamento;
	}

	public String getDsGiustificativo() {
		return dsGiustificativo;
	}

	public void setDsGiustificativo(String dsGiustificativo) {
		this.dsGiustificativo = dsGiustificativo;
	}

	public String getDsNoGiustificativo() {
		return dsNoGiustificativo;
	}

	public void setDsNoGiustificativo(String dsNoGiustificativo) {
		this.dsNoGiustificativo = dsNoGiustificativo;
	}

	public String getIdGiustificativo() {
		return idGiustificativo;
	}

	public void setIdGiustificativo(String idGiustificativo) {
		this.idGiustificativo = idGiustificativo;
	}

	public BigDecimal getCambioSpesa() {
		return cambioSpesa;
	}

	public void setCambioSpesa(BigDecimal cambioSpesa) {
		this.cambioSpesa = cambioSpesa;
	}

	public String getCdDivisaSpesa() {
		return cdDivisaSpesa;
	}

	public void setCdDivisaSpesa(String cdDivisaSpesa) {
		this.cdDivisaSpesa = cdDivisaSpesa;
	}

	public String getTiCdTiSpesa() {
		return tiCdTiSpesa;
	}

	public void setTiCdTiSpesa(String tiCdTiSpesa) {
		this.tiCdTiSpesa = tiCdTiSpesa;
	}

}
