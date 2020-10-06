package it.cnr.si.missioni.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"title",
	"utentePresidenteSpeciale"
})
public class UtentiPresidenteSpeciali implements Serializable{

	@JsonProperty("title")
	private String title;
	@JsonProperty("utentePresidenteSpeciale")
	private List<UtentePresidenteSpeciale> utentePresidenteSpeciale = new ArrayList<UtentePresidenteSpeciale>();
	/**
	 *
	 * @return
	 * The title
	 */
	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	/**
	 *
	 * @param title
	 * The title
	 */
	@JsonProperty("title")
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 *
	 * @return
	 * The utentePresidenteSpeciale
	 */
	@JsonProperty("utentePresidenteSpeciale")
	public List<UtentePresidenteSpeciale> getUtentePresidenteSpeciale() {
		return utentePresidenteSpeciale;
	}

	/**
	 *
	 * @param uo
	 * The utentePresidenteSpeciale
	 */
	@JsonProperty("utentePresidenteSpeciale")
	public void setUtentePresidenteSpeciale(List<UtentePresidenteSpeciale> utentePresidenteSpeciale) {
		this.utentePresidenteSpeciale = utentePresidenteSpeciale;
	}

}