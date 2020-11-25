package it.cnr.si.missioni.cmis;

import java.math.BigDecimal;

public class CMISRimborsoMissione extends CMISMissione {
	private Long idMissioneRimborso;
	private String wfOrdineMissione;
	private String idOrdineMissione;
	private String dataInizioEstero;
	private String dataFineEstero;
	private String anticipoRicevuto;
	private String annoMandato;

	public Long getIdMissioneRimborso() {
		return idMissioneRimborso;
	}

	public void setIdMissioneRimborso(Long idMissioneRimborso) {
		this.idMissioneRimborso = idMissioneRimborso;
	}

	private String numeroMandato;
	private String importoMandato;
	private String differenzeOrdineRimborso;
	private BigDecimal totaleRimborsoMissione;
	public String getWfOrdineMissione() {
		return wfOrdineMissione;
	}
	public void setWfOrdineMissione(String wfOrdineMissione) {
		this.wfOrdineMissione = wfOrdineMissione;
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
	public String getAnticipoRicevuto() {
		return anticipoRicevuto;
	}
	public void setAnticipoRicevuto(String anticipoRicevuto) {
		this.anticipoRicevuto = anticipoRicevuto;
	}
	public String getAnnoMandato() {
		return annoMandato;
	}
	public void setAnnoMandato(String annoMandato) {
		this.annoMandato = annoMandato;
	}
	public String getNumeroMandato() {
		return numeroMandato;
	}
	public void setNumeroMandato(String numeroMandato) {
		this.numeroMandato = numeroMandato;
	}
	public String getImportoMandato() {
		return importoMandato;
	}
	public void setImportoMandato(String importoMandato) {
		this.importoMandato = importoMandato;
	}
	public String getIdOrdineMissione() {
		return idOrdineMissione;
	}
	public void setIdOrdineMissione(String idOrdineMissione) {
		this.idOrdineMissione = idOrdineMissione;
	}
	public BigDecimal getTotaleRimborsoMissione() {
		return totaleRimborsoMissione;
	}
	public void setTotaleRimborsoMissione(BigDecimal totaleRimborsoMissione) {
		this.totaleRimborsoMissione = totaleRimborsoMissione;
	}
	public String getDifferenzeOrdineRimborso() {
		return differenzeOrdineRimborso;
	}
	public void setDifferenzeOrdineRimborso(String differenzeOrdineRimborso) {
		this.differenzeOrdineRimborso = differenzeOrdineRimborso;
	}

}
