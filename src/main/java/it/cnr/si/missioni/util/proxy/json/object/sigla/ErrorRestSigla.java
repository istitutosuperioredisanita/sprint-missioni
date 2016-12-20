package it.cnr.si.missioni.util.proxy.json.object.sigla;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorRestSigla {
	private String error;

	@JsonProperty("ERROR")
	public String getError() {
		return error;
	}

	/**
	 *
	 * @param codiceSede
	 * The codice_sede
	 */
	@JsonProperty("ERROR")
	public void setError(String error) {
		this.error = error;
	}
	
}
