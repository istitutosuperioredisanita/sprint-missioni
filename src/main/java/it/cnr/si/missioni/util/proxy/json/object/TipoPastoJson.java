package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class TipoPastoJson extends  CommonJsonRest<TipoPasto> implements Serializable {
	List<TipoPasto> elements;
	public List<TipoPasto> getElements() {
		return elements;
	}
	public void setElements(List<TipoPasto> elements) {
		this.elements = elements;
	}
}
