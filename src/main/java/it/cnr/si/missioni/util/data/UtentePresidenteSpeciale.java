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
        "codice_utente",
        "nome",
        "cognome"
})
public class UtentePresidenteSpeciale implements Serializable {

    @JsonProperty("codice_utente")
    private String codice_utente;
    @JsonProperty("cognome")
    private String cognome;
    @JsonProperty("nome")
    private String nome;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The codice_utente
     */
    @JsonProperty("codice_utente")
    public String getCodiceUtente() {
        return codice_utente;
    }

    /**
     * @param codiceUo The codice_utente
     */
    @JsonProperty("codice_utente")
    public void setCodiceUtente(String codice_utente) {
        this.codice_utente = codice_utente;
    }

    /**
     * @return The cognome
     */
    @JsonProperty("cognome")
    public String getCognome() {
        return cognome;
    }

    /**
     * @param cognome The cognome
     */
    @JsonProperty("cognome")
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    /**
     * @return The nome
     */
    @JsonProperty("nome")
    public String getNome() {
        return nome;
    }

    /**
     * @param nome The nome
     */
    @JsonProperty("nome")
    public void setNome(String nome) {
        this.nome = nome;
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