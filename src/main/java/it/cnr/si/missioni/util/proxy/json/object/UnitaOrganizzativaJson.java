package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class UnitaOrganizzativaJson extends CommonJsonRest<UnitaOrganizzativa> implements Serializable {
	List<UnitaOrganizzativa> elements;

	@Override
	public List<UnitaOrganizzativa> getElements() {
		return elements;
	}
	
	@Override
	public void setElements(List<UnitaOrganizzativa> elements) {
		this.elements = elements;
	}
}
