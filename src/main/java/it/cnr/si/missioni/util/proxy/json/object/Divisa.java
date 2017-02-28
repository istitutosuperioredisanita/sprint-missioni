
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
    "cd_divisa",
    "ds_divisa",
    "precisione",
    "fl_calcola_con_diviso"
})
public class Divisa extends RestServiceBean implements Serializable{

    @JsonProperty("cd_divisa")
    private String cdDivisa;
    @JsonProperty("ds_divisa")
    private String dsDivisa;
    @JsonProperty("precisione")
    private Integer precisione;
    @JsonProperty("fl_calcola_con_diviso")
    private Boolean flCalcolaConDiviso;
    @JsonIgnore
    private Map<String, Serializable> additionalProperties = new HashMap<String, Serializable>();

    /**
     * 
     * @return
     *     The cdDivisa
     */
    @JsonProperty("cd_divisa")
    public String getCdDivisa() {
        return cdDivisa;
    }

    /**
     * 
     * @param cdDivisa
     *     The cd_divisa
     */
    @JsonProperty("cd_divisa")
    public void setCdDivisa(String cdDivisa) {
        this.cdDivisa = cdDivisa;
    }

    /**
     * 
     * @return
     *     The dsDivisa
     */
    @JsonProperty("ds_divisa")
    public String getDsDivisa() {
        return dsDivisa;
    }

    /**
     * 
     * @param dsDivisa
     *     The ds_divisa
     */
    @JsonProperty("ds_divisa")
    public void setDsDivisa(String dsDivisa) {
        this.dsDivisa = dsDivisa;
    }

    /**
     * 
     * @return
     *     The precisione
     */
    @JsonProperty("precisione")
    public Integer getPrecisione() {
        return precisione;
    }

    /**
     * 
     * @param precisione
     *     The precisione
     */
    @JsonProperty("precisione")
    public void setPrecisione(Integer precisione) {
        this.precisione = precisione;
    }

    /**
     * 
     * @return
     *     The flCalcolaConDiviso
     */
    @JsonProperty("fl_calcola_con_diviso")
    public Boolean getFlCalcolaConDiviso() {
        return flCalcolaConDiviso;
    }

    /**
     * 
     * @param flCalcolaConDiviso
     *     The fl_calcola_con_diviso
     */
    @JsonProperty("fl_calcola_con_diviso")
    public void setFlCalcolaConDiviso(Boolean flCalcolaConDiviso) {
        this.flCalcolaConDiviso = flCalcolaConDiviso;
    }

    @JsonAnyGetter
    public Map<String, Serializable> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Serializable value) {
        this.additionalProperties.put(name, value);
    }

}
