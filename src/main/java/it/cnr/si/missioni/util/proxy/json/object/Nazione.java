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
        "pg_nazione",
        "ds_nazione",
        "cd_area_estera",
        "ti_nazione"
})
public class Nazione extends RestServiceBean implements Serializable {

    @JsonProperty("pg_nazione")
    private Long pg_nazione;
    @JsonProperty("ds_nazione")
    private String ds_nazione;
    @JsonProperty("cd_area_estera")
    private String cd_area_estera;
    @JsonProperty("ti_nazione")
    private String ti_nazione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The pgNazione
     */
    @JsonProperty("pg_nazione")
    public Long getPg_nazione() {
        return pg_nazione;
    }

    /**
     * @param pgNazione The pg_nazione
     */
    @JsonProperty("pg_nazione")
    public void setPg_nazione(Long pg_nazione) {
        this.pg_nazione = pg_nazione;
    }

    /**
     * @return The dsNazione
     */
    @JsonProperty("ds_nazione")
    public String getDs_nazione() {
        return ds_nazione;
    }

    /**
     * @param dsNazione The ds_nazione
     */
    @JsonProperty("ds_nazione")
    public void setDs_nazione(String ds_nazione) {
        this.ds_nazione = ds_nazione;
    }

    /**
     * @return The cdAreaEstera
     */
    @JsonProperty("cd_area_estera")
    public String getCd_area_estera() {
        return cd_area_estera;
    }

    /**
     * @param cdAreaEstera The cd_area_estera
     */
    @JsonProperty("cd_area_estera")
    public void setCd_area_estera(String cd_area_estera) {
        this.cd_area_estera = cd_area_estera;
    }

    /**
     * @return The tiNazione
     */
    @JsonProperty("ti_nazione")
    public String getTi_nazione() {
        return ti_nazione;
    }

    /**
     * @param tiNazione The ti_nazione
     */
    @JsonProperty("ti_nazione")
    public void setTi_nazione(String ti_nazione) {
        this.ti_nazione = ti_nazione;
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