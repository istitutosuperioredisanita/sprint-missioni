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

package it.cnr.si.missioni.util.data;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "uid",
        "uoForUsersSpecial",
        "all"
})
public class UsersSpecial implements Serializable {

    @JsonProperty("uid")
    private String uid;
    @JsonProperty("uoForUsersSpecial")
    private List<UoForUsersSpecial> uoForUsersSpecial;
    @JsonProperty("all")
    private String all;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * @return The uid
     */
    @JsonProperty("uid")
    public String getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    @JsonProperty("uid")
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return The uoForUsersSpecial
     */
    @JsonProperty("uoForUsersSpecial")
    public List<UoForUsersSpecial> getUoForUsersSpecials() {
        return uoForUsersSpecial;
    }

    /**
     * @param uoForUsersSpecial The uoForUsersSpecial
     */
    @JsonProperty("usersSpecial")
    public void setUoForUsersSpecial(List<UoForUsersSpecial> uoForUsersSpecial) {
        this.uoForUsersSpecial = uoForUsersSpecial;
    }

    /**
     * @return The all
     */
    @JsonProperty("all")
    public String getAll() {
        return all;
    }

    /**
     * @param all The all
     */
    @JsonProperty("all")
    public void setAll(String all) {
        this.all = all;
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