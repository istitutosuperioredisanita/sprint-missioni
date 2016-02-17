package it.cnr.si.missioni.util.proxy.json;

public class JSONOrderBy {
	public JSONOrderBy(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	
	public JSONOrderBy() {
		super();
	}
	
	private String name;
	private String type;
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
