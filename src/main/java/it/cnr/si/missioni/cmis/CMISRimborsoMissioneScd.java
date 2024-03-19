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
import it.cnr.si.missioni.cmis.*;
import it.cnr.si.missioni.domain.custom.DatiFlusso;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.missioni.repository.CRUDComponentSession;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
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
@Profile("cnr")
public class CMISRimborsoMissioneScd extends AbstractCMISRimborsoMissioneService {

    @Autowired(required = false)
    protected MessageForFlowsService messageForFlowsService;

    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        if (rimborsoMissione.isStatoRespintoFlusso() && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())) {
            messageForFlowsService.annullaFlusso(parameters, rimborsoMissione.getIdFlusso());
        } else {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido. Id Rimborso " + rimborsoMissione.getId());
        }
        rimborsoMissione.setStatoFlusso(Costanti.STATO_ANNULLATO);
    }

    @Override
    void sendRimborsoOrdineMissioneToSign(RimborsoMissione rimborsoMissione, CMISRimborsoMissione cmisRimborsoMissione,
                                          StorageObject documento,
                                          List<StorageObject> allegati, List<StorageObject> giustificativi) {
        MessageForFlowRimborso messageForFlow = new MessageForFlowRimborso();
        try {

            messageForFlow.setTipologiaMissione(MessageForFlow.TIPOLOGIA_MISSIONE_RIMBORSO);
            messageForFlow.setIdMissione(cmisRimborsoMissione.getIdMissioneRimborso().toString());
            messageForFlow.setIdMissioneOrdine(cmisRimborsoMissione.getIdMissioneRimborso().toString());
            messageForFlow.setIdMissioneRimborso(cmisRimborsoMissione.getIdMissioneRimborso().toString());
            messageForFlow.setTitolo(cmisRimborsoMissione.getWfDescription());
            messageForFlow.setDescrizione(cmisRimborsoMissione.getWfDescriptionComplete());
            messageForFlow = (MessageForFlowRimborso) messageForFlowsService.impostaGruppiFirmatari(cmisRimborsoMissione, messageForFlow);

            messageForFlow.setPathFascicoloDocumenti(rimborsoMissione.getStringBasePath());



            if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty()) {
                for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()) {
                    List<StorageObject> children = getChildrenDettaglio(dettaglio, true);
                    if (children != null && missioniCMISService.esisteAlmenoUnDocumentoValido(children)) {
                        giustificativi.addAll(children);
                    } else {
                        if (dettaglio.isGiustificativoObbligatorio() && !StringUtils.hasLength(dettaglio.getDsNoGiustificativo())) {
                            throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa " + dettaglio.getDsTiSpesa() + " del " + DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa()) + " è obbligatorio allegare almeno un giustificativo.");
                        }
                    }
                }
            }

            messageForFlow.setNoteAutorizzazioniAggiuntive(cmisRimborsoMissione.getNoteAutorizzazioniAggiuntive());
            messageForFlow.setDescrizioneOrdine(cmisRimborsoMissione.getOggetto());
            messageForFlow.setNote(cmisRimborsoMissione.getNote());
            messageForFlow.setNoteSegreteria(cmisRimborsoMissione.getNoteSegreteria());
            messageForFlow.setBpm_sendEMailNotifications("no");
            messageForFlow.setBpm_workflowDueDate(cmisRimborsoMissione.getWfDueDate());
            messageForFlow.setBpm_workflowPriority(Utility.nvl(cmisRimborsoMissione.getPriorita(), Costanti.PRIORITA_MEDIA));
            messageForFlow.setValidazioneSpesaFlag(cmisRimborsoMissione.getValidazioneSpesa());
            messageForFlow.setUserNameUtenteMissione(cmisRimborsoMissione.getUsernameUtenteOrdine());
            messageForFlow.setUserNameRichiedente(cmisRimborsoMissione.getUsernameRichiedente());
            messageForFlow.setUserNamePrimoFirmatario(cmisRimborsoMissione.getUserNamePrimoFirmatario());
            messageForFlow.setUserNameFirmatarioSpesa(cmisRimborsoMissione.getUserNameFirmatarioSpesa());
            messageForFlow.setUserNameAmministrativo1("");
            messageForFlow.setUserNameAmministrativo2("");
            messageForFlow.setUserNameAmministrativo3("");
            messageForFlow.setUoRich(cmisRimborsoMissione.getUoRich());
            messageForFlow.setDescrizioneUoRich(cmisRimborsoMissione.getDescrizioneUoRich());
            messageForFlow.setUoSpesa(cmisRimborsoMissione.getUoSpesa());
            messageForFlow.setDescrizioneUoSpesa(cmisRimborsoMissione.getDescrizioneUoSpesa());
            messageForFlow.setUoCompetenza(cmisRimborsoMissione.getUoCompetenza());
            messageForFlow.setDescrizioneUoCompetenza(cmisRimborsoMissione.getDescrizioneUoCompetenza());
            messageForFlow.setNoleggioFlag(cmisRimborsoMissione.getNoleggioFlag());
            messageForFlow.setTaxiFlag(cmisRimborsoMissione.getTaxiFlag());
            messageForFlow.setServizioFlagOk(cmisRimborsoMissione.getAutoServizioFlag());
            messageForFlow.setPersonaSeguitoFlagOk(cmisRimborsoMissione.getPersonaSeguitoFlag());
            messageForFlow.setCapitolo(cmisRimborsoMissione.getCapitolo());
            messageForFlow.setDescrizioneCapitolo(cmisRimborsoMissione.getDescrizioneCapitolo());
            messageForFlow.setGae(cmisRimborsoMissione.getGae());
            messageForFlow.setDescrizioneGae(cmisRimborsoMissione.getDescrizioneGae());
            messageForFlow.setImpegnoAnnoResiduo(cmisRimborsoMissione.getImpegnoAnnoResiduo() == null ? "" : cmisRimborsoMissione.getImpegnoAnnoResiduo().toString());
            messageForFlow.setImpegnoAnnoCompetenza(cmisRimborsoMissione.getImpegnoAnnoCompetenza() == null ? "" : cmisRimborsoMissione.getImpegnoAnnoCompetenza().toString());
            messageForFlow.setImpegnoNumeroOk(cmisRimborsoMissione.getImpegnoNumero() == null ? "" : cmisRimborsoMissione.getImpegnoNumero().toString());
            messageForFlow.setDescrizioneImpegno(cmisRimborsoMissione.getDescrizioneImpegno());
            messageForFlow.setImportoMissione(cmisRimborsoMissione.getImportoMissione() == null ? "" : cmisRimborsoMissione.getImportoMissione().toString());
            messageForFlow.setDisponibilita(cmisRimborsoMissione.getDisponibilita() == null ? "" : cmisRimborsoMissione.getDisponibilita().toString());
            messageForFlow.setMissioneEsteraFlag(cmisRimborsoMissione.getMissioneEsteraFlag());
            messageForFlow.setDestinazione(cmisRimborsoMissione.getDestinazione());
            messageForFlow.setDataInizioMissione(cmisRimborsoMissione.getDataInizioMissione());
            messageForFlow.setDataFineMissione(cmisRimborsoMissione.getDataFineMissione());
            messageForFlow.setTrattamento(cmisRimborsoMissione.getTrattamento());
            messageForFlow.setDataInizioEstero(cmisRimborsoMissione.getDataInizioEstero() == null ? "" : cmisRimborsoMissione.getDataInizioEstero());
            messageForFlow.setDataFineEstero(cmisRimborsoMissione.getDataFineEstero() == null ? "" : cmisRimborsoMissione.getDataFineEstero());
            messageForFlow.setAnticipoRicevuto(cmisRimborsoMissione.getAnticipoRicevuto());
            messageForFlow.setAnnoMandato(cmisRimborsoMissione.getAnnoMandato());
            messageForFlow.setNumeroMandatoOk(cmisRimborsoMissione.getNumeroMandato());
            messageForFlow.setImportoMandato(cmisRimborsoMissione.getImportoMandato());
            if (cmisRimborsoMissione.getWfOrdineMissione() != null) {
                messageForFlow.setLinkToOtherWorkflows(cmisRimborsoMissione.getWfOrdineMissione());
            }
            messageForFlow.setDifferenzeOrdineRimborso(cmisRimborsoMissione.getDifferenzeOrdineRimborso());
            messageForFlow.setTotaleRimborsoMissione(cmisRimborsoMissione.getTotaleRimborsoMissione() == null ? "" : cmisRimborsoMissione.getTotaleRimborsoMissione().toString());


            MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> maps = mapper.convertValue(messageForFlow, new TypeReference<Map<String, Object>>() {
            });
            parameters.setAll(maps);

            messageForFlowsService.caricaDocumento(parameters, Costanti.TIPO_DOCUMENTO_MISSIONE, documento, rimborsoMissione.getStatoFlusso());

            messageForFlowsService.aggiungiDocumentiMultipli(allegati, parameters, Costanti.TIPO_DOCUMENTO_ALLEGATO);
            messageForFlowsService.aggiungiDocumentiMultipli(giustificativi, parameters, Costanti.TIPO_DOCUMENTO_GIUSTIFICATIVO);

            if (rimborsoMissione.isStatoNonInviatoAlFlusso()) {
                parameters.add("commento", "");
            } else {
                if ((rimborsoMissione.isStatoInviatoAlFlusso() || rimborsoMissione.isStatoRespintoFlusso()) && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())) {
                    parameters = messageForFlowsService.aggiungiParametriRiavviaFlusso(parameters, rimborsoMissione.getIdFlusso());
                } else {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "Anomalia nei dati. Stato di invio al flusso non valido.");
                }
            }

            try {
                if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                    rimborsoMissioneService.popolaCoda(rimborsoMissione);
                } else {
                    String idFlusso = messageForFlowsService.avviaFlusso(parameters);
                    if (StringUtils.isEmpty(rimborsoMissione.getIdFlusso())) {
                        rimborsoMissione.setIdFlusso(idFlusso);
                    }
                    rimborsoMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);

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





}
