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

package it.cnr.si.missioni.util.proxy.cache.json;

import com.fasterxml.jackson.annotation.*;
import it.cnr.si.missioni.util.proxy.json.JSONOrderBy;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "app",
        "url",
        "skipLoadStartup",
        "classeJson",
        "classe",
        "clause",
        "clauseVariable",
        "clauseFixed"
})
public class RestService implements Serializable {

    @JsonProperty("app")
    private String app;
    @JsonProperty("url")
    private String url;
    @JsonProperty("skipLoadStartup")
    private String skipLoadStartup;
    @JsonProperty("classeJson")
    private String classeJson;
    @JsonProperty("classe")
    private String classe;
    @JsonProperty("clauseVariable")
    private List<Clause> clauseVariable = new ArrayList<Clause>();
    @JsonProperty("clauseFixed")
    private List<Clause> clauseFixed = new ArrayList<Clause>();
    @JsonProperty("clauseToIterate")
    private List<ClauseToIterate> clauseToIterate = new ArrayList<ClauseToIterate>();
    @JsonProperty("order")
    private List<JSONOrderBy> order = new ArrayList<JSONOrderBy>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The app
     */
    @JsonProperty("app")
    public String getApp() {
        return app;
    }

    /**
     * @param app The app
     */
    @JsonProperty("app")
    public void setApp(String app) {
        this.app = app;
    }

    /**
     * @return The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The skipLoadStartup
     */
    @JsonProperty("skipLoadStartup")
    public String getSkipLoadStartup() {
        return skipLoadStartup;
    }

    /**
     * @param skipLoadStartup The skipLoadStartup
     */
    @JsonProperty("skipLoadStartup")
    public void setSkipLoadStartup(String skipLoadStartup) {
        this.skipLoadStartup = skipLoadStartup;
    }

    /**
     * @return The classeJson
     */
    @JsonProperty("classeJson")
    public String getClasseJson() {
        return classeJson;
    }

    /**
     * @param classeJson The classeJson
     */
    @JsonProperty("classeJson")
    public void setClasseJson(String classeJson) {
        this.classeJson = classeJson;
    }

    /**
     * @return The classe
     */
    @JsonProperty("classe")
    public String getClasse() {
        return classe;
    }

    /**
     * @param classe The classe
     */
    @JsonProperty("classe")
    public void setClasse(String classe) {
        this.classe = classe;
    }

    /**
     * @return The clauseVariable
     */
    @JsonProperty("clauseVariable")
    public List<Clause> getClauseVariable() {
        return clauseVariable;
    }

    /**
     * @param clauseVariable The clauseVariable
     */
    @JsonProperty("clauseVariable")
    public void setClauseVariable(List<Clause> clauseVariable) {
        this.clauseVariable = clauseVariable;
    }

    /**
     * @return The clauseFixed
     */
    @JsonProperty("clauseFixed")
    public List<Clause> getClauseFixed() {
        return clauseFixed;
    }

    /**
     * @param clauseFixed The clauseFixed
     */
    @JsonProperty("clauseFixed")
    public void setClauseFixed(List<Clause> clauseFixed) {
        this.clauseFixed = clauseFixed;
    }

    /**
     * @return The clauseToIterate
     */
    @JsonProperty("clauseToIterate")
    public List<ClauseToIterate> getClauseToIterate() {
        return clauseToIterate;
    }

    /**
     * @param clauseToIterate The clauseToIterate
     */
    @JsonProperty("clauseToIterate")
    public void setClauseToIterate(List<ClauseToIterate> clauseToIterate) {
        this.clauseToIterate = clauseToIterate;
    }

    /**
     * @return The order
     */
    @JsonProperty("order")
    public List<JSONOrderBy> getOrder() {
        return order;
    }

    /**
     * @param order The order
     */
    @JsonProperty("order")
    public void setOrder(List<JSONOrderBy> order) {
        this.order = order;
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