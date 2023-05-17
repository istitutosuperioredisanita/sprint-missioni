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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import it.cnr.si.missioni.awesome.exception.AwesomeException;

import javax.annotation.Generated;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "restService"
})
public class Services implements DataSerializable {

    @JsonProperty("restService")
    private List<RestService> restService = new ArrayList<RestService>();

    /**
     * @return The restService
     */
    @JsonProperty("restService")
    public List<RestService> getRestService() {
        return restService;
    }

    /**
     * @param restService The restService
     */
    @JsonProperty("restService")
    public void setRestService(List<RestService> restService) {
        this.restService = restService;
    }

    @Override
    public String toString() {
        return "Services [RestServices=" + restService + "]";
    }

    public String getMd5() {
        String objectToString = toString();
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(objectToString.getBytes(), 0, objectToString.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            new AwesomeException("Errore nel recupero dell'algoritmo MD5 " + e.getMessage());
        }
        return "";
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(toString());
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        in.readData();
    }
}
