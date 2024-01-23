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

import java.math.BigDecimal;

public class CMISMissione {
    private String nomeFile;
    private String anno;
    private String numero;
    private String oggetto;
    private String note;
    private boolean missioneEstera;
    private boolean missioneCug;
    private boolean missionePresidente;
    private boolean missioneGratuita = false;
    private String noteSegreteria;
    private String wfDescription;
    private String wfDescriptionComplete;
    private String wfDueDate;
    private String priorita;
    private String cdsRich;
    private String cdsSpesa;
    private String validazioneSpesa;
    private String usernameUtenteOrdine;
    private String usernameRichiedente;
    private String userNameResponsabileModulo;
    private String userNamePrimoFirmatario;
    private String userNameFirmatarioSpesa;
    private String uoRich;
    private String descrizioneUoRich;
    private String uoRichSigla;
    private String uoCompetenzaSigla;
    private String uoSpesaSigla;
    private String uoSpesa;
    private String uoCompetenza;
    private String descrizioneUoSpesa;
    private String descrizioneUoCompetenza;
    private String autoPropriaFlag;
    /*private String autoServizioFlag;
    private String personaSeguitoFlag;*/
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

    public boolean isMissioneCug() {
        return missioneCug;
    }

    public void setMissioneCug(boolean missioneCug) {
        this.missioneCug = missioneCug;
    }

    public boolean isMissionePresidente() {
        return missionePresidente;
    }

    public void setMissionePresidente(boolean missionePresidente) {
        this.missionePresidente = missionePresidente;
    }

    public boolean isMissioneGratuita() {
        return missioneGratuita;
    }

    public void setMissioneGratuita(boolean missioneGratuita) {
        this.missioneGratuita = missioneGratuita;
    }

    public boolean isMissioneEstera() {
        return missioneEstera;
    }

    public void setMissioneEstera(boolean missioneEstera) {
        this.missioneEstera = missioneEstera;
    }

    public String getWfDescriptionComplete() {
        return wfDescriptionComplete;
    }

    public void setWfDescriptionComplete(String wfDescriptionComplete) {
        this.wfDescriptionComplete = wfDescriptionComplete;
    }

    public String getCdsRich() {
        return cdsRich;
    }

    public void setCdsRich(String cdsRich) {
        this.cdsRich = cdsRich;
    }

    public String getCdsSpesa() {
        return cdsSpesa;
    }

    public void setCdsSpesa(String cdsSpesa) {
        this.cdsSpesa = cdsSpesa;
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

    /*public String getAutoServizioFlag() {
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
    }*/

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
