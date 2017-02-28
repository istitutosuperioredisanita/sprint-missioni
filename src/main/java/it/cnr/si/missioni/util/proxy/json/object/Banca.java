
package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
    "ti_pagamento",
    "abi",
    "cab",
    "numero_conto",
    "intestazione",
    "codice_iban",
    "codice_swift",
    "fl_cancellato",
    "fl_cc_cds",
    "cin",
    "quietanza",
    "cd_terzo_delegato",
    "ds_terzo_delegato"
})
public class Banca extends RestServiceBean implements Serializable {

    @JsonProperty("ti_pagamento")
    private String tiPagamento;
    @JsonProperty("abi")
    private String abi;
    @JsonProperty("cab")
    private String cab;
    @JsonProperty("numero_conto")
    private String numeroConto;
    @JsonProperty("intestazione")
    private String intestazione;
    @JsonProperty("codice_iban")
    private String codiceIban;
    @JsonProperty("codice_swift")
    private String codiceSwift;
    @JsonProperty("fl_cancellato")
    private Boolean flCancellato;
    @JsonProperty("fl_cc_cds")
    private Boolean flCcCds;
    @JsonProperty("cin")
    private String cin;
    @JsonProperty("quietanza")
    private String quietanza;
    @JsonProperty("cd_terzo_delegato")
    private String cdTerzoDelegato;
    @JsonProperty("ds_terzo_delegato")
    private String dsTerzoDelegato;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The tiPagamento
     */
    @JsonProperty("ti_pagamento")
    public String getTiPagamento() {
        return tiPagamento;
    }

    /**
     * 
     * @param tiPagamento
     *     The ti_pagamento
     */
    @JsonProperty("ti_pagamento")
    public void setTiPagamento(String tiPagamento) {
        this.tiPagamento = tiPagamento;
    }

    /**
     * 
     * @return
     *     The abi
     */
    @JsonProperty("abi")
    public String getAbi() {
        return abi;
    }

    /**
     * 
     * @param abi
     *     The abi
     */
    @JsonProperty("abi")
    public void setAbi(String abi) {
        this.abi = abi;
    }

    /**
     * 
     * @return
     *     The cab
     */
    @JsonProperty("cab")
    public String getCab() {
        return cab;
    }

    /**
     * 
     * @param cab
     *     The cab
     */
    @JsonProperty("cab")
    public void setCab(String cab) {
        this.cab = cab;
    }

    /**
     * 
     * @return
     *     The numeroConto
     */
    @JsonProperty("numero_conto")
    public String getNumeroConto() {
        return numeroConto;
    }

    /**
     * 
     * @param numeroConto
     *     The numero_conto
     */
    @JsonProperty("numero_conto")
    public void setNumeroConto(String numeroConto) {
        this.numeroConto = numeroConto;
    }

    /**
     * 
     * @return
     *     The intestazione
     */
    @JsonProperty("intestazione")
    public String getIntestazione() {
        return intestazione;
    }

    /**
     * 
     * @param intestazione
     *     The intestazione
     */
    @JsonProperty("intestazione")
    public void setIntestazione(String intestazione) {
        this.intestazione = intestazione;
    }

    /**
     * 
     * @return
     *     The codiceIban
     */
    @JsonProperty("codice_iban")
    public String getCodiceIban() {
        return codiceIban;
    }

    /**
     * 
     * @param codiceIban
     *     The codice_iban
     */
    @JsonProperty("codice_iban")
    public void setCodiceIban(String codiceIban) {
        this.codiceIban = codiceIban;
    }

    /**
     * 
     * @return
     *     The codiceSwift
     */
    @JsonProperty("codice_swift")
    public String getCodiceSwift() {
        return codiceSwift;
    }

    /**
     * 
     * @param codiceSwift
     *     The codice_swift
     */
    @JsonProperty("codice_swift")
    public void setCodiceSwift(String codiceSwift) {
        this.codiceSwift = codiceSwift;
    }

    /**
     * 
     * @return
     *     The flCancellato
     */
    @JsonProperty("fl_cancellato")
    public Boolean getFlCancellato() {
        return flCancellato;
    }

    /**
     * 
     * @param flCancellato
     *     The fl_cancellato
     */
    @JsonProperty("fl_cancellato")
    public void setFlCancellato(Boolean flCancellato) {
        this.flCancellato = flCancellato;
    }

    /**
     * 
     * @return
     *     The flCcCds
     */
    @JsonProperty("fl_cc_cds")
    public Boolean getFlCcCds() {
        return flCcCds;
    }

    /**
     * 
     * @param flCcCds
     *     The fl_cc_cds
     */
    @JsonProperty("fl_cc_cds")
    public void setFlCcCds(Boolean flCcCds) {
        this.flCcCds = flCcCds;
    }

    /**
     * 
     * @return
     *     The cin
     */
    @JsonProperty("cin")
    public String getCin() {
        return cin;
    }

    /**
     * 
     * @param cin
     *     The cin
     */
    @JsonProperty("cin")
    public void setCin(String cin) {
        this.cin = cin;
    }

    /**
     * 
     * @return
     *     The quietanza
     */
    @JsonProperty("quietanza")
    public String getQuietanza() {
        return quietanza;
    }

    /**
     * 
     * @param quietanza
     *     The quietanza
     */
    @JsonProperty("quietanza")
    public void setQuietanza(String quietanza) {
        this.quietanza = quietanza;
    }

    /**
     * 
     * @return
     *     The cdTerzoDelegato
     */
    @JsonProperty("cd_terzo_delegato")
    public String getCdTerzoDelegato() {
        return cdTerzoDelegato;
    }

    /**
     * 
     * @param cdTerzoDelegato
     *     The cd_terzo_delegato
     */
    @JsonProperty("cd_terzo_delegato")
    public void setCdTerzoDelegato(String cdTerzoDelegato) {
        this.cdTerzoDelegato = cdTerzoDelegato;
    }

    /**
     * 
     * @return
     *     The dsTerzoDelegato
     */
    @JsonProperty("ds_terzo_delegato")
    public String getDsTerzoDelegato() {
        return dsTerzoDelegato;
    }

    /**
     * 
     * @param dsTerzoDelegato
     *     The ds_terzo_delegato
     */
    @JsonProperty("ds_terzo_delegato")
    public void setDsTerzoDelegato(String dsTerzoDelegato) {
        this.dsTerzoDelegato = dsTerzoDelegato;
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
