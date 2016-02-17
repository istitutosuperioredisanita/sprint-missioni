package it.cnr.si.missioni.util.proxy.json.object.sigla;

import it.cnr.si.missioni.util.DateUtils;

public class Context {
	private int esercizio = DateUtils.getCurrentYear();
	private String cd_unita_organizzativa;
	private String cd_cds;
	private String cd_cdr;
	
	public String getCd_unita_organizzativa() {
		return cd_unita_organizzativa;
	}
	public void setCd_unita_organizzativa(String cd_unita_organizzativa) {
		this.cd_unita_organizzativa = cd_unita_organizzativa;
	}
	public String getCd_cds() {
		return cd_cds;
	}
	public void setCd_cds(String cd_cds) {
		this.cd_cds = cd_cds;
	}
	public String getCd_cdr() {
		return cd_cdr;
	}
	public void setCd_cdr(String cd_cdr) {
		this.cd_cdr = cd_cdr;
	}
	public int getEsercizio() {
		return esercizio;
	}
	public void setEsercizio(int esercizio) {
		this.esercizio = esercizio;
	}
		
}
