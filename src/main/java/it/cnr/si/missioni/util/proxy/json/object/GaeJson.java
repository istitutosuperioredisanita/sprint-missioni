package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class GaeJson extends  CommonJsonRest<Gae> implements Serializable {
	List<Gae> elements;
	public List<Gae> getElements() {
		return elements;
	}
	public void setElements(List<Gae> elements) {
		this.elements = elements;
	}
}
