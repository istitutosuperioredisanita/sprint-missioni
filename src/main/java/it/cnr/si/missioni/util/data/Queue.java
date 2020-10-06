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
	"tipo",
	"id"
})
public class Queue implements Serializable{

	@JsonProperty("tipo")
	private String tipo;
	@JsonProperty("id")
	private String id;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The codiceUo
	 */
	@JsonProperty("tipo")
	public String getTipo() {
		return tipo;
	}

	/**
	 *
	 * @param codiceUo
	 * The codice_uo
	 */
	@JsonProperty("tipo")
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	/**
	 *
	 * @return
	 * The uidDirettore
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}

	/**
	 *
	 * @param uidDirettore
	 * The uid_direttore
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
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