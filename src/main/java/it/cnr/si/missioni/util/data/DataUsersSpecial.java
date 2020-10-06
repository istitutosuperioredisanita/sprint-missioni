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
	"usersSpecial"
})
public class DataUsersSpecial implements Serializable{

	@JsonProperty("title")
	private String title;
	@JsonProperty("usersSpecial")
	private List<UsersSpecial> usersSpecial = new ArrayList<UsersSpecial>();

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

}