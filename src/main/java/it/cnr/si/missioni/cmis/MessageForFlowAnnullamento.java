package it.cnr.si.missioni.cmis;

import java.io.Serializable;

public class MessageForFlowAnnullamento extends MessageForFlowOrdine implements Serializable{
	private String wfOrdineDaRevoca;

	public String getIdMissioneRevoca() {
		return idMissioneRevoca;
	}

	public void setIdMissioneRevoca(String idMissioneRevoca) {
		this.idMissioneRevoca = idMissioneRevoca;
	}

	public String getWfOrdineDaRevoca() {
		return wfOrdineDaRevoca;
	}

	public void setWfOrdineDaRevoca(String wfOrdineDaRevoca) {
		this.wfOrdineDaRevoca = wfOrdineDaRevoca;
	}

	String idMissioneRevoca;

	@Override
	public String toString() {
		return "MessageForFlowAnnullamento{" +
				"wfOrdineDaRevoca='" + wfOrdineDaRevoca + '\'' +
				", noteAutorizzazioniAggiuntive='" + noteAutorizzazioniAggiuntive + '\'' +
				", missioneGratuita='" + missioneGratuita + '\'' +
				", descrizioneOrdine='" + descrizioneOrdine + '\'' +
				", note='" + note + '\'' +
				", noteSegreteria='" + noteSegreteria + '\'' +
				", bpm_workflowDueDate='" + bpm_workflowDueDate + '\'' +
				", bpm_workflowPriority='" + bpm_workflowPriority + '\'' +
				", validazioneSpesaFlag='" + validazioneSpesaFlag + '\'' +
				", missioneConAnticipoFlag='" + missioneConAnticipoFlag + '\'' +
				", validazioneModuloFlag='" + validazioneModuloFlag + '\'' +
				", userNameUtenteOrdineMissione='" + userNameUtenteMissione + '\'' +
				", userNameRichiedente='" + userNameRichiedente + '\'' +
				", userNameResponsabileModulo='" + userNameResponsabileModulo + '\'' +
				", userNamePrimoFirmatario='" + userNamePrimoFirmatario + '\'' +
				", userNameFirmatarioSpesa='" + userNameFirmatarioSpesa + '\'' +
				", userNameAmministrativo1='" + userNameAmministrativo1 + '\'' +
				", userNameAmministrativo2='" + userNameAmministrativo2 + '\'' +
				", userNameAmministrativo3='" + userNameAmministrativo3 + '\'' +
				", uoOrdine='" + uoRich + '\'' +
				", descrizioneUoOrdine='" + descrizioneUoRich + '\'' +
				", uoSpesa='" + uoSpesa + '\'' +
				", descrizioneUoSpesa='" + descrizioneUoSpesa + '\'' +
				", uoCompetenza='" + uoCompetenza + '\'' +
				", descrizioneUoCompetenza='" + descrizioneUoCompetenza + '\'' +
				", autoPropriaFlag='" + autoPropriaFlag + '\'' +
				", noleggioFlag='" + noleggioFlag + '\'' +
				", taxiFlag='" + taxiFlag + '\'' +
				", servizioFlagOk='" + servizioFlagOk + '\'' +
				", personaSeguitoFlagOk='" + personaSeguitoFlagOk + '\'' +
				", capitolo='" + capitolo + '\'' +
				", descrizioneCapitolo='" + descrizioneCapitolo + '\'' +
				", progetto='" + progetto + '\'' +
				", descrizioneProgetto='" + descrizioneProgetto + '\'' +
				", gae='" + gae + '\'' +
				", descrizioneGae='" + descrizioneGae + '\'' +
				", impegnoAnnoResiduo='" + impegnoAnnoResiduo + '\'' +
				", impegnoAnnoCompetenza='" + impegnoAnnoCompetenza + '\'' +
				", impegnoNumeroOk='" + impegnoNumeroOk + '\'' +
				", descrizioneImpegno='" + descrizioneImpegno + '\'' +
				", importoMissione='" + importoMissione + '\'' +
				", disponibilita='" + disponibilita + '\'' +
				", missioneEsteraFlag='" + missioneEsteraFlag + '\'' +
				", destinazione='" + destinazione + '\'' +
				", dataInizioMissione='" + dataInizioMissione + '\'' +
				", dataFineMissione='" + dataFineMissione + '\'' +
				", trattamento='" + trattamento + '\'' +
				", competenzaResiduo='" + competenzaResiduo + '\'' +
				", autoPropriaAltriMotivi='" + autoPropriaAltriMotivi + '\'' +
				", autoPropriaPrimoMotivo='" + autoPropriaPrimoMotivo + '\'' +
				", autoPropriaSecondoMotivo='" + autoPropriaSecondoMotivo + '\'' +
				", autoPropriaTerzoMotivo='" + autoPropriaTerzoMotivo + '\'' +
				", pathFascicoloDocumenti='" + pathFascicoloDocumenti + '\'' +
				", titolo='" + titolo + '\'' +
				", descrizione='" + descrizione + '\'' +
				", gruppoFirmatarioUo='" + gruppoFirmatarioUo + '\'' +
				", gruppoFirmatarioSpesa='" + gruppoFirmatarioSpesa + '\'' +
				", idStrutturaUoMissioni='" + idStrutturaUoMissioni + '\'' +
				", idStrutturaSpesaMissioni='" + idStrutturaSpesaMissioni + '\'' +
				'}';
	}
}
