package it.cnr.si.missioni.util.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"codice_utente",
	"nome",
	"cognome"
})
public class UtentePresidenteSpeciale implements Serializable{

	@JsonProperty("codice_utente")
	private String codice_utente;
	@JsonProperty("cognome")
	private String cognome;
	@JsonProperty("nome")
	private String nome;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The codice_utente
	 */
	@JsonProperty("codice_utente")
	public String getCodiceUtente() {
		return codice_utente;
	}

	/**
	 *
	 * @param codiceUo
	 * The codice_utente
	 */
	@JsonProperty("codice_utente")
	public void setCodiceUtente(String codice_utente) {
		this.codice_utente = codice_utente;
	}

	/**
	 *
	 * @return
	 * The cognome
	 */
	@JsonProperty("cognome")
	public String getCognome() {
		return cognome;
	}

	/**
	 *
	 * @param cognome
	 * The cognome
	 */
	@JsonProperty("cognome")
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	/**
	 *
	 * @return
	 * The nome
	 */
	@JsonProperty("nome")
	public String getNome() {
		return nome;
	}

	/**
	 *
	 * @param nome
	 * The nome
	 */
	@JsonProperty("nome")
	public void setNome(String nome) {
		this.nome = nome;
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