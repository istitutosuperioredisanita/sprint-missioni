package it.cnr.si.missioni.util.data;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"uid",
	"uoForUsersSpecial",
	"all"
})
public class UsersSpecial implements Serializable{

	@JsonProperty("uid")
	private String uid;
	@JsonProperty("uoForUsersSpecial")
	private List<UoForUsersSpecial> uoForUsersSpecial;
	@JsonProperty("all")
	private String all;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The uid
	 */
	@JsonProperty("uid")
	public String getUid() {
		return uid;
	}

	/**
	 *
	 * @param uid
	 * The uid
	 */
	@JsonProperty("uid")
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 *
	 * @return
	 * The uoForUsersSpecial
	 */
	@JsonProperty("uoForUsersSpecial")
	public List<UoForUsersSpecial> getUoForUsersSpecials() {
		return uoForUsersSpecial;
	}

	/**
	 *
	 * @param uoForUsersSpecial
	 * The uoForUsersSpecial
	 */
	@JsonProperty("usersSpecial")
	public void setUoForUsersSpecial(List<UoForUsersSpecial> uoForUsersSpecial) {
		this.uoForUsersSpecial = uoForUsersSpecial;
	}

	/**
	 *
	 * @return
	 * The all
	 */
	@JsonProperty("all")
	public String getAll() {
		return all;
	}

	/**
	 *
	 * @param all
	 * The all
	 */
	@JsonProperty("all")
	public void setAll(String all) {
		this.all = all;
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