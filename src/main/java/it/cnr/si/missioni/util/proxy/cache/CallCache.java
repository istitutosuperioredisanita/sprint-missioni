package it.cnr.si.missioni.util.proxy.cache;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONBody;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

public class CallCache implements DataSerializable {
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
		String jsonBody = null;
    	try {
    		ObjectMapper mapper = new ObjectMapper();
    		jsonBody = mapper.writeValueAsString(body);
    	} catch (Exception ex) {
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella manipolazione del file JSON per la preparazione del body della richiesta REST ("+Utility.getMessageException(ex)+").");
    	}
		return "CallCache [httpMethod=" + httpMethod + ", body=" + jsonBody.toString()
				+ ", app=" + app + ", classeJson=" + classeJson + ", url="
				+ url + ", queryString=" + queryString + ", authorization="
				+ authorization + "]";
	}
	public String getMd5() {
		String objectToString = toString();
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance("MD5");
		    m.update(objectToString.getBytes(),0,objectToString.length());
		    return new BigInteger(1,m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			new AwesomeException("Errore nel recupero dell'algoritmo MD5 "+e.getMessage());
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
