
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
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "owner",
        "workflowInstance",
        "isClaimable",
        "description",
        "title",
        "propertyLabels",
        "url",
        "path",
        "isEditable",
        "name",
        "isReleasable",
        "id",
        "state",
        "isReassignable",
        "isPooled",
        "properties",
        "outcome"
})
public class Task {
    public final static String IN_PROGRESS = "IN_PROGRESS";
    @JsonProperty("owner")
    private Owner owner;
    @JsonProperty("workflowInstance")
    private WorkflowInstance workflowInstance;
    @JsonProperty("isClaimable")
    private Boolean isClaimable;
    @JsonProperty("description")
    private String description;
    @JsonProperty("title")
    private String title;
    @JsonProperty("propertyLabels")
    private PropertyLabels propertyLabels;
    @JsonProperty("url")
    private String url;
    @JsonProperty("path")
    private String path;
    @JsonProperty("isEditable")
    private Boolean isEditable;
    @JsonProperty("name")
    private String name;
    @JsonProperty("isReleasable")
    private Boolean isReleasable;
    @JsonProperty("id")
    private String id;
    @JsonProperty("state")
    private String state;
    @JsonProperty("isReassignable")
    private Boolean isReassignable;
    @JsonProperty("isPooled")
    private Boolean isPooled;
    @JsonProperty("properties")
    private Properties properties;
    @JsonProperty("outcome")
    private String outcome;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("owner")
    public Owner getOwner() {
        return owner;
    }

    @JsonProperty("owner")
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @JsonProperty("workflowInstance")
    public WorkflowInstance getWorkflowInstance() {
        return workflowInstance;
    }

    @JsonProperty("workflowInstance")
    public void setWorkflowInstance(WorkflowInstance workflowInstance) {
        this.workflowInstance = workflowInstance;
    }

    @JsonProperty("isClaimable")
    public Boolean getIsClaimable() {
        return isClaimable;
    }

    @JsonProperty("isClaimable")
    public void setIsClaimable(Boolean isClaimable) {
        this.isClaimable = isClaimable;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("propertyLabels")
    public PropertyLabels getPropertyLabels() {
        return propertyLabels;
    }

    @JsonProperty("propertyLabels")
    public void setPropertyLabels(PropertyLabels propertyLabels) {
        this.propertyLabels = propertyLabels;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }

    @JsonProperty("isEditable")
    public Boolean getIsEditable() {
        return isEditable;
    }

    @JsonProperty("isEditable")
    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("isReleasable")
    public Boolean getIsReleasable() {
        return isReleasable;
    }

    @JsonProperty("isReleasable")
    public void setIsReleasable(Boolean isReleasable) {
        this.isReleasable = isReleasable;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("isReassignable")
    public Boolean getIsReassignable() {
        return isReassignable;
    }

    @JsonProperty("isReassignable")
    public void setIsReassignable(Boolean isReassignable) {
        this.isReassignable = isReassignable;
    }

    @JsonProperty("isPooled")
    public Boolean getIsPooled() {
        return isPooled;
    }

    @JsonProperty("isPooled")
    public void setIsPooled(Boolean isPooled) {
        this.isPooled = isPooled;
    }

    @JsonProperty("properties")
    public Properties getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @JsonProperty("outcome")
    public String getOutcome() {
        return outcome;
    }

    @JsonProperty("outcome")
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Boolean isInProgress() {
        return IN_PROGRESS.equals(getState());
    }
}
