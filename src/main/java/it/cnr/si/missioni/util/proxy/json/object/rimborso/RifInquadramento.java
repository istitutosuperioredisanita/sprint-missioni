
package it.cnr.si.missioni.util.proxy.json.object.rimborso;

import java.io.Serializable;
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
    "pg_rif_inquadramento"
})
public class RifInquadramento implements Serializable{

    @JsonProperty("pg_rif_inquadramento")
    private Integer pgRifInquadramento;
    @JsonIgnore
    private Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

    /**
     * 
     * @return
     *     The pgRifInquadramento
     */
    @JsonProperty("pg_rif_inquadramento")
    public Integer getPgRifInquadramento() {
        return pgRifInquadramento;
    }

    /**
     * 
     * @param pgRifInquadramento
     *     The pg_rif_inquadramento
     */
    @JsonProperty("pg_rif_inquadramento")
    public void setPgRifInquadramento(Integer pgRifInquadramento) {
        this.pgRifInquadramento = pgRifInquadramento;
    }

    @JsonAnyGetter
    public Map<String, Serializable> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Serializable value) {
        this.additionalProperties.put(name, value);
    }

}
