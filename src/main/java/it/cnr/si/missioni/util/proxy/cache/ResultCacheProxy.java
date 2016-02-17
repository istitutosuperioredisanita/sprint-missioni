package it.cnr.si.missioni.util.proxy.cache;

import it.cnr.si.missioni.util.proxy.cache.json.RestService;
import it.cnr.si.missioni.util.proxy.json.JSONClause;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.RestServiceBean;

import java.util.List;

public class ResultCacheProxy {
	List<JSONClause> listClausesDeleted;
	RestService restService;
	String body;
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
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

}
