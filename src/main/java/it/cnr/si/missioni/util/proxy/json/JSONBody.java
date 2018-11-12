package it.cnr.si.missioni.util.proxy.json;

import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneBulk;
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
	String data;
	Long nazione;
	Long inquadramento;
	Long idRimborsoMissione;
	String cdTipoSpesa;
	String cdTipoPasto;
	String divisa;
	String km;
	String importoSpesa;
	MissioneBulk missioneBulk;
	
	public JSONBody() {
		super();
	}

	public JSONBody(Integer activePage, Integer maxItemsPerPage,
			List<JSONOrderBy> orderBy, List<JSONClause> clauses,String data, Long nazione,
			Long inquadramento, String cdTipoSpesa,
			String cdTipoPasto, String divisa,
			String km, String importoSpesa, MissioneBulk missioneBulk, Long idRimborsoMissione) {
		super();
		this.activePage = activePage;
		this.maxItemsPerPage = maxItemsPerPage;
		this.orderBy = orderBy;
		this.clauses = clauses;
		this.data = data;
		this.nazione = nazione;
		this.inquadramento = inquadramento;
		this.cdTipoSpesa = cdTipoSpesa;
		this.cdTipoPasto = cdTipoPasto;
		this.divisa = divisa;
		this.km = km;
		this.importoSpesa = importoSpesa;
		this.missioneBulk = missioneBulk;
		this.idRimborsoMissione = idRimborsoMissione;
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

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Long getNazione() {
		return nazione;
	}

	public void setNazione(Long nazione) {
		this.nazione = nazione;
	}

	public Long getInquadramento() {
		return inquadramento;
	}

	public void setInquadramento(Long inquadramento) {
		this.inquadramento = inquadramento;
	}

	public String getCdTipoSpesa() {
		return cdTipoSpesa;
	}

	public void setCdTipoSpesa(String cdTipoSpesa) {
		this.cdTipoSpesa = cdTipoSpesa;
	}

	public String getCdTipoPasto() {
		return cdTipoPasto;
	}

	public void setCdTipoPasto(String cdTipoPasto) {
		this.cdTipoPasto = cdTipoPasto;
	}

	public String getDivisa() {
		return divisa;
	}

	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}

	public String getKm() {
		return km;
	}

	public void setKm(String km) {
		this.km = km;
	}

	public String getImportoSpesa() {
		return importoSpesa;
	}

	public void setImportoSpesa(String importoSpesa) {
		this.importoSpesa = importoSpesa;
	}

	public MissioneBulk getMissioneBulk() {
		return missioneBulk;
	}

	public void setMissioneBulk(MissioneBulk missioneBulk) {
		this.missioneBulk = missioneBulk;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Long getIdRimborsoMissione() {
		return idRimborsoMissione;
	}

	public void setIdRimborsoMissione(Long idRimborsoMissione) {
		this.idRimborsoMissione = idRimborsoMissione;
	}


}
