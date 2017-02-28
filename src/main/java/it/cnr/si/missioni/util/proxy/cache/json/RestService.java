package it.cnr.si.missioni.util.proxy.cache.json;

import it.cnr.si.missioni.util.proxy.json.JSONOrderBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	"app",
	"url",
	"skipLoadStartup",
	"classeJson",
	"classe",
	"clause",
	"clauseVariable",
	"clauseFixed"
})
public class RestService {

	@JsonProperty("app")
	private String app;
	@JsonProperty("url")
	private String url;
	@JsonProperty("skipLoadStartup")
	private String skipLoadStartup;
	@JsonProperty("classeJson")
	private String classeJson;
	@JsonProperty("classe")
	private String classe;
	@JsonProperty("clauseVariable")
	private List<Clause> clauseVariable = new ArrayList<Clause>();
	@JsonProperty("clauseFixed")
	private List<Clause> clauseFixed = new ArrayList<Clause>();
	@JsonProperty("clauseToIterate")
	private List<ClauseToIterate> clauseToIterate = new ArrayList<ClauseToIterate>();
	@JsonProperty("order")
	private List<JSONOrderBy> order = new ArrayList<JSONOrderBy>();
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The app
	 */
	@JsonProperty("app")
	public String getApp() {
		return app;
	}

	/**
	 *
	 * @param app
	 * The app
	 */
	@JsonProperty("app")
	public void setApp(String app) {
		this.app = app;
	}

	/**
	 *
	 * @return
	 * The url
	 */
	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	/**
	 *
	 * @param url
	 * The url
	 */
	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 *
	 * @return
	 * The skipLoadStartup
	 */
	@JsonProperty("skipLoadStartup")
	public String getSkipLoadStartup() {
		return skipLoadStartup;
	}

	/**
	 *
	 * @param skipLoadStartup
	 * The skipLoadStartup
	 */
	@JsonProperty("skipLoadStartup")
	public void setSkipLoadStartup(String skipLoadStartup) {
		this.skipLoadStartup = skipLoadStartup;
	}

	/**
	 *
	 * @return
	 * The classeJson
	 */
	@JsonProperty("classeJson")
	public String getClasseJson() {
		return classeJson;
	}

	/**
	 *
	 * @param classeJson
	 * The classeJson
	 */
	@JsonProperty("classeJson")
	public void setClasseJson(String classeJson) {
		this.classeJson = classeJson;
	}

	/**
	 *
	 * @return
	 * The classe
	 */
	@JsonProperty("classe")
	public String getClasse() {
		return classe;
	}

	/**
	 *
	 * @param classe
	 * The classe
	 */
	@JsonProperty("classe")
	public void setClasse(String classe) {
		this.classe = classe;
	}

	/**
	 *
	 * @return
	 * The clauseVariable
	 */
	@JsonProperty("clauseVariable")
	public List<Clause> getClauseVariable() {
		return clauseVariable;
	}

	/**
	 *
	 * @param clauseVariable
	 * The clauseVariable
	 */
	@JsonProperty("clauseVariable")
	public void setClauseVariable(List<Clause> clauseVariable) {
		this.clauseVariable = clauseVariable;
	}

	/**
	 *
	 * @return
	 * The clauseFixed
	 */
	@JsonProperty("clauseFixed")
	public List<Clause> getClauseFixed() {
		return clauseFixed;
	}

	/**
	 *
	 * @param clauseFixed
	 * The clauseFixed
	 */
	@JsonProperty("clauseFixed")
	public void setClauseFixed(List<Clause> clauseFixed) {
		this.clauseFixed = clauseFixed;
	}

	/**
	 *
	 * @return
	 * The clauseToIterate
	 */
	@JsonProperty("clauseToIterate")
	public List<ClauseToIterate> getClauseToIterate() {
		return clauseToIterate;
	}

	/**
	 *
	 * @param clauseToIterate
	 * The clauseToIterate
	 */
	@JsonProperty("clauseToIterate")
	public void setClauseToIterate(List<ClauseToIterate> clauseToIterate) {
		this.clauseToIterate = clauseToIterate;
	}

	/**
	 *
	 * @return
	 * The order
	 */
	@JsonProperty("order")
	public List<JSONOrderBy> getOrder() {
		return order;
	}

	/**
	 *
	 * @param order
	 * The order
	 */
	@JsonProperty("order")
	public void setOrder(List<JSONOrderBy> order) {
		this.order = order;
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