
package it.cnr.si.missioni.util.proxy.json.object.rimborso;

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
    "cd_divisa"
})
public class DivisaTappa {

    @JsonProperty("cd_divisa")
    private String cdDivisa;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cdDivisa
     */
    @JsonProperty("cd_divisa")
    public String getCdDivisa() {
        return cdDivisa;
    }

    /**
     * 
     * @param cdDivisa
     *     The cd_divisa
     */
    @JsonProperty("cd_divisa")
    public void setCdDivisa(String cdDivisa) {
        this.cdDivisa = cdDivisa;
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
