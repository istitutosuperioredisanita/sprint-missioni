package it.cnr.si.missioni.util.data;

import java.io.Serializable;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"codice_uo",
	"ordine_da_validare",
	"rendi_definitivo"
})
public class UoForUsersSpecial implements Serializable{

	@JsonProperty("codice_uo")
	private String codice_uo;

	@JsonProperty("ordine_da_validare")
	private String ordine_da_validare;

	@JsonProperty("rendi_definitivo")
	private String rendi_definitivo;

	/**
	 *
	 * @return
	 * The codice_uo
	 */
	@JsonProperty("codice_uo")
	public String getCodice_uo() {
		return codice_uo;
	}

	/**
	 *
	 * @param codice_uo
	 * The codice_uo
	 */
	@JsonProperty("codice_uo")
	public void setCodice_uo(String codice_uo) {
		this.codice_uo = codice_uo;
	}

	@JsonProperty("ordine_da_validare")
	public String getOrdine_da_validare() {
		return ordine_da_validare;
	}

	@JsonProperty("ordine_da_validare")
	public void setOrdine_da_validare(String ordine_da_validare) {
		this.ordine_da_validare = ordine_da_validare;
	}

	@JsonProperty("rendi_definitivo")
	public String getRendi_definitivo() {
		return rendi_definitivo;
	}

	@JsonProperty("rendi_definitivo")
	public void setRendi_definitivo(String rendi_definitivo) {
		this.rendi_definitivo = rendi_definitivo;
	}

}