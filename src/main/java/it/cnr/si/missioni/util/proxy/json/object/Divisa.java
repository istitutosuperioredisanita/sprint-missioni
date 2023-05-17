
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
        "cd_divisa",
        "ds_divisa",
        "precisione",
        "fl_calcola_con_diviso"
})
public class Divisa extends RestServiceBean implements Serializable {

    @JsonProperty("cd_divisa")
    private String cdDivisa;
    @JsonProperty("ds_divisa")
    private String dsDivisa;
    @JsonProperty("precisione")
    private Integer precisione;
    @JsonProperty("fl_calcola_con_diviso")
    private Boolean flCalcolaConDiviso;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The cdDivisa
     */
    @JsonProperty("cd_divisa")
    public String getCdDivisa() {
        return cdDivisa;
    }

    /**
     * @param cdDivisa The cd_divisa
     */
    @JsonProperty("cd_divisa")
    public void setCdDivisa(String cdDivisa) {
        this.cdDivisa = cdDivisa;
    }

    /**
     * @return The dsDivisa
     */
    @JsonProperty("ds_divisa")
    public String getDsDivisa() {
        return dsDivisa;
    }

    /**
     * @param dsDivisa The ds_divisa
     */
    @JsonProperty("ds_divisa")
    public void setDsDivisa(String dsDivisa) {
        this.dsDivisa = dsDivisa;
    }

    /**
     * @return The precisione
     */
    @JsonProperty("precisione")
    public Integer getPrecisione() {
        return precisione;
    }

    /**
     * @param precisione The precisione
     */
    @JsonProperty("precisione")
    public void setPrecisione(Integer precisione) {
        this.precisione = precisione;
    }

    /**
     * @return The flCalcolaConDiviso
     */
    @JsonProperty("fl_calcola_con_diviso")
    public Boolean getFlCalcolaConDiviso() {
        return flCalcolaConDiviso;
    }

    /**
     * @param flCalcolaConDiviso The fl_calcola_con_diviso
     */
    @JsonProperty("fl_calcola_con_diviso")
    public void setFlCalcolaConDiviso(Boolean flCalcolaConDiviso) {
        this.flCalcolaConDiviso = flCalcolaConDiviso;
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
