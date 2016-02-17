package it.cnr.si.missioni.util.proxy.json.object;

import java.util.List;

public class ImpegnoJson extends CommonJson{
	List<Impegno> elements;
	public List<Impegno> getElements() {
		return elements;
	}
	public void setElements(List<Impegno> elements) {
		this.elements = elements;
	}
}
