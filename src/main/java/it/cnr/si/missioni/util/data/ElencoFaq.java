package it.cnr.si.missioni.util.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"tipo",
	"anomalia",
	"risposta"
	})
public class ElencoFaq implements Serializable{

	@JsonProperty("tipo")
	private String tipo;
	@JsonProperty("anomalia")
	private String anomalia;
	@JsonProperty("risposta")
	private String risposta;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getAnomalia() {
		return anomalia;
	}
	public void setAnomalia(String anomalia) {
		this.anomalia = anomalia;
	}
	public String getRisposta() {
		return risposta;
	}
	public void setRisposta(String risposta) {
		this.risposta = risposta;
	}
	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}
	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

}