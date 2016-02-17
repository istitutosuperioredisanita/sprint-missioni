package it.cnr.si.missioni.util.proxy.json.object;

import java.util.List;

public abstract class CommonJsonRest<T extends RestServiceBean> {
	private Integer totalNumItems;
	private Integer maxItemsPerPage;
	private Integer activePage;
	private List<T> elements;
	public Integer getTotalNumItems() {
		return totalNumItems;
	}
	public void setTotalNumItems(Integer totalNumItems) {
		this.totalNumItems = totalNumItems;
	}
	public Integer getMaxItemsPerPage() {
		return maxItemsPerPage;
	}
	public void setMaxItemsPerPage(Integer maxItemsPerPage) {
		this.maxItemsPerPage = maxItemsPerPage;
	}
	public Integer getActivePage() {
		return activePage;
	}
	public void setActivePage(Integer activePage) {
		this.activePage = activePage;
	}
	public List<T> getElements() {
		return elements;
	}
	public void setElements(List<T> elements) {
		this.elements = elements;
	}
}
