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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.awesome.exception.TaskIdNonTrovatoException;
import it.cnr.si.missioni.config.FlowIsScrivaniaDigitale;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.service.*;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.*;
import it.cnr.si.missioni.util.proxy.json.service.*;
import it.cnr.si.service.SecurityService;
import it.cnr.si.spring.storage.StorageDriver;
import it.cnr.si.spring.storage.StorageException;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Conditional(FlowIsScrivaniaDigitale.class)
public  class CMISOrdineMissioneServiceScd extends AbstractCMISOrdineMissioneService{

    private static final Log logger = LogFactory.getLog(CMISOrdineMissioneServiceScd.class);

    private void impostaGruppiFirmatari( MessageForFlow messageForFlow, List<String> gruppiFirmatari){
        messageForFlow.setGruppoFirmatarioUo(gruppiFirmatari.get(0));
        messageForFlow.setGruppoFirmatarioSpesa(gruppiFirmatari.get(1));
    }
    public void avviaFlusso(AnnullamentoOrdineMissione annullamento) {
        String username = securityService.getCurrentUserLogin();
        byte[] stampa = printAnnullamentoOrdineMissioneService.printOrdineMissione(annullamento, username);
        CMISOrdineMissione cmisOrdineMissione = create(annullamento.getOrdineMissione(), annullamento.getAnno());
        StorageObject so = salvaStampaAnnullamentoOrdineMissioneSuCMIS(stampa, annullamento);

        MessageForFlowAnnullamento messageForFlows = new MessageForFlowAnnullamento();
        try {

            messageForFlows.setIdMissione(annullamento.getId().toString());
            messageForFlows.setIdMissioneOrdine(annullamento.getOrdineMissione().getId().toString());

            messageForFlows.setIdMissioneRevoca(annullamento.getId().toString());
            messageForFlows.setTitolo("Annullamento " + cmisOrdineMissione.getWfDescription());
            messageForFlows.setDescrizione(cmisOrdineMissione.getWfDescriptionComplete());
            messageForFlows.setTipologiaMissione(MessageForFlow.TIPOLOGIA_MISSIONE_REVOCA);
            //List<String> gruppoFirmatari=messageForFlowsService.getGruppiFirmatari(cmisOrdineMissione, false);
            impostaGruppiFirmatari(messageForFlows,messageForFlowsService.getGruppiFirmatari(cmisOrdineMissione, false));
            //messageForFlows = (MessageForFlowAnnullamento) messageForFlowsService.impostaGruppiFirmatari(cmisOrdineMissione, messageForFlows);

            messageForFlows.setPathFascicoloDocumenti(createFolderOrdineMissione(annullamento.getOrdineMissione()));
            messageForFlows.setNoteAutorizzazioniAggiuntive(cmisOrdineMissione.getNoteAutorizzazioniAggiuntive());
            messageForFlows.setMissioneGratuita(cmisOrdineMissione.getMissioneGratuita());
            messageForFlows.setDescrizioneOrdine(cmisOrdineMissione.getOggetto());
            messageForFlows.setNote(cmisOrdineMissione.getNote());
            messageForFlows.setNoteSegreteria(Utility.nvl(annullamento.getConsentiRimborso(), "N").equals("S") ? Costanti.TESTO_RIMBORSO_CONSENTITO_SU_ORDINE_ANNULLATO : cmisOrdineMissione.getNoteSegreteria());
            messageForFlows.setBpm_workflowDueDate(cmisOrdineMissione.getWfDueDate());
            messageForFlows.setBpm_workflowPriority(cmisOrdineMissione.getPriorita());
            messageForFlows.setValidazioneSpesaFlag(cmisOrdineMissione.getValidazioneSpesa());
            messageForFlows.setMissioneConAnticipoFlag(cmisOrdineMissione.getAnticipo());
            messageForFlows.setValidazioneModuloFlag(StringUtils.isEmpty(cmisOrdineMissione.getUserNameResponsabileModulo()) ? "no" : "si");
            messageForFlows.setUserNameUtenteMissione(cmisOrdineMissione.getUsernameUtenteOrdine());
            messageForFlows.setUserNameRichiedente(cmisOrdineMissione.getUsernameRichiedente());
            messageForFlows.setUserNameResponsabileModulo(cmisOrdineMissione.getUserNameResponsabileModulo());
            messageForFlows.setUserNamePrimoFirmatario(cmisOrdineMissione.getUserNamePrimoFirmatario());
            messageForFlows.setUserNameFirmatarioSpesa(cmisOrdineMissione.getUserNameFirmatarioSpesa());
            messageForFlows.setUserNameAmministrativo1("");
            messageForFlows.setUserNameAmministrativo2("");
            messageForFlows.setUserNameAmministrativo3("");
            messageForFlows.setUoRich(cmisOrdineMissione.getUoRich());
            messageForFlows.setDescrizioneUoRich(cmisOrdineMissione.getDescrizioneUoRich());
            messageForFlows.setUoSpesa(cmisOrdineMissione.getUoSpesa());
            messageForFlows.setDescrizioneUoSpesa(cmisOrdineMissione.getDescrizioneUoSpesa());
            messageForFlows.setUoCompetenza(cmisOrdineMissione.getUoCompetenza());
            messageForFlows.setDescrizioneUoCompetenza(cmisOrdineMissione.getDescrizioneUoCompetenza());
            messageForFlows.setAutoPropriaFlag(cmisOrdineMissione.getAutoPropriaFlag());
            messageForFlows.setNoleggioFlag(cmisOrdineMissione.getNoleggioFlag());
            messageForFlows.setTaxiFlag(cmisOrdineMissione.getTaxiFlag());
            messageForFlows.setServizioFlagOk(cmisOrdineMissione.getAutoServizioFlag());
            messageForFlows.setPersonaSeguitoFlagOk(cmisOrdineMissione.getPersonaSeguitoFlag());
            messageForFlows.setCapitolo(cmisOrdineMissione.getCapitolo());
            messageForFlows.setDescrizioneCapitolo(cmisOrdineMissione.getDescrizioneCapitolo());
            messageForFlows.setProgetto(cmisOrdineMissione.getModulo());
            messageForFlows.setDescrizioneProgetto(cmisOrdineMissione.getDescrizioneModulo());
            messageForFlows.setGae(cmisOrdineMissione.getGae());
            messageForFlows.setDescrizioneGae(cmisOrdineMissione.getDescrizioneGae());
            messageForFlows.setImpegnoAnnoResiduo(cmisOrdineMissione.getImpegnoAnnoResiduo() == null ? "" : cmisOrdineMissione.getImpegnoAnnoResiduo().toString());
            messageForFlows.setImpegnoAnnoCompetenza(cmisOrdineMissione.getImpegnoAnnoCompetenza() == null ? "" : cmisOrdineMissione.getImpegnoAnnoCompetenza().toString());
            messageForFlows.setImpegnoNumeroOk(cmisOrdineMissione.getImpegnoNumero() == null ? "" : cmisOrdineMissione.getImpegnoNumero().toString());
            messageForFlows.setDescrizioneImpegno(cmisOrdineMissione.getDescrizioneImpegno());
            messageForFlows.setImportoMissione(cmisOrdineMissione.getImportoMissione() == null ? "" : cmisOrdineMissione.getImportoMissione().toString());
            messageForFlows.setDisponibilita(cmisOrdineMissione.getDisponibilita() == null ? "" : cmisOrdineMissione.getDisponibilita().toString());
            messageForFlows.setMissioneEsteraFlag(cmisOrdineMissione.getMissioneEsteraFlag());
            messageForFlows.setDestinazione(cmisOrdineMissione.getDestinazione());
            messageForFlows.setDataInizioMissione(cmisOrdineMissione.getDataInizioMissione());
            messageForFlows.setDataFineMissione(cmisOrdineMissione.getDataFineMissione());
            messageForFlows.setTrattamento(cmisOrdineMissione.getTrattamento());
            messageForFlows.setCompetenzaResiduo(cmisOrdineMissione.getFondi());
            messageForFlows.setAutoPropriaAltriMotivi(cmisOrdineMissione.getAltriMotiviAutoPropria());
            messageForFlows.setAutoPropriaPrimoMotivo(cmisOrdineMissione.getPrimoMotivoAutoPropria());
            messageForFlows.setAutoPropriaSecondoMotivo(cmisOrdineMissione.getSecondoMotivoAutoPropria());
            messageForFlows.setAutoPropriaTerzoMotivo(cmisOrdineMissione.getTerzoMotivoAutoPropria());
            if (!annullamento.getOrdineMissione().isOrdineMissioneVecchiaScrivania()) {
                messageForFlows.setLinkToOtherWorkflows(annullamento.getOrdineMissione().getIdFlusso());
            }
            messageForFlows.setValidazioneSpesaFlag("si");

        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: " + e);
        }
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> maps = mapper.convertValue(messageForFlows, new TypeReference<Map<String, Object>>() {
        });
        parameters.setAll(maps);

        messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_MISSIONE, so, annullamento.getStatoFlusso());

