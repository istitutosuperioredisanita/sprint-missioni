package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class ProgettoJson extends CommonJsonRest<Progetto> implements Serializable {
	private List<Progetto> elements;
	public List<Progetto> getElements() {
		return elements;
	}
	public void setElements(List<Progetto> elements) {
		this.elements = elements;
	}
}
