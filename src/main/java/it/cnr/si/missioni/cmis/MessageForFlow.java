package it.cnr.si.missioni.cmis;

import java.io.Serializable;

import it.cnr.si.missioni.util.proxy.json.JSONBody;

public class MessageForFlow extends JSONBody implements Serializable{
	String assoc_bpm_assignee_added;
	String assoc_bpm_assignee_removed;
	String prop_bpm_percentComplete;
	String assoc_packageItems_added;
	String assoc_packageItems_removed;
	String prop_cnrmissioni_noteAutorizzazioniAggiuntive;
	String prop_cnrmissioni_missioneGratuita;
	String prop_cnrmissioni_descrizioneOrdine;
	String prop_cnrmissioni_note;
	String prop_cnrmissioni_noteSegreteria;
	String prop_bpm_workflowDescription;
	String prop_bpm_workflowDueDate;
	String prop_bpm_status;
	String prop_wfcnr_groupName;
	String prop_wfcnr_wfCounterIndex;
	String prop_wfcnr_wfCounterId;
	String prop_wfcnr_wfCounterAnno;
	String prop_bpm_workflowPriority;
	String prop_cnrmissioni_validazioneSpesaFlag;
	String prop_cnrmissioni_missioneConAnticipoFlag;
	String prop_cnrmissioni_validazioneModuloFlag;
	String prop_cnrmissioni_userNameUtenteOrdineMissione;
	String prop_cnrmissioni_userNameRichiedente;
	String prop_cnrmissioni_userNameResponsabileModulo;
	String prop_cnrmissioni_userNamePrimoFirmatario;
	String prop_cnrmissioni_userNameFirmatarioSpesa;
	String prop_cnrmissioni_userNameAmministrativo1;
	String prop_cnrmissioni_userNameAmministrativo2;
	String prop_cnrmissioni_userNameAmministrativo3;
	String prop_cnrmissioni_uoOrdine;
	String prop_cnrmissioni_descrizioneUoOrdine;
	String prop_cnrmissioni_uoSpesa;
	String prop_cnrmissioni_descrizioneUoSpesa;
	String prop_cnrmissioni_uoCompetenza;
	String prop_cnrmissioni_descrizioneUoCompetenza;
	String prop_cnrmissioni_autoPropriaFlag;
	String prop_cnrmissioni_noleggioFlag;
	String prop_cnrmissioni_taxiFlag;
	String prop_cnrmissioni_servizioFlagOk;
	String prop_cnrmissioni_personaSeguitoFlagOk;
	String prop_cnrmissioni_capitolo;
	String prop_cnrmissioni_descrizioneCapitolo;
	String prop_cnrmissioni_modulo;
	String prop_cnrmissioni_descrizioneModulo;
	String prop_cnrmissioni_gae;
	String prop_cnrmissioni_descrizioneGae;
	String prop_cnrmissioni_impegnoAnnoResiduo;
	String prop_cnrmissioni_impegnoAnnoCompetenza;
	String prop_cnrmissioni_impegnoNumeroOk;
	String prop_cnrmissioni_descrizioneImpegno;
	String prop_cnrmissioni_importoMissione;
	String prop_cnrmissioni_disponibilita;
	String prop_cnrmissioni_missioneEsteraFlag;
	String prop_cnrmissioni_destinazione;
	String prop_cnrmissioni_dataInizioMissione;
	String prop_cnrmissioni_dataFineMissione;
	String prop_cnrmissioni_trattamento;
	String prop_cnrmissioni_competenzaResiduo;
	String prop_cnrmissioni_autoPropriaAltriMotivi;
	String prop_cnrmissioni_autoPropriaPrimoMotivo;
	String prop_cnrmissioni_autoPropriaSecondoMotivo;
	String prop_cnrmissioni_autoPropriaTerzoMotivo;
	String prop_bpm_comment;
	String prop_wfcnr_reviewOutcome;
	String prop_transitions;
	public String getAssoc_bpm_assignee_added() {
		return assoc_bpm_assignee_added;
	}
	public void setAssoc_bpm_assignee_added(String assoc_bpm_assignee_added) {
		this.assoc_bpm_assignee_added = assoc_bpm_assignee_added;
	}
	public String getAssoc_bpm_assignee_removed() {
		return assoc_bpm_assignee_removed;
	}
	public void setAssoc_bpm_assignee_removed(String assoc_bpm_assignee_removed) {
		this.assoc_bpm_assignee_removed = assoc_bpm_assignee_removed;
	}
	public String getProp_bpm_percentComplete() {
		return prop_bpm_percentComplete;
	}
	public void setProp_bpm_percentComplete(String prop_bpm_percentComplete) {
		this.prop_bpm_percentComplete = prop_bpm_percentComplete;
	}
	public String getAssoc_packageItems_added() {
		return assoc_packageItems_added;
	}
	public void setAssoc_packageItems_added(String assoc_packageItems_added) {
		this.assoc_packageItems_added = assoc_packageItems_added;
	}
	public String getAssoc_packageItems_removed() {
		return assoc_packageItems_removed;
	}
	public void setAssoc_packageItems_removed(String assoc_packageItems_removed) {
		this.assoc_packageItems_removed = assoc_packageItems_removed;
	}
	public String getProp_cnrmissioni_noteAutorizzazioniAggiuntive() {
		return prop_cnrmissioni_noteAutorizzazioniAggiuntive;
	}
	public void setProp_cnrmissioni_noteAutorizzazioniAggiuntive(String prop_cnrmissioni_noteAutorizzazioniAggiuntive) {
		this.prop_cnrmissioni_noteAutorizzazioniAggiuntive = prop_cnrmissioni_noteAutorizzazioniAggiuntive;
	}
	public String getProp_cnrmissioni_missioneGratuita() {
		return prop_cnrmissioni_missioneGratuita;
	}
	public void setProp_cnrmissioni_missioneGratuita(String prop_cnrmissioni_missioneGratuita) {
		this.prop_cnrmissioni_missioneGratuita = prop_cnrmissioni_missioneGratuita;
	}
	public String getProp_cnrmissioni_descrizioneOrdine() {
		return prop_cnrmissioni_descrizioneOrdine;
	}
	public void setProp_cnrmissioni_descrizioneOrdine(String prop_cnrmissioni_descrizioneOrdine) {
		this.prop_cnrmissioni_descrizioneOrdine = prop_cnrmissioni_descrizioneOrdine;
	}
	public String getProp_cnrmissioni_note() {
		return prop_cnrmissioni_note;
	}
	public void setProp_cnrmissioni_note(String prop_cnrmissioni_note) {
		this.prop_cnrmissioni_note = prop_cnrmissioni_note;
	}
	public String getProp_cnrmissioni_noteSegreteria() {
		return prop_cnrmissioni_noteSegreteria;
	}
	public void setProp_cnrmissioni_noteSegreteria(String prop_cnrmissioni_noteSegreteria) {
		this.prop_cnrmissioni_noteSegreteria = prop_cnrmissioni_noteSegreteria;
	}
	public String getProp_bpm_workflowDescription() {
		return prop_bpm_workflowDescription;
	}
	public void setProp_bpm_workflowDescription(String prop_bpm_workflowDescription) {
		this.prop_bpm_workflowDescription = prop_bpm_workflowDescription;
	}
	public String getProp_bpm_workflowDueDate() {
		return prop_bpm_workflowDueDate;
	}
	public void setProp_bpm_workflowDueDate(String prop_bpm_workflowDueDate) {
		this.prop_bpm_workflowDueDate = prop_bpm_workflowDueDate;
	}
	public String getProp_bpm_status() {
		return prop_bpm_status;
	}
	public void setProp_bpm_status(String prop_bpm_status) {
		this.prop_bpm_status = prop_bpm_status;
	}
	public String getProp_wfcnr_groupName() {
		return prop_wfcnr_groupName;
	}
	public void setProp_wfcnr_groupName(String prop_wfcnr_groupName) {
		this.prop_wfcnr_groupName = prop_wfcnr_groupName;
	}
	public String getProp_wfcnr_wfCounterIndex() {
		return prop_wfcnr_wfCounterIndex;
	}
	public void setProp_wfcnr_wfCounterIndex(String prop_wfcnr_wfCounterIndex) {
		this.prop_wfcnr_wfCounterIndex = prop_wfcnr_wfCounterIndex;
	}
	public String getProp_wfcnr_wfCounterId() {
		return prop_wfcnr_wfCounterId;
	}
	public void setProp_wfcnr_wfCounterId(String prop_wfcnr_wfCounterId) {
		this.prop_wfcnr_wfCounterId = prop_wfcnr_wfCounterId;
	}
	public String getProp_wfcnr_wfCounterAnno() {
		return prop_wfcnr_wfCounterAnno;
	}
	public void setProp_wfcnr_wfCounterAnno(String prop_wfcnr_wfCounterAnno) {
		this.prop_wfcnr_wfCounterAnno = prop_wfcnr_wfCounterAnno;
	}
	public String getProp_bpm_workflowPriority() {
		return prop_bpm_workflowPriority;
	}
	public void setProp_bpm_workflowPriority(String prop_bpm_workflowPriority) {
		this.prop_bpm_workflowPriority = prop_bpm_workflowPriority;
	}
	public String getProp_cnrmissioni_validazioneSpesaFlag() {
		return prop_cnrmissioni_validazioneSpesaFlag;
	}
	public void setProp_cnrmissioni_validazioneSpesaFlag(String prop_cnrmissioni_validazioneSpesaFlag) {
		this.prop_cnrmissioni_validazioneSpesaFlag = prop_cnrmissioni_validazioneSpesaFlag;
	}
	public String getProp_cnrmissioni_missioneConAnticipoFlag() {
		return prop_cnrmissioni_missioneConAnticipoFlag;
	}
	public void setProp_cnrmissioni_missioneConAnticipoFlag(String prop_cnrmissioni_missioneConAnticipoFlag) {
		this.prop_cnrmissioni_missioneConAnticipoFlag = prop_cnrmissioni_missioneConAnticipoFlag;
	}
	public String getProp_cnrmissioni_validazioneModuloFlag() {
		return prop_cnrmissioni_validazioneModuloFlag;
	}
	public void setProp_cnrmissioni_validazioneModuloFlag(String prop_cnrmissioni_validazioneModuloFlag) {
		this.prop_cnrmissioni_validazioneModuloFlag = prop_cnrmissioni_validazioneModuloFlag;
	}
	public String getProp_cnrmissioni_userNameUtenteOrdineMissione() {
		return prop_cnrmissioni_userNameUtenteOrdineMissione;
	}
	public void setProp_cnrmissioni_userNameUtenteOrdineMissione(String prop_cnrmissioni_userNameUtenteOrdineMissione) {
		this.prop_cnrmissioni_userNameUtenteOrdineMissione = prop_cnrmissioni_userNameUtenteOrdineMissione;
	}
	public String getProp_cnrmissioni_userNameRichiedente() {
		return prop_cnrmissioni_userNameRichiedente;
	}
	public void setProp_cnrmissioni_userNameRichiedente(String prop_cnrmissioni_userNameRichiedente) {
		this.prop_cnrmissioni_userNameRichiedente = prop_cnrmissioni_userNameRichiedente;
	}
	public String getProp_cnrmissioni_userNameResponsabileModulo() {
		return prop_cnrmissioni_userNameResponsabileModulo;
	}
	public void setProp_cnrmissioni_userNameResponsabileModulo(String prop_cnrmissioni_userNameResponsabileModulo) {
		this.prop_cnrmissioni_userNameResponsabileModulo = prop_cnrmissioni_userNameResponsabileModulo;
	}
	public String getProp_cnrmissioni_userNamePrimoFirmatario() {
		return prop_cnrmissioni_userNamePrimoFirmatario;
	}
	public void setProp_cnrmissioni_userNamePrimoFirmatario(String prop_cnrmissioni_userNamePrimoFirmatario) {
		this.prop_cnrmissioni_userNamePrimoFirmatario = prop_cnrmissioni_userNamePrimoFirmatario;
	}
	public String getProp_cnrmissioni_userNameFirmatarioSpesa() {
		return prop_cnrmissioni_userNameFirmatarioSpesa;
	}
	public void setProp_cnrmissioni_userNameFirmatarioSpesa(String prop_cnrmissioni_userNameFirmatarioSpesa) {
		this.prop_cnrmissioni_userNameFirmatarioSpesa = prop_cnrmissioni_userNameFirmatarioSpesa;
	}
	public String getProp_cnrmissioni_userNameAmministrativo1() {
		return prop_cnrmissioni_userNameAmministrativo1;
	}
	public void setProp_cnrmissioni_userNameAmministrativo1(String prop_cnrmissioni_userNameAmministrativo1) {
		this.prop_cnrmissioni_userNameAmministrativo1 = prop_cnrmissioni_userNameAmministrativo1;
	}
	public String getProp_cnrmissioni_userNameAmministrativo2() {
		return prop_cnrmissioni_userNameAmministrativo2;
	}
	public void setProp_cnrmissioni_userNameAmministrativo2(String prop_cnrmissioni_userNameAmministrativo2) {
		this.prop_cnrmissioni_userNameAmministrativo2 = prop_cnrmissioni_userNameAmministrativo2;
	}
	public String getProp_cnrmissioni_userNameAmministrativo3() {
		return prop_cnrmissioni_userNameAmministrativo3;
	}
	public void setProp_cnrmissioni_userNameAmministrativo3(String prop_cnrmissioni_userNameAmministrativo3) {
		this.prop_cnrmissioni_userNameAmministrativo3 = prop_cnrmissioni_userNameAmministrativo3;
	}
	public String getProp_cnrmissioni_uoOrdine() {
		return prop_cnrmissioni_uoOrdine;
	}
	public void setProp_cnrmissioni_uoOrdine(String prop_cnrmissioni_uoOrdine) {
		this.prop_cnrmissioni_uoOrdine = prop_cnrmissioni_uoOrdine;
	}
	public String getProp_cnrmissioni_descrizioneUoOrdine() {
		return prop_cnrmissioni_descrizioneUoOrdine;
	}
	public void setProp_cnrmissioni_descrizioneUoOrdine(String prop_cnrmissioni_descrizioneUoOrdine) {
		this.prop_cnrmissioni_descrizioneUoOrdine = prop_cnrmissioni_descrizioneUoOrdine;
	}
	public String getProp_cnrmissioni_uoSpesa() {
		return prop_cnrmissioni_uoSpesa;
	}
	public void setProp_cnrmissioni_uoSpesa(String prop_cnrmissioni_uoSpesa) {
		this.prop_cnrmissioni_uoSpesa = prop_cnrmissioni_uoSpesa;
	}
	public String getProp_cnrmissioni_descrizioneUoSpesa() {
		return prop_cnrmissioni_descrizioneUoSpesa;
	}
	public void setProp_cnrmissioni_descrizioneUoSpesa(String prop_cnrmissioni_descrizioneUoSpesa) {
		this.prop_cnrmissioni_descrizioneUoSpesa = prop_cnrmissioni_descrizioneUoSpesa;
	}
	public String getProp_cnrmissioni_uoCompetenza() {
		return prop_cnrmissioni_uoCompetenza;
	}
	public void setProp_cnrmissioni_uoCompetenza(String prop_cnrmissioni_uoCompetenza) {
		this.prop_cnrmissioni_uoCompetenza = prop_cnrmissioni_uoCompetenza;
	}
	public String getProp_cnrmissioni_descrizioneUoCompetenza() {
		return prop_cnrmissioni_descrizioneUoCompetenza;
	}
	public void setProp_cnrmissioni_descrizioneUoCompetenza(String prop_cnrmissioni_descrizioneUoCompetenza) {
		this.prop_cnrmissioni_descrizioneUoCompetenza = prop_cnrmissioni_descrizioneUoCompetenza;
	}
	public String getProp_cnrmissioni_autoPropriaFlag() {
		return prop_cnrmissioni_autoPropriaFlag;
	}
	public void setProp_cnrmissioni_autoPropriaFlag(String prop_cnrmissioni_autoPropriaFlag) {
		this.prop_cnrmissioni_autoPropriaFlag = prop_cnrmissioni_autoPropriaFlag;
	}
	public String getProp_cnrmissioni_noleggioFlag() {
		return prop_cnrmissioni_noleggioFlag;
	}
	public void setProp_cnrmissioni_noleggioFlag(String prop_cnrmissioni_noleggioFlag) {
		this.prop_cnrmissioni_noleggioFlag = prop_cnrmissioni_noleggioFlag;
	}
	public String getProp_cnrmissioni_taxiFlag() {
		return prop_cnrmissioni_taxiFlag;
	}
	public void setProp_cnrmissioni_taxiFlag(String prop_cnrmissioni_taxiFlag) {
		this.prop_cnrmissioni_taxiFlag = prop_cnrmissioni_taxiFlag;
	}
	public String getProp_cnrmissioni_servizioFlagOk() {
		return prop_cnrmissioni_servizioFlagOk;
	}
	public void setProp_cnrmissioni_servizioFlagOk(String prop_cnrmissioni_servizioFlagOk) {
		this.prop_cnrmissioni_servizioFlagOk = prop_cnrmissioni_servizioFlagOk;
	}
	public String getProp_cnrmissioni_personaSeguitoFlagOk() {
		return prop_cnrmissioni_personaSeguitoFlagOk;
	}
	public void setProp_cnrmissioni_personaSeguitoFlagOk(String prop_cnrmissioni_personaSeguitoFlagOk) {
		this.prop_cnrmissioni_personaSeguitoFlagOk = prop_cnrmissioni_personaSeguitoFlagOk;
	}
	public String getProp_cnrmissioni_capitolo() {
		return prop_cnrmissioni_capitolo;
	}
	public void setProp_cnrmissioni_capitolo(String prop_cnrmissioni_capitolo) {
		this.prop_cnrmissioni_capitolo = prop_cnrmissioni_capitolo;
	}
	public String getProp_cnrmissioni_descrizioneCapitolo() {
		return prop_cnrmissioni_descrizioneCapitolo;
	}
	public void setProp_cnrmissioni_descrizioneCapitolo(String prop_cnrmissioni_descrizioneCapitolo) {
		this.prop_cnrmissioni_descrizioneCapitolo = prop_cnrmissioni_descrizioneCapitolo;
	}
	public String getProp_cnrmissioni_modulo() {
		return prop_cnrmissioni_modulo;
	}
	public void setProp_cnrmissioni_modulo(String prop_cnrmissioni_modulo) {
		this.prop_cnrmissioni_modulo = prop_cnrmissioni_modulo;
	}
	public String getProp_cnrmissioni_descrizioneModulo() {
		return prop_cnrmissioni_descrizioneModulo;
	}
	public void setProp_cnrmissioni_descrizioneModulo(String prop_cnrmissioni_descrizioneModulo) {
		this.prop_cnrmissioni_descrizioneModulo = prop_cnrmissioni_descrizioneModulo;
	}
	public String getProp_cnrmissioni_gae() {
		return prop_cnrmissioni_gae;
	}
	public void setProp_cnrmissioni_gae(String prop_cnrmissioni_gae) {
		this.prop_cnrmissioni_gae = prop_cnrmissioni_gae;
	}
	public String getProp_cnrmissioni_descrizioneGae() {
		return prop_cnrmissioni_descrizioneGae;
	}
	public void setProp_cnrmissioni_descrizioneGae(String prop_cnrmissioni_descrizioneGae) {
		this.prop_cnrmissioni_descrizioneGae = prop_cnrmissioni_descrizioneGae;
	}
	public String getProp_cnrmissioni_impegnoAnnoResiduo() {
		return prop_cnrmissioni_impegnoAnnoResiduo;
	}
	public void setProp_cnrmissioni_impegnoAnnoResiduo(String prop_cnrmissioni_impegnoAnnoResiduo) {
		this.prop_cnrmissioni_impegnoAnnoResiduo = prop_cnrmissioni_impegnoAnnoResiduo;
	}
	public String getProp_cnrmissioni_impegnoAnnoCompetenza() {
		return prop_cnrmissioni_impegnoAnnoCompetenza;
	}
	public void setProp_cnrmissioni_impegnoAnnoCompetenza(String prop_cnrmissioni_impegnoAnnoCompetenza) {
		this.prop_cnrmissioni_impegnoAnnoCompetenza = prop_cnrmissioni_impegnoAnnoCompetenza;
	}
	public String getProp_cnrmissioni_impegnoNumeroOk() {
		return prop_cnrmissioni_impegnoNumeroOk;
	}
	public void setProp_cnrmissioni_impegnoNumeroOk(String prop_cnrmissioni_impegnoNumeroOk) {
		this.prop_cnrmissioni_impegnoNumeroOk = prop_cnrmissioni_impegnoNumeroOk;
	}
	public String getProp_cnrmissioni_descrizioneImpegno() {
		return prop_cnrmissioni_descrizioneImpegno;
	}
	public void setProp_cnrmissioni_descrizioneImpegno(String prop_cnrmissioni_descrizioneImpegno) {
		this.prop_cnrmissioni_descrizioneImpegno = prop_cnrmissioni_descrizioneImpegno;
	}
	public String getProp_cnrmissioni_importoMissione() {
		return prop_cnrmissioni_importoMissione;
	}
	public void setProp_cnrmissioni_importoMissione(String prop_cnrmissioni_importoMissione) {
		this.prop_cnrmissioni_importoMissione = prop_cnrmissioni_importoMissione;
	}
	public String getProp_cnrmissioni_disponibilita() {
		return prop_cnrmissioni_disponibilita;
	}
	public void setProp_cnrmissioni_disponibilita(String prop_cnrmissioni_disponibilita) {
		this.prop_cnrmissioni_disponibilita = prop_cnrmissioni_disponibilita;
	}
	public String getProp_cnrmissioni_missioneEsteraFlag() {
		return prop_cnrmissioni_missioneEsteraFlag;
	}
	public void setProp_cnrmissioni_missioneEsteraFlag(String prop_cnrmissioni_missioneEsteraFlag) {
		this.prop_cnrmissioni_missioneEsteraFlag = prop_cnrmissioni_missioneEsteraFlag;
	}
	public String getProp_cnrmissioni_destinazione() {
		return prop_cnrmissioni_destinazione;
	}
	public void setProp_cnrmissioni_destinazione(String prop_cnrmissioni_destinazione) {
		this.prop_cnrmissioni_destinazione = prop_cnrmissioni_destinazione;
	}
	public String getProp_cnrmissioni_dataInizioMissione() {
		return prop_cnrmissioni_dataInizioMissione;
	}
	public void setProp_cnrmissioni_dataInizioMissione(String prop_cnrmissioni_dataInizioMissione) {
		this.prop_cnrmissioni_dataInizioMissione = prop_cnrmissioni_dataInizioMissione;
	}
	public String getProp_cnrmissioni_dataFineMissione() {
		return prop_cnrmissioni_dataFineMissione;
	}
	public void setProp_cnrmissioni_dataFineMissione(String prop_cnrmissioni_dataFineMissione) {
		this.prop_cnrmissioni_dataFineMissione = prop_cnrmissioni_dataFineMissione;
	}
	public String getProp_cnrmissioni_trattamento() {
		return prop_cnrmissioni_trattamento;
	}
	public void setProp_cnrmissioni_trattamento(String prop_cnrmissioni_trattamento) {
		this.prop_cnrmissioni_trattamento = prop_cnrmissioni_trattamento;
	}
	public String getProp_cnrmissioni_competenzaResiduo() {
		return prop_cnrmissioni_competenzaResiduo;
	}
	public void setProp_cnrmissioni_competenzaResiduo(String prop_cnrmissioni_competenzaResiduo) {
		this.prop_cnrmissioni_competenzaResiduo = prop_cnrmissioni_competenzaResiduo;
	}
	public String getProp_cnrmissioni_autoPropriaAltriMotivi() {
		return prop_cnrmissioni_autoPropriaAltriMotivi;
	}
	public void setProp_cnrmissioni_autoPropriaAltriMotivi(String prop_cnrmissioni_autoPropriaAltriMotivi) {
		this.prop_cnrmissioni_autoPropriaAltriMotivi = prop_cnrmissioni_autoPropriaAltriMotivi;
	}
	public String getProp_cnrmissioni_autoPropriaPrimoMotivo() {
		return prop_cnrmissioni_autoPropriaPrimoMotivo;
	}
	public void setProp_cnrmissioni_autoPropriaPrimoMotivo(String prop_cnrmissioni_autoPropriaPrimoMotivo) {
		this.prop_cnrmissioni_autoPropriaPrimoMotivo = prop_cnrmissioni_autoPropriaPrimoMotivo;
	}
	public String getProp_cnrmissioni_autoPropriaSecondoMotivo() {
		return prop_cnrmissioni_autoPropriaSecondoMotivo;
	}
	public void setProp_cnrmissioni_autoPropriaSecondoMotivo(String prop_cnrmissioni_autoPropriaSecondoMotivo) {
		this.prop_cnrmissioni_autoPropriaSecondoMotivo = prop_cnrmissioni_autoPropriaSecondoMotivo;
	}
	public String getProp_cnrmissioni_autoPropriaTerzoMotivo() {
		return prop_cnrmissioni_autoPropriaTerzoMotivo;
	}
	public void setProp_cnrmissioni_autoPropriaTerzoMotivo(String prop_cnrmissioni_autoPropriaTerzoMotivo) {
		this.prop_cnrmissioni_autoPropriaTerzoMotivo = prop_cnrmissioni_autoPropriaTerzoMotivo;
	}
	public String getProp_bpm_comment() {
		return prop_bpm_comment;
	}
	public void setProp_bpm_comment(String prop_bpm_comment) {
		this.prop_bpm_comment = prop_bpm_comment;
	}
	public String getProp_wfcnr_reviewOutcome() {
		return prop_wfcnr_reviewOutcome;
	}
	public void setProp_wfcnr_reviewOutcome(String prop_wfcnr_reviewOutcome) {
		this.prop_wfcnr_reviewOutcome = prop_wfcnr_reviewOutcome;
	}
	public String getProp_transitions() {
		return prop_transitions;
	}
	public void setProp_transitions(String prop_transitions) {
		this.prop_transitions = prop_transitions;
	}
}
