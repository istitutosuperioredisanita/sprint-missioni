
package it.cnr.si.missioni.util.proxy.json.object.rimborso;

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
    "userContext",
    "oggettoBulk"
})
public class MissioneSigla {

    @JsonProperty("userContext")
    private UserContext userContext;
    @JsonProperty("oggettoBulk")
    private OggettoBulk oggettoBulk;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The userContext
     */
    @JsonProperty("userContext")
    public UserContext getUserContext() {
        return userContext;
    }

    /**
     * 
     * @param userContext
     *     The userContext
     */
    @JsonProperty("userContext")
    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    /**
     * 
     * @return
     *     The oggettoBulk
     */
    @JsonProperty("oggettoBulk")
    public OggettoBulk getOggettoBulk() {
        return oggettoBulk;
    }

    /**
     * 
     * @param oggettoBulk
     *     The oggettoBulk
     */
    @JsonProperty("oggettoBulk")
    public void setOggettoBulk(OggettoBulk oggettoBulk) {
        this.oggettoBulk = oggettoBulk;
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
