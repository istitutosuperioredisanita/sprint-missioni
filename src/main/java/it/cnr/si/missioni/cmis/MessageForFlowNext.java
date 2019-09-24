package it.cnr.si.missioni.cmis;

import java.io.Serializable;

import it.cnr.si.missioni.util.proxy.json.JSONBody;

public class MessageForFlowNext extends JSONBody implements Serializable{
	private String next;

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

}
