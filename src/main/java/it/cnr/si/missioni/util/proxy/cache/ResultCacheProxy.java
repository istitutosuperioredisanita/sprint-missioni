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

package it.cnr.si.missioni.util.proxy.cache;

import it.cnr.si.missioni.util.proxy.cache.json.RestService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;

import java.util.List;

public class ResultCacheProxy {
    List<JSONClause> listClausesDeleted;
    RestService restService;
    JSONBody body;
    boolean isUrlToCache;

    public List<JSONClause> getListClausesDeleted() {
        return listClausesDeleted;
    }

    public void setListClausesDeleted(List<JSONClause> listClausesDeleted) {
        this.listClausesDeleted = listClausesDeleted;
    }

    public RestService getRestService() {
        return restService;
    }

    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    public boolean isUrlToCache() {
        return isUrlToCache;
    }

    public void setUrlToCache(boolean isUrlToCache) {
        this.isUrlToCache = isUrlToCache;
    }

    public JSONBody getBody() {
        return body;
    }

    public void setBody(JSONBody body) {
        this.body = body;
    }

}
