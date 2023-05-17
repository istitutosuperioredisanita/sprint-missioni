
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

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "cambio_tappa",
        "divisa_tappa",
        "fl_rimborso",
        "fl_comune_estero",
        "fl_alloggio_gratuito",
        "fl_navigazione",
        "fl_vitto_gratuito",
        "fl_vitto_alloggio_gratuito",
        "fl_comune_proprio",
        "fl_comune_altro",
        "fl_no_diaria",
        "dt_inizio_tappa",
        "dt_fine_tappa",
        "nazione"
})
public class TappeMissioneColl implements Cloneable, Serializable {

    @JsonProperty("cambio_tappa")
    private Integer cambioTappa;
    @JsonProperty("divisa_tappa")
    private DivisaTappa divisaTappa;
    @JsonProperty("fl_rimborso")
    private Boolean flRimborso;
    @JsonProperty("fl_comune_estero")
    private Boolean flComuneEstero;
    @JsonProperty("fl_alloggio_gratuito")
    private Boolean flAlloggioGratuito;
    @JsonProperty("fl_navigazione")
    private Boolean flNavigazione;
    @JsonProperty("fl_vitto_gratuito")
    private Boolean flVittoGratuito;
    @JsonProperty("fl_vitto_alloggio_gratuito")
    private Boolean flVittoAlloggioGratuito;
    @JsonProperty("fl_comune_proprio")
    private Boolean flComuneProprio;
    @JsonProperty("fl_comune_altro")
    private Boolean flComuneAltro;
    @JsonProperty("fl_no_diaria")
    private Boolean flNoDiaria;
    @JsonProperty("dt_inizio_tappa")
    private String dtInizioTappa;
    @JsonProperty("dt_fine_tappa")
    private String dtFineTappa;
    @JsonProperty("nazione")
    private Nazione nazione;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The cambioTappa
     */
    @JsonProperty("cambio_tappa")
    public Integer getCambioTappa() {
        return cambioTappa;
    }

    /**
     * @param cambioTappa The cambio_tappa
     */
    @JsonProperty("cambio_tappa")
    public void setCambioTappa(Integer cambioTappa) {
        this.cambioTappa = cambioTappa;
    }

    /**
     * @return The divisaTappa
     */
    @JsonProperty("divisa_tappa")
    public DivisaTappa getDivisaTappa() {
        return divisaTappa;
    }

    /**
     * @param divisaTappa The divisa_tappa
     */
    @JsonProperty("divisa_tappa")
    public void setDivisaTappa(DivisaTappa divisaTappa) {
        this.divisaTappa = divisaTappa;
    }

    /**
     * @return The flRimborso
     */
    @JsonProperty("fl_rimborso")
    public Boolean getFlRimborso() {
        return flRimborso;
    }

    /**
     * @param flRimborso The fl_rimborso
     */
    @JsonProperty("fl_rimborso")
    public void setFlRimborso(Boolean flRimborso) {
        this.flRimborso = flRimborso;
    }

    /**
     * @return The flComuneEstero
     */
    @JsonProperty("fl_comune_estero")
    public Boolean getFlComuneEstero() {
        return flComuneEstero;
    }

    /**
     * @param flComuneEstero The fl_comune_estero
     */
    @JsonProperty("fl_comune_estero")
    public void setFlComuneEstero(Boolean flComuneEstero) {
        this.flComuneEstero = flComuneEstero;
    }

    /**
     * @return The flAlloggioGratuito
     */
    @JsonProperty("fl_alloggio_gratuito")
    public Boolean getFlAlloggioGratuito() {
        return flAlloggioGratuito;
    }

    /**
     * @param flAlloggioGratuito The fl_alloggio_gratuito
     */
    @JsonProperty("fl_alloggio_gratuito")
    public void setFlAlloggioGratuito(Boolean flAlloggioGratuito) {
        this.flAlloggioGratuito = flAlloggioGratuito;
    }

