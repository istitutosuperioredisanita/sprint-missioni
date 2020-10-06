package it.cnr.si.missioni.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	"title",
	"uo"
})
public class DatiUo implements Serializable{

	@JsonProperty("title")
	private String title;
	@JsonProperty("uo")
	private List<Uo> uo = new ArrayList<Uo>();
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
	@JsonProperty("uo")
	public List<Uo> getUo() {
		return uo;
	}

	/**
	 *
	 * @param uo
	 * The uo
	 */
	@JsonProperty("uo")
	public void setUo(List<Uo> uo) {
		this.uo = uo;
	}

}