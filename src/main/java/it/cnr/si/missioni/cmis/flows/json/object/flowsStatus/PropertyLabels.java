
package it.cnr.si.missioni.cmis.flows.json.object.flowsStatus;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
