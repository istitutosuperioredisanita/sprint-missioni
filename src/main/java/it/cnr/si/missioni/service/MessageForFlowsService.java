package it.cnr.si.missioni.service;

import it.cnr.si.flows.model.ProcessDefinitions;
import it.cnr.si.flows.model.StartWorkflowResponse;
import it.cnr.si.flows.model.TaskResponse;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.*;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageForFlowsService {
    private static final Log logger = LogFactory.getLog(MessageForFlowsService.class);

    @Autowired
    UoService uoService;

    @Autowired
    MissioniAceService missioniAceService;

    @Autowired
    DatiIstitutoService datiIstitutoService;

    @Autowired
    private FlowsService flowsService;

    @Autowired
    private MissioniCMISService missioniCMISService;

    public MessageForFlow impostaGruppiFirmatari(CMISMissione cmisMissione, MessageForFlow messageForFlows){
        Uo uoDatiSpesa = uoService.recuperoUo(cmisMissione.getUoSpesa());
        String uoRichSigla = cmisMissione.getUoRichSigla();
        String uoSpesaSigla = null;
        String uoRich = cmisMissione.getUoRich();
        String uoSpesa = null;
        if (uoDatiSpesa != null && uoDatiSpesa.getFirmaSpesa() != null && uoDatiSpesa.getFirmaSpesa().equals("N")){
            if (StringUtils.hasLength(cmisMissione.getUoCompetenza())){
                uoSpesa = cmisMissione.getUoCompetenza();
                uoSpesaSigla = cmisMissione.getUoCompetenzaSigla();
            } else {
                uoSpesa = cmisMissione.getUoRich();
                uoSpesaSigla = cmisMissione.getUoRichSigla();
            }
        } else {
            uoSpesa = cmisMissione.getUoSpesa();
            uoSpesaSigla = cmisMissione.getUoSpesaSigla();
        }

        String gruppoPrimoFirmatario = null;
        String gruppoSecondoFirmatario = null;

        DatiIstituto datiIstitutoUoSpesa = datiIstitutoService.getDatiIstituto(uoSpesaSigla, new Integer(cmisMissione.getAnno()));
        DatiIstituto datiIstitutoUoRich = datiIstitutoService.getDatiIstituto(uoRichSigla, new Integer(cmisMissione.getAnno()));

        SimplePersonaWebDto persona = missioniAceService.getPersona(cmisMissione.getUsernameUtenteOrdine());
        Integer idSede = persona.getSede().getId();
        String ruolo = Costanti.RUOLO_FIRMA;
        if (cmisMissione.isMissioneEstera() && !cmisMissione.isMissionePresidente()){
            ruolo = Costanti.RUOLO_FIRMA_ESTERE;
            if (uoRich.startsWith(Costanti.CDS_SAC)){
                Account direttore = uoService.getDirettore(uoRich);
                if (direttore.getUid().equals(cmisMissione.getUsernameUtenteOrdine())){
                    idSede = missioniAceService.getSedeResponsabileUtente(direttore.getUid());
                }
            }
        }

        if (cmisMissione.isMissioneGratuita()){
            gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
            gruppoSecondoFirmatario = gruppoPrimoFirmatario;
        } else {
            if (cmisMissione.isMissioneCug()){
                gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                SimpleEntitaOrganizzativaWebDto sedeCug = recuperoSedeCug();
                gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo, sedeCug.getId());
            } else if (cmisMissione.isMissionePresidente()){
                SimpleEntitaOrganizzativaWebDto sedePresidente = recuperoSedePresidenza();

                if (messageForFlows instanceof MessageForFlowRimborso){
                    gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                    gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo, sedePresidente.getId());
                } else {
                    gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, sedePresidente.getId());
                    gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                }
            } else {
                List<SimpleEntitaOrganizzativaWebDto> listaSediSpesa = missioniAceService.recuperoSediDaUo(uoSpesa);
                if (cmisMissione.getCdsRich().equals(cmisMissione.getCdsSpesa()) ){
                    if (Utility.nvl(datiIstitutoUoSpesa.getSaltaFirmaUosUoCds(),"N").equals("S") ){
                        SimpleEntitaOrganizzativaWebDto sedePrincipale = recuperoSedePrincipale(listaSediSpesa);
                        if (sedePrincipale != null){
                            gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, sedePrincipale.getId());
                            gruppoSecondoFirmatario = gruppoPrimoFirmatario;
                        }
                    } else if (Utility.nvl(datiIstitutoUoRich.getSaltaFirmaUosUoCds(),"N").equals("S")){
                        List<SimpleEntitaOrganizzativaWebDto> listaSediRich = missioniAceService.recuperoSediDaUo(uoRich);
                        SimpleEntitaOrganizzativaWebDto sedePrincipale = recuperoSedePrincipale(listaSediRich);
                        if (sedePrincipale != null){
                            gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, sedePrincipale.getId());
                            gruppoSecondoFirmatario = gruppoPrimoFirmatario;
                        }
                    } else {
                        gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                        gruppoSecondoFirmatario = recuperoGruppoSecondoFirmatarioStandard(uoSpesa, ruolo, idSede);
                    }
                } else {
                    gruppoPrimoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                    if (uoSpesa.equals(uoRich)){
                        gruppoSecondoFirmatario = costruisciGruppoFirmatario(ruolo, idSede);
                    } else {
                        gruppoSecondoFirmatario = recuperoGruppoSecondoFirmatarioStandard(uoSpesa, ruolo, idSede);
                    }
                }
            }
        }
        if (gruppoPrimoFirmatario == null){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è state recuperate un gruppo per il primo firmatario.");
        }

        if (gruppoSecondoFirmatario == null){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è state recuperate un gruppo per il secondo firmatario.");
        }

        messageForFlows.setGruppoFirmatarioUo(gruppoPrimoFirmatario);
        messageForFlows.setGruppoFirmatarioSpesa(gruppoSecondoFirmatario);

        return messageForFlows;
    }
    public String recuperoGruppoSecondoFirmatarioStandard(String uo, String ruolo, Integer idSedePrimoGruppoFirmatario){
        List<SimpleEntitaOrganizzativaWebDto> lista = missioniAceService.recuperoSediDaUo(uo);
        if (lista.size() == 0){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non sono state recuperate sedi per la uo "+uo);
        }
        SimpleEntitaOrganizzativaWebDto sede = recuperoSedePrincipale(lista);
        if (sede != null){
            return costruisciGruppoFirmatario(ruolo, sede.getId());
        } else {
            for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativaWebDto : lista){
                if (entitaOrganizzativaWebDto.getId().compareTo(idSedePrimoGruppoFirmatario) == 0){
                    return costruisciGruppoFirmatario(ruolo, entitaOrganizzativaWebDto.getId());
                }
            }
            return costruisciGruppoFirmatario(ruolo, lista.get(0).getId());
        }
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedeCug(){
        List<SimpleEntitaOrganizzativaWebDto> lista = missioniAceService.recuperoSediByTerm(Costanti.ACE_SIGLA_CUG);
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = Optional.ofNullable(lista.stream()
                .filter(entita -> {
                    return Costanti.ACE_SIGLA_CUG.equals(entita.getSigla());
                }).collect(Collectors.toList())).orElse(new ArrayList<SimpleEntitaOrganizzativaWebDto>());
        if (listaEntitaUo.size() == 0){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Entità organizzativa CUG non trovata.");
        } else if (listaEntitaUo.size() > 1){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Esistono più entità organizzativa CUG.");
        } else {
            return lista.get(0);
        }
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedePresidenza(){
        List<SimpleEntitaOrganizzativaWebDto> lista = missioniAceService.recuperoSediByTerm(Costanti.ACE_SIGLA_PRESIDENTE);
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = Optional.ofNullable(lista.stream()
                .filter(entita -> {
                    return Costanti.ACE_SIGLA_PRESIDENTE.equals(entita.getSigla());
                }).collect(Collectors.toList())).orElse(new ArrayList<SimpleEntitaOrganizzativaWebDto>());
        if (listaEntitaUo.size() == 0){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Entità organizzativa Presidenza non trovata.");
        } else if (listaEntitaUo.size() > 1){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Esistono più entità organizzativa Presidenza.");
        } else {
            return lista.get(0);
        }
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedePrincipale(List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo ){
        for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativaWebDto : listaEntitaUo){
            String tipoEntitaOrganizzativa = entitaOrganizzativaWebDto.getTipo().getSigla();
            if (tipoEntitaOrganizzativa.equals("UFF") ||
                    tipoEntitaOrganizzativa.equals("SPRINC")||
                    tipoEntitaOrganizzativa.equals("AREA")||
                    tipoEntitaOrganizzativa.equals("DIP")){
                return entitaOrganizzativaWebDto;
            }
        }
        return null;
    }
    private String costruisciGruppoFirmatario(String ruolo, Integer idSede){
        return ruolo+"@"+idSede;
    }

    public MultiValueMap<String, Object> aggiungiParametriRiavviaFlusso(MultiValueMap<String, Object> parameters , String idFlusso){
        preparaParametriPerRiavvioFlusso(parameters, idFlusso);
        parameters.add("sceltaUtente", "FirmaUo");
        return parameters;
    }

    private void preparaParametriPerRiavvioFlusso(MultiValueMap<String, Object> parameters, String idFlusso) {
        ResponseEntity<TaskResponse> taskIdResponse = flowsService.getTaskId(idFlusso);
        String taskId = taskIdResponse.getBody().getId();
        parameters.add("taskId", taskId);
        parameters.add("commento", "");
    }

    public void annullaFlusso(MultiValueMap<String, Object> parameters , String idFlusso){
        preparaParametriPerRiavvioFlusso(parameters, idFlusso);
        parameters.add("sceltaUtente", "Annulla");
        ResponseEntity<ProcessDefinitions> processDefinitions = flowsService.getProcessDefinitions(Costanti.NOME_PROCESSO_FLOWS_MISSIONI);
        if (processDefinitions.getStatusCode().is2xxSuccessful()){

            logger.info("Annulla Flusso. Parametri: "+parameters);
            ResponseEntity<StartWorkflowResponse> startWorkflowResponseResponseEntity = flowsService.startWorkflow(processDefinitions.getBody().getId(), parameters);
            if(!startWorkflowResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                String erroreFlows = "Errore Annullamento Flusso! "+ startWorkflowResponseResponseEntity.getStatusCode().value()+" per Id Flusso "+ idFlusso+" Status Code ritornato: "+startWorkflowResponseResponseEntity.getStatusCode().toString();
                logger.info(erroreFlows);
                throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
            }
        } else {
            String erroreFlows = "Errore Recupero Process Definitions! "+ processDefinitions.getStatusCode().value()+" per id Flusso "+idFlusso+" Status Code ritornato: "+processDefinitions.getStatusCode().toString();
            logger.info(erroreFlows);
            throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
        }
    }

    public String avviaFlusso(MultiValueMap<String, Object> parameters){
        String tipoFlusso = (String) ((List<Object>)parameters.get("tipologiaMissione")).get(0);
        String idMissione = (String) ((List<Object>)parameters.get("idMissione")).get(0);

        ResponseEntity<ProcessDefinitions> processDefinitions = flowsService.getProcessDefinitions(Costanti.NOME_PROCESSO_FLOWS_MISSIONI);
        if (processDefinitions.getStatusCode().is2xxSuccessful()){

            logger.info("Avvio Flusso. Parametri: "+parameters);
            ResponseEntity<StartWorkflowResponse> startWorkflowResponseResponseEntity = flowsService.startWorkflow(processDefinitions.getBody().getId(), parameters);
            if(startWorkflowResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                logger.info(tipoFlusso+" missione "+idMissione+" inviato alla firma");
                if (startWorkflowResponseResponseEntity.getBody() != null){
                    return startWorkflowResponseResponseEntity.getBody().getId();
                }
                return "";
            } else {
                String erroreFlows = "Errore Flows! "+ startWorkflowResponseResponseEntity.getStatusCode().value()+" per "+ tipoFlusso+" missione "+ idMissione+" Status Code ritornato: "+startWorkflowResponseResponseEntity.getStatusCode().toString();
                logger.info(erroreFlows);
                throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
            }
        } else {
            String erroreFlows = "Errore Recupero Process Definitions! "+ processDefinitions.getStatusCode().value()+" per "+tipoFlusso+" missione "+ idMissione+" Status Code ritornato: "+processDefinitions.getStatusCode().toString();
            logger.info(erroreFlows);
            throw new AwesomeException(CodiciErrore.ERRGEN, erroreFlows);
        }

    }
    public void aggiungiDocumentiMultipli(List<StorageObject> allegati,
                                  MultiValueMap params, String tipoDocumento) {
        if (allegati != null && !allegati.isEmpty()){
            int i = 0;
            for (StorageObject so : allegati){
                caricaDocumento(params, tipoDocumento, so, null,tipoDocumento+i);
                i++;
            }
        }
    }

    public void caricaDocumento(MultiValueMap params, String tipoDocumento, StorageObject so, String statoFlusso){
        caricaDocumento(params, tipoDocumento, so, statoFlusso, tipoDocumento);
    }

    private String getPathWithoutFileName(StorageObject so) {
        return so.getPath().substring(0, so.getPath().length() - so.getPropertyValue(StoragePropertyNames.NAME.value()).toString().length() -1);
    }

    private void caricaDocumento(MultiValueMap params, String tipoDocumento, StorageObject so, String statoFlusso, String nomeDocumentoFlows){
        if (so != null){
            params.add(nomeDocumentoFlows+"_label", Costanti.TIPO_DOCUMENTO_FLOWS.get(tipoDocumento));
            params.add(nomeDocumentoFlows+"_nodeRef", so.getKey());
            params.add(nomeDocumentoFlows+"_mimetype", so.getPropertyValue(StoragePropertyNames.CONTENT_STREAM_MIME_TYPE.value()));
            params.add(nomeDocumentoFlows+"_aggiorna", "true");
            params.add(nomeDocumentoFlows+"_path", so.getPath());
            params.add(nomeDocumentoFlows+"_filename", so.getPropertyValue(StoragePropertyNames.NAME.value()));
            if (missioniCMISService.isDocumentoEliminato(so)){
                params.add(nomeDocumentoFlows+"_stati_json", "Annullato");
            } else if (Costanti.TIPI_DOCUMENTO_FLOWS_DA_FIRMARE.contains(tipoDocumento) && Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO.equals(statoFlusso)){
                params.add(nomeDocumentoFlows+"_stati_json", "[]");
            }
        }
    }
}
