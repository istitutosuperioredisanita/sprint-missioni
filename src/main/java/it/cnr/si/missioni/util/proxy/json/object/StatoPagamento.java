
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
        "cd_cds",
        "cd_siope",
        "ds_siope",
        "pg_mandato",
        "dt_emissione",
        "dt_trasmissione",
        "dt_pagamento",
        "esercizio_obbligazione",
        "esercizio_ori_obbligazione",
        "pg_obbligazione",
        "pg_obbligazione_scadenzario",
        "cd_cds_doc_amm",
        "cd_uo_doc_amm",
        "esercizio_doc_amm",
        "ds_tipo_doc_amm",
        "pg_doc_amm",
        "importo"})
public class StatoPagamento extends RestServiceBean implements Serializable {

    @JsonProperty("cd_cds")
    private String cdCds;
    @JsonProperty("cd_siope")
    private String cdSiope;
    @JsonProperty("ds_siope")
    private String dsSiope;
    @JsonProperty("pg_mandato")
    private Integer pgMandato;
    @JsonProperty("dt_emissione")
    private Integer dtEmissione;
    @JsonProperty("dt_trasmissione")
    private Integer dtTrasmissione;
    @JsonProperty("dt_pagamento")
    private Integer dtPagamento;
    @JsonProperty("esercizio_obbligazione")
    private Integer esercizioObbligazione;
    @JsonProperty("esercizio_ori_obbligazione")
    private Integer esercizioOriObbligazione;
    @JsonProperty("pg_obbligazione")
    private Integer pgObbligazione;
    @JsonProperty("pg_obbligazione_scadenzario")
    private Integer pgObbligazioneScadenzario;
    @JsonProperty("cd_cds_doc_amm")
    private String cdCdsDocAmm;
    @JsonProperty("cd_uo_doc_amm")
    private String cdUoDocAmm;
    @JsonProperty("esercizio_doc_amm")
    private Integer esercizioDocAmm;
    @JsonProperty("ds_tipo_doc_amm")
    private String dsTipoDocAmm;
    @JsonProperty("pg_doc_amm")
    private Integer pgDocAmm;
    @JsonProperty("importo")
    private Double importo;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("cd_cds")
    public String getCdCds() {
        return cdCds;
    }

    @JsonProperty("cd_cds")
    public void setCdCds(String cdCds) {
        this.cdCds = cdCds;
    }

    @JsonProperty("cd_siope")
    public String getCdSiope() {
        return cdSiope;
    }

    @JsonProperty("cd_siope")
    public void setCdSiope(String cdSiope) {
        this.cdSiope = cdSiope;
    }

    @JsonProperty("ds_siope")
    public String getDsSiope() {
        return dsSiope;
    }

    @JsonProperty("ds_siope")
    public void setDsSiope(String dsSiope) {
        this.dsSiope = dsSiope;
    }

    @JsonProperty("pg_mandato")
    public Integer getPgMandato() {
        return pgMandato;
    }

    @JsonProperty("pg_mandato")
    public void setPgMandato(Integer pgMandato) {
        this.pgMandato = pgMandato;
    }

    @JsonProperty("dt_emissione")
    public Integer getDtEmissione() {
        return dtEmissione;
    }

    @JsonProperty("dt_emissione")
    public void setDtEmissione(Integer dtEmissione) {
        this.dtEmissione = dtEmissione;
    }

    @JsonProperty("dt_trasmissione")
    public Integer getDtTrasmissione() {
        return dtTrasmissione;
    }

    @JsonProperty("dt_trasmissione")
    public void setDtTrasmissione(Integer dtTrasmissione) {
        this.dtTrasmissione = dtTrasmissione;
    }

    @JsonProperty("dt_pagamento")
    public Integer getDtPagamento() {
        return dtPagamento;
    }

    @JsonProperty("dt_pagamento")
    public void setDtPagamento(Integer dtPagamento) {
        this.dtPagamento = dtPagamento;
    }

    @JsonProperty("esercizio_obbligazione")
    public Integer getEsercizioObbligazione() {
        return esercizioObbligazione;
    }

    @JsonProperty("esercizio_obbligazione")
    public void setEsercizioObbligazione(Integer esercizioObbligazione) {
        this.esercizioObbligazione = esercizioObbligazione;
    }

    @JsonProperty("esercizio_ori_obbligazione")
    public Integer getEsercizioOriObbligazione() {
        return esercizioOriObbligazione;
    }

    @JsonProperty("esercizio_ori_obbligazione")
    public void setEsercizioOriObbligazione(Integer esercizioOriObbligazione) {
        this.esercizioOriObbligazione = esercizioOriObbligazione;
    }

    @JsonProperty("pg_obbligazione")
    public Integer getPgObbligazione() {
        return pgObbligazione;
    }

    @JsonProperty("pg_obbligazione")
    public void setPgObbligazione(Integer pgObbligazione) {
        this.pgObbligazione = pgObbligazione;
    }

    @JsonProperty("pg_obbligazione_scadenzario")
    public Integer getPgObbligazioneScadenzario() {
        return pgObbligazioneScadenzario;
    }

    @JsonProperty("pg_obbligazione_scadenzario")
    public void setPgObbligazioneScadenzario(Integer pgObbligazioneScadenzario) {
        this.pgObbligazioneScadenzario = pgObbligazioneScadenzario;
    }

    @JsonProperty("cd_cds_doc_amm")
    public String getCdCdsDocAmm() {
        return cdCdsDocAmm;
    }

    @JsonProperty("cd_cds_doc_amm")
    public void setCdCdsDocAmm(String cdCdsDocAmm) {
        this.cdCdsDocAmm = cdCdsDocAmm;
    }

    @JsonProperty("cd_uo_doc_amm")
    public String getCdUoDocAmm() {
        return cdUoDocAmm;
    }

    @JsonProperty("cd_uo_doc_amm")
    public void setCdUoDocAmm(String cdUoDocAmm) {
        this.cdUoDocAmm = cdUoDocAmm;
    }

    @JsonProperty("esercizio_doc_amm")
    public Integer getEsercizioDocAmm() {
        return esercizioDocAmm;
    }

    @JsonProperty("esercizio_doc_amm")
    public void setEsercizioDocAmm(Integer esercizioDocAmm) {
        this.esercizioDocAmm = esercizioDocAmm;
    }

    @JsonProperty("ds_tipo_doc_amm")
    public String getDsTipoDocAmm() {
        return dsTipoDocAmm;
    }

    @JsonProperty("ds_tipo_doc_amm")
    public void setDsTipoDocAmm(String dsTipoDocAmm) {
        this.dsTipoDocAmm = dsTipoDocAmm;
    }

    @JsonProperty("pg_doc_amm")
    public Integer getPgDocAmm() {
        return pgDocAmm;
    }

    @JsonProperty("pg_doc_amm")
    public void setPgDocAmm(Integer pgDocAmm) {
        this.pgDocAmm = pgDocAmm;
    }

    @JsonProperty("importo")
    public Double getImporto() {
        return importo;
    }

    @JsonProperty("importo")
    public void setImporto(Double importo) {
        this.importo = importo;
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