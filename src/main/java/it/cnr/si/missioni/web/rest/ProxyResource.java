package it.cnr.si.missioni.web.rest;


import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.cnr.si.missioni.service.ProxyService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
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

/**
 * REST controller for proxy to different application.
 */
@RestController
@RequestMapping("api/proxy/{app}")
public class ProxyResource {

    private final Logger log = LoggerFactory.getLogger(ProxyResource.class);

    public final static String PROXY_URL = "proxyURL";
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private ProxyService proxyService;
    
    @Autowired
    private CacheService cacheService;
    
    @RequestMapping(method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> get(@PathVariable String app, @RequestParam(value=PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
    	if (log.isDebugEnabled())
    		log.debug("GET from app: " + app + " with proxyURL: " + url);
    	return process(HttpMethod.GET, null, app, url, request, response);
    }

    @RequestMapping(method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> post(@RequestBody JSONBody body, @PathVariable String app, @RequestParam(value=PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
    	if (log.isDebugEnabled())
    		log.debug("POST from app: " + app + " with proxyURL: " + url);
    	try {
        	return process(HttpMethod.POST, body, app, url, request, response);
		} catch (Exception e) {
			log.error("ERRORE post",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
    }

    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.ALL_VALUE)
    @RolesAllowed(AuthoritiesConstants.USER)    
    public ResponseEntity<String> put(@RequestBody JSONBody body, @PathVariable String app, @RequestParam(value=PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
    	if (log.isDebugEnabled())
    		log.debug("PUT from app: " + app + " with proxyURL: " + url);
    	try {
    		ResponseEntity<String> risposta = process(HttpMethod.PUT, body, app, url, request, response);

    		return risposta;
    	} catch (Exception e) {
			log.error("ERRORE put",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    	}
    }

    @RequestMapping(method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE) 
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<String> delete(@PathVariable String app, @RequestParam(value=PROXY_URL) String url, HttpServletRequest request, HttpServletResponse response) {
    	if (log.isDebugEnabled())
    		log.debug("DELETE from app: " + app + " with proxyURL: " + url);    	
    	return process(HttpMethod.DELETE, null, app, url, request, response);
    }
    
    private ResponseEntity<String> process(HttpMethod httpMethod, JSONBody body, String app, String url, HttpServletRequest request, HttpServletResponse response) {
    	try{
        	ResultProxy result = null;
    		ResultCacheProxy resultCacheProxy = cacheService.manageCache(url, body, app);
    		String existsClauseVariable = existsClauseVariable(resultCacheProxy);
    		if (isCacheable(resultCacheProxy)){
    			CallCache callCache = new CallCache(httpMethod, resultCacheProxy.getBody(), app, url, request.getQueryString(), request.getHeader(Costanti.HEADER_FOR_PROXY_AUTHORIZATION), resultCacheProxy.getRestService().getClasseJson());
    			result = proxyService.processInCache(callCache);
        	} else {
        		if (body != null && body.getContext() == null){
            		cacheService.setContext(body, app);
        		} else if (body == null && app != null && app.equals(Costanti.APP_SIGLA)){
        			body = new JSONBody();
            		cacheService.setContext(body, app);
        		}
            	result = proxyService.process(httpMethod, body, app, url, request.getQueryString(), request.getHeader(Costanti.HEADER_FOR_PROXY_AUTHORIZATION));
        	}
    		if (result.getStatus().compareTo(HttpStatus.OK) != 0){
                return JSONResponseEntity.getResponse(result.getStatus(), "");
    		}
        	response.setContentType(result.getType());
        	response.setStatus(result.getStatus().value());
    		String risposta = result.getBody();
    		CommonJsonRest<RestServiceBean> commonJsonRest = result.getCommonJsonResponse();

    		if (isCacheable(resultCacheProxy) && !app.equals(Costanti.APP_SIPER)){
    			if (existsClauseVariable.equals("S")){
    		    	risposta = cacheService.manageResponse(resultCacheProxy.getRestService(), resultCacheProxy.getListClausesDeleted(), commonJsonRest);
    			} else {
    				
    		    	try {
    					risposta = cacheService.createResponse(commonJsonRest);
    				} catch (JsonProcessingException e) {
    					log.error("ERRORE process",e);
    		            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    				}

    			}
    		}
    		risposta = manageResponseForAccountRest(url, result, risposta);
        	return JSONResponseEntity.ok(risposta);
		} catch (Exception e) {
			log.error("ERRORE ProxyResource",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
    }

	private String manageResponseForAccountRest(String url, ResultProxy result,
			String risposta) {
		String uid = SecurityUtils.getCurrentUserLogin();
		if (isAccountRest(url, uid)){
			String resp = accountService.manageResponseForAccountRest(uid, result.getBody());
			if (resp != null){
				log.info("Response for Account. Url: "+url+" - Resp: "+resp);
				return resp;
			} else {
				log.info("Response for Account. Url: "+url+" - Risposta: "+risposta);
				return risposta;
			}
		} else if (isAccountRestWithAnotherUSer(url)){
			String resp = accountService.manageResponseForAccountRest(result.getBody());
			if (resp != null){
				log.info("Response for Account With Another User. Url: "+url+" - Resp: "+resp);
				return resp;
			} else {
				log.info("Response for Account With Another User. Url: "+url+" - Risposta: "+risposta);
				return risposta;
			}
		}
		return risposta;
	}

	public String createResponseForAccountRest(String risposta,
			Account account, UsersSpecial user) {
		if (user != null){
			account.setAllUoForUsersSpecial(user.getAll());
			account.setUoForUsersSpecial(user.getUoForUsersSpecials());
			risposta = accountService.getBodyAccount(account);
		}
		return risposta;
	}

	private Boolean isAccountRest(String url, String uid){
		if (url.equals(Costanti.REST_ACCOUNT+uid)){
			return true;
		}
		return false;
	}
	private Boolean isAccountRestWithAnotherUSer(String url){
		if (url.startsWith(Costanti.REST_ACCOUNT)){
			return true;
		}
		return false;
	}
	private String existsClauseVariable(ResultCacheProxy resultCacheProxy) {
		String existsClauseVariable = "N";
		if (resultCacheProxy != null && resultCacheProxy.getListClausesDeleted() != null && !resultCacheProxy.getListClausesDeleted().isEmpty()){
			existsClauseVariable = "S";
		}
		return existsClauseVariable;
	}
	
    private Boolean isCacheable(ResultCacheProxy resultCacheProxy){
		if (resultCacheProxy != null && resultCacheProxy.isUrlToCache()){
			return true;
		}
		return false;
    }
}
