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

package it.cnr.si.missioni.util.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class ResultProxy implements Serializable {
    private String type;
    private HttpStatus status;
    private String body;
    private CommonJsonRest<RestServiceBean> commonJsonResponse;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
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

    public void setCommonJsonResponse(
            CommonJsonRest<RestServiceBean> commonJsonResponse) {
        this.commonJsonResponse = commonJsonResponse;
    }

    @Override
    public String toString() {
        String jsonBody = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonBody = mapper.writeValueAsString(body);
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella manipolazione del file JSON per la responde della REST (" + Utility.getMessageException(ex) + ").");
        }
        return "CallCache [status=" + status + ", body=" + jsonBody
                + ", type=" + type + ", commonJsonResponse=" + commonJsonResponse;
    }
}