        if (annullamento.isStatoNonInviatoAlFlusso()) {
            parameters.add("commento", "");
        } else {
            if ((annullamento.isStatoInviatoAlFlusso() || annullamento.isStatoRespintoFlusso()) && !StringUtils.isEmpty(annullamento.getIdFlusso())) {
                parameters = messageForFlowsService.aggiungiParametriRiavviaFlusso(parameters, annullamento.getIdFlusso());
            } else {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido. Id Annullamento " + annullamento.getId());
            }
        }


        try {
            String idFlusso = messageForFlowsService.avviaFlusso(parameters);
            if (StringUtils.isEmpty(annullamento.getIdFlusso())) {
                annullamento.setIdFlusso(idFlusso);
            }
            annullamento.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);

        } catch (AwesomeException e) {
            throw e;
        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
        }
    }

    @Override
    public void avviaFlusso(OrdineMissione ordineMissione) {
        if (ordineMissione.isOrdineMissioneVecchiaScrivania()) {
//			avviaFlussoVecchiaScrivania(ordineMissione);
        } else {
            //avviaFlussoNuovaScrivania(ordineMissione);
            super.avviaFlusso(ordineMissione);
        }
    }

    protected void sendOrdineMissioneToSign(OrdineMissione ordineMissione, CMISOrdineMissione cmisOrdineMissione, StorageObject documento, OrdineMissioneAnticipo anticipo, StorageObject documentoAnticipo, List<StorageObject> allegati, StorageObject documentoAutoPropria) {
        MessageForFlowOrdine messageForFlows = new MessageForFlowOrdine();
        try {

            messageForFlows.setIdMissione(cmisOrdineMissione.getIdMissioneOrdine().toString());
            messageForFlows.setIdMissioneOrdine(cmisOrdineMissione.getIdMissioneOrdine().toString());
            messageForFlows.setTitolo(cmisOrdineMissione.getWfDescription());
            messageForFlows.setDescrizione(cmisOrdineMissione.getWfDescriptionComplete());
            messageForFlows.setTipologiaMissione(MessageForFlow.TIPOLOGIA_MISSIONE_ORDINE);

            //messageForFlows = (MessageForFlowOrdine) messageForFlowsService.impostaGruppiFirmatari(cmisOrdineMissione, messageForFlows);
            impostaGruppiFirmatari(messageForFlows,messageForFlowsService.getGruppiFirmatari(cmisOrdineMissione, false));
            messageForFlows.setPathFascicoloDocumenti(ordineMissione.getStringBasePath());
            messageForFlows.setNoteAutorizzazioniAggiuntive(cmisOrdineMissione.getNoteAutorizzazioniAggiuntive());
            messageForFlows.setMissioneGratuita(cmisOrdineMissione.getMissioneGratuita());
            messageForFlows.setDescrizioneOrdine(cmisOrdineMissione.getOggetto());
            messageForFlows.setNote(cmisOrdineMissione.getNote());
            messageForFlows.setOggetto(cmisOrdineMissione.getOggetto());
            messageForFlows.setAnnoMissione(cmisOrdineMissione.getAnno());
            messageForFlows.setNumeroMissione(cmisOrdineMissione.getNumero());
            messageForFlows.setNoteSegreteria(cmisOrdineMissione.getNoteSegreteria());
            messageForFlows.setBpm_workflowDueDate(cmisOrdineMissione.getWfDueDate());
            messageForFlows.setBpm_workflowPriority(cmisOrdineMissione.getPriorita());
            messageForFlows.setValidazioneSpesaFlag(cmisOrdineMissione.getValidazioneSpesa());
            messageForFlows.setMissioneConAnticipoFlag(cmisOrdineMissione.getAnticipo());
            messageForFlows.setValidazioneModuloFlag(StringUtils.isEmpty(cmisOrdineMissione.getUserNameResponsabileModulo()) ? "no" : "si");
            messageForFlows.setUserNameUtenteMissione(cmisOrdineMissione.getUsernameUtenteOrdine());
            messageForFlows.setUserNameRichiedente(cmisOrdineMissione.getUsernameRichiedente());
            messageForFlows.setUserNameResponsabileModulo(cmisOrdineMissione.getUserNameResponsabileModulo());
            messageForFlows.setUserNamePrimoFirmatario(cmisOrdineMissione.getUserNamePrimoFirmatario());
            messageForFlows.setUserNameFirmatarioSpesa(cmisOrdineMissione.getUserNameFirmatarioSpesa());
            messageForFlows.setUserNameAmministrativo1("");
            messageForFlows.setUserNameAmministrativo2("");
            messageForFlows.setUserNameAmministrativo3("");
            messageForFlows.setUoRich(cmisOrdineMissione.getUoRich());
            messageForFlows.setDescrizioneUoRich(cmisOrdineMissione.getDescrizioneUoRich());
            messageForFlows.setUoSpesa(cmisOrdineMissione.getUoSpesa());
            messageForFlows.setDescrizioneUoSpesa(cmisOrdineMissione.getDescrizioneUoSpesa());
            messageForFlows.setUoCompetenza(cmisOrdineMissione.getUoCompetenza());
            messageForFlows.setDescrizioneUoCompetenza(cmisOrdineMissione.getDescrizioneUoCompetenza());
            messageForFlows.setAutoPropriaFlag(cmisOrdineMissione.getAutoPropriaFlag());
            messageForFlows.setNoleggioFlag(cmisOrdineMissione.getNoleggioFlag());
            messageForFlows.setTaxiFlag(cmisOrdineMissione.getTaxiFlag());
            messageForFlows.setServizioFlagOk(cmisOrdineMissione.getAutoServizioFlag());
            messageForFlows.setPersonaSeguitoFlagOk(cmisOrdineMissione.getPersonaSeguitoFlag());
            messageForFlows.setCapitolo(cmisOrdineMissione.getCapitolo());
            messageForFlows.setDescrizioneCapitolo(cmisOrdineMissione.getDescrizioneCapitolo());
            messageForFlows.setProgetto(cmisOrdineMissione.getModulo());
            messageForFlows.setDescrizioneProgetto(cmisOrdineMissione.getDescrizioneModulo());
            messageForFlows.setGae(cmisOrdineMissione.getGae());
            messageForFlows.setDescrizioneGae(cmisOrdineMissione.getDescrizioneGae());
            messageForFlows.setImpegnoAnnoResiduo(cmisOrdineMissione.getImpegnoAnnoResiduo() == null ? "" : cmisOrdineMissione.getImpegnoAnnoResiduo().toString());
            messageForFlows.setImpegnoAnnoCompetenza(cmisOrdineMissione.getImpegnoAnnoCompetenza() == null ? "" : cmisOrdineMissione.getImpegnoAnnoCompetenza().toString());
            messageForFlows.setImpegnoNumeroOk(cmisOrdineMissione.getImpegnoNumero() == null ? "" : cmisOrdineMissione.getImpegnoNumero().toString());
            messageForFlows.setDescrizioneImpegno(cmisOrdineMissione.getDescrizioneImpegno());
            messageForFlows.setImportoMissione(cmisOrdineMissione.getImportoMissione() == null ? "" : cmisOrdineMissione.getImportoMissione().toString());
            messageForFlows.setDisponibilita(cmisOrdineMissione.getDisponibilita() == null ? "" : cmisOrdineMissione.getDisponibilita().toString());
            messageForFlows.setMissioneEsteraFlag(cmisOrdineMissione.getMissioneEsteraFlag());
            messageForFlows.setDestinazione(cmisOrdineMissione.getDestinazione());
            messageForFlows.setDataInizioMissione(cmisOrdineMissione.getDataInizioMissione());
            messageForFlows.setDataFineMissione(cmisOrdineMissione.getDataFineMissione());
            messageForFlows.setTrattamento(cmisOrdineMissione.getTrattamento());
            messageForFlows.setCompetenzaResiduo(cmisOrdineMissione.getFondi());
            messageForFlows.setAutoPropriaAltriMotivi(cmisOrdineMissione.getAltriMotiviAutoPropria());
            messageForFlows.setAutoPropriaPrimoMotivo(cmisOrdineMissione.getPrimoMotivoAutoPropria());
            messageForFlows.setAutoPropriaSecondoMotivo(cmisOrdineMissione.getSecondoMotivoAutoPropria());
            messageForFlows.setAutoPropriaTerzoMotivo(cmisOrdineMissione.getTerzoMotivoAutoPropria());

            messageForFlows.setValidazioneSpesaFlag("si");

            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> maps = mapper.convertValue(messageForFlows, new TypeReference<Map<String, Object>>() {
            });
            parameters.setAll(maps);

            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_MISSIONE, documento, ordineMissione.getStatoFlusso());
            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_ANTICIPO, documentoAnticipo, ordineMissione.getStatoFlusso());
            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_AUTO_PROPRIA, documentoAutoPropria, ordineMissione.getStatoFlusso());

            messageForFlowsService.aggiungiDocumentiMultipli(allegati, parameters, Costanti.TIPO_DOCUMENTO_ALLEGATO);

            if (ordineMissione.isStatoNonInviatoAlFlusso()) {
                parameters.add("commento", "");
            } else {
                if ((ordineMissione.isStatoInviatoAlFlusso() || ordineMissione.isStatoRespintoFlusso()) && !StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
                    parameters = messageForFlowsService.aggiungiParametriRiavviaFlusso(parameters, ordineMissione.getIdFlusso());
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido. Id Ordine " + ordineMissione.getId());
                }
            }

            try {
                if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                    ordineMissioneService.popolaCoda(ordineMissione);
                } else {
                    String idFlusso = messageForFlowsService.avviaFlusso(parameters);
                    if (StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
                        ordineMissione.setIdFlusso(idFlusso);
                        if (anticipo != null) {
                            anticipo.setIdFlusso(idFlusso);
                        }
                    }
                    ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);

                }
            } catch (AwesomeException e) {
                throw e;
            } catch (Exception e) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
            }

        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: " + e);
        }
    }
   /* private void avviaFlussoNuovaScrivania(OrdineMissione ordineMissione) {
        String username = securityService.getCurrentUserLogin();
        byte[] stampa = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);
        CMISOrdineMissione cmisOrdineMissione = create(ordineMissione);
        StorageObject documento = salvaStampaOrdineMissioneSuCMIS(stampa, ordineMissione, cmisOrdineMissione);
        OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService.getAnticipo(Long.valueOf(ordineMissione.getId().toString()));
        OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(Long.valueOf(ordineMissione.getId().toString()), true);
        StorageObject documentoAnticipo = null;
        List<StorageObject> allegati = new ArrayList<>();
        List<StorageObject> allegatiOrdineMissione = getDocumentsOrdineMissione(ordineMissione, true);
        if (allegatiOrdineMissione != null && !allegatiOrdineMissione.isEmpty()) {
            allegati.addAll(allegatiOrdineMissione);
        }
        if (anticipo != null) {
            anticipo.setOrdineMissione(ordineMissione);
            documentoAnticipo = creaDocumentoAnticipo(username, anticipo);
            List<StorageObject> allegatiAnticipo = getAttachmentsAnticipo(ordineMissione);
            if (allegatiAnticipo != null && !allegatiAnticipo.isEmpty()) {
                allegati.addAll(allegatiAnticipo);
            }
        }


        StorageObject documentoAutoPropria = null;
        if (autoPropria != null) {
            autoPropria.setOrdineMissione(ordineMissione);
            documentoAutoPropria = creaDocumentoAutoPropria(username, autoPropria);
        }
        if (!Optional.ofNullable(messageForFlowsService).isPresent()) {
            ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            FlowResult flowResult = new FlowResult();
            flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMA_UO);
            ordineMissioneService.aggiornaOrdineMissione(ordineMissione, flowResult);
            flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
            ordineMissioneService.aggiornaOrdineMissione(ordineMissione, flowResult);
            return;
        }

        MessageForFlowOrdine messageForFlows = new MessageForFlowOrdine();
        try {

            messageForFlows.setIdMissione(cmisOrdineMissione.getIdMissioneOrdine().toString());
            messageForFlows.setIdMissioneOrdine(cmisOrdineMissione.getIdMissioneOrdine().toString());
            messageForFlows.setTitolo(cmisOrdineMissione.getWfDescription());
            messageForFlows.setDescrizione(cmisOrdineMissione.getWfDescriptionComplete());
            messageForFlows.setTipologiaMissione(MessageForFlow.TIPOLOGIA_MISSIONE_ORDINE);

            messageForFlows = (MessageForFlowOrdine) messageForFlowsService.impostaGruppiFirmatari(cmisOrdineMissione, messageForFlows);

            messageForFlows.setPathFascicoloDocumenti(ordineMissione.getStringBasePath());
            messageForFlows.setNoteAutorizzazioniAggiuntive(cmisOrdineMissione.getNoteAutorizzazioniAggiuntive());
            messageForFlows.setMissioneGratuita(cmisOrdineMissione.getMissioneGratuita());
            messageForFlows.setDescrizioneOrdine(cmisOrdineMissione.getOggetto());
            messageForFlows.setNote(cmisOrdineMissione.getNote());
            messageForFlows.setOggetto(cmisOrdineMissione.getOggetto());
            messageForFlows.setAnnoMissione(cmisOrdineMissione.getAnno());
            messageForFlows.setNumeroMissione(cmisOrdineMissione.getNumero());
            messageForFlows.setNoteSegreteria(cmisOrdineMissione.getNoteSegreteria());
            messageForFlows.setBpm_workflowDueDate(cmisOrdineMissione.getWfDueDate());
            messageForFlows.setBpm_workflowPriority(cmisOrdineMissione.getPriorita());
            messageForFlows.setValidazioneSpesaFlag(cmisOrdineMissione.getValidazioneSpesa());
            messageForFlows.setMissioneConAnticipoFlag(cmisOrdineMissione.getAnticipo());
            messageForFlows.setValidazioneModuloFlag(StringUtils.isEmpty(cmisOrdineMissione.getUserNameResponsabileModulo()) ? "no" : "si");
            messageForFlows.setUserNameUtenteMissione(cmisOrdineMissione.getUsernameUtenteOrdine());
            messageForFlows.setUserNameRichiedente(cmisOrdineMissione.getUsernameRichiedente());
            messageForFlows.setUserNameResponsabileModulo(cmisOrdineMissione.getUserNameResponsabileModulo());
            messageForFlows.setUserNamePrimoFirmatario(cmisOrdineMissione.getUserNamePrimoFirmatario());
            messageForFlows.setUserNameFirmatarioSpesa(cmisOrdineMissione.getUserNameFirmatarioSpesa());
            messageForFlows.setUserNameAmministrativo1("");
            messageForFlows.setUserNameAmministrativo2("");
            messageForFlows.setUserNameAmministrativo3("");
            messageForFlows.setUoRich(cmisOrdineMissione.getUoRich());
            messageForFlows.setDescrizioneUoRich(cmisOrdineMissione.getDescrizioneUoRich());
            messageForFlows.setUoSpesa(cmisOrdineMissione.getUoSpesa());
            messageForFlows.setDescrizioneUoSpesa(cmisOrdineMissione.getDescrizioneUoSpesa());
            messageForFlows.setUoCompetenza(cmisOrdineMissione.getUoCompetenza());
            messageForFlows.setDescrizioneUoCompetenza(cmisOrdineMissione.getDescrizioneUoCompetenza());
            messageForFlows.setAutoPropriaFlag(cmisOrdineMissione.getAutoPropriaFlag());
            messageForFlows.setNoleggioFlag(cmisOrdineMissione.getNoleggioFlag());
            messageForFlows.setTaxiFlag(cmisOrdineMissione.getTaxiFlag());
            messageForFlows.setServizioFlagOk(cmisOrdineMissione.getAutoServizioFlag());
            messageForFlows.setPersonaSeguitoFlagOk(cmisOrdineMissione.getPersonaSeguitoFlag());
            messageForFlows.setCapitolo(cmisOrdineMissione.getCapitolo());
            messageForFlows.setDescrizioneCapitolo(cmisOrdineMissione.getDescrizioneCapitolo());
            messageForFlows.setProgetto(cmisOrdineMissione.getModulo());
            messageForFlows.setDescrizioneProgetto(cmisOrdineMissione.getDescrizioneModulo());
            messageForFlows.setGae(cmisOrdineMissione.getGae());
            messageForFlows.setDescrizioneGae(cmisOrdineMissione.getDescrizioneGae());
            messageForFlows.setImpegnoAnnoResiduo(cmisOrdineMissione.getImpegnoAnnoResiduo() == null ? "" : cmisOrdineMissione.getImpegnoAnnoResiduo().toString());
            messageForFlows.setImpegnoAnnoCompetenza(cmisOrdineMissione.getImpegnoAnnoCompetenza() == null ? "" : cmisOrdineMissione.getImpegnoAnnoCompetenza().toString());
            messageForFlows.setImpegnoNumeroOk(cmisOrdineMissione.getImpegnoNumero() == null ? "" : cmisOrdineMissione.getImpegnoNumero().toString());
            messageForFlows.setDescrizioneImpegno(cmisOrdineMissione.getDescrizioneImpegno());
            messageForFlows.setImportoMissione(cmisOrdineMissione.getImportoMissione() == null ? "" : cmisOrdineMissione.getImportoMissione().toString());
            messageForFlows.setDisponibilita(cmisOrdineMissione.getDisponibilita() == null ? "" : cmisOrdineMissione.getDisponibilita().toString());
            messageForFlows.setMissioneEsteraFlag(cmisOrdineMissione.getMissioneEsteraFlag());
            messageForFlows.setDestinazione(cmisOrdineMissione.getDestinazione());
            messageForFlows.setDataInizioMissione(cmisOrdineMissione.getDataInizioMissione());
            messageForFlows.setDataFineMissione(cmisOrdineMissione.getDataFineMissione());
            messageForFlows.setTrattamento(cmisOrdineMissione.getTrattamento());
            messageForFlows.setCompetenzaResiduo(cmisOrdineMissione.getFondi());
            messageForFlows.setAutoPropriaAltriMotivi(cmisOrdineMissione.getAltriMotiviAutoPropria());
            messageForFlows.setAutoPropriaPrimoMotivo(cmisOrdineMissione.getPrimoMotivoAutoPropria());
            messageForFlows.setAutoPropriaSecondoMotivo(cmisOrdineMissione.getSecondoMotivoAutoPropria());
            messageForFlows.setAutoPropriaTerzoMotivo(cmisOrdineMissione.getTerzoMotivoAutoPropria());

            messageForFlows.setValidazioneSpesaFlag("si");

            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> maps = mapper.convertValue(messageForFlows, new TypeReference<Map<String, Object>>() {
            });
            parameters.setAll(maps);

            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_MISSIONE, documento, ordineMissione.getStatoFlusso());
            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_ANTICIPO, documentoAnticipo, ordineMissione.getStatoFlusso());
            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_AUTO_PROPRIA, documentoAutoPropria, ordineMissione.getStatoFlusso());

            messageForFlowsService.aggiungiDocumentiMultipli(allegati, parameters, Costanti.TIPO_DOCUMENTO_ALLEGATO);

            if (ordineMissione.isStatoNonInviatoAlFlusso()) {
                parameters.add("commento", "");
            } else {
                if ((ordineMissione.isStatoInviatoAlFlusso() || ordineMissione.isStatoRespintoFlusso()) && !StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
                    parameters = messageForFlowsService.aggiungiParametriRiavviaFlusso(parameters, ordineMissione.getIdFlusso());
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido. Id Ordine " + ordineMissione.getId());
                }
            }

            try {
                if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                    ordineMissioneService.popolaCoda(ordineMissione);
                } else {
                    String idFlusso = messageForFlowsService.avviaFlusso(parameters);
                    if (StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
                        ordineMissione.setIdFlusso(idFlusso);
                        if (anticipo != null) {
                            anticipo.setIdFlusso(idFlusso);
                        }
                    }
                    ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);

                }
            } catch (AwesomeException e) {
                throw e;
            } catch (Exception e) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase avvio flusso documentale. Errore: " + Utility.getMessageException(e) + ".");
            }

        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di preparazione del flusso documentale. Errore: " + e);
        }

    }

    */

}
