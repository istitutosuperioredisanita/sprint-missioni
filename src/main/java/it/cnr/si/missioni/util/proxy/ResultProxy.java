package it.cnr.si.missioni.util.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;
import org.springframework.http.HttpStatusCode;

import java.io.Serializable;

public class ResultProxy implements Serializable {
    private String type;
    private HttpStatusCode status;
    private String body;
    private CommonJsonRest<RestServiceBean> commonJsonResponse;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HttpStatusCode getStatus() {
        return status;
    }

    public void setStatus(HttpStatusCode status) {
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public CommonJsonRest<RestServiceBean> getCommonJsonResponse() {
        return commonJsonResponse;
    }

    public void setCommonJsonResponse(CommonJsonRest<RestServiceBean> commonJsonResponse) {
        this.commonJsonResponse = commonJsonResponse;
    }

    @Override
    public String toString() {
        String jsonBody = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonBody = mapper.writeValueAsString(body);
        } catch (Exception ex) {
            throw new AwesomeException(
                    CodiciErrore.ERRGEN,
                    "Errore nella manipolazione del file JSON per la responde della REST (" + Utility.getMessageException(ex) + ")."
            );
        }
        return "CallCache [status=" + status + ", body=" + jsonBody
                + ", type=" + type + ", commonJsonResponse=" + commonJsonResponse;
    }
}