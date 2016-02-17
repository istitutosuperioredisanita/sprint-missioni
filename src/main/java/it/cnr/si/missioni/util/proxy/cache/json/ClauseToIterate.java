package it.cnr.si.missioni.util.proxy.cache.json;

import it.cnr.si.missioni.util.DateUtils;

import java.util.Collections;
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
	"type",
	"condition",
	"operator",
	"fieldName",
	"fromValue",
	"toSpecialValue",
	"urlInCache",
	"fieldGetForSpecialValue"
})
public class ClauseToIterate {

	public final static String ANNO_CORRENTE = "ANNO_CORRENTE";

	public final static Map<String, Object> SPECIAL_VALUES;
    static {
        Map<String, Object> aMap = new HashMap<String, Object>();
        aMap.put(ANNO_CORRENTE, DateUtils.getCurrentYear());
        SPECIAL_VALUES = Collections.unmodifiableMap(aMap);
    }

	@JsonProperty("type")
	private String type;
	@JsonProperty("condition")
	private String condition;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("fieldName")
	private String fieldName;
	@JsonProperty("fromValue")
	private String fromValue;
	@JsonProperty("toSpecialValue")
	private String toSpecialValue;

	@JsonProperty("urlInCache")
	private String urlInCache;
	@JsonProperty("fieldGetForSpecialValue")
	private String fieldGetForSpecialValue;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 *
	 * @return
	 * The condition
	 */
	@JsonProperty("condition")
	public String getCondition() {
		return condition;
	}

	/**
	 *
	 * @param condition
	 * The condition
	 */
	@JsonProperty("condition")
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 *
	 * @return
	 * The operator
	 */
	@JsonProperty("operator")
	public String getOperator() {
		return operator;
	}

	/**
	 *
	 * @param operator
	 * The operator
	 */
	@JsonProperty("operator")
	public void setOperator(String operator) {
		this.operator = operator;
	}

	/**
	 *
	 * @return
	 * The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 *
	 * @param type
	 * The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 *
	 * @return
	 * The fieldName
	 */
	@JsonProperty("fieldName")
	public String getFieldName() {
		return fieldName;
	}

	/**
	 *
	 * @param fieldName
	 * The fieldName
	 */
	@JsonProperty("fieldName")
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 *
	 * @return
	 * The fieldValue
	 */
	@JsonProperty("fromValue")
	public String getFromValue() {
		return fromValue;
	}

	/**
	 *
	 * @param fieldValue
	 * The fieldValue
	 */
	@JsonProperty("fromValue")
	public void setFromValue(String fromValue) {
		this.fromValue = fromValue;
	}

	/**
	 *
	 * @param toSpecialValue
	 * The toSpecialValue
	 */
	@JsonProperty("toSpecialValue")
	public void setToSpecialValue(String toSpecialValue) {
		this.toSpecialValue = toSpecialValue;
	}

	/**
	 *
	 * @return
	 * The toSpecialValue
	 */
	@JsonProperty("toSpecialValue")
	public String getToSpecialValue() {
		return toSpecialValue;
	}

	/**
	 *
	 * @param urlInCache
	 * The urlInCache
	 */
	@JsonProperty("urlInCache")
	public void setUrlInCache(String urlInCache) {
		this.urlInCache = urlInCache;
	}

	/**
	 *
	 * @return
	 * The urlInCache
	 */
	@JsonProperty("urlInCache")
	public String getUrlInCache() {
		return urlInCache;
	}

	/**
	 *
	 * @param fieldGetForSpecialValue
	 * The fieldGetForSpecialValue
	 */
	@JsonProperty("fieldGetForSpecialValue")
	public void setFieldGetForSpecialValue(String fieldGetForSpecialValue) {
		this.fieldGetForSpecialValue = fieldGetForSpecialValue;
	}

	/**
	 *
	 * @return
	 * The fieldGetForSpecialValue
	 */
	@JsonProperty("fieldGetForSpecialValue")
	public String getFieldGetForSpecialValue() {
		return fieldGetForSpecialValue;
	}

	public Boolean containsToSpecialValue(){
		if (getValueFromToSpecialValue() != null){
			return true;
		}
		return false;
	}
	
	public Object getValueFromToSpecialValue(){
		if (getToSpecialValue() != null){
			Object value = SPECIAL_VALUES.get(getToSpecialValue());
			return value;
		}
		return null;
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