package it.cnr.si.missioni.web.filter;

import java.util.Date;

public class OrdineMissioneFilter {
	private String user;
	private String stato;
	private Integer anno;
	private Long daId;
	private Long aId;
	private Long daNumero;
	private Long aNumero;
	private Date daData;
	private Date aData;
	private String cdsRich;
	private String uoRich;
	private String toFinal;
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
	public Date getDaData() {
		return daData;
	}
	public void setDaData(Date daData) {
		this.daData = daData;
	}
	public Date getaData() {
		return aData;
	}
	public void setaData(Date aData) {
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
}
