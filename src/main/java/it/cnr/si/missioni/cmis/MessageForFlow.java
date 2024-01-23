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

import it.cnr.si.missioni.util.proxy.json.JSONBody;

import java.io.Serializable;

public class MessageForFlow extends JSONBody implements Serializable {
    public final static String TIPOLOGIA_MISSIONE_ORDINE = "ordine";
    public final static String TIPOLOGIA_MISSIONE_REVOCA = "revoca";
    public final static String TIPOLOGIA_MISSIONE_RIMBORSO = "rimborso";

    String idMissione;
    String idMissioneOrdine;
    String oggetto;
    String annoMissione;
    String numeroMissione;
    String noteAutorizzazioniAggiuntive;
    String missioneGratuita;
    String descrizioneOrdine;
    String note;
    String noteSegreteria;
    String bpm_workflowDueDate;
    String bpm_workflowPriority;
    String validazioneSpesaFlag;
    String missioneConAnticipoFlag;
    String validazioneModuloFlag;
    String userNameUtenteMissione;
    String userNameRichiedente;
    String userNameResponsabileModulo;
    String userNamePrimoFirmatario;
    String userNameFirmatarioSpesa;
    String userNameAmministrativo1;
    String userNameAmministrativo2;
    String userNameAmministrativo3;
    String uoRich;
    String descrizioneUoRich;
    String uoSpesa;
    String linkToOtherWorkflows;
    String descrizioneUoSpesa;
    String uoCompetenza;
    String descrizioneUoCompetenza;
    String autoPropriaFlag;
    String noleggioFlag;
    String taxiFlag;
    //String servizioFlagOk;
    //String personaSeguitoFlagOk;
    String capitolo;
    String uoRichSigla;
    String uoCompetenzaSigla;
    String uoSpesaSigla;
    String descrizioneCapitolo;
    String progetto;
    String descrizioneProgetto;
    String gae;
    String descrizioneGae;
    String impegnoAnnoResiduo;
    String impegnoAnnoCompetenza;
    String impegnoNumeroOk;
    String descrizioneImpegno;
    String importoMissione;
    String disponibilita;
    String missioneEsteraFlag;
    String destinazione;
    String dataInizioMissione;
    String dataFineMissione;
    String trattamento;
    String competenzaResiduo;
    String autoPropriaAltriMotivi;
    String autoPropriaPrimoMotivo;
    String autoPropriaSecondoMotivo;
    String autoPropriaTerzoMotivo;
    String taxiAltriMotivi;
    String taxiPrimoMotivo;
    String taxiSecondoMotivo;
    String taxiTerzoMotivo;
    String taxiQuartoMotivo;
    String pathFascicoloDocumenti;
    String titolo;
    String descrizione;
    String gruppoFirmatarioUo;
    String gruppoFirmatarioSpesa;
    String idStrutturaUoMissioni;
    String idStrutturaSpesaMissioni;
    private String tipologiaMissione;

    public String getIdMissioneOrdine() {
        return idMissioneOrdine;
    }

    public void setIdMissioneOrdine(String idMissioneOrdine) {
        this.idMissioneOrdine = idMissioneOrdine;
    }

    public String getIdMissione() {
        return idMissione;
    }

    public void setIdMissione(String idMissione) {
        this.idMissione = idMissione;
    }

    public String getOggetto() {
        return oggetto;
    }

    public void setOggetto(String oggetto) {
        this.oggetto = oggetto;
    }

    public String getAnnoMissione() {
        return annoMissione;
    }

    public void setAnnoMissione(String annoMissione) {
        this.annoMissione = annoMissione;
    }

    public String getNumeroMissione() {
        return numeroMissione;
    }

    public void setNumeroMissione(String numeroMissione) {
        this.numeroMissione = numeroMissione;
    }

    public String getLinkToOtherWorkflows() {
        return linkToOtherWorkflows;
    }

    public void setLinkToOtherWorkflows(String linkToOtherWorkflows) {
        this.linkToOtherWorkflows = linkToOtherWorkflows;
    }

    public String getUoRichSigla() {
        return uoRichSigla;
    }

    public void setUoRichSigla(String uoRichSigla) {
        this.uoRichSigla = uoRichSigla;
    }

    public String getUoCompetenzaSigla() {
        return uoCompetenzaSigla;
    }

    public void setUoCompetenzaSigla(String uoCompetenzaSigla) {
        this.uoCompetenzaSigla = uoCompetenzaSigla;
    }

    public String getUoSpesaSigla() {
        return uoSpesaSigla;
    }

    public void setUoSpesaSigla(String uoSpesaSigla) {
        this.uoSpesaSigla = uoSpesaSigla;
    }

    public String getProgetto() {
        return progetto;
    }

    public void setProgetto(String progetto) {
        this.progetto = progetto;
    }

    public String getDescrizioneProgetto() {
        return descrizioneProgetto;
    }

    public void setDescrizioneProgetto(String descrizioneProgetto) {
        this.descrizioneProgetto = descrizioneProgetto;
    }

    public String getTipologiaMissione() {
        return tipologiaMissione;
    }

