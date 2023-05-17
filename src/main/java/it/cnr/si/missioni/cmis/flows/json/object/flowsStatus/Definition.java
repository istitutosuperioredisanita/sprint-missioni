
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

package it.cnr.si.missioni.cmis.flows.json.object.flowsStatus;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "taskDefinitions",
        "name",
        "description",
        "id",
        "title",
        "startTaskDefinitionUrl",
        "version",
        "startTaskDefinitionType",
        "url"
})
public class Definition {

    @JsonProperty("taskDefinitions")
    private List<TaskDefinition> taskDefinitions = null;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("id")
    private String id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("startTaskDefinitionUrl")
    private String startTaskDefinitionUrl;
    @JsonProperty("version")
    private String version;
    @JsonProperty("startTaskDefinitionType")
    private String startTaskDefinitionType;
    @JsonProperty("url")
    private String url;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("taskDefinitions")
    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    @JsonProperty("taskDefinitions")
    public void setTaskDefinitions(List<TaskDefinition> taskDefinitions) {
        this.taskDefinitions = taskDefinitions;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("startTaskDefinitionUrl")
    public String getStartTaskDefinitionUrl() {
        return startTaskDefinitionUrl;
    }

    @JsonProperty("startTaskDefinitionUrl")
    public void setStartTaskDefinitionUrl(String startTaskDefinitionUrl) {
        this.startTaskDefinitionUrl = startTaskDefinitionUrl;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(String version) {
        this.version = version;
    }

    @JsonProperty("startTaskDefinitionType")
    public String getStartTaskDefinitionType() {
        return startTaskDefinitionType;
    }

    @JsonProperty("startTaskDefinitionType")
    public void setStartTaskDefinitionType(String startTaskDefinitionType) {
        this.startTaskDefinitionType = startTaskDefinitionType;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
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
