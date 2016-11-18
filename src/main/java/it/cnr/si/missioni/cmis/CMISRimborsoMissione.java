package it.cnr.si.missioni.cmis;

public class CMISRimborsoMissione extends CMISMissione {
	private String idOrdineMissione;
	private String dataInizioEstero;
	private String dataFineEstero;
	public String getIdOrdineMissione() {
		return idOrdineMissione;
	}
	public void setIdOrdineMissione(String idOrdineMissione) {
		this.idOrdineMissione = idOrdineMissione;
	}
	public String getDataInizioEstero() {
		return dataInizioEstero;
	}
	public void setDataInizioEstero(String dataInizioEstero) {
		this.dataInizioEstero = dataInizioEstero;
	}
	public String getDataFineEstero() {
		return dataFineEstero;
	}
	public void setDataFineEstero(String dataFineEstero) {
		this.dataFineEstero = dataFineEstero;
	}

}
