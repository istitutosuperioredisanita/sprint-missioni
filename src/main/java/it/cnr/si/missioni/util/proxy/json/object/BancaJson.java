package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class BancaJson extends CommonJsonRest<Banca> implements Serializable {
	List<Banca> elements;
	public List<Banca> getElements() {
		return elements;
	}
	public void setElements(List<Banca> elements) {
		this.elements = elements;
	}
}
