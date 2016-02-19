package it.cnr.si.missioni.util.proxy.json;

import it.cnr.si.missioni.util.proxy.json.object.sigla.Context;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSONBody implements Cloneable, Serializable{
	private Integer activePage;
	private Integer maxItemsPerPage;
	private List<JSONOrderBy> orderBy;
	private List<JSONClause> clauses;
	private Context context;
	
	public JSONBody() {
		super();
	}

	public JSONBody(Integer activePage, Integer maxItemsPerPage,
			List<JSONOrderBy> orderBy, List<JSONClause> clauses) {
		super();
		this.activePage = activePage;
		this.maxItemsPerPage = maxItemsPerPage;
		this.orderBy = orderBy;
		this.clauses = clauses;
	}

	public Integer getActivePage() {
		return activePage;
	}
	public void setActivePage(Integer activePage) {
		this.activePage = activePage;
	}
	public Integer getMaxItemsPerPage() {
		return maxItemsPerPage;
	}
	public void setMaxItemsPerPage(Integer maxItemsPerPage) {
		this.maxItemsPerPage = maxItemsPerPage;
	}
	public List<JSONOrderBy> getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(List<JSONOrderBy> orderBy) {
		this.orderBy = orderBy;
	}
	public List<JSONClause> getClauses() {
		return clauses;
	}
	public void setClauses(List<JSONClause> clauses) {
		this.clauses = clauses;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
}
