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
        "cd_linea_attivita",
        "ds_linea_attivita",
        "cd_centro_responsabilita",
        "pg_progetto",
        "esercizio_inizio",
        "esercizioFine"
})
public class Gae extends RestServiceBean implements Serializable {

    @JsonProperty("cd_linea_attivita")
    private String cd_linea_attivita;
    @JsonProperty("ds_linea_attivita")
    private String ds_linea_attivita;
    @JsonProperty("cd_centro_responsabilita")
    private String cd_centro_responsabilita;
    @JsonProperty("pg_progetto")
    private Long pg_progetto;
    @JsonProperty("esercizio_inizio")
    private Integer esercizio_inizio;
    @JsonProperty("esercizio_fine")
    private Integer esercizio_fine;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The cd_linea_attivita
     */
    @JsonProperty("cd_linea_attivita")
    public String getCd_linea_attivita() {
        return cd_linea_attivita;
    }

    /**
     * @param cd_linea_attivita The cd_linea_attivita
     */
    @JsonProperty("cd_linea_attivita")
    public void setCd_linea_attivita(String cd_linea_attivita) {
        this.cd_linea_attivita = cd_linea_attivita;
    }

    /**
     * @return The ds_linea_attivita
     */
    @JsonProperty("ds_linea_attivita")
    public String getDs_linea_attivita() {
        return ds_linea_attivita;
    }

    /**
     * @param ds_linea_attivita The ds_linea_attivita
     */
    @JsonProperty("ds_linea_attivita")
    public void setDs_linea_attivita(String ds_linea_attivita) {
        this.ds_linea_attivita = ds_linea_attivita;
    }

    /**
     * @return The cdCentroResponsabilita
     */
    @JsonProperty("cd_centro_responsabilita")
    public String getCd_centro_responsabilita() {
        return cd_centro_responsabilita;
    }

    /**
     * @param cdCentroResponsabilita The cd_centro_responsabilita
     */
    @JsonProperty("cd_centro_responsabilita")
    public void setCd_centro_responsabilita(String cd_centro_responsabilita) {
        this.cd_centro_responsabilita = cd_centro_responsabilita;
    }

    /**
     * @return The pgProgetto
     */
    @JsonProperty("pg_progetto")
    public Long getPg_progetto() {
        return pg_progetto;
    }

    /**
     * @param pgProgetto The pg_progetto
     */
    @JsonProperty("pg_progetto")
    public void setPg_progetto(Long pg_progetto) {
        this.pg_progetto = pg_progetto;
    }

    /**
     * @return The esercizio
     */
    @JsonProperty("esercizio_inizio")
    public Integer getEsercizio_inizio() {
        return esercizio_inizio;
    }

    /**
     * @param esercizio The esercizio
     */
    @JsonProperty("esercizio_inizio")
    public void setEsercizio_inizio(Integer esercizio_inizio) {
        this.esercizio_inizio = esercizio_inizio;
    }

    /**
     * @return The esercizio
     */
    @JsonProperty("esercizio_fine")
    public Integer getEsercizio_fine() {
        return esercizio_fine;
    }

    /**
     * @param esercizio The esercizio
     */
    @JsonProperty("esercizio_fine")
    public void setEsercizio_fine(Integer esercizio_fine) {
        this.esercizio_fine = esercizio_fine;
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
