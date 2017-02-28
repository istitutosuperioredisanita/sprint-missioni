package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class MandatoJson extends CommonJsonRest<Mandato> implements Serializable {
	List<Mandato> elements;
	public List<Mandato> getElements() {
		return elements;
	}
	public void setElements(List<Mandato> elements) {
		this.elements = elements;
	}
}
