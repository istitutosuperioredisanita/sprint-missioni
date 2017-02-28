package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class TerzoJson extends  CommonJsonRest<Terzo> implements Serializable {
	private List<Terzo> elements;
	public List<Terzo> getElements() {
		return elements;
	}
	public void setElements(List<Terzo> elements) {
		this.elements = elements;
	}
}
