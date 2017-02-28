package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class InquadramentoJson extends  CommonJsonRest<Inquadramento> implements Serializable {
	private List<Inquadramento> elements;
	public List<Inquadramento> getElements() {
		return elements;
	}
	public void setElements(List<Inquadramento> elements) {
		this.elements = elements;
	}
}
