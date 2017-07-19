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
	"codice_uo",
	"uid_direttore",
	"firma_spesa",
	"ordine_da_validare"
})
public class Uo implements Serializable{

	@JsonProperty("codice_uo")
	private String codiceUo;
	@JsonProperty("uid_direttore")
	private String uidDirettore;
	@JsonProperty("firma_spesa")
	private String firmaSpesa;
	@JsonProperty("ordine_da_validare")
	private String ordineDaValidare;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The codiceUo
	 */
	@JsonProperty("codice_uo")
	public String getCodiceUo() {
		return codiceUo;
	}

	/**
	 *
	 * @param codiceUo
	 * The codice_uo
	 */
	@JsonProperty("codice_uo")
	public void setCodiceUo(String codiceUo) {
		this.codiceUo = codiceUo;
	}

	/**
	 *
	 * @return
	 * The uidDirettore
	 */
	@JsonProperty("uid_direttore")
	public String getUidDirettore() {
		return uidDirettore;
	}

	/**
	 *
	 * @param uidDirettore
	 * The uid_direttore
	 */
	@JsonProperty("uid_direttore")
	public void setUidDirettore(String uidDirettore) {
		this.uidDirettore = uidDirettore;
	}

	/**
	 *
	 * @return
	 * The firmaSpesa
	 */
	@JsonProperty("firma_spesa")
	public String getFirmaSpesa() {
		return firmaSpesa;
	}

	/**
	 *
	 * @param firmaSpesa
	 * The firma_spesa
	 */
	@JsonProperty("firma_spesa")
	public void setFirmaSpesa(String firmaSpesa) {
		this.firmaSpesa = firmaSpesa;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

	@JsonProperty("ordine_da_validare")
	public String getOrdineDaValidare() {
		return ordineDaValidare;
	}

	@JsonProperty("ordine_da_validare")
	public void setOrdineDaValidare(String ordineDaValidare) {
		this.ordineDaValidare = ordineDaValidare;
	}

}