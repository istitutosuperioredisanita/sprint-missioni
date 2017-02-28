
package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
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
    "cd_modalita_pag",
    "ti_pagamento",
    "ds_modalita_pag"
})
public class ModalitaPagamento  extends RestServiceBean  implements Serializable {

    @JsonProperty("cd_modalita_pag")
    private String cdModalitaPag;
    @JsonProperty("ti_pagamento")
    private String tiPagamento;
    @JsonProperty("ds_modalita_pag")
    private String dsModalitaPag;
    @JsonIgnore
    private Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

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

    /**
     * 
     * @return
     *     The tiPagamento
     */
    @JsonProperty("ti_pagamento")
    public String getTiPagamento() {
        return tiPagamento;
    }

    /**
     * 
     * @param tiPagamento
     *     The ti_pagamento
     */
    @JsonProperty("ti_pagamento")
    public void setTiPagamento(String tiPagamento) {
        this.tiPagamento = tiPagamento;
    }

    /**
     * 
     * @return
     *     The dsModalitaPag
     */
    @JsonProperty("ds_modalita_pag")
    public String getDsModalitaPag() {
        return dsModalitaPag;
    }

    /**
     * 
     * @param dsModalitaPag
     *     The ds_modalita_pag
     */
    @JsonProperty("ds_modalita_pag")
    public void setDsModalitaPag(String dsModalitaPag) {
        this.dsModalitaPag = dsModalitaPag;
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
