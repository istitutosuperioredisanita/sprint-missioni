
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
    "pg_banca"
})
public class Banca implements Serializable{

    @JsonProperty("pg_banca")
    private Integer pgBanca;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The pgBanca
     */
    @JsonProperty("pg_banca")
    public Integer getPgBanca() {
        return pgBanca;
    }

    /**
     * 
     * @param pgBanca
     *     The pg_banca
     */
    @JsonProperty("pg_banca")
    public void setPgBanca(Integer pgBanca) {
        this.pgBanca = pgBanca;
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
