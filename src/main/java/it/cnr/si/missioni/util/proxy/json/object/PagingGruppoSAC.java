package it.cnr.si.missioni.util.proxy.json.object;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
