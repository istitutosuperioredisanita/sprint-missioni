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

package it.cnr.si.missioni.util.proxy.json.object;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "maxItems",
        "skipCount",
        "totalItems",
        "totalItemsRangeEnd",
        "confidence"
})

public class PagingGruppoSAC {

    @JsonProperty("maxItems")
    private Integer maxItems;
    @JsonProperty("skipCount")
    private Integer skipCount;
    @JsonProperty("totalItems")
    private Integer totalItems;
    @JsonProperty("totalItemsRangeEnd")
    private Object totalItemsRangeEnd;
    @JsonProperty("confidence")
    private String confidence;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("maxItems")
    public Integer getMaxItems() {
        return maxItems;
    }

    @JsonProperty("maxItems")
    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    @JsonProperty("skipCount")
    public Integer getSkipCount() {
        return skipCount;
    }

    @JsonProperty("skipCount")
    public void setSkipCount(Integer skipCount) {
        this.skipCount = skipCount;
    }

    @JsonProperty("totalItems")
    public Integer getTotalItems() {
        return totalItems;
    }

    @JsonProperty("totalItems")
    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    @JsonProperty("totalItemsRangeEnd")
    public Object getTotalItemsRangeEnd() {
        return totalItemsRangeEnd;
    }

    @JsonProperty("totalItemsRangeEnd")
    public void setTotalItemsRangeEnd(Object totalItemsRangeEnd) {
        this.totalItemsRangeEnd = totalItemsRangeEnd;
    }

    @JsonProperty("confidence")
    public String getConfidence() {
        return confidence;
    }

    @JsonProperty("confidence")
    public void setConfidence(String confidence) {
        this.confidence = confidence;
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
