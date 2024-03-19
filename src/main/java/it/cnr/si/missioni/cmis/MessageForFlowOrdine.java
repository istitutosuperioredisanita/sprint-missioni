/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.cmis;

import java.io.Serializable;

public class MessageForFlowOrdine extends MessageForFlow implements Serializable {
    @Override
    public String toString() {
        return "MessageForFlowOrdine{" +
                "oggetto='" + oggetto + '\'' +
                ", linkToOtherWorkflows='" + linkToOtherWorkflows + '\'' +
                ", anno='" + annoMissione + '\'' +
                ", numero='" + numeroMissione + '\'' +
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
                ", taxiAltriMotivi='" + taxiAltriMotivi + '\'' +
                ", taxiPrimoMotivo='" + taxiPrimoMotivo + '\'' +
                ", taxiSecondoMotivo='" + taxiSecondoMotivo + '\'' +
                ", taxiTerzoMotivo='" + taxiTerzoMotivo + '\'' +
                ", taxiQuartoMotivo='" + taxiQuartoMotivo + '\'' +
                ", autoNoleggioPrimoMotivo='" + autoNoleggioPrimoMotivo + '\'' +
                ", autoNoleggioSecondoMotivo='" + autoNoleggioSecondoMotivo + '\'' +
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
