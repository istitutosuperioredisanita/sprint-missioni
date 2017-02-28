package it.cnr.si.missioni.util.proxy.cache.json;

import it.cnr.si.missioni.util.DateUtils;

import java.io.Serializable;
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
	"condition",
	"fieldName",
	"operator",
	"fieldValue",
	"specialValue"
})
public class Clause implements Serializable{

	public static final String ANNO_CORRENTE = "ANNO_CORRENTE";

	public static final Map<String, Object> SPECIAL_VALUES;
    static {
        Map<String, Object> aMap = new HashMap<String, Object>();
        aMap.put(ANNO_CORRENTE, DateUtils.getCurrentYear());
        SPECIAL_VALUES = Collections.unmodifiableMap(aMap);
    }

	@JsonProperty("condition")
	private String condition;
	@JsonProperty("fieldName")
	private String fieldName;
	@JsonProperty("operator")
	private String operator;
	@JsonProperty("fieldValue")
	private Object fieldValue;
	@JsonProperty("specialValue")
	private String specialValue;
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
	 * The fieldValue
	 */
	@JsonProperty("fieldValue")
	public Object getFieldValue() {
		return fieldValue;
	}

	/**
	 *
	 * @param fieldValue
	 * The fieldValue
	 */
	@JsonProperty("fieldValue")
	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	/**
	 *
	 * @param specialValue
	 * The specialValue
	 */
	@JsonProperty("specialValue")
	public void setSpecialValue(String specialValue) {
		this.specialValue = specialValue;
	}

	/**
	 *
	 * @return
	 * The specialValue
	 */
	@JsonProperty("specialValue")
	public String getSpecialValue() {
		return specialValue;
	}

	public Boolean containsSpecialValue(){
		if (getValueFromSpecialValue() != null){
			return true;
		}
		return false;
	}
	
	public Object getValueFromSpecialValue(){
		if (getSpecialValue() != null){
			Object value = SPECIAL_VALUES.get(getSpecialValue());
			return value;
		}
		return null;
	}
	
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Serializable value) {
		this.additionalProperties.put(name, value);
	}

}