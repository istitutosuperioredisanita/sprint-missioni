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
        "type",
        "condition",
        "operator",
        "fieldName",
        "fromValue",
        "toSpecialValue",
        "urlInCache",
        "fieldGetForSpecialValue"
})
public class ClauseToIterate implements Serializable {

    public static final String ANNO_CORRENTE = "ANNO_CORRENTE";

    public static final Map<String, Object> SPECIAL_VALUES;

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
     * @return The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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
     * @return The fieldValue
     */
    @JsonProperty("fromValue")
    public String getFromValue() {
        return fromValue;
    }

    /**
     * @param fieldValue The fieldValue
     */
    @JsonProperty("fromValue")
    public void setFromValue(String fromValue) {
        this.fromValue = fromValue;
    }

    /**
     * @return The toSpecialValue
     */
    @JsonProperty("toSpecialValue")
    public String getToSpecialValue() {
        return toSpecialValue;
    }

    /**
     * @param toSpecialValue The toSpecialValue
     */
    @JsonProperty("toSpecialValue")
    public void setToSpecialValue(String toSpecialValue) {
        this.toSpecialValue = toSpecialValue;
    }

    /**
     * @return The urlInCache
     */
    @JsonProperty("urlInCache")
    public String getUrlInCache() {
        return urlInCache;
    }

    /**
     * @param urlInCache The urlInCache
     */
    @JsonProperty("urlInCache")
    public void setUrlInCache(String urlInCache) {
        this.urlInCache = urlInCache;
    }

    /**
     * @return The fieldGetForSpecialValue
     */
    @JsonProperty("fieldGetForSpecialValue")
    public String getFieldGetForSpecialValue() {
        return fieldGetForSpecialValue;
    }

    /**
     * @param fieldGetForSpecialValue The fieldGetForSpecialValue
     */
    @JsonProperty("fieldGetForSpecialValue")
    public void setFieldGetForSpecialValue(String fieldGetForSpecialValue) {
        this.fieldGetForSpecialValue = fieldGetForSpecialValue;
    }

    public Boolean containsToSpecialValue() {
        if (getValueFromToSpecialValue() != null) {
            return true;
        }
        return false;
    }

    public Object getValueFromToSpecialValue() {
        if (getToSpecialValue() != null) {
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