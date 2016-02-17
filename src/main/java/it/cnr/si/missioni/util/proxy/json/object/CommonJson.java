package it.cnr.si.missioni.util.proxy.json.object;

public abstract class CommonJson {
	private Integer totalNumItems;
	private Integer maxItemsPerPage;
	private Integer activePage;
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
}
