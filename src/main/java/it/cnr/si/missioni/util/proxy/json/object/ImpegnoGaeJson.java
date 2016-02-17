package it.cnr.si.missioni.util.proxy.json.object;

import java.util.List;

public class ImpegnoGaeJson extends CommonJson{
	List<ImpegnoGae> elements;
	public List<ImpegnoGae> getElements() {
		return elements;
	}
	public void setElements(List<ImpegnoGae> elements) {
		this.elements = elements;
	}
}
