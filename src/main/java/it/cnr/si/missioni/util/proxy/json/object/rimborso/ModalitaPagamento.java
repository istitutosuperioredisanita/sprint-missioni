
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
    "cd_modalita_pag"
})
public class ModalitaPagamento implements Serializable{

    @JsonProperty("cd_modalita_pag")
    private String cdModalitaPag;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The cdModalitaPag
     */
    @JsonProperty("cd_modalita_pag")
    public String getCdModalitaPag() {
        return cdModalitaPag;
    }

    /**
     * 
     * @param cdModalitaPag
     *     The cd_modalita_pag
     */
    @JsonProperty("cd_modalita_pag")
    public void setCdModalitaPag(String cdModalitaPag) {
        this.cdModalitaPag = cdModalitaPag;
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
