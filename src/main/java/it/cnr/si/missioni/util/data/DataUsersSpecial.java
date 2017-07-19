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
	"usersSpecial"
})
public class DataUsersSpecial implements Serializable{

	@JsonProperty("title")
	private String title;
	@JsonProperty("usersSpecial")
	private List<UsersSpecial> usersSpecial = new ArrayList<UsersSpecial>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
	 * The usersSpecial
	 */
	@JsonProperty("usersSpecial")
	public List<UsersSpecial> getUsersSpecials() {
		return usersSpecial;
	}

	/**
	 *
	 * @param usersSpecial
	 * The usersSpecial
	 */
	@JsonProperty("usersSpecial")
	public void setUsersSpecial(List<UsersSpecial> usersSpecial) {
		this.usersSpecial = usersSpecial;
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