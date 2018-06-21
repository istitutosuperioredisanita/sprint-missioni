package it.cnr.si.missioni.util.proxy.json.object.print;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Key {

	@JsonProperty("nomeParam")
	private String nomeParam;
	
	/**
	 *
	 * @return
	 * The nomeParam
	 */
	public String getNomeParam() {
		return nomeParam;
	}

	/**
	 *
	 * @param nomeParam
	 * The nomeParam
	 */
	public void setNomeParam(String nomeParam) {
		this.nomeParam = nomeParam;
	}


}
