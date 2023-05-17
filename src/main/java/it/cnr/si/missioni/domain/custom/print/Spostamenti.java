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

package it.cnr.si.missioni.domain.custom.print;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "percorsoDa",
        "percorsoA"
})
public class Spostamenti {

    @JsonProperty("percorsoDa")
    private String percorsoDa;
    @JsonProperty("percorsoA")
    private String percorsoA;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The percorsoDa
     */
    @JsonProperty("percorsoDa")
    public String getPercorsoDa() {
        return percorsoDa;
    }

    /**
     * @param percorsoDa The percorsoDa
     */
    @JsonProperty("percorsoDa")
    public void setPercorsoDa(String percorsoDa) {
        this.percorsoDa = percorsoDa;
    }

    /**
     * @return The percorsoA
     */
    @JsonProperty("percorsoA")
    public String getPercorsoA() {
        return percorsoA;
    }

    /**
     * @param percorsoA The percorsoA
     */
    @JsonProperty("percorsoA")
    public void setPercorsoA(String percorsoA) {
        this.percorsoA = percorsoA;
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