package it.cnr.si.missioni.cmis;

import java.io.Serializable;

public class MessageForFlowRimborso extends MessageForFlow implements Serializable{
	String prop_bpm_sendEMailNotifications;
	String prop_cnrmissioni_dataInizioEstero;
	String prop_cnrmissioni_dataFineEstero;
	String prop_cnrmissioni_anticipoRicevuto;
	String prop_cnrmissioni_annoMandato;
	String prop_cnrmissioni_numeroMandatoOk;
	String prop_cnrmissioni_importoMandato;
	String prop_cnrmissioni_wfOrdineDaRimborso;
	String prop_cnrmissioni_differenzeOrdineRimborso;
	String prop_cnrmissioni_totaleRimborsoMissione;

		public String getProp_bpm_sendEMailNotifications() {
		return prop_bpm_sendEMailNotifications;
	}
	public void setProp_bpm_sendEMailNotifications(String prop_bpm_sendEMailNotifications) {
		this.prop_bpm_sendEMailNotifications = prop_bpm_sendEMailNotifications;
	}
	public String getProp_cnrmissioni_dataInizioEstero() {
		return prop_cnrmissioni_dataInizioEstero;
	}
	public void setProp_cnrmissioni_dataInizioEstero(String prop_cnrmissioni_dataInizioEstero) {
		this.prop_cnrmissioni_dataInizioEstero = prop_cnrmissioni_dataInizioEstero;
	}
	public String getProp_cnrmissioni_dataFineEstero() {
		return prop_cnrmissioni_dataFineEstero;
	}
	public void setProp_cnrmissioni_dataFineEstero(String prop_cnrmissioni_dataFineEstero) {
		this.prop_cnrmissioni_dataFineEstero = prop_cnrmissioni_dataFineEstero;
	}
	public String getProp_cnrmissioni_anticipoRicevuto() {
		return prop_cnrmissioni_anticipoRicevuto;
	}
	public void setProp_cnrmissioni_anticipoRicevuto(String prop_cnrmissioni_anticipoRicevuto) {
		this.prop_cnrmissioni_anticipoRicevuto = prop_cnrmissioni_anticipoRicevuto;
	}
	public String getProp_cnrmissioni_annoMandato() {
		return prop_cnrmissioni_annoMandato;
	}
	public void setProp_cnrmissioni_annoMandato(String prop_cnrmissioni_annoMandato) {
		this.prop_cnrmissioni_annoMandato = prop_cnrmissioni_annoMandato;
	}
	public String getProp_cnrmissioni_numeroMandatoOk() {
		return prop_cnrmissioni_numeroMandatoOk;
	}
	public void setProp_cnrmissioni_numeroMandatoOk(String prop_cnrmissioni_numeroMandatoOk) {
		this.prop_cnrmissioni_numeroMandatoOk = prop_cnrmissioni_numeroMandatoOk;
	}
	public String getProp_cnrmissioni_importoMandato() {
		return prop_cnrmissioni_importoMandato;
	}
	public void setProp_cnrmissioni_importoMandato(String prop_cnrmissioni_importoMandato) {
		this.prop_cnrmissioni_importoMandato = prop_cnrmissioni_importoMandato;
	}
	public String getProp_cnrmissioni_wfOrdineDaRimborso() {
		return prop_cnrmissioni_wfOrdineDaRimborso;
	}
	public void setProp_cnrmissioni_wfOrdineDaRimborso(String prop_cnrmissioni_wfOrdineDaRimborso) {
		this.prop_cnrmissioni_wfOrdineDaRimborso = prop_cnrmissioni_wfOrdineDaRimborso;
	}
	public String getProp_cnrmissioni_differenzeOrdineRimborso() {
		return prop_cnrmissioni_differenzeOrdineRimborso;
	}
	public void setProp_cnrmissioni_differenzeOrdineRimborso(String prop_cnrmissioni_differenzeOrdineRimborso) {
		this.prop_cnrmissioni_differenzeOrdineRimborso = prop_cnrmissioni_differenzeOrdineRimborso;
	}
	public String getProp_cnrmissioni_totaleRimborsoMissione() {
		return prop_cnrmissioni_totaleRimborsoMissione;
	}
	public void setProp_cnrmissioni_totaleRimborsoMissione(String prop_cnrmissioni_totaleRimborsoMissione) {
		this.prop_cnrmissioni_totaleRimborsoMissione = prop_cnrmissioni_totaleRimborsoMissione;
	}
	@Override
	public String toString() {
		return "MessageForFlowRimborso [prop_bpm_sendEMailNotifications=" + prop_bpm_sendEMailNotifications
				+ ", prop_cnrmissioni_dataInizioEstero=" + prop_cnrmissioni_dataInizioEstero
				+ ", prop_cnrmissioni_dataFineEstero=" + prop_cnrmissioni_dataFineEstero
				+ ", prop_cnrmissioni_anticipoRicevuto=" + prop_cnrmissioni_anticipoRicevuto
				+ ", prop_cnrmissioni_annoMandato=" + prop_cnrmissioni_annoMandato
				+ ", prop_cnrmissioni_numeroMandatoOk=" + prop_cnrmissioni_numeroMandatoOk
				+ ", prop_cnrmissioni_importoMandato=" + prop_cnrmissioni_importoMandato
				+ ", prop_cnrmissioni_wfOrdineDaRimborso=" + prop_cnrmissioni_wfOrdineDaRimborso
				+ ", prop_cnrmissioni_differenzeOrdineRimborso=" + prop_cnrmissioni_differenzeOrdineRimborso
				+ ", prop_cnrmissioni_totaleRimborsoMissione=" + prop_cnrmissioni_totaleRimborsoMissione
				+ ", assoc_bpm_assignee_added=" + assoc_bpm_assignee_added + ", assoc_bpm_assignee_removed="
				+ assoc_bpm_assignee_removed + ", prop_bpm_percentComplete=" + prop_bpm_percentComplete
				+ ", assoc_packageItems_added=" + assoc_packageItems_added + ", assoc_packageItems_removed="
				+ assoc_packageItems_removed + ", prop_cnrmissioni_noteAutorizzazioniAggiuntive="
				+ prop_cnrmissioni_noteAutorizzazioniAggiuntive + ", prop_cnrmissioni_missioneGratuita="
				+ prop_cnrmissioni_missioneGratuita + ", prop_cnrmissioni_descrizioneOrdine="
				+ prop_cnrmissioni_descrizioneOrdine + ", prop_cnrmissioni_note=" + prop_cnrmissioni_note
				+ ", prop_cnrmissioni_noteSegreteria=" + prop_cnrmissioni_noteSegreteria
				+ ", prop_bpm_workflowDescription=" + prop_bpm_workflowDescription + ", prop_bpm_workflowDueDate="
				+ prop_bpm_workflowDueDate + ", prop_bpm_status=" + prop_bpm_status + ", prop_wfcnr_groupName="
				+ prop_wfcnr_groupName + ", prop_wfcnr_wfCounterIndex=" + prop_wfcnr_wfCounterIndex
				+ ", prop_wfcnr_wfCounterId=" + prop_wfcnr_wfCounterId + ", prop_wfcnr_wfCounterAnno="
				+ prop_wfcnr_wfCounterAnno + ", prop_bpm_workflowPriority=" + prop_bpm_workflowPriority
				+ ", prop_cnrmissioni_validazioneSpesaFlag=" + prop_cnrmissioni_validazioneSpesaFlag
				+ ", prop_cnrmissioni_missioneConAnticipoFlag=" + prop_cnrmissioni_missioneConAnticipoFlag
				+ ", prop_cnrmissioni_validazioneModuloFlag=" + prop_cnrmissioni_validazioneModuloFlag
				+ ", prop_cnrmissioni_userNameUtenteOrdineMissione=" + prop_cnrmissioni_userNameUtenteOrdineMissione
				+ ", prop_cnrmissioni_userNameRichiedente=" + prop_cnrmissioni_userNameRichiedente
				+ ", prop_cnrmissioni_userNameResponsabileModulo=" + prop_cnrmissioni_userNameResponsabileModulo
				+ ", prop_cnrmissioni_userNamePrimoFirmatario=" + prop_cnrmissioni_userNamePrimoFirmatario
				+ ", prop_cnrmissioni_userNameFirmatarioSpesa=" + prop_cnrmissioni_userNameFirmatarioSpesa
				+ ", prop_cnrmissioni_userNameAmministrativo1=" + prop_cnrmissioni_userNameAmministrativo1
				+ ", prop_cnrmissioni_userNameAmministrativo2=" + prop_cnrmissioni_userNameAmministrativo2
				+ ", prop_cnrmissioni_userNameAmministrativo3=" + prop_cnrmissioni_userNameAmministrativo3
				+ ", prop_cnrmissioni_uoOrdine=" + prop_cnrmissioni_uoOrdine + ", prop_cnrmissioni_descrizioneUoOrdine="
				+ prop_cnrmissioni_descrizioneUoOrdine + ", prop_cnrmissioni_uoSpesa=" + prop_cnrmissioni_uoSpesa
				+ ", prop_cnrmissioni_descrizioneUoSpesa=" + prop_cnrmissioni_descrizioneUoSpesa
				+ ", prop_cnrmissioni_uoCompetenza=" + prop_cnrmissioni_uoCompetenza
				+ ", prop_cnrmissioni_descrizioneUoCompetenza=" + prop_cnrmissioni_descrizioneUoCompetenza
				+ ", prop_cnrmissioni_autoPropriaFlag=" + prop_cnrmissioni_autoPropriaFlag
				+ ", prop_cnrmissioni_noleggioFlag=" + prop_cnrmissioni_noleggioFlag + ", prop_cnrmissioni_taxiFlag="
				+ prop_cnrmissioni_taxiFlag + ", prop_cnrmissioni_servizioFlagOk=" + prop_cnrmissioni_servizioFlagOk
				+ ", prop_cnrmissioni_personaSeguitoFlagOk=" + prop_cnrmissioni_personaSeguitoFlagOk
				+ ", prop_cnrmissioni_capitolo=" + prop_cnrmissioni_capitolo + ", prop_cnrmissioni_descrizioneCapitolo="
				+ prop_cnrmissioni_descrizioneCapitolo + ", prop_cnrmissioni_modulo=" + prop_cnrmissioni_modulo
				+ ", prop_cnrmissioni_descrizioneModulo=" + prop_cnrmissioni_descrizioneModulo
				+ ", prop_cnrmissioni_gae=" + prop_cnrmissioni_gae + ", prop_cnrmissioni_descrizioneGae="
				+ prop_cnrmissioni_descrizioneGae + ", prop_cnrmissioni_impegnoAnnoResiduo="
				+ prop_cnrmissioni_impegnoAnnoResiduo + ", prop_cnrmissioni_impegnoAnnoCompetenza="
				+ prop_cnrmissioni_impegnoAnnoCompetenza + ", prop_cnrmissioni_impegnoNumeroOk="
				+ prop_cnrmissioni_impegnoNumeroOk + ", prop_cnrmissioni_descrizioneImpegno="
				+ prop_cnrmissioni_descrizioneImpegno + ", prop_cnrmissioni_importoMissione="
				+ prop_cnrmissioni_importoMissione + ", prop_cnrmissioni_disponibilita="
				+ prop_cnrmissioni_disponibilita + ", prop_cnrmissioni_missioneEsteraFlag="
				+ prop_cnrmissioni_missioneEsteraFlag + ", prop_cnrmissioni_destinazione="
				+ prop_cnrmissioni_destinazione + ", prop_cnrmissioni_dataInizioMissione="
				+ prop_cnrmissioni_dataInizioMissione + ", prop_cnrmissioni_dataFineMissione="
				+ prop_cnrmissioni_dataFineMissione + ", prop_cnrmissioni_trattamento=" + prop_cnrmissioni_trattamento
				+ ", prop_cnrmissioni_competenzaResiduo=" + prop_cnrmissioni_competenzaResiduo
				+ ", prop_cnrmissioni_autoPropriaAltriMotivi=" + prop_cnrmissioni_autoPropriaAltriMotivi
				+ ", prop_cnrmissioni_autoPropriaPrimoMotivo=" + prop_cnrmissioni_autoPropriaPrimoMotivo
				+ ", prop_cnrmissioni_autoPropriaSecondoMotivo=" + prop_cnrmissioni_autoPropriaSecondoMotivo
				+ ", prop_cnrmissioni_autoPropriaTerzoMotivo=" + prop_cnrmissioni_autoPropriaTerzoMotivo
				+ ", prop_bpm_comment=" + prop_bpm_comment + ", prop_wfcnr_reviewOutcome=" + prop_wfcnr_reviewOutcome
				+ ", prop_transitions=" + prop_transitions + "]";
	}
}
