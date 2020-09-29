package it.cnr.si.missioni.cmis;

import java.math.BigDecimal;

public class CMISMissione {
	private String nomeFile;
	private String anno;
	private String numero;
	private String oggetto;
	private String note;
	private String noteSegreteria;
	private String wfDescription;
	private String wfDescriptionComplete;
	private String wfDueDate;
	private String priorita;

	public String getWfDescriptionComplete() {
		return wfDescriptionComplete;
	}

	public void setWfDescriptionComplete(String wfDescriptionComplete) {
		this.wfDescriptionComplete = wfDescriptionComplete;
	}

	private String validazioneSpesa;
	private String usernameUtenteOrdine;
	private String usernameRichiedente;
	private String userNameResponsabileModulo;
	private String userNamePrimoFirmatario;
	private String userNameFirmatarioSpesa;
	private String uoOrdine;
	private String descrizioneUoOrdine;
	private String uoSpesa;
	private String uoCompetenza;
	private String descrizioneUoSpesa;
	private String descrizioneUoCompetenza;
	private String autoPropriaFlag;
	private String autoServizioFlag;
	private String personaSeguitoFlag;
	private String noleggioFlag;
	private String taxiFlag;
	private String capitolo;
	private String descrizioneCapitolo;
	private String trattamento;
	private String gae;
	private String descrizioneGae;
	private String destinazione;
	private String dataInizioMissione;
	private String dataFineMissione;
	private String missioneEsteraFlag;
	private Long impegnoAnnoResiduo;
	private Long impegnoAnnoCompetenza;
	private Long impegnoNumero;
	private String descrizioneImpegno;
	private BigDecimal importoMissione;
	private BigDecimal disponibilita;
	private String noteAutorizzazioniAggiuntive;

	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getWfDescription() {
		return wfDescription;
	}
	public void setWfDescription(String wfDescription) {
		this.wfDescription = wfDescription;
	}
	public String getWfDueDate() {
		return wfDueDate;
	}
	public void setWfDueDate(String wfDueDate) {
		this.wfDueDate = wfDueDate;
	}
	public String getPriorita() {
		return priorita;
	}
	public void setPriorita(String priorita) {
		this.priorita = priorita;
	}
	public String getValidazioneSpesa() {
		return validazioneSpesa;
	}
	public void setValidazioneSpesa(String validazioneSpesa) {
		this.validazioneSpesa = validazioneSpesa;
	}
	public String getUsernameUtenteOrdine() {
		return usernameUtenteOrdine;
	}
	public void setUsernameUtenteOrdine(String usernameUtenteOrdine) {
		this.usernameUtenteOrdine = usernameUtenteOrdine;
	}
	public String getUsernameRichiedente() {
		return usernameRichiedente;
	}
	public void setUsernameRichiedente(String usernameRichiedente) {
		this.usernameRichiedente = usernameRichiedente;
	}
	public String getUserNameResponsabileModulo() {
		return userNameResponsabileModulo;
	}
	public void setUserNameResponsabileModulo(String userNameResponsabileModulo) {
		this.userNameResponsabileModulo = userNameResponsabileModulo;
	}
	public String getUserNamePrimoFirmatario() {
		return userNamePrimoFirmatario;
	}
	public void setUserNamePrimoFirmatario(String userNamePrimoFirmatario) {
		this.userNamePrimoFirmatario = userNamePrimoFirmatario;
	}
	public String getUserNameFirmatarioSpesa() {
		return userNameFirmatarioSpesa;
	}
	public void setUserNameFirmatarioSpesa(String userNameFirmatarioSpesa) {
		this.userNameFirmatarioSpesa = userNameFirmatarioSpesa;
	}
	public String getUoOrdine() {
		return uoOrdine;
	}
	public void setUoOrdine(String uoOrdine) {
		this.uoOrdine = uoOrdine;
	}
	public String getDescrizioneUoOrdine() {
		return descrizioneUoOrdine;
	}
	public void setDescrizioneUoOrdine(String descrizioneUoOrdine) {
		this.descrizioneUoOrdine = descrizioneUoOrdine;
	}
	public String getUoSpesa() {
		return uoSpesa;
	}
	public void setUoSpesa(String uoSpesa) {
		this.uoSpesa = uoSpesa;
	}
	public String getDescrizioneUoSpesa() {
		return descrizioneUoSpesa;
	}
	public void setDescrizioneUoSpesa(String descrizioneUoSpesa) {
		this.descrizioneUoSpesa = descrizioneUoSpesa;
	}
	public String getAutoPropriaFlag() {
		return autoPropriaFlag;
	}
	public void setAutoPropriaFlag(String autoPropriaFlag) {
		this.autoPropriaFlag = autoPropriaFlag;
	}
	public String getNoleggioFlag() {
		return noleggioFlag;
	}
	public void setNoleggioFlag(String noleggioFlag) {
		this.noleggioFlag = noleggioFlag;
	}
	public String getTaxiFlag() {
		return taxiFlag;
	}
	public void setTaxiFlag(String taxiFlag) {
		this.taxiFlag = taxiFlag;
	}
	public String getCapitolo() {
		return capitolo;
	}
	public void setCapitolo(String capitolo) {
		this.capitolo = capitolo;
	}
	public String getDescrizioneCapitolo() {
		return descrizioneCapitolo;
	}
	public void setDescrizioneCapitolo(String descrizioneCapitolo) {
		this.descrizioneCapitolo = descrizioneCapitolo;
	}
	public String getGae() {
		return gae;
	}
	public void setGae(String gae) {
		this.gae = gae;
	}
	public String getDescrizioneGae() {
		return descrizioneGae;
	}
	public void setDescrizioneGae(String descrizioneGae) {
		this.descrizioneGae = descrizioneGae;
	}
	public Long getImpegnoAnnoResiduo() {
		return impegnoAnnoResiduo;
	}
	public void setImpegnoAnnoResiduo(Long impegnoAnnoResiduo) {
		this.impegnoAnnoResiduo = impegnoAnnoResiduo;
	}
	public Long getImpegnoAnnoCompetenza() {
		return impegnoAnnoCompetenza;
	}
	public void setImpegnoAnnoCompetenza(Long impegnoAnnoCompetenza) {
		this.impegnoAnnoCompetenza = impegnoAnnoCompetenza;
	}
	public Long getImpegnoNumero() {
		return impegnoNumero;
	}
	public void setImpegnoNumero(Long impegnoNumero) {
		this.impegnoNumero = impegnoNumero;
	}
	public String getDescrizioneImpegno() {
		return descrizioneImpegno;
	}
	public void setDescrizioneImpegno(String descrizioneImpegno) {
		this.descrizioneImpegno = descrizioneImpegno;
	}
	public BigDecimal getImportoMissione() {
		return importoMissione;
	}
	public void setImportoMissione(BigDecimal importoMissione) {
		this.importoMissione = importoMissione;
	}
	public BigDecimal getDisponibilita() {
		return disponibilita;
	}
	public void setDisponibilita(BigDecimal disponibilita) {
		this.disponibilita = disponibilita;
	}
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getDestinazione() {
		return destinazione;
	}
	public void setDestinazione(String destinazione) {
		this.destinazione = destinazione;
	}
	public String getDataInizioMissione() {
		return dataInizioMissione;
	}
	public void setDataInizioMissione(String dataInizioMissione) {
		this.dataInizioMissione = dataInizioMissione;
	}
	public String getDataFineMissione() {
		return dataFineMissione;
	}
	public void setDataFineMissione(String dataFineMissione) {
		this.dataFineMissione = dataFineMissione;
	}
	public String getMissioneEsteraFlag() {
		return missioneEsteraFlag;
	}
	public void setMissioneEsteraFlag(String missioneEsteraFlag) {
		this.missioneEsteraFlag = missioneEsteraFlag;
	}
	public String getUoCompetenza() {
		return uoCompetenza;
	}
	public void setUoCompetenza(String uoCompetenza) {
		this.uoCompetenza = uoCompetenza;
	}
	public String getDescrizioneUoCompetenza() {
		return descrizioneUoCompetenza;
	}
	public void setDescrizioneUoCompetenza(String descrizioneUoCompetenza) {
		this.descrizioneUoCompetenza = descrizioneUoCompetenza;
	}
	public String getTrattamento() {
		return trattamento;
	}
	public void setTrattamento(String trattamento) {
		this.trattamento = trattamento;
	}
	public String getAutoServizioFlag() {
		return autoServizioFlag;
	}
	public void setAutoServizioFlag(String autoServizioFlag) {
		this.autoServizioFlag = autoServizioFlag;
	}
	public String getPersonaSeguitoFlag() {
		return personaSeguitoFlag;
	}
	public void setPersonaSeguitoFlag(String personaSeguitoFlag) {
		this.personaSeguitoFlag = personaSeguitoFlag;
	}
	public String getNoteSegreteria() {
		return noteSegreteria;
	}
	public void setNoteSegreteria(String noteSegreteria) {
		this.noteSegreteria = noteSegreteria;
	}
	public String getNoteAutorizzazioniAggiuntive() {
		return noteAutorizzazioniAggiuntive;
	}
	public void setNoteAutorizzazioniAggiuntive(String noteAutorizzazioniAggiuntive) {
		this.noteAutorizzazioniAggiuntive = noteAutorizzazioniAggiuntive;
	}
	public String getNomeFile() {
		return nomeFile;
	}
	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}
}
