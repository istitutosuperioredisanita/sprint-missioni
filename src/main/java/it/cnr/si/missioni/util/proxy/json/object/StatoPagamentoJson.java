package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class StatoPagamentoJson extends CommonJsonRest<StatoPagamento> implements Serializable {
	private List<StatoPagamento> elements;
	public List<StatoPagamento> getElements() {
		return elements;
	}
	public void setElements(List<StatoPagamento> elements) {
		this.elements = elements;
	}
}
