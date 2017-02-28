package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class DivisaJson extends  CommonJsonRest<Divisa> implements Serializable {
	List<Divisa> elements;
	public List<Divisa> getElements() {
		return elements;
	}
	public void setElements(List<Divisa> elements) {
		this.elements = elements;
	}
}
