
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
        "cd_tipo_documento_cont",
        "pg_documento_cont",
        "ti_documento_cont",
        "im_documento_cont",
        "ds_documento_cont",
        "dt_trasmissione",
        "dt_pagamento_incasso",
        "cd_terzo",
        "cd_anag",
        "cd_cds",
        "esercizio",
        "im_pagato_incassato",
        "cd_unita_organizzativa",
        "cd_cds_origine",
        "cd_uo_origine"
})
public class Mandato extends RestServiceBean implements Serializable {

    @JsonProperty("cd_tipo_documento_cont")
    private String cdTipoDocumentoCont;
    @JsonProperty("pg_documento_cont")
    private Integer pgDocumentoCont;
    @JsonProperty("ti_documento_cont")
    private String tiDocumentoCont;
    @JsonProperty("im_documento_cont")
    private Double imDocumentoCont;
    @JsonProperty("ds_documento_cont")
    private String dsDocumentoCont;
    @JsonProperty("dt_trasmissione")
    private Integer dtTrasmissione;
    @JsonProperty("dt_pagamento_incasso")
    private Integer dtPagamentoIncasso;
    @JsonProperty("cd_terzo")
    private Integer cdTerzo;
    @JsonProperty("cd_anag")
    private Integer cdAnag;
    @JsonProperty("cd_cds")
    private String cdCds;
    @JsonProperty("esercizio")
    private Integer esercizio;
    @JsonProperty("im_pagato_incassato")
    private Double imPagatoIncassato;
    @JsonProperty("cd_unita_organizzativa")
    private String cdUnitaOrganizzativa;
    @JsonProperty("cd_cds_origine")
    private String cdCdsOrigine;
    @JsonProperty("cd_uo_origine")
    private String cdUoOrigine;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The cdTipoDocumentoCont
     */
    @JsonProperty("cd_tipo_documento_cont")
    public String getCdTipoDocumentoCont() {
        return cdTipoDocumentoCont;
    }

    /**
     * @param cdTipoDocumentoCont The cd_tipo_documento_cont
     */
    @JsonProperty("cd_tipo_documento_cont")
    public void setCdTipoDocumentoCont(String cdTipoDocumentoCont) {
        this.cdTipoDocumentoCont = cdTipoDocumentoCont;
    }

    /**
     * @return The pgDocumentoCont
     */
    @JsonProperty("pg_documento_cont")
    public Integer getPgDocumentoCont() {
        return pgDocumentoCont;
    }

    /**
     * @param pgDocumentoCont The pg_documento_cont
     */
    @JsonProperty("pg_documento_cont")
    public void setPgDocumentoCont(Integer pgDocumentoCont) {
        this.pgDocumentoCont = pgDocumentoCont;
    }

    /**
     * @return The tiDocumentoCont
     */
    @JsonProperty("ti_documento_cont")
    public String getTiDocumentoCont() {
        return tiDocumentoCont;
    }

    /**
     * @param tiDocumentoCont The ti_documento_cont
     */
    @JsonProperty("ti_documento_cont")
    public void setTiDocumentoCont(String tiDocumentoCont) {
        this.tiDocumentoCont = tiDocumentoCont;
    }

    /**
     * @return The imDocumentoCont
     */
    @JsonProperty("im_documento_cont")
    public Double getImDocumentoCont() {
        return imDocumentoCont;
    }

    /**
     * @param imDocumentoCont The im_documento_cont
     */
    @JsonProperty("im_documento_cont")
    public void setImDocumentoCont(Double imDocumentoCont) {
        this.imDocumentoCont = imDocumentoCont;
    }

    /**
     * @return The dsDocumentoCont
     */
    @JsonProperty("ds_documento_cont")
    public String getDsDocumentoCont() {
        return dsDocumentoCont;
    }

    /**
     * @param dsDocumentoCont The ds_documento_cont
     */
    @JsonProperty("ds_documento_cont")
    public void setDsDocumentoCont(String dsDocumentoCont) {
        this.dsDocumentoCont = dsDocumentoCont;
    }

