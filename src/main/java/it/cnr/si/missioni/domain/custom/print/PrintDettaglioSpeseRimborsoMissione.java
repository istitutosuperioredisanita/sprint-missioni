package it.cnr.si.missioni.domain.custom.print;


public class PrintDettaglioSpeseRimborsoMissione {

	private String data;
	private String tipologiaSpesa;
	private String divisa;
	private String importo;
	private String kmPercorsi;
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getTipologiaSpesa() {
		return tipologiaSpesa;
	}
	public void setTipologiaSpesa(String tipologiaSpesa) {
		this.tipologiaSpesa = tipologiaSpesa;
	}
	public String getDivisa() {
		return divisa;
	}
	public void setDivisa(String divisa) {
		this.divisa = divisa;
	}
	public String getImporto() {
		return importo;
	}
	public void setImporto(String importo) {
		this.importo = importo;
	}
	public String getKmPercorsi() {
		return kmPercorsi;
	}
	public void setKmPercorsi(String kmPercorsi) {
		this.kmPercorsi = kmPercorsi;
	}


}