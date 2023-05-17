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

public class MessageForFlowRimborso extends MessageForFlow implements Serializable {
    String bpm_sendEMailNotifications;
    String dataInizioEstero;
    String dataFineEstero;
    String anticipoRicevuto;
    String annoMandato;
    String numeroMandatoOk;
    String importoMandato;
    String differenzeOrdineRimborso;
    String totaleRimborsoMissione;
    String idMissioneRimborso;

    public String getIdMissioneRimborso() {
        return idMissioneRimborso;
    }

    public void setIdMissioneRimborso(String idMissioneRimborso) {
        this.idMissioneRimborso = idMissioneRimborso;
    }

    public String getBpm_sendEMailNotifications() {
        return bpm_sendEMailNotifications;
    }

    public void setBpm_sendEMailNotifications(String bpm_sendEMailNotifications) {
        this.bpm_sendEMailNotifications = bpm_sendEMailNotifications;
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

    public String getNumeroMandatoOk() {
        return numeroMandatoOk;
    }

    public void setNumeroMandatoOk(String numeroMandatoOk) {
        this.numeroMandatoOk = numeroMandatoOk;
    }

    public String getImportoMandato() {
        return importoMandato;
    }

    public void setImportoMandato(String importoMandato) {
        this.importoMandato = importoMandato;
    }

    public String getDifferenzeOrdineRimborso() {
        return differenzeOrdineRimborso;
    }

    public void setDifferenzeOrdineRimborso(String differenzeOrdineRimborso) {
        this.differenzeOrdineRimborso = differenzeOrdineRimborso;
    }

    public String getTotaleRimborsoMissione() {
        return totaleRimborsoMissione;
    }

    public void setTotaleRimborsoMissione(String totaleRimborsoMissione) {
        this.totaleRimborsoMissione = totaleRimborsoMissione;
    }

    @Override
    public String toString() {
        return "MessageForFlowRimborso{" +
                "bpm_sendEMailNotifications='" + bpm_sendEMailNotifications + '\'' +
                ", dataInizioEstero='" + dataInizioEstero + '\'' +
                ", dataFineEstero='" + dataFineEstero + '\'' +
                ", anticipoRicevuto='" + anticipoRicevuto + '\'' +
                ", annoMandato='" + annoMandato + '\'' +
                ", numeroMandatoOk='" + numeroMandatoOk + '\'' +
                ", importoMandato='" + importoMandato + '\'' +
                ", linkToOtherWorkflows='" + linkToOtherWorkflows + '\'' +
                ", differenzeOrdineRimborso='" + differenzeOrdineRimborso + '\'' +
                ", totaleRimborsoMissione='" + totaleRimborsoMissione + '\'' +
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
                ", modulo='" + progetto + '\'' +
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