    /**
     * @return The dtTrasmissione
     */
    @JsonProperty("dt_trasmissione")
    public Integer getDtTrasmissione() {
        return dtTrasmissione;
    }

    /**
     * @param dtTrasmissione The dt_trasmissione
     */
    @JsonProperty("dt_trasmissione")
    public void setDtTrasmissione(Integer dtTrasmissione) {
        this.dtTrasmissione = dtTrasmissione;
    }

    /**
     * @return The dtPagamentoIncasso
     */
    @JsonProperty("dt_pagamento_incasso")
    public Integer getDtPagamentoIncasso() {
        return dtPagamentoIncasso;
    }

    /**
     * @param dtPagamentoIncasso The dt_pagamento_incasso
     */
    @JsonProperty("dt_pagamento_incasso")
    public void setDtPagamentoIncasso(Integer dtPagamentoIncasso) {
        this.dtPagamentoIncasso = dtPagamentoIncasso;
    }

    /**
     * @return The cdTerzo
     */
    @JsonProperty("cd_terzo")
    public Integer getCdTerzo() {
        return cdTerzo;
    }

    /**
     * @param cdTerzo The cd_terzo
     */
    @JsonProperty("cd_terzo")
    public void setCdTerzo(Integer cdTerzo) {
        this.cdTerzo = cdTerzo;
    }

    /**
     * @return The cdAnag
     */
    @JsonProperty("cd_anag")
    public Integer getCdAnag() {
        return cdAnag;
    }

    /**
     * @param cdAnag The cd_anag
     */
    @JsonProperty("cd_anag")
    public void setCdAnag(Integer cdAnag) {
        this.cdAnag = cdAnag;
    }

    /**
     * @return The cdCds
     */
    @JsonProperty("cd_cds")
    public String getCdCds() {
        return cdCds;
    }

    /**
     * @param cdCds The cd_cds
     */
    @JsonProperty("cd_cds")
    public void setCdCds(String cdCds) {
        this.cdCds = cdCds;
    }

    /**
     * @return The esercizio
     */
    @JsonProperty("esercizio")
    public Integer getEsercizio() {
        return esercizio;
    }

    /**
     * @param esercizio The esercizio
     */
    @JsonProperty("esercizio")
    public void setEsercizio(Integer esercizio) {
        this.esercizio = esercizio;
    }

    /**
     * @return The imPagatoIncassato
     */
    @JsonProperty("im_pagato_incassato")
    public Double getImPagatoIncassato() {
        return imPagatoIncassato;
    }

    /**
     * @param imPagatoIncassato The im_pagato_incassato
     */
    @JsonProperty("im_pagato_incassato")
    public void setImPagatoIncassato(Double imPagatoIncassato) {
        this.imPagatoIncassato = imPagatoIncassato;
    }

    /**
     * @return The cdUnitaOrganizzativa
     */
    @JsonProperty("cd_unita_organizzativa")
    public String getCdUnitaOrganizzativa() {
        return cdUnitaOrganizzativa;
    }

    /**
     * @param cdUnitaOrganizzativa The cd_unita_organizzativa
     */
    @JsonProperty("cd_unita_organizzativa")
    public void setCdUnitaOrganizzativa(String cdUnitaOrganizzativa) {
        this.cdUnitaOrganizzativa = cdUnitaOrganizzativa;
    }

    /**
     * @return The cdCdsOrigine
     */
    @JsonProperty("cd_cds_origine")
    public String getCdCdsOrigine() {
        return cdCdsOrigine;
    }

    /**
     * @param cdCdsOrigine The cd_cds_origine
     */
    @JsonProperty("cd_cds_origine")
    public void setCdCdsOrigine(String cdCdsOrigine) {
        this.cdCdsOrigine = cdCdsOrigine;
    }

    /**
     * @return The cdUoOrigine
     */
    @JsonProperty("cd_uo_origine")
    public String getCdUoOrigine() {
        return cdUoOrigine;
    }

    /**
     * @param cdUoOrigine The cd_uo_origine
     */
    @JsonProperty("cd_uo_origine")
    public void setCdUoOrigine(String cdUoOrigine) {
        this.cdUoOrigine = cdUoOrigine;
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
