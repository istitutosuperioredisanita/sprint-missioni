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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


public interface MessageForFlowsService {

    public List<String> getGruppiFirmatari(CMISMissione cmisMissione, Boolean isRimborso) ;

   // public MessageForFlow impostaGruppiFirmatari(CMISMissione cmisMissione, MessageForFlow messageForFlows) ;

    public String recuperoGruppoSecondoFirmatarioStandard(String uo, String ruolo, Integer idSedePrimoGruppoFirmatario, LocalDate dataInizioMissione);




    public MultiValueMap<String, Object> aggiungiParametriRiavviaFlusso(MultiValueMap<String, Object> parameters, String idFlusso);


    public void annullaFlusso(MultiValueMap<String, Object> parameters, String idFlusso);

    public String avviaFlusso(MultiValueMap<String, Object> parameters);
    public void aggiungiDocumentiMultipli(List<StorageObject> allegati,
                                          MultiValueMap params, String tipoDocumento);

    public void caricaDocumento(MultiValueMap params, String tipoDocumento, StorageObject so, String statoFlusso);


}
