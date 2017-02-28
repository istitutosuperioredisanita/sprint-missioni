package it.cnr.si.missioni.util.proxy.json;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.cnr.si.missioni.util.proxy.json.object.sigla.Context;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONSIGLABody implements Cloneable, Serializable {
	private Context context;

	public JSONSIGLABody() {
		super();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
