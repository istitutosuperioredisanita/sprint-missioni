package it.cnr.si.missioni.util.proxy.json.object.print;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Param {

	@JsonProperty("key")
	private Key key;

	@JsonProperty("valoreParam")
	private String valoreParam;

	@JsonProperty("paramType")
	private String paramType;

	/**
	 *
	 * @return
	 * The key
	 */
	public Key getKey() {
		return key;
	}

	/**
	 *
	 * @param key
	 * The key
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 *
	 * @return
	 * The valoreParam
	 */
	public String getValoreParam() {
		return valoreParam;
	}

	/**
	 *
	 * @param valoreParam
	 * The valoreParam
	 */
	public void setValoreParam(String valoreParam) {
		this.valoreParam = valoreParam;
	}

	public String getParamType() {
		return paramType;
	}

	public void setParamType(String paramType) {
		this.paramType = paramType;
	}

}
