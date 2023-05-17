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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.annotation.Generated;
import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "codice_uo",
        "ordine_da_validare",
        "rendi_definitivo"
})
public class UoForUsersSpecial implements Serializable {

    @JsonProperty("codice_uo")
    private String codice_uo;

    @JsonProperty("ordine_da_validare")
    private String ordine_da_validare;

    @JsonProperty("rendi_definitivo")
    private String rendi_definitivo;

    /**
     * @return The codice_uo
     */
    @JsonProperty("codice_uo")
    public String getCodice_uo() {
        return codice_uo;
    }

    /**
     * @param codice_uo The codice_uo
     */
    @JsonProperty("codice_uo")
    public void setCodice_uo(String codice_uo) {
        this.codice_uo = codice_uo;
    }

    @JsonProperty("ordine_da_validare")
    public String getOrdine_da_validare() {
        return ordine_da_validare;
    }

    @JsonProperty("ordine_da_validare")
    public void setOrdine_da_validare(String ordine_da_validare) {
        this.ordine_da_validare = ordine_da_validare;
    }

    @JsonProperty("rendi_definitivo")
    public String getRendi_definitivo() {
        return rendi_definitivo;
    }

    @JsonProperty("rendi_definitivo")
    public void setRendi_definitivo(String rendi_definitivo) {
        this.rendi_definitivo = rendi_definitivo;
    }

}