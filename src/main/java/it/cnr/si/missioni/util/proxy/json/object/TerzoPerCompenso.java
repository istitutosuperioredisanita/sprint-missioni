package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
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
"cd_terzo",
"cd_anag",
"ti_dipendente_altro",
"codice_fiscale",
"ds_tipo_rapporto",
"dt_ini_validita",
"dt_fin_validita"
})

public class TerzoPerCompenso extends RestServiceBean  implements Serializable {


	@JsonProperty("cd_terzo")
	private Integer cdTerzo;
	@JsonProperty("cd_anag")
	private Integer cdAnag;
	@JsonProperty("ti_dipendente_altro")
	private String tiDipendenteAltro;
	@JsonProperty("codice_fiscale")
	private String codiceFiscale;
	@JsonProperty("ds_tipo_rapporto")
	private String dsTipoRapporto;
	@JsonProperty("dt_ini_validita")
	private Object dtIniValidita;
	@JsonProperty("dt_fin_validita")
	private Object dtFinValidita;
	@JsonProperty("ds_comune_fiscale")
	private String dsComuneFiscale;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("cd_terzo")
	public Integer getCdTerzo() {
	return cdTerzo;
	}

	@JsonProperty("cd_terzo")
	public void setCdTerzo(Integer cdTerzo) {
	this.cdTerzo = cdTerzo;
	}

	@JsonProperty("cd_anag")
	public Integer getCdAnag() {
	return cdAnag;
	}

	@JsonProperty("cd_anag")
	public void setCdAnag(Integer cdAnag) {
	this.cdAnag = cdAnag;
	}

	@JsonProperty("ti_dipendente_altro")
	public String getTiDipendenteAltro() {
	return tiDipendenteAltro;
	}

	@JsonProperty("ti_dipendente_altro")
	public void setTiDipendenteAltro(String tiDipendenteAltro) {
	this.tiDipendenteAltro = tiDipendenteAltro;
	}

	@JsonProperty("codice_fiscale")
	public String getCodiceFiscale() {
	return codiceFiscale;
	}

	@JsonProperty("codice_fiscale")
	public void setCodiceFiscale(String codiceFiscale) {
	this.codiceFiscale = codiceFiscale;
	}

	@JsonProperty("ds_tipo_rapporto")
	public String getDsTipoRapporto() {
	return dsTipoRapporto;
	}

	@JsonProperty("ds_tipo_rapporto")
	public void setDsTipoRapporto(String dsTipoRapporto) {
	this.dsTipoRapporto = dsTipoRapporto;
	}

	@JsonProperty("dt_ini_validita")
	public Object getDtIniValidita() {
	return dtIniValidita;
	}

	@JsonProperty("dt_ini_validita")
	public void setDtIniValidita(Object dtIniValidita) {
	this.dtIniValidita = dtIniValidita;
	}

	@JsonProperty("dt_fin_validita")
	public Object getDtFinValidita() {
	return dtFinValidita;
	}

	@JsonProperty("dt_fin_validita")
	public void setDtFinValidita(Object dtFinValidita) {
	this.dtFinValidita = dtFinValidita;
	}

	@JsonProperty("ds_comune_fiscale")
	public String getDsComuneFiscale() {
	return dsComuneFiscale;
	}

	@JsonProperty("ds_comune_fiscale")
	public void setDsComuneFiscale(String dsComuneFiscale) {
	this.dsComuneFiscale = dsComuneFiscale;
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
