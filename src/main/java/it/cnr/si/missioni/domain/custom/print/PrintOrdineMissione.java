package it.cnr.si.missioni.domain.custom.print;


public class PrintOrdineMissione extends PrintMissione{

    private String pgObbligazione;

    private String esercizioOriginaleObbligazione;

    private String obbligoRientro;

	private String partenzaDa;

    private String distanzaDallaSede;

    private String note;

    private String priorita;

    private String importoPresunto;

    private String utilizzoTaxi;

	private String utilizzoAutoNoleggio;

	private String noteUtilizzoTaxiNoleggio;
			
	public String getObbligoRientro() {
		return obbligoRientro;
	}

	public void setObbligoRientro(String obbligoRientro) {
		this.obbligoRientro = obbligoRientro;
	}

	public String getPartenzaDa() {
		return partenzaDa;
	}

	public void setPartenzaDa(String partenzaDa) {
		this.partenzaDa = partenzaDa;
	}

	public String getDistanzaDallaSede() {
		return distanzaDallaSede;
	}

	public void setDistanzaDallaSede(String distanzaDallaSede) {
		this.distanzaDallaSede = distanzaDallaSede;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getPriorita() {
		return priorita;
	}

	public void setPriorita(String priorita) {
		this.priorita = priorita;
	}

	public String getImportoPresunto() {
		return importoPresunto;
	}

	public void setImportoPresunto(String importoPresunto) {
		this.importoPresunto = importoPresunto;
	}

	public String getUtilizzoTaxi() {
		return utilizzoTaxi;
	}

	public void setUtilizzoTaxi(String utilizzoTaxi) {
		this.utilizzoTaxi = utilizzoTaxi;
	}

	public String getUtilizzoAutoNoleggio() {
		return utilizzoAutoNoleggio;
	}

	public void setUtilizzoAutoNoleggio(String utilizzoAutoNoleggio) {
		this.utilizzoAutoNoleggio = utilizzoAutoNoleggio;
	}

	public String getNoteUtilizzoTaxiNoleggio() {
		return noteUtilizzoTaxiNoleggio;
	}

	public void setNoteUtilizzoTaxiNoleggio(String noteUtilizzoTaxiNoleggio) {
		this.noteUtilizzoTaxiNoleggio = noteUtilizzoTaxiNoleggio;
	} 

    public String getPgObbligazione() {
		return pgObbligazione;
	}

	public void setPgObbligazione(String pgObbligazione) {
		this.pgObbligazione = pgObbligazione;
	}

	public String getEsercizioOriginaleObbligazione() {
		return esercizioOriginaleObbligazione;
	}

	public void setEsercizioOriginaleObbligazione(String esercizioOriginaleObbligazione) {
		this.esercizioOriginaleObbligazione = esercizioOriginaleObbligazione;
	}

}
