package it.cnr.si.missioni.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	"title",
	"elencoFaq"
})
public class Faq implements Serializable{

	@JsonProperty("title")
	private String title;
	@JsonProperty("elencoFaq")
	private List<ElencoFaq> elencoFaq = new ArrayList<ElencoFaq>();
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
	 * The uo
	 */
	@JsonProperty("elencoFaq")
	public List<ElencoFaq> getElencoFaq() {
		return elencoFaq;
	}

	/**
	 *
	 * @param uo
	 * The uo
	 */
	@JsonProperty("elencoFaq")
	public void setElencoFaq(List<ElencoFaq> elencoFaq) {
		this.elencoFaq = elencoFaq;
	}

}