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

package it.cnr.si.missioni.util.data;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "codice_uo",
        "uid_direttore",
        "firma_spesa",
        "ordine_da_validare"
})
public class Uo implements Serializable {

    @JsonProperty("codice_uo")
    private String codiceUo;
    @JsonProperty("uid_direttore")
    private String uidDirettore;
    @JsonProperty("firma_spesa")
    private String firmaSpesa;
    @JsonProperty("ordine_da_validare")
    private String ordineDaValidare;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The codiceUo
     */
    @JsonProperty("codice_uo")
    public String getCodiceUo() {
        return codiceUo;
    }

    /**
     * @param codiceUo The codice_uo
     */
    @JsonProperty("codice_uo")
    public void setCodiceUo(String codiceUo) {
        this.codiceUo = codiceUo;
    }

    /**
     * @return The uidDirettore
     */
    @JsonProperty("uid_direttore")
    public String getUidDirettore() {
        return uidDirettore;
    }

    /**
     * @param uidDirettore The uid_direttore
     */
    @JsonProperty("uid_direttore")
    public void setUidDirettore(String uidDirettore) {
        this.uidDirettore = uidDirettore;
    }

    /**
     * @return The firmaSpesa
     */
    @JsonProperty("firma_spesa")
    public String getFirmaSpesa() {
        return firmaSpesa;
    }

    /**
     * @param firmaSpesa The firma_spesa
     */
    @JsonProperty("firma_spesa")
    public void setFirmaSpesa(String firmaSpesa) {
        this.firmaSpesa = firmaSpesa;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @JsonProperty("ordine_da_validare")
    public String getOrdineDaValidare() {
        return ordineDaValidare;
    }

    @JsonProperty("ordine_da_validare")
    public void setOrdineDaValidare(String ordineDaValidare) {
        this.ordineDaValidare = ordineDaValidare;
    }

}