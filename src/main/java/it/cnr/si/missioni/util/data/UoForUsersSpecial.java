package it.cnr.si.missioni.util.data;

import javax.annotation.Generated;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"codice_uo"
})
public class UoForUsersSpecial {

	@JsonProperty("codice_uo")
	private String codice_uo;

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

}