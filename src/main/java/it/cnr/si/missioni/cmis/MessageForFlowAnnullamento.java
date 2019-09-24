package it.cnr.si.missioni.cmis;

import java.io.Serializable;

public class MessageForFlowAnnullamento extends MessageForFlowOrdine implements Serializable{
	private String prop_cnrmissioni_wfOrdineDaRevoca;

	public String getProp_cnrmissioni_wfOrdineDaRevoca() {
		return prop_cnrmissioni_wfOrdineDaRevoca;
	}

	public void setProp_cnrmissioni_wfOrdineDaRevoca(String prop_cnrmissioni_wfOrdineDaRevoca) {
		this.prop_cnrmissioni_wfOrdineDaRevoca = prop_cnrmissioni_wfOrdineDaRevoca;
	}

	@Override
	public String toString() {
		return "MessageForFlowAnnullamento [prop_cnrmissioni_wfOrdineDaRevoca=" + prop_cnrmissioni_wfOrdineDaRevoca
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
