package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class VoceJson extends  CommonJsonRest<Voce> implements Serializable {
	private List<Voce> elements;
	public List<Voce> getElements() {
		return elements;
	}
	public void setElements(List<Voce> elements) {
		this.elements = elements;
	}
}
