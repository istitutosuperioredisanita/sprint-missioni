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
	"cd_progetto",
	"pg_progetto",
	"dt_proroga",
	"ds_progetto",
	"cd_responsabile_terzo",
	"codice_fiscale_responsabile",
	"dt_inizio",
	"dt_fine",
	"stato",
	"esercizio",
	"cd_unita_organizzativa"
})
public class Progetto extends RestServiceBean  implements Serializable {

	@JsonProperty("cd_progetto")
	private String cd_progetto;
	@JsonProperty("pg_progetto")
	private Long pg_progetto;
	@JsonProperty("dt_proroga")
	private Serializable dt_proroga;
	@JsonProperty("ds_progetto")
	private String ds_progetto;
	@JsonProperty("cd_responsabile_terzo")
	private Integer cd_responsabile_terzo;
	@JsonProperty("codice_fiscale_responsabile")
	private String codice_fiscale_responsabile;
	@JsonProperty("dt_inizio")
	private Serializable dt_inizio;
	@JsonProperty("dt_fine")
	private Serializable dt_fine;
	@JsonProperty("stato")
	private String stato;
	@JsonProperty("esercizio")
	private Integer esercizio;
	@JsonProperty("cd_unita_organizzativa")
	private String cd_unita_organizzativa;
	@JsonIgnore
	private Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

	/**
	 *
	 * @return
	 * The cd_progetto
	 */
	@JsonProperty("cd_progetto")
	public String getCd_progetto() {
		return cd_progetto;
	}

	/**
	 *
	 * @param cd_progetto
	 * The cd_progetto
	 */
	@JsonProperty("cd_progetto")
	public void setCd_progetto(String cd_progetto) {
		this.cd_progetto = cd_progetto;
	}

	/**
	 *
	 * @return
	 * The pg_progetto
	 */
	@JsonProperty("pg_progetto")
	public Long getPg_progetto() {
		return pg_progetto;
	}

	/**
	 *
	 * @param pg_progetto
	 * The pg_progetto
	 */
	@JsonProperty("pg_progetto")
	public void setPg_progetto(Long pg_progetto) {
		this.pg_progetto = pg_progetto;
	}

	/**
	 *
	 * @return
	 * The dt_proroga
	 */
	@JsonProperty("dt_proroga")
	public Serializable getDt_proroga() {
		return dt_proroga;
	}

	/**
	 *
	 * @param dt_proroga
	 * The dt_proroga
	 */
	@JsonProperty("dt_proroga")
	public void setDt_proroga(Serializable dt_proroga) {
		this.dt_proroga = dt_proroga;
	}

	/**
	 *
	 * @return
	 * The ds_progetto
	 */
	@JsonProperty("ds_progetto")
	public String getDs_progetto() {
		return ds_progetto;
	}

	/**
	 *
	 * @param ds_progetto
	 * The ds_progetto
	 */
	@JsonProperty("ds_progetto")
	public void setDs_progetto(String ds_progetto) {
		this.ds_progetto = ds_progetto;
	}

	/**
	 *
	 * @return
	 * The cd_responsabile_terzo
	 */
	@JsonProperty("cd_responsabile_terzo")
	public Integer getCd_responsabile_terzo() {
		return cd_responsabile_terzo;
	}

	/**
	 *
	 * @param cd_responsabile_terzo
	 * The cd_responsabile_terzo
	 */
	@JsonProperty("cd_responsabile_terzo")
	public void setCd_responsabile_terzo(Integer cd_responsabile_terzo) {
		this.cd_responsabile_terzo = cd_responsabile_terzo;
	}


	/**
	 *
	 * @return
	 * The dt_inizio
	 */
	@JsonProperty("dt_inizio")
	public Serializable getDt_inizio() {
		return dt_inizio;
	}

	/**
	 *
	 * @param dt_inizio
	 * The dt_inizio
	 */
	@JsonProperty("dt_inizio")
	public void setDt_inizio(Serializable dt_inizio) {
		this.dt_inizio = dt_inizio;
	}

	/**
	 *
	 * @return
	 * The dt_fine
	 */
	@JsonProperty("dt_fine")
	public Serializable getDt_fine() {
		return dt_fine;
	}

	/**
	 *
	 * @param dt_fine
	 * The dt_fine
	 */
	@JsonProperty("dt_fine")
	public void setDt_fine(Serializable dt_fine) {
		this.dt_fine = dt_fine;
	}

	/**
	 *
	 * @return
	 * The stato
	 */
	@JsonProperty("stato")
	public String getStato() {
		return stato;
	}

	/**
	 *
	 * @param stato
	 * The stato
	 */
	@JsonProperty("stato")
	public void setStato(String stato) {
		this.stato = stato;
	}

	@JsonAnyGetter
	public Map<String, Serializable> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Serializable value) {
		this.additionalProperties.put(name, value);
	}

	/**
	 *
	 * @return
	 * The codice_fiscale_responsabile
	 */
	@JsonProperty("codice_fiscale_responsabile")
	public String getCodice_fiscale_responsabile() {
		return codice_fiscale_responsabile;
	}

	/**
	 *
	 * @param codice_fiscale_responsabile
	 * The codice_fiscale_responsabile
	 */
	@JsonProperty("codice_fiscale_responsabile")
	public void setCodice_fiscale_responsabile(String codice_fiscale_responsabile) {
		this.codice_fiscale_responsabile = codice_fiscale_responsabile;
	}

	/**
	 *
	 * @return
	 * The esercizio
	 */
	@JsonProperty("esercizio")
	public Integer getEsercizio() {
		return esercizio;
	}

	/**
	 *
	 * @param esercizio
	 * The esercizio
	 */
	@JsonProperty("esercizio")
	public void setEsercizio(Integer esercizio) {
		this.esercizio = esercizio;
	}

	@JsonProperty("cd_unita_organizzativa")
	public String getCd_unita_organizzativa() {
		return cd_unita_organizzativa;
	}

	@JsonProperty("cd_unita_organizzativa")
	public void setCd_unita_organizzativa(String cd_unita_organizzativa) {
		this.cd_unita_organizzativa = cd_unita_organizzativa;
	}
}
