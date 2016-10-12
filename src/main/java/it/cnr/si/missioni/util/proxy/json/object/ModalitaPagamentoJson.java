package it.cnr.si.missioni.util.proxy.json.object;

import java.io.Serializable;
import java.util.List;

public class ModalitaPagamentoJson extends CommonJsonRest<ModalitaPagamento> implements Serializable {
	List<ModalitaPagamento> elements;
	public List<ModalitaPagamento> getElements() {
		return elements;
	}
	public void setElements(List<ModalitaPagamento> elements) {
		this.elements = elements;
	}
}
