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

package it.cnr.si.missioni.service;

import it.cnr.si.flows.model.ProcessDefinitions;
import it.cnr.si.flows.model.StartWorkflowResponse;
import it.cnr.si.flows.model.TaskResponse;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.awesome.exception.TaskIdNonTrovatoException;
import it.cnr.si.missioni.cmis.CMISMissione;
import it.cnr.si.missioni.cmis.MessageForFlow;
import it.cnr.si.missioni.cmis.MessageForFlowRimborso;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.config.FlowIsScrivaniaDigitale;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.service.application.FlowsService;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import it.iss.si.service.AceURLCondition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Conditional(FlowIsScrivaniaDigitale.class)
public class MessageForFlowsServiceScd extends AbstractMessageForFlowsService{
    private static final Log logger = LogFactory.getLog(MessageForFlowsServiceScd.class);
    @Autowired
    private FlowsService flowsService;

    private SimpleEntitaOrganizzativaWebDto recuperoSedeCug(LocalDate data) {
        List<SimpleEntitaOrganizzativaWebDto> lista = missioniAceService.recuperoSediByTerm(Costanti.ACE_SIGLA_CUG, data);
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = Optional.ofNullable(lista.stream()
                .filter(entita -> {
                    return Costanti.ACE_SIGLA_CUG.equals(entita.getSigla());
                }).collect(Collectors.toList())).orElse(new ArrayList<SimpleEntitaOrganizzativaWebDto>());
        if (listaEntitaUo.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Entità organizzativa CUG non trovata.");
        } else if (listaEntitaUo.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Esistono più entità organizzativa CUG.");
        } else {
            return lista.get(0);
        }
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedePresidenza(LocalDate data) {
        List<SimpleEntitaOrganizzativaWebDto> lista = missioniAceService.recuperoSediByTerm(Costanti.ACE_SIGLA_PRESIDENTE, data);
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = Optional.ofNullable(lista.stream()
                .filter(entita -> {
                    return Costanti.ACE_SIGLA_PRESIDENTE.equals(entita.getSigla());
                }).collect(Collectors.toList())).orElse(new ArrayList<SimpleEntitaOrganizzativaWebDto>());
        if (listaEntitaUo.size() == 0) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Entità organizzativa Presidenza non trovata.");
        } else if (listaEntitaUo.size() > 1) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Esistono più entità organizzativa Presidenza.");
        } else {
            return listaEntitaUo.get(0);
        }
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedePrincipale(List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo) {
        for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativaWebDto : listaEntitaUo) {
            String tipoEntitaOrganizzativa = entitaOrganizzativaWebDto.getTipo().getSigla();
            if (tipoEntitaOrganizzativa.equals("UFF") ||
                    tipoEntitaOrganizzativa.equals("SPRINC") ||
                    tipoEntitaOrganizzativa.equals("AREA") ||
                    tipoEntitaOrganizzativa.equals("DIP") ||
                    tipoEntitaOrganizzativa.equals("un")) {
                return entitaOrganizzativaWebDto;
            }
        }
        return null;
    }

    protected String costruisciGruppoFirmatario(String ruolo, Integer idSede) {
        return ruolo + "@" + idSede;
    }



    protected void preparaParametriPerRiavvioFlusso(MultiValueMap<String, Object> parameters, String idFlusso) {
        try {
            ResponseEntity<TaskResponse> taskIdResponse = flowsService.getTaskId(idFlusso);
            String taskId = taskIdResponse.getBody().getId();
            parameters.add("taskId", taskId);
            parameters.add("commento", "");
        } catch (HttpServerErrorException e) {
            logger.error("Non e' stato possibile recuperare il task attivo per il flusso " + idFlusso + ". Probabilmente il flusso e' terminato", e);
            throw new TaskIdNonTrovatoException(e);
        }

    }

    public void annullaFlusso(MultiValueMap<String, Object> parameters, String idFlusso) {
        preparaParametriPerRiavvioFlusso(parameters, idFlusso);
        parameters.add("sceltaUtente", "Annulla");
        ResponseEntity<ProcessDefinitions> processDefinitions = flowsService.getProcessDefinitions(Costanti.NOME_PROCESSO_FLOWS_MISSIONI);
        if (processDefinitions.getStatusCode().is2xxSuccessful()) {

            logger.info("Annulla Flusso. Parametri: " + parameters);
            ResponseEntity<StartWorkflowResponse> startWorkflowResponseResponseEntity = flowsService.startWorkflow(processDefinitions.getBody().getId(), parameters);
            if (!startWorkflowResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                String erroreFlows = "Errore Annullamento Flusso! " + startWorkflowResponseResponseEntity.getStatusCode().value() + " per Id Flusso " + idFlusso + " Status Code ritornato: " + startWorkflowResponseResponseEntity.getStatusCode();
                logger.info(erroreFlows);
                throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
            }
        } else {
            String erroreFlows = "Errore Recupero Process Definitions! " + processDefinitions.getStatusCode().value() + " per id Flusso " + idFlusso + " Status Code ritornato: " + processDefinitions.getStatusCode();
            logger.info(erroreFlows);
            throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
        }
    }

    public String avviaFlusso(MultiValueMap<String, Object> parameters) {
        String tipoFlusso = (String) parameters.get("tipologiaMissione").get(0);
        String idMissione = (String) parameters.get("idMissione").get(0);

        ResponseEntity<ProcessDefinitions> processDefinitions = flowsService.getProcessDefinitions(Costanti.NOME_PROCESSO_FLOWS_MISSIONI);
        if (processDefinitions.getStatusCode().is2xxSuccessful()) {

            logger.info("Avvio Flusso. Parametri: " + parameters);
            ResponseEntity<StartWorkflowResponse> startWorkflowResponseResponseEntity = flowsService.startWorkflow(processDefinitions.getBody().getId(), parameters);
            if (startWorkflowResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                logger.info(tipoFlusso + " missione " + idMissione + " inviato alla firma");
                if (startWorkflowResponseResponseEntity.getBody() != null) {
                    return startWorkflowResponseResponseEntity.getBody().getId();
                }
                return "";
            } else {
                String erroreFlows = "Errore Flows! " + startWorkflowResponseResponseEntity.getStatusCode().value() + " per " + tipoFlusso + " missione " + idMissione + " Status Code ritornato: " + startWorkflowResponseResponseEntity.getStatusCode();
                logger.info(erroreFlows);
                throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
            }
        } else {
            String erroreFlows = "Errore Recupero Process Definitions! " + processDefinitions.getStatusCode().value() + " per " + tipoFlusso + " missione " + idMissione + " Status Code ritornato: " + processDefinitions.getStatusCode();
            logger.info(erroreFlows);
            throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
        }

    }



    private String getPathWithoutFileName(StorageObject so) {
        return so.getPath().substring(0, so.getPath().length() - so.getPropertyValue(StoragePropertyNames.NAME.value()).toString().length() - 1);
    }

    private void caricaDocumento(MultiValueMap params, String tipoDocumento, StorageObject so, String statoFlusso, String nomeDocumentoFlows) {
        if (so != null) {
            params.add(nomeDocumentoFlows + "_label", Costanti.TIPO_DOCUMENTO_FLOWS.get(tipoDocumento));
            params.add(nomeDocumentoFlows + "_nodeRef", so.getKey());
            params.add(nomeDocumentoFlows + "_mimetype", so.getPropertyValue(StoragePropertyNames.CONTENT_STREAM_MIME_TYPE.value()));
            params.add(nomeDocumentoFlows + "_aggiorna", "true");
            params.add(nomeDocumentoFlows + "_path", so.getPath());
            params.add(nomeDocumentoFlows + "_filename", so.getPropertyValue(StoragePropertyNames.NAME.value()));
            if (missioniCMISService.isDocumentoEliminato(so)) {
                params.add(nomeDocumentoFlows + "_stati_json", "Annullato");
            } else if (Costanti.TIPI_DOCUMENTO_FLOWS_DA_FIRMARE.contains(tipoDocumento) && Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO.equals(statoFlusso)) {
                params.add(nomeDocumentoFlows + "_stati_json", "[]");
            }
        }
    }
}
