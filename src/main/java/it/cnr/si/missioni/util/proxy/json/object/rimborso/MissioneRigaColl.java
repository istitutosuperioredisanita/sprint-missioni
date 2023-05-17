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

package it.cnr.si.missioni.util.proxy.json.object.rimborso;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "progressivo_riga",
        "cd_cds_obbligazione",
        "esercizio_obbligazione",
        "esercizio_ori_obbligazione",
        "pg_obbligazione",
        "im_totale_riga_missione"
})
public class MissioneRigaColl implements Serializable {

    @JsonProperty("progressivo_riga")
    private Integer progressivoRiga;
    @JsonProperty("cd_cds_obbligazione")
    private String cdCdsObbligazione;
    @JsonProperty("esercizio_obbligazione")
    private String esercizioObbligazione;
    @JsonProperty("esercizio_ori_obbligazione")
    private String esercizioOriObbligazione;
    @JsonProperty("pg_obbligazione")
    private String pgObbligazione;
    @JsonProperty("im_totale_riga_missione")
    private BigDecimal imTotaleRigaMissione;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Integer getProgressivoRiga() {
        return progressivoRiga;
    }

    public void setProgressivoRiga(Integer progressivoRiga) {
        this.progressivoRiga = progressivoRiga;
    }

    public String getCdCdsObbligazione() {
        return cdCdsObbligazione;
    }

    public void setCdCdsObbligazione(String cdCdsObbligazione) {
        this.cdCdsObbligazione = cdCdsObbligazione;
    }

    public String getEsercizioObbligazione() {
        return esercizioObbligazione;
    }

    public void setEsercizioObbligazione(String esercizioObbligazione) {
        this.esercizioObbligazione = esercizioObbligazione;
    }

    public String getEsercizioOriObbligazione() {
        return esercizioOriObbligazione;
    }

    public void setEsercizioOriObbligazione(String esercizioOriObbligazione) {
        this.esercizioOriObbligazione = esercizioOriObbligazione;
    }

    public String getPgObbligazione() {
        return pgObbligazione;
    }

    public void setPgObbligazione(String pgObbligazione) {
        this.pgObbligazione = pgObbligazione;
    }

    public BigDecimal getImTotaleRigaMissione() {
        return imTotaleRigaMissione;
    }

    public void setImTotaleRigaMissione(BigDecimal imTotaleRigaMissione) {
        this.imTotaleRigaMissione = imTotaleRigaMissione;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

}
