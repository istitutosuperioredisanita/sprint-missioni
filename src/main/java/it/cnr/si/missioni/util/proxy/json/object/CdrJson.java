package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class CdrJson extends CommonJsonRest<Cdr> implements Serializable {
	List<Cdr> elements;
	public List<Cdr> getElements() {
		return elements;
	}
	public void setElements(List<Cdr> elements) {
		this.elements = elements;
	}
}
