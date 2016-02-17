package it.cnr.si.missioni.domain.custom.print;

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
	"percorsoDa",
	"percorsoA"
})
public class Spostamenti {

	@JsonProperty("percorsoDa")
	private String percorsoDa;
	@JsonProperty("percorsoA")
	private String percorsoA;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The percorsoDa
	 */
	@JsonProperty("percorsoDa")
	public String getPercorsoDa() {
		return percorsoDa;
	}

	/**
	 *
	 * @param percorsoDa
	 * The percorsoDa
	 */
	@JsonProperty("percorsoDa")
	public void setPercorsoDa(String percorsoDa) {
		this.percorsoDa = percorsoDa;
	}

	/**
	 *
	 * @return
	 * The percorsoA
	 */
	@JsonProperty("percorsoA")
	public String getPercorsoA() {
		return percorsoA;
	}

	/**
	 *
	 * @param percorsoA
	 * The percorsoA
	 */
	@JsonProperty("percorsoA")
	public void setPercorsoA(String percorsoA) {
		this.percorsoA = percorsoA;
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