package it.cnr.si.missioni.util.proxy.cache;

import it.cnr.si.missioni.util.proxy.json.JSONBody;

import java.io.Serializable;

import org.springframework.http.HttpMethod;

public class CallCache implements Serializable {
	private HttpMethod httpMethod;
	private JSONBody body;
	private String app;
	private String classeJson;
	public CallCache(HttpMethod httpMethod, JSONBody body, String app,
			String url, String queryString, String authorization, String classeJson) {
		super();
		this.httpMethod = httpMethod;
		this.body = body;
		this.app = app;
		this.url = url;
		this.queryString = queryString;
		this.authorization = authorization;
		this.classeJson = classeJson;
	}
	private String url;
	private String queryString;
	private String authorization;
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
	public JSONBody getBody() {
		return body;
	}
	public void setBody(JSONBody body) {
		this.body = body;
	}
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getAuthorization() {
		return authorization;
	}
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
	public String getClasseJson() {
		return classeJson;
	}
	public void setClasseJson(String classeJson) {
		this.classeJson = classeJson;
	}
	@Override
	public String toString() {
		return "CallCache [httpMethod=" + httpMethod + ", body=" + body.toString()
				+ ", app=" + app + ", classeJson=" + classeJson + ", url="
				+ url + ", queryString=" + queryString + ", authorization="
				+ authorization + "]";
	}
}
