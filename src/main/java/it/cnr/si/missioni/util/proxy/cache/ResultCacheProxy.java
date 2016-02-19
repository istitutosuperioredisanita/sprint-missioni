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