    public void setTipologiaMissione(String tipologiaMissione) {
        this.tipologiaMissione = tipologiaMissione;
    }


    public String getNoteAutorizzazioniAggiuntive() {
        return noteAutorizzazioniAggiuntive;
    }

    public void setNoteAutorizzazioniAggiuntive(String noteAutorizzazioniAggiuntive) {
        this.noteAutorizzazioniAggiuntive = noteAutorizzazioniAggiuntive;
    }

    public String getMissioneGratuita() {
        return missioneGratuita;
    }

    public void setMissioneGratuita(String missioneGratuita) {
        this.missioneGratuita = missioneGratuita;
    }

    public String getDescrizioneOrdine() {
        return descrizioneOrdine;
    }

    public void setDescrizioneOrdine(String descrizioneOrdine) {
        this.descrizioneOrdine = descrizioneOrdine;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getNoteSegreteria() {
        return noteSegreteria;
    }

    public void setNoteSegreteria(String noteSegreteria) {
        this.noteSegreteria = noteSegreteria;
    }

    public String getBpm_workflowDueDate() {
        return bpm_workflowDueDate;
    }

    public void setBpm_workflowDueDate(String bpm_workflowDueDate) {
        this.bpm_workflowDueDate = bpm_workflowDueDate;
    }

    public String getBpm_workflowPriority() {
        return bpm_workflowPriority;
    }

    public void setBpm_workflowPriority(String bpm_workflowPriority) {
        this.bpm_workflowPriority = bpm_workflowPriority;
    }

    public String getValidazioneSpesaFlag() {
        return validazioneSpesaFlag;
    }

    public void setValidazioneSpesaFlag(String validazioneSpesaFlag) {
        this.validazioneSpesaFlag = validazioneSpesaFlag;
    }

    public String getMissioneConAnticipoFlag() {
        return missioneConAnticipoFlag;
    }

    public void setMissioneConAnticipoFlag(String missioneConAnticipoFlag) {
        this.missioneConAnticipoFlag = missioneConAnticipoFlag;
    }

    public String getValidazioneModuloFlag() {
        return validazioneModuloFlag;
    }

    public void setValidazioneModuloFlag(String validazioneModuloFlag) {
        this.validazioneModuloFlag = validazioneModuloFlag;
    }

    public String getUserNameRichiedente() {
        return userNameRichiedente;
    }

    public void setUserNameRichiedente(String userNameRichiedente) {
        this.userNameRichiedente = userNameRichiedente;
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

    public String getUserNameUtenteMissione() {
        return userNameUtenteMissione;
    }

    public void setUserNameUtenteMissione(String userNameUtenteMissione) {
        this.userNameUtenteMissione = userNameUtenteMissione;
    }

    public String getUserNameFirmatarioSpesa() {
        return userNameFirmatarioSpesa;
    }

    public void setUserNameFirmatarioSpesa(String userNameFirmatarioSpesa) {
        this.userNameFirmatarioSpesa = userNameFirmatarioSpesa;
    }

    public String getUserNameAmministrativo1() {
        return userNameAmministrativo1;
    }

    public void setUserNameAmministrativo1(String userNameAmministrativo1) {
        this.userNameAmministrativo1 = userNameAmministrativo1;
    }

    public String getUserNameAmministrativo2() {
        return userNameAmministrativo2;
    }

    public void setUserNameAmministrativo2(String userNameAmministrativo2) {
        this.userNameAmministrativo2 = userNameAmministrativo2;
    }

    public String getUserNameAmministrativo3() {
        return userNameAmministrativo3;
    }

    public void setUserNameAmministrativo3(String userNameAmministrativo3) {
        this.userNameAmministrativo3 = userNameAmministrativo3;
    }

    public String getUoRich() {
        return uoRich;
    }

    public void setUoRich(String uoRich) {
        this.uoRich = uoRich;
    }

    public String getDescrizioneUoRich() {
        return descrizioneUoRich;
    }

    public void setDescrizioneUoRich(String descrizioneUoRich) {
        this.descrizioneUoRich = descrizioneUoRich;
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
/*
    public String getServizioFlagOk() {
        return servizioFlagOk;
    }

    public void setServizioFlagOk(String servizioFlagOk) {
        this.servizioFlagOk = servizioFlagOk;
    }

    public String getPersonaSeguitoFlagOk() {
        return personaSeguitoFlagOk;
    }

    public void setPersonaSeguitoFlagOk(String personaSeguitoFlagOk) {
        this.personaSeguitoFlagOk = personaSeguitoFlagOk;
    }*/

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

    public String getImpegnoAnnoResiduo() {
        return impegnoAnnoResiduo;
    }

    public void setImpegnoAnnoResiduo(String impegnoAnnoResiduo) {
        this.impegnoAnnoResiduo = impegnoAnnoResiduo;
    }

    public String getImpegnoAnnoCompetenza() {
        return impegnoAnnoCompetenza;
    }

    public void setImpegnoAnnoCompetenza(String impegnoAnnoCompetenza) {
        this.impegnoAnnoCompetenza = impegnoAnnoCompetenza;
    }

    public String getImpegnoNumeroOk() {
        return impegnoNumeroOk;
    }

    public void setImpegnoNumeroOk(String impegnoNumeroOk) {
        this.impegnoNumeroOk = impegnoNumeroOk;
    }

    public String getDescrizioneImpegno() {
        return descrizioneImpegno;
    }

    public void setDescrizioneImpegno(String descrizioneImpegno) {
        this.descrizioneImpegno = descrizioneImpegno;
    }

    public String getImportoMissione() {
        return importoMissione;
    }

    public void setImportoMissione(String importoMissione) {
        this.importoMissione = importoMissione;
    }

    public String getDisponibilita() {
        return disponibilita;
    }

    public void setDisponibilita(String disponibilita) {
        this.disponibilita = disponibilita;
    }

    public String getMissioneEsteraFlag() {
        return missioneEsteraFlag;
    }

    public void setMissioneEsteraFlag(String missioneEsteraFlag) {
        this.missioneEsteraFlag = missioneEsteraFlag;
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

    public String getTrattamento() {
        return trattamento;
    }

    public void setTrattamento(String trattamento) {
        this.trattamento = trattamento;
    }

    public String getCompetenzaResiduo() {
        return competenzaResiduo;
    }

    public void setCompetenzaResiduo(String competenzaResiduo) {
        this.competenzaResiduo = competenzaResiduo;
    }

    public String getAutoPropriaAltriMotivi() {
        return autoPropriaAltriMotivi;
    }

    public void setAutoPropriaAltriMotivi(String autoPropriaAltriMotivi) {
        this.autoPropriaAltriMotivi = autoPropriaAltriMotivi;
    }

    public String getAutoPropriaPrimoMotivo() {
        return autoPropriaPrimoMotivo;
    }

    public void setAutoPropriaPrimoMotivo(String autoPropriaPrimoMotivo) {
        this.autoPropriaPrimoMotivo = autoPropriaPrimoMotivo;
    }

    public String getAutoPropriaSecondoMotivo() {
        return autoPropriaSecondoMotivo;
    }

    public void setAutoPropriaSecondoMotivo(String autoPropriaSecondoMotivo) {
        this.autoPropriaSecondoMotivo = autoPropriaSecondoMotivo;
    }

    public String getAutoPropriaTerzoMotivo() {
        return autoPropriaTerzoMotivo;
    }

    public void setAutoPropriaTerzoMotivo(String autoPropriaTerzoMotivo) {
        this.autoPropriaTerzoMotivo = autoPropriaTerzoMotivo;
    }

    public String getTaxiAltriMotivi() {
        return taxiAltriMotivi;
    }

    public void setTaxiAltriMotivi(String taxiAltriMotivi) {
        this.taxiAltriMotivi = taxiAltriMotivi;
    }

    public String getTaxiPrimoMotivo() {
        return taxiPrimoMotivo;
    }

    public void setTaxiPrimoMotivo(String taxiPrimoMotivo) {
        this.taxiPrimoMotivo = taxiPrimoMotivo;
    }

    public String getTaxiSecondoMotivo() {
        return taxiSecondoMotivo;
    }

    public void setTaxiSecondoMotivo(String taxiSecondoMotivo) {
        this.taxiSecondoMotivo = taxiSecondoMotivo;
    }

    public String getTaxiTerzoMotivo() {
        return taxiTerzoMotivo;
    }

    public void setTaxiTerzoMotivo(String taxiTerzoMotivo) {
        this.taxiTerzoMotivo = taxiTerzoMotivo;
    }

    public String getTaxiQuartoMotivo() {
        return taxiQuartoMotivo;
    }

    public void setTaxiQuartoMotivo(String taxiQuartoMotivo) {
        this.taxiQuartoMotivo = taxiQuartoMotivo;
    }

    public String getPathFascicoloDocumenti() {
        return pathFascicoloDocumenti;
    }

    public void setPathFascicoloDocumenti(String pathFascicoloDocumenti) {
        this.pathFascicoloDocumenti = pathFascicoloDocumenti;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getGruppoFirmatarioUo() {
        return gruppoFirmatarioUo;
    }

    public void setGruppoFirmatarioUo(String gruppoFirmatarioUo) {
        this.gruppoFirmatarioUo = gruppoFirmatarioUo;
    }

    public String getGruppoFirmatarioSpesa() {
        return gruppoFirmatarioSpesa;
    }

    public void setGruppoFirmatarioSpesa(String gruppoFirmatarioSpesa) {
        this.gruppoFirmatarioSpesa = gruppoFirmatarioSpesa;
    }

    public String getIdStrutturaUoMissioni() {
        return idStrutturaUoMissioni;
    }

    public void setIdStrutturaUoMissioni(String idStrutturaUoMissioni) {
        this.idStrutturaUoMissioni = idStrutturaUoMissioni;
    }

    public String getIdStrutturaSpesaMissioni() {
        return idStrutturaSpesaMissioni;
    }

    public void setIdStrutturaSpesaMissioni(String idStrutturaSpesaMissioni) {
        this.idStrutturaSpesaMissioni = idStrutturaSpesaMissioni;
    }
}