    /**
     * @return The flNavigazione
     */
    @JsonProperty("fl_navigazione")
    public Boolean getFlNavigazione() {
        return flNavigazione;
    }

    /**
     * @param flNavigazione The fl_navigazione
     */
    @JsonProperty("fl_navigazione")
    public void setFlNavigazione(Boolean flNavigazione) {
        this.flNavigazione = flNavigazione;
    }

    /**
     * @return The flVittoGratuito
     */
    @JsonProperty("fl_vitto_gratuito")
    public Boolean getFlVittoGratuito() {
        return flVittoGratuito;
    }

    /**
     * @param flVittoGratuito The fl_vitto_gratuito
     */
    @JsonProperty("fl_vitto_gratuito")
    public void setFlVittoGratuito(Boolean flVittoGratuito) {
        this.flVittoGratuito = flVittoGratuito;
    }

    /**
     * @return The flVittoAlloggioGratuito
     */
    @JsonProperty("fl_vitto_alloggio_gratuito")
    public Boolean getFlVittoAlloggioGratuito() {
        return flVittoAlloggioGratuito;
    }

    /**
     * @param flVittoAlloggioGratuito The fl_vitto_alloggio_gratuito
     */
    @JsonProperty("fl_vitto_alloggio_gratuito")
    public void setFlVittoAlloggioGratuito(Boolean flVittoAlloggioGratuito) {
        this.flVittoAlloggioGratuito = flVittoAlloggioGratuito;
    }

    /**
     * @return The flComuneProprio
     */
    @JsonProperty("fl_comune_proprio")
    public Boolean getFlComuneProprio() {
        return flComuneProprio;
    }

    /**
     * @param flComuneProprio The fl_comune_proprio
     */
    @JsonProperty("fl_comune_proprio")
    public void setFlComuneProprio(Boolean flComuneProprio) {
        this.flComuneProprio = flComuneProprio;
    }

    /**
     * @return The flComuneAltro
     */
    @JsonProperty("fl_comune_altro")
    public Boolean getFlComuneAltro() {
        return flComuneAltro;
    }

    /**
     * @param flComuneAltro The fl_comune_altro
     */
    @JsonProperty("fl_comune_altro")
    public void setFlComuneAltro(Boolean flComuneAltro) {
        this.flComuneAltro = flComuneAltro;
    }

    /**
     * @return The flNoDiaria
     */
    @JsonProperty("fl_no_diaria")
    public Boolean getFlNoDiaria() {
        return flNoDiaria;
    }

    /**
     * @param flNoDiaria The fl_no_diaria
     */
    @JsonProperty("fl_no_diaria")
    public void setFlNoDiaria(Boolean flNoDiaria) {
        this.flNoDiaria = flNoDiaria;
    }

    /**
     * @return The dtInizioTappa
     */
    @JsonProperty("dt_inizio_tappa")
    public String getDtInizioTappa() {
        return dtInizioTappa;
    }

    /**
     * @param dtInizioTappa The dt_inizio_tappa
     */
    @JsonProperty("dt_inizio_tappa")
    public void setDtInizioTappa(String dtInizioTappa) {
        this.dtInizioTappa = dtInizioTappa;
    }

    /**
     * @return The dtFineTappa
     */
    @JsonProperty("dt_fine_tappa")
    public String getDtFineTappa() {
        return dtFineTappa;
    }

    /**
     * @param dtFineTappa The dt_fine_tappa
     */
    @JsonProperty("dt_fine_tappa")
    public void setDtFineTappa(String dtFineTappa) {
        this.dtFineTappa = dtFineTappa;
    }

    /**
     * @return The nazione
     */
    @JsonProperty("nazione")
    public Nazione getNazione() {
        return nazione;
    }

    /**
     * @param nazione The nazione
     */
    @JsonProperty("nazione")
    public void setNazione(Nazione nazione) {
        this.nazione = nazione;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
