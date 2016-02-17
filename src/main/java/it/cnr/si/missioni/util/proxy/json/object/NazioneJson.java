package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class NazioneJson extends CommonJsonRest<Nazione> implements Serializable {
	List<Nazione> elements;
	public List<Nazione> getElements() {
		return elements;
	}
	public void setElements(List<Nazione> elements) {
		this.elements = elements;
	}
}
