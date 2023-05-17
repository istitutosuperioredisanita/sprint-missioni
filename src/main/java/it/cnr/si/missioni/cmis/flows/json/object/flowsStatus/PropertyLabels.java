
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
        "bpm_priority",
        "bpm_status",
        "bpm_workflowPriority"
})
public class PropertyLabels {

    @JsonProperty("bpm_priority")
    private String bpmPriority;
    @JsonProperty("bpm_status")
    private String bpmStatus;
    @JsonProperty("bpm_workflowPriority")
    private String bpmWorkflowPriority;
    @JsonIgnore
    private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("bpm_priority")
    public String getBpmPriority() {
        return bpmPriority;
    }

    @JsonProperty("bpm_priority")
    public void setBpmPriority(String bpmPriority) {
        this.bpmPriority = bpmPriority;
    }

    @JsonProperty("bpm_status")
    public String getBpmStatus() {
        return bpmStatus;
    }

    @JsonProperty("bpm_status")
    public void setBpmStatus(String bpmStatus) {
        this.bpmStatus = bpmStatus;
    }

    @JsonProperty("bpm_workflowPriority")
    public String getBpmWorkflowPriority() {
        return bpmWorkflowPriority;
    }

    @JsonProperty("bpm_workflowPriority")
    public void setBpmWorkflowPriority(String bpmWorkflowPriority) {
        this.bpmWorkflowPriority = bpmWorkflowPriority;
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
