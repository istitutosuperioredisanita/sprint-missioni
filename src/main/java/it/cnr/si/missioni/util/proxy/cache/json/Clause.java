/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.util.proxy.cache.json;

import com.fasterxml.jackson.annotation.*;
import it.cnr.si.missioni.util.DateUtils;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "condition",
        "fieldName",
        "operator",
        "fieldValue",
        "specialValue"
})
public class Clause implements Serializable {

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
     * @return The condition
     */
    @JsonProperty("condition")
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition The condition
     */
    @JsonProperty("condition")
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /**
     * @return The fieldName
     */
    @JsonProperty("fieldName")
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName The fieldName
     */
    @JsonProperty("fieldName")
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return The operator
     */
    @JsonProperty("operator")
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator The operator
     */
    @JsonProperty("operator")
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return The fieldValue
     */
    @JsonProperty("fieldValue")
    public Object getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue The fieldValue
     */
    @JsonProperty("fieldValue")
    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * @return The specialValue
     */
    @JsonProperty("specialValue")
    public String getSpecialValue() {
        return specialValue;
    }

    /**
     * @param specialValue The specialValue
     */
    @JsonProperty("specialValue")
    public void setSpecialValue(String specialValue) {
        this.specialValue = specialValue;
    }

    public Boolean containsSpecialValue() {
        if (getValueFromSpecialValue() != null) {
            return true;
        }
        return false;
    }

    public Object getValueFromSpecialValue() {
        if (getSpecialValue() != null) {
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