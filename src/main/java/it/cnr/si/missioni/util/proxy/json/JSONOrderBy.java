package it.cnr.si.missioni.util.proxy.json;

import java.io.Serializable;

public class JSONOrderBy implements Serializable{
	private String name;
	private String type;
	public JSONOrderBy(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public JSONOrderBy() {
		super();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
