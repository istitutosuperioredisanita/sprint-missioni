package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class TipoSpesaJson extends  CommonJsonRest<TipoSpesa> implements Serializable {
	List<TipoSpesa> elements;
	public List<TipoSpesa> getElements() {
		return elements;
	}
	public void setElements(List<TipoSpesa> elements) {
		this.elements = elements;
	}
}
