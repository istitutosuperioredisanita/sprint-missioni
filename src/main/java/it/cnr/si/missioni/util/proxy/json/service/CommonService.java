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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

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
		if (restInCache != null){
			List<JSONClause> listaNewClauses = null;
			if (restInCache.getClauseFixed() != null && !restInCache.getClauseFixed().isEmpty()){
				listaNewClauses = new ArrayList<JSONClause>();
				for (JSONClause clause : clauses){
					boolean existsClause = false;
					if (cacheService.existsClause(restInCache.getClauseFixed(), clause)){
						existsClause = true;
					}
					if (!existsClause){
						listaNewClauses.add(clause);
					}
				}
			} else {
				listaNewClauses = clauses;
			}

			CallCache callCache = cacheService.prepareCallCache(restInCache, clausesToAdd);
			ResultProxy result = proxyService.processInCache(callCache);
			CommonJsonRest<RestServiceBean> commonJsonRest = result.getCommonJsonResponse();
			risposta = cacheService.manageResponse(restInCache, listaNewClauses, commonJsonRest);
		} else {
			JSONBody jBody = null;
			jBody = proxyService.inizializzaJson();
			jBody.setClauses(clauses);
			cacheService.setContext(jBody, app);
			ResultProxy result = proxyService.process(HttpMethod.POST, jBody, app, url, "proxyURL="+url, null);
			risposta = result.getBody();
		}
		return risposta;
	}
}
