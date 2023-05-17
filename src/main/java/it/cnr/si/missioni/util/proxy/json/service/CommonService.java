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

package it.cnr.si.missioni.util.proxy.json.service;

import it.cnr.si.missioni.service.ProxyService;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.CallCache;
import it.cnr.si.missioni.util.proxy.cache.json.RestService;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommonService {

    private final Logger log = LoggerFactory.getLogger(CommonService.class);
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ProxyService proxyService;

    public String process(List<JSONClause> clauses, String app, String url) {
        return process(clauses, app, url, null);
    }

    public String process(List<JSONClause> clauses, String app, String url, List<JSONClause> clausesToAdd) {
        String risposta = null;
        RestService restInCache = cacheService.getBasicRest(app, url);
        if (restInCache != null) {
            List<JSONClause> listaNewClauses = null;
            if (restInCache.getClauseFixed() != null && !restInCache.getClauseFixed().isEmpty()) {
                listaNewClauses = new ArrayList<JSONClause>();
                for (JSONClause clause : clauses) {
                    boolean existsClause = cacheService.existsClause(restInCache.getClauseFixed(), clause);
                    if (!existsClause) {
                        listaNewClauses.add(clause);
                    }
                }
            } else {
                listaNewClauses = clauses;
            }

            CallCache callCache = cacheService.prepareCallCache(restInCache, clausesToAdd);
            log.debug("Start Common Cache : " + url);
            ResultProxy result = proxyService.processInCache(callCache);
            log.debug("End Common Cache : " + url);
            CommonJsonRest<RestServiceBean> commonJsonRest = result.getCommonJsonResponse();
            risposta = cacheService.manageResponse(restInCache, listaNewClauses, commonJsonRest);
        } else {
            JSONBody jBody = null;
            jBody = proxyService.inizializzaJson();
            jBody.setClauses(clauses);
            cacheService.setContext(jBody, app);
            ResultProxy result = proxyService.process(HttpMethod.POST, jBody, app, url, "proxyURL=" + url, null);
            risposta = result.getBody();
        }
        return risposta;
    }

    public String process(JSONBody jBody, String app, String url) {
        cacheService.setContext(jBody, app);
        return process(jBody, app, url, false);
    }

    public String processWithContextHeader(JSONBody jBody, String app, String url) {
        return process(jBody, app, url, true);
    }

    public String process(String body, String app, String url, Boolean value, HttpMethod httpMethod) {
        String risposta = null;
        ResultProxy result = proxyService.process(httpMethod, body, app, url, "proxyURL=" + url, null, value);
        risposta = result.getBody();
        return risposta;
    }

    public String process(JSONBody jBody, String app, String url, Boolean value, HttpMethod httpMethod) {
        String risposta = null;
        ResultProxy result = proxyService.process(httpMethod, jBody, app, url, "proxyURL=" + url, null, value);
        risposta = result.getBody();
        return risposta;
    }

    private String process(JSONBody jBody, String app, String url, Boolean value) {
        return process(jBody, app, url, value, HttpMethod.POST);
    }
}
