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
import it.cnr.si.missioni.config.FlowIsHappySign;
import it.cnr.si.missioni.config.FlowIsScrivaniaDigitale;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.service.application.FlowsService;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Conditional(FlowIsHappySign.class)
public class MessageForFlowsServiceHappySign extends AbstractMessageForFlowsService{
    private static final Log logger = LogFactory.getLog(MessageForFlowsServiceHappySign.class);
    @Autowired
    private FlowsService flowsService;

    public MessageForFlow impostaGruppiFirmatari(CMISMissione cmisMissione, MessageForFlow messageForFlows) {
        return messageForFlows;
    }
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
        return "";
    }



    protected void preparaParametriPerRiavvioFlusso(MultiValueMap<String, Object> parameters, String idFlusso) {
        /*
        try {
            ResponseEntity<TaskResponse> taskIdResponse = flowsService.getTaskId(idFlusso);
            String taskId = taskIdResponse.getBody().getId();
            parameters.add("taskId", taskId);
            parameters.add("commento", "");
        } catch (HttpServerErrorException e) {
            logger.error("Non e' stato possibile recuperare il task attivo per il flusso " + idFlusso + ". Probabilmente il flusso e' terminato", e);
            throw new TaskIdNonTrovatoException(e);
        }
         */
        return;
    }

    public void annullaFlusso(MultiValueMap<String, Object> parameters, String idFlusso) {
        logger.info("MessageForFlowsServiceHappySign::annullaFlusso" );
    }

    public String avviaFlusso(MultiValueMap<String, Object> parameters) {
        logger.info("MessageForFlowsServiceHappySign::avviaFlusso" );
        return "avviaFlusso";
    }






}
