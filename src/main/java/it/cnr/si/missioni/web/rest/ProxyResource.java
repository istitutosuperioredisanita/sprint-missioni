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

package it.cnr.si.missioni.web.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import it.cnr.si.missioni.service.ProxyService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.CallCache;
import it.cnr.si.missioni.util.proxy.cache.ResultCacheProxy;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for proxy to different application.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("api/proxy/{app}")
public class ProxyResource {

    public final static String PROXY_URL = "proxyURL";
    private final Logger log = LoggerFactory.getLogger(ProxyResource.class);
    @Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ProxyService proxyService;

    @Autowired
    private CacheService cacheService;

    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> get(@PathVariable String app, @RequestParam(value = PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
        if (log.isDebugEnabled())
            log.debug("GET from app: " + app + " with proxyURL: " + url);
        return process(HttpMethod.GET, null, app, url, request, response);
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> post(@RequestBody JSONBody body, @PathVariable String app, @RequestParam(value = PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
        if (log.isDebugEnabled())
            log.debug("POST from app: " + app + " with proxyURL: " + url);
        try {
            return process(HttpMethod.POST, body, app, url, request, response);
        } catch (Exception e) {
            log.error("ERRORE post", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.ALL_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> put(@RequestBody JSONBody body, @PathVariable String app, @RequestParam(value = PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
        if (log.isDebugEnabled())
            log.debug("PUT from app: " + app + " with proxyURL: " + url);
        try {
            ResponseEntity<String> risposta = process(HttpMethod.PUT, body, app, url, request, response);

            return risposta;
        } catch (Exception e) {
            log.error("ERRORE put", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> delete(@PathVariable String app, @RequestParam(value = PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
        if (log.isDebugEnabled())
            log.debug("DELETE from app: " + app + " with proxyURL: " + url);
        return process(HttpMethod.DELETE, null, app, url, request, response);
    }

    private ResponseEntity<String> process(HttpMethod httpMethod, JSONBody body, String app, String url, HttpServletRequest request, HttpServletResponse response) {
        try {
            ResultProxy result = null;
            ResultCacheProxy resultCacheProxy = cacheService.manageCache(url, body, app);
            String existsClauseVariable = existsClauseVariable(resultCacheProxy);
            if (isCacheable(resultCacheProxy)) {
                CallCache callCache = new CallCache(httpMethod, resultCacheProxy.getBody(), app, url, request.getQueryString(), request.getHeader(Costanti.HEADER_FOR_PROXY_AUTHORIZATION), resultCacheProxy.getRestService().getClasseJson());
                log.info("Start Cache :");
                result = proxyService.processInCache(callCache);
                log.info("End Cache :");
            } else {
                if (body != null && body.getContext() == null) {
                    cacheService.setContext(body, app);
                } else if (body == null && app != null && app.equals(Costanti.APP_SIGLA)) {
                    body = new JSONBody();
                    cacheService.setContext(body, app);
                }
                result = proxyService.process(httpMethod, body, app, url, request.getQueryString(), request.getHeader(Costanti.HEADER_FOR_PROXY_AUTHORIZATION));
            }
            if (result.getStatus().compareTo(HttpStatus.OK) != 0) {
                return JSONResponseEntity.getResponse(result.getStatus(), "");
            }
            response.setContentType(result.getType());
            response.setStatus(result.getStatus().value());
            String risposta = result.getBody();
            CommonJsonRest<RestServiceBean> commonJsonRest = result.getCommonJsonResponse();

            if (isCacheable(resultCacheProxy) && !app.equals(Costanti.APP_SIPER)) {
                if (existsClauseVariable.equals("S")) {
                    risposta = cacheService.manageResponse(resultCacheProxy.getRestService(), resultCacheProxy.getListClausesDeleted(), commonJsonRest);
                } else {

                    try {
                        risposta = cacheService.createResponse(commonJsonRest);
                    } catch (JsonProcessingException e) {
                        log.error("ERRORE process", e);
                        return JSONResponseEntity.badRequest(Utility.getMessageException(e));
                    }

                }
            }
            risposta = manageResponseForAccountRest(url, result, risposta);
            return JSONResponseEntity.ok(risposta);
        } catch (Exception e) {
            log.error("ERRORE ProxyResource", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    private String manageResponseForAccountRest(String url, ResultProxy result,
                                                String risposta) {
        String uid = securityService.getCurrentUserLogin();
        if (isAccountRest(url, uid)) {
            String resp = accountService.manageResponseForAccountRest(result.getBody());
            if (resp != null) {
                log.info("Response for Account. Url: " + url + " - Resp: " + resp);
                return resp;
            } else {
                log.info("Response for Account. Url: " + url + " - Risposta: " + risposta);
                return risposta;
            }
        } else if (isAccountRestWithAnotherUSer(url)) {
            String resp = accountService.manageResponseForAccountRest(result.getBody());
            if (resp != null) {
                log.info("Response for Account With Another User. Url: " + url + " - Resp: " + resp);
                return resp;
            } else {
                log.info("Response for Account With Another User. Url: " + url + " - Risposta: " + risposta);
                return risposta;
            }
        }
        return risposta;
    }

    public String createResponseForAccountRest(String risposta,
                                               Account account, UsersSpecial user) {
        if (user != null) {
            account.setAllUoForUsersSpecial(user.getAll());
            account.setUoForUsersSpecial(user.getUoForUsersSpecials());
            risposta = accountService.getBodyAccount(account);
        }
        return risposta;
    }

    private Boolean isAccountRest(String url, String uid) {
        return url.equals(Costanti.REST_ACCOUNT + uid);
    }

    private Boolean isAccountRestWithAnotherUSer(String url) {
        return url.startsWith(Costanti.REST_ACCOUNT);
    }

    private String existsClauseVariable(ResultCacheProxy resultCacheProxy) {
        String existsClauseVariable = "N";
        if (resultCacheProxy != null && resultCacheProxy.getListClausesDeleted() != null && !resultCacheProxy.getListClausesDeleted().isEmpty()) {
            existsClauseVariable = "S";
        }
        return existsClauseVariable;
    }

    private Boolean isCacheable(ResultCacheProxy resultCacheProxy) {
        return resultCacheProxy != null && resultCacheProxy.isUrlToCache();
    }
}
