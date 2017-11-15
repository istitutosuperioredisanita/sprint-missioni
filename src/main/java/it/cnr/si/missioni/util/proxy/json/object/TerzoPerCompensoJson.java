package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class TerzoPerCompensoJson extends  CommonJsonRest<TerzoPerCompenso> implements Serializable {
	private List<TerzoPerCompenso> elements;
	public List<TerzoPerCompenso> getElements() {
		return elements;
	}
	public void setElements(List<TerzoPerCompenso> elements) {
		this.elements = elements;
	}
}
