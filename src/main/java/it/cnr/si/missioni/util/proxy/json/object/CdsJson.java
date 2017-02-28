package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class CdsJson extends CommonJsonRest<Cds> implements Serializable {
	private List<Cds> elements;
	public List<Cds> getElements() {
		return elements;
	}
	public void setElements(List<Cds> elements) {
		this.elements = elements;
	}
}
