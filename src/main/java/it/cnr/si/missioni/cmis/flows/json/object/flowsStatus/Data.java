
package it.cnr.si.missioni.cmis.flows.json.object.flowsStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "package",
    "dueDate",
        "endDate",
        "context",
    "initiator",
    "description",
    "definitionUrl",
    "title",
    "isActive",
    "priority",
    "message",
    "url",
    "startTaskInstanceId",
    "name",
    "definition",
    "id",
    "diagramUrl",
    "startDate",
    "tasks"
})
public class Data {

    @JsonProperty("package")
    private String _package;
    @JsonProperty("dueDate")
    private String dueDate;
    @JsonProperty("endDate")
    private String endDate;
    @JsonProperty("context")
    private String context;
    @JsonProperty("initiator")
    private Initiator initiator;
    @JsonProperty("description")
    private String description;
    @JsonProperty("definitionUrl")
    private String definitionUrl;
    @JsonProperty("title")
    private String title;
    @JsonProperty("isActive")
    private Boolean isActive;
    @JsonProperty("priority")
    private Integer priority;
    @JsonProperty("message")
    private String message;
    @JsonProperty("url")
    private String url;
    @JsonProperty("startTaskInstanceId")
    private String startTaskInstanceId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("definition")
    private Definition definition;
    @JsonProperty("id")
    private String id;
    @JsonProperty("diagramUrl")
    private String diagramUrl;
    @JsonProperty("startDate")
    private String startDate;
    @JsonProperty("tasks")
    private List<Task> tasks = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("package")
    public String getPackage() {
        return _package;
    }

    @JsonProperty("package")
    public void setPackage(String _package) {
        this._package = _package;
    }

    @JsonProperty("dueDate")
    public String getDueDate() {
        return dueDate;
    }

    @JsonProperty("dueDate")
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @JsonProperty("context")
    public String getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(String context) {
        this.context = context;
    }

    @JsonProperty("initiator")
    public Initiator getInitiator() {
        return initiator;
    }

    @JsonProperty("initiator")
    public void setInitiator(Initiator initiator) {
        this.initiator = initiator;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("definitionUrl")
    public String getDefinitionUrl() {
        return definitionUrl;
    }

    @JsonProperty("definitionUrl")
    public void setDefinitionUrl(String definitionUrl) {
        this.definitionUrl = definitionUrl;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("isActive")
    public Boolean getIsActive() {
        return isActive;
    }

    @JsonProperty("isActive")
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @JsonProperty("priority")
    public Integer getPriority() {
        return priority;
    }

    @JsonProperty("priority")
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("startTaskInstanceId")
    public String getStartTaskInstanceId() {
        return startTaskInstanceId;
    }

    @JsonProperty("startTaskInstanceId")
    public void setStartTaskInstanceId(String startTaskInstanceId) {
        this.startTaskInstanceId = startTaskInstanceId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("definition")
    public Definition getDefinition() {
        return definition;
    }

    @JsonProperty("definition")
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("diagramUrl")
    public String getDiagramUrl() {
        return diagramUrl;
    }

    @JsonProperty("diagramUrl")
    public void setDiagramUrl(String diagramUrl) {
        this.diagramUrl = diagramUrl;
    }

    @JsonProperty("startDate")
    public String getStartDate() {
        return startDate;
    }

    @JsonProperty("startDate")
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @JsonProperty("tasks")
    public List<Task> getTasks() {
        return tasks;
    }

    @JsonProperty("tasks")
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
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
