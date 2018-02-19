package it.cnr.si.missioni.web.filter;

import java.util.List;

import it.cnr.si.missioni.util.Utility;

public class MissioneFilter {
	private List<String> listaStatiMissione;
	private List<String> listaStatiFlussoMissione;
	private String user;
	private String stato;
	private String statoFlusso;
	private String validato;
	private String giaRimborsato;
	private String daAnnullare;
	private Integer anno;
	private Long daId;
	private Long aId;
	private Boolean soloMissioniNonGratuite = false;
	private String includiMissioniAnnullate;
	private String respGruppo;
	private Long daNumero;
	private Long aNumero;
	private String daData;
	private String aData;
	private String cdsRich;
	private String uoRich;
	private String toFinal;
	private String daCron;
	public Long getDaNumero() {
		return daNumero;
	}
	public void setDaNumero(Long daNumero) {
		this.daNumero = daNumero;
	}
	public Long getaNumero() {
		return aNumero;
	}
	public void setaNumero(Long aNumero) {
		this.aNumero = aNumero;
	}
	public String getCdsRich() {
		return cdsRich;
	}
	public void setCdsRich(String cdsRich) {
		this.cdsRich = cdsRich;
	}
	public String getDaData() {
		return daData;
	}
	public void setDaData(String daData) {
		this.daData = daData;
	}
	public String getaData() {
		return aData;
	}
	public void setaData(String aData) {
		this.aData = aData;
	}
	public Integer getAnno() {
		return anno;
	}
	public void setAnno(Integer anno) {
		this.anno = anno;
	}
	public String getUoRich() {
		return uoRich;
	}
	public void setUoRich(String uoRich) {
		this.uoRich = uoRich;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Long getDaId() {
		return daId;
	}
	public void setDaId(Long daId) {
		this.daId = daId;
	}
	public Long getaId() {
		return aId;
	}
	public void setaId(Long aId) {
		this.aId = aId;
	}
	public String getToFinal() {
		return toFinal;
	}
	public void setToFinal(String toFinal) {
		this.toFinal = toFinal;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public String getStatoFlusso() {
		return statoFlusso;
	}
	public void setStatoFlusso(String statoFlusso) {
		this.statoFlusso = statoFlusso;
	}
	public String getValidato() {
		return validato;
	}
	public void setValidato(String validato) {
		this.validato = validato;
	}
	public List<String> getListaStatiMissione() {
		return listaStatiMissione;
	}
	public void setListaStatiMissione(List<String> listaStatiMissione) {
		this.listaStatiMissione = listaStatiMissione;
	}
	public List<String> getListaStatiFlussoMissione() {
		return listaStatiFlussoMissione;
	}
	public void setListaStatiFlussoMissione(List<String> listaStatiFlussoMissione) {
		this.listaStatiFlussoMissione = listaStatiFlussoMissione;
	}
	public String getDaCron() {
		return daCron;
	}
	public void setDaCron(String daCron) {
		this.daCron = daCron;
	}
	public Boolean getSoloMissioniNonGratuite() {
		return soloMissioniNonGratuite;
	}
	public void setSoloMissioniNonGratuite(Boolean soloMissioniNonGratuite) {
		this.soloMissioniNonGratuite = soloMissioniNonGratuite;
	}
	public String getIncludiMissioniAnnullate() {
		return includiMissioniAnnullate;
	}
	public void setIncludiMissioniAnnullate(String includiMissioniAnnullate) {
		this.includiMissioniAnnullate = includiMissioniAnnullate;
	}
	public String getGiaRimborsato() {
		return giaRimborsato;
	}
	public void setGiaRimborsato(String giaRimborsato) {
		this.giaRimborsato = giaRimborsato;
	}
	public Boolean isDaCron(){
		return Utility.nvl(getDaCron(), "N").equals("S");
	}
	public String getDaAnnullare() {
		return daAnnullare;
	}
	public void setDaAnnullare(String daAnnullare) {
		this.daAnnullare = daAnnullare;
	}
	public String getRespGruppo() {
		return respGruppo;
	}
	public void setRespGruppo(String respGruppo) {
		this.respGruppo = respGruppo;
	}
}
