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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.annotation.Generated;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "cap_residenza",
        "sesso",
        "data_nascita",
        "comune_nascita",
        "fl_cittadino_italiano",
        "indirizzo_residenza",
        "provincia_nascita",
        "nazione_nascita",
        "comune_residenza",
        "provincia_residenza",
        "telefono_comunicazioni"
})

public class TerzoInfo extends RestServiceBean implements Serializable {
    @JsonProperty("cap_residenza")
    private String cap_residenza;
    @JsonProperty("sesso")
    private String sesso;
    @JsonProperty("data_nascita")
    private String data_nascita;
    @JsonProperty("comune_nascita")
    private String comune_nascita;
    @JsonProperty("fl_cittadino_italiano")
    private Boolean fl_cittadino_italiano;
    @JsonProperty("indirizzo_residenza")
    private String indirizzo_residenza;
    @JsonProperty("provincia_nascita")
    private String provincia_nascita;
    @JsonProperty("nazione_nascita")
    private String nazione_nascita;
    @JsonProperty("comune_residenza")
    private String comune_residenza;
    @JsonProperty("provincia_residenza")
    private String provincia_residenza;
    @JsonProperty("telefono_comunicazioni")
    private String telefono_comunicazioni;

    public String getCap_residenza() {
        return cap_residenza;
    }

    public void setCap_residenza(String cap_residenza) {
        this.cap_residenza = cap_residenza;
    }

    public String getSesso() {
        return sesso;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public String getData_nascita() {
        return data_nascita;
    }

    public void setData_nascita(String data_nascita) {
        this.data_nascita = data_nascita;
    }

    public String getComune_nascita() {
        return comune_nascita;
    }

    public void setComune_nascita(String comune_nascita) {
        this.comune_nascita = comune_nascita;
    }

    public Boolean getFl_cittadino_italiano() {
        return fl_cittadino_italiano;
    }

    public void setFl_cittadino_italiano(Boolean fl_cittadino_italiano) {
        this.fl_cittadino_italiano = fl_cittadino_italiano;
    }

    public String getIndirizzo_residenza() {
        return indirizzo_residenza;
    }

    public void setIndirizzo_residenza(String indirizzo_residenza) {
        this.indirizzo_residenza = indirizzo_residenza;
    }

    public String getProvincia_nascita() {
        return provincia_nascita;
    }

    public void setProvincia_nascita(String provincia_nascita) {
        this.provincia_nascita = provincia_nascita;
    }

    public String getNazione_nascita() {
        return nazione_nascita;
    }

    public void setNazione_nascita(String nazione_nascita) {
        this.nazione_nascita = nazione_nascita;
    }

    public String getComune_residenza() {
        return comune_residenza;
    }

    public void setComune_residenza(String comune_residenza) {
        this.comune_residenza = comune_residenza;
    }

    public String getProvincia_residenza() {
        return provincia_residenza;
    }

    public void setProvincia_residenza(String provincia_residenza) {
        this.provincia_residenza = provincia_residenza;
    }

    public String getTelefono_comunicazioni() {
        return telefono_comunicazioni;
    }

    public void setTelefono_comunicazioni(String telefono_comunicazioni) {
        this.telefono_comunicazioni = telefono_comunicazioni;
    }
}
