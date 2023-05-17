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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "cd_terzo",
        "cd_anag",
        "ti_dipendente_altro",
        "codice_fiscale",
        "ds_tipo_rapporto",
        "dt_ini_validita",
        "dt_fin_validita"
})

public class TerzoPerCompenso extends RestServiceBean implements Serializable {


    @JsonProperty("cd_terzo")
    private Integer cdTerzo;
    @JsonProperty("cd_anag")
    private Integer cdAnag;
    @JsonProperty("ti_dipendente_altro")
    private String tiDipendenteAltro;
    @JsonProperty("codice_fiscale")
    private String codiceFiscale;
    @JsonProperty("ds_tipo_rapporto")
    private String dsTipoRapporto;
    @JsonProperty("dt_ini_validita")
    private Object dtIniValidita;
    @JsonProperty("dt_fin_validita")
    private Object dtFinValidita;
    @JsonProperty("ds_comune_fiscale")
    private String dsComuneFiscale;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cd_terzo")
    public Integer getCdTerzo() {
        return cdTerzo;
    }

    @JsonProperty("cd_terzo")
    public void setCdTerzo(Integer cdTerzo) {
        this.cdTerzo = cdTerzo;
    }

    @JsonProperty("cd_anag")
    public Integer getCdAnag() {
        return cdAnag;
    }

    @JsonProperty("cd_anag")
    public void setCdAnag(Integer cdAnag) {
        this.cdAnag = cdAnag;
    }

    @JsonProperty("ti_dipendente_altro")
    public String getTiDipendenteAltro() {
        return tiDipendenteAltro;
    }

    @JsonProperty("ti_dipendente_altro")
    public void setTiDipendenteAltro(String tiDipendenteAltro) {
        this.tiDipendenteAltro = tiDipendenteAltro;
    }

    @JsonProperty("codice_fiscale")
    public String getCodice_fiscale() {
        return codiceFiscale;
    }

    @JsonProperty("codice_fiscale")
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    @JsonProperty("ds_tipo_rapporto")
    public String getDsTipoRapporto() {
        return dsTipoRapporto;
    }

    @JsonProperty("ds_tipo_rapporto")
    public void setDsTipoRapporto(String dsTipoRapporto) {
        this.dsTipoRapporto = dsTipoRapporto;
    }

    @JsonProperty("dt_ini_validita")
    public Object getDtIniValidita() {
        return dtIniValidita;
    }

    @JsonProperty("dt_ini_validita")
    public void setDtIniValidita(Object dtIniValidita) {
        this.dtIniValidita = dtIniValidita;
    }

    @JsonProperty("dt_fin_validita")
    public Object getDtFinValidita() {
        return dtFinValidita;
    }

    @JsonProperty("dt_fin_validita")
    public void setDtFinValidita(Object dtFinValidita) {
        this.dtFinValidita = dtFinValidita;
    }

    @JsonProperty("ds_comune_fiscale")
    public String getDsComuneFiscale() {
        return dsComuneFiscale;
    }

    @JsonProperty("ds_comune_fiscale")
    public void setDsComuneFiscale(String dsComuneFiscale) {
        this.dsComuneFiscale = dsComuneFiscale;
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
