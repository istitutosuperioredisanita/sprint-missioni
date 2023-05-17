
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

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "cd_modalita_pag",
        "ti_pagamento",
        "ds_modalita_pag"
})
public class ModalitaPagamento extends RestServiceBean implements Serializable {

    @JsonProperty("cd_modalita_pag")
    private String cdModalitaPag;
    @JsonProperty("ti_pagamento")
    private String tiPagamento;
    @JsonProperty("ds_modalita_pag")
    private String dsModalitaPag;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The cdModalitaPag
     */
    @JsonProperty("cd_modalita_pag")
    public String getCdModalitaPag() {
        return cdModalitaPag;
    }

    /**
     * @param cdModalitaPag The cd_modalita_pag
     */
    @JsonProperty("cd_modalita_pag")
    public void setCdModalitaPag(String cdModalitaPag) {
        this.cdModalitaPag = cdModalitaPag;
    }

    /**
     * @return The tiPagamento
     */
    @JsonProperty("ti_pagamento")
    public String getTiPagamento() {
        return tiPagamento;
    }

    /**
     * @param tiPagamento The ti_pagamento
     */
    @JsonProperty("ti_pagamento")
    public void setTiPagamento(String tiPagamento) {
        this.tiPagamento = tiPagamento;
    }

    /**
     * @return The dsModalitaPag
     */
    @JsonProperty("ds_modalita_pag")
    public String getDsModalitaPag() {
        return dsModalitaPag;
    }

    /**
     * @param dsModalitaPag The ds_modalita_pag
     */
    @JsonProperty("ds_modalita_pag")
    public void setDsModalitaPag(String dsModalitaPag) {
        this.dsModalitaPag = dsModalitaPag;
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
