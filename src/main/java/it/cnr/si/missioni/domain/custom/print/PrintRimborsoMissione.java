package it.cnr.si.missioni.domain.custom.print;

import java.util.ArrayList;
import java.util.List;

public class PrintRimborsoMissione extends PrintMissione{
    private String dataInizioEstero;

    private String dataFineEstero;

    private String modpag;

    private String conto;

    private String iban;

    private String anticipo;

    private String importoMandato;

    private String numeroMandato;

    private String annoMandato;

    private String altreSpese;

    private String altreSpeseImporto;

    private String speseTerzi;

    private String speseTerziImporto;

    private String cdTerzo;

    private String totMissione;

	private List<PrintRimborsoMissioneDettagli> printDettagliSpeseRimborsoMissione = new ArrayList<PrintRimborsoMissioneDettagli>();

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

	public String getModpag() {
		return modpag;
	}

	public void setModpag(String modpag) {
		this.modpag = modpag;
	}

	public String getConto() {
		return conto;
	}

	public void setConto(String conto) {
		this.conto = conto;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getAnticipo() {
		return anticipo;
	}

	public void setAnticipo(String anticipo) {
		this.anticipo = anticipo;
	}

	public String getImportoMandato() {
		return importoMandato;
	}

	public void setImportoMandato(String importoMandato) {
		this.importoMandato = importoMandato;
	}

	public String getNumeroMandato() {
		return numeroMandato;
	}

	public void setNumeroMandato(String numeroMandato) {
		this.numeroMandato = numeroMandato;
	}

	public String getAnnoMandato() {
		return annoMandato;
	}

	public void setAnnoMandato(String annoMandato) {
		this.annoMandato = annoMandato;
	}

	public String getAltreSpese() {
		return altreSpese;
	}

	public void setAltreSpese(String altreSpese) {
		this.altreSpese = altreSpese;
	}

	public String getAltreSpeseImporto() {
		return altreSpeseImporto;
	}

	public void setAltreSpeseImporto(String altreSpeseImporto) {
		this.altreSpeseImporto = altreSpeseImporto;
	}

	public String getSpeseTerzi() {
		return speseTerzi;
	}

	public void setSpeseTerzi(String speseTerzi) {
		this.speseTerzi = speseTerzi;
	}

	public String getSpeseTerziImporto() {
		return speseTerziImporto;
	}

	public void setSpeseTerziImporto(String speseTerziImporto) {
		this.speseTerziImporto = speseTerziImporto;
	}

	public String getCdTerzo() {
		return cdTerzo;
	}

	public void setCdTerzo(String cdTerzo) {
		this.cdTerzo = cdTerzo;
	}

	public List<PrintRimborsoMissioneDettagli> getPrintDettagliSpeseRimborsoMissione() {
		return printDettagliSpeseRimborsoMissione;
	}

	public void setPrintDettagliSpeseRimborsoMissione(
			List<PrintRimborsoMissioneDettagli> printDettagliSpeseRimborsoMissione) {
		this.printDettagliSpeseRimborsoMissione = printDettagliSpeseRimborsoMissione;
	}

	public String getTotMissione() {
		return totMissione;
	}

	public void setTotMissione(String totMissione) {
		this.totMissione = totMissione;
	}
}
