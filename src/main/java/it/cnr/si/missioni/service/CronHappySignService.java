package it.cnr.si.missioni.service;

import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.dto.happysign.base.EnumEsitoFlowDocumentStatus;
import it.iss.si.dto.happysign.base.SignersDocumentDetails;
import it.iss.si.dto.happysign.request.GetStatusRequest;
import it.iss.si.dto.happysign.response.GetDocumentDetailResponse;
import it.iss.si.dto.happysign.response.GetDocumentResponse;
import it.iss.si.service.HappySignURLCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Conditional(HappySignURLCondition.class)
public class CronHappySignService {
    private static final Logger log = LoggerFactory.getLogger(CronHappySignService.class);
    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired(required = false)
    private it.iss.si.service.HappySignService happySignService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private OrdineMissioneService ordineMissioneService;
    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;
    @Autowired
    private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    @Autowired
    private CMISOrdineMissioneService cmisOrdineMissioneService;

    @Autowired
    private CMISRimborsoMissioneService cmisRimborsoMissioneService;


    //public final AtomicInteger errorCount = new AtomicInteger(0);

    public void aggiornaEsistiMissioni(){
        MissioneFilter filtro = new MissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");
        List<OrdineMissione> listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(filtro, false, false);
        if ( Optional.ofNullable(listaOrdiniMissione).isPresent()){
            for ( OrdineMissione ordineMissione:listaOrdiniMissione) {
                try {
                    // Verifica che l'ID flusso non sia null prima di procedere
                    if (ordineMissione.getIdFlusso() == null || ordineMissione.getIdFlusso().isEmpty()) {
                        log.warn("OrdineMissione {} ha idFlusso null o vuoto, salto elaborazione", ordineMissione.getId());
                        continue;
                    }

                    // Verifica che il servizio HappySign sia disponibile
                    if (happySignService == null) {
                        log.warn("HappySignService non disponibile per ordineMissione {}", ordineMissione.getId());
                        continue;
                    }

                    EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus = null;

                    // Chiamata protetta al servizio
                    esitoFlowDocumentStatus = happySignService.getDocumentStatus(ordineMissione.getIdFlusso());

                    // Verifica che il risultato non sia null
                    if (esitoFlowDocumentStatus == null) {
                        log.warn("getDocumentStatus ha ritornato null per ordineMissione {} con idFlusso {}",
                                ordineMissione.getId(), ordineMissione.getIdFlusso());
                        continue;
                    }

                    if ( EnumEsitoFlowDocumentStatus.SIGNED == esitoFlowDocumentStatus) {
                        GetDocumentResponse getDocumentResponse = happySignService.getDocument(ordineMissione.getIdFlusso());
                        if ( getDocumentResponse != null && getDocumentResponse.getDocument() != null){
                            StorageObject so = cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(
                                    getDocumentResponse.getDocument(),
                                    ordineMissione
                            );
                        }
                        //aggiorna file missione on Azure Cloud
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(ordineMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                        flowService.aggiornaMissioneFlows(flowResult);
                    }

                    if ( EnumEsitoFlowDocumentStatus.REFUSED == esitoFlowDocumentStatus){
                        GetDocumentDetailResponse documentDetails = happySignService.getDocumentDetails(ordineMissione.getIdFlusso());
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(ordineMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                        setNoteMissioneRespinta(documentDetails, flowResult);
                        flowResult.setUser("Utente Flusso Firma");
                        flowService.aggiornaMissioneFlows(flowResult);
                    }
                } catch (Exception e) {
                    // Log dell'errore ma continua con il prossimo elemento
                    log.error("Errore durante elaborazione ordineMissione id={}, idFlusso={}: {}",
                            ordineMissione.getId(),
                            ordineMissione.getIdFlusso(),
                            e.getMessage());
                    // Log del dettaglio solo in debug per non inquinare i log
                    log.debug("Stack trace completo per ordineMissione " + ordineMissione.getId(), e);
                }
            }
        }
    }

    public void aggiornaEsistiRimborsiMissioni(){
        RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");
        List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(filtro, false, false);

        if (Optional.ofNullable(listaRimborsiMissione).isPresent()){
            for (RimborsoMissione rimborsoMissione : listaRimborsiMissione) {
                try {
                    // Verifica che l'ID flusso non sia null prima di procedere
                    if (rimborsoMissione.getIdFlusso() == null || rimborsoMissione.getIdFlusso().isEmpty()) {
                        log.warn("RimborsoMissione {} ha idFlusso null o vuoto, salto elaborazione", rimborsoMissione.getId());
                        continue;
                    }

                    // Verifica che il servizio HappySign sia disponibile
                    if (happySignService == null) {
                        log.warn("HappySignService non disponibile per rimborsoMissione {}", rimborsoMissione.getId());
                        continue;
                    }

                    // Chiamata protetta al servizio
                    EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus = happySignService.getDocumentStatus(rimborsoMissione.getIdFlusso());

                    // Verifica che il risultato non sia null
                    if (esitoFlowDocumentStatus == null) {
                        log.warn("getDocumentStatus ha ritornato null per rimborsoMissione {} con idFlusso {}",
                                rimborsoMissione.getId(), rimborsoMissione.getIdFlusso());
                        continue;
                    }

                    if (EnumEsitoFlowDocumentStatus.SIGNED == esitoFlowDocumentStatus) {
                        GetDocumentResponse getDocumentResponse = happySignService.getDocument(rimborsoMissione.getIdFlusso());
                        if (getDocumentResponse != null && getDocumentResponse.getDocument() != null){
                            StorageObject so = cmisRimborsoMissioneService.salvaStampaRimborsoMissioneSuCMIS(
                                    getDocumentResponse.getDocument(),
                                    rimborsoMissione
                            );
                        }
                        // Aggiorna file missione on Azure Cloud
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(rimborsoMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                        flowService.aggiornaMissioneFlows(flowResult);
                    }

                    if (EnumEsitoFlowDocumentStatus.REFUSED == esitoFlowDocumentStatus) {
                        GetDocumentDetailResponse documentDetails = happySignService.getDocumentDetails(rimborsoMissione.getIdFlusso());
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(rimborsoMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                        setNoteMissioneRespinta(documentDetails, flowResult);
                        flowResult.setUser("Utente Flusso Firma");
                        flowService.aggiornaMissioneFlows(flowResult);
                    }
                } catch (Exception e) {
                    // Log dell'errore ma continua con il prossimo elemento
                    log.error("Errore durante elaborazione rimborsoMissione id={}, idFlusso={}: {}",
                            rimborsoMissione.getId(),
                            rimborsoMissione.getIdFlusso(),
                            e.getMessage());
                    // Log del dettaglio solo in debug per non inquinare i log
                    log.debug("Stack trace completo per rimborsoMissione " + rimborsoMissione.getId(), e);
                }
            }
        }
    }

    public void aggiornaEsistiAnnullamentiMissioni(){
        RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");
        List<AnnullamentoOrdineMissione> listaAnnullamentiOrdini = annullamentoOrdineMissioneService.getAnnullamenti(filtro, false, false);

        if (Optional.ofNullable(listaAnnullamentiOrdini).isPresent()){
            for (AnnullamentoOrdineMissione annullamentoOrdine : listaAnnullamentiOrdini) {
                try {
                    // Verifica che l'ID flusso non sia null prima di procedere
                    if (annullamentoOrdine.getIdFlusso() == null || annullamentoOrdine.getIdFlusso().isEmpty()) {
                        log.warn("AnnullamentoOrdineMissione {} ha idFlusso null o vuoto, salto elaborazione", annullamentoOrdine.getId());
                        continue;
                    }

                    // Verifica che il servizio HappySign sia disponibile
                    if (happySignService == null) {
                        log.warn("HappySignService non disponibile per annullamentoOrdine {}", annullamentoOrdine.getId());
                        continue;
                    }

                    // Chiamata protetta al servizio
                    EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus = happySignService.getDocumentStatus(annullamentoOrdine.getIdFlusso());

                    // Verifica che il risultato non sia null
                    if (esitoFlowDocumentStatus == null) {
                        log.warn("getDocumentStatus ha ritornato null per annullamentoOrdine {} con idFlusso {}",
                                annullamentoOrdine.getId(), annullamentoOrdine.getIdFlusso());
                        continue;
                    }

                    if (EnumEsitoFlowDocumentStatus.SIGNED == esitoFlowDocumentStatus) {
                        GetDocumentResponse getDocumentResponse = happySignService.getDocument(annullamentoOrdine.getIdFlusso());
                        if (getDocumentResponse != null && getDocumentResponse.getDocument() != null){
                            StorageObject so = cmisOrdineMissioneService.salvaStampaAnnullamentoOrdineMissioneSuCMIS(
                                    getDocumentResponse.getDocument(),
                                    annullamentoOrdine
                            );
                        }
                        // Aggiorna file missione on Azure Cloud
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(annullamentoOrdine.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                        flowService.aggiornaMissioneFlows(flowResult);
                    }

                    if (EnumEsitoFlowDocumentStatus.REFUSED == esitoFlowDocumentStatus) {
                        GetDocumentDetailResponse documentDetails = happySignService.getDocumentDetails(annullamentoOrdine.getIdFlusso());
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(annullamentoOrdine.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                        setNoteMissioneRespinta(documentDetails, flowResult);
                        flowResult.setUser("Utente Flusso Firma");
                        flowService.aggiornaMissioneFlows(flowResult);
                    }
                } catch (Exception e) {
                    // Log dell'errore ma continua con il prossimo elemento
                    log.error("Errore durante elaborazione annullamentoOrdine id={}, idFlusso={}: {}",
                            annullamentoOrdine.getId(),
                            annullamentoOrdine.getIdFlusso(),
                            e.getMessage());
                    // Log del dettaglio solo in debug per non inquinare i log
                    log.debug("Stack trace completo per annullamentoOrdine " + annullamentoOrdine.getId(), e);
                }
            }
        }
    }


    /**
     * Imposta nel FlowResult la nota del primo firmatario che ha rifiutato la firma.
     * Controlla il campo "operation" dei firmatari per individuare chi ha rifiutato
     * (REFUSED o contiene "ha rifiutato in firma") e usa la sua nota se presente,
     * altrimenti imposta un messaggio di default.
     */
    private void setNoteMissioneRespinta(
            GetDocumentDetailResponse documentDetails,
            FlowResult flowResult) {

        String noteRespinta = Arrays.stream(documentDetails.getSigners())
                .filter(signer -> {
                    String operation = signer.getOperation();
                    return operation != null && (
                            operation.equalsIgnoreCase(EnumEsitoFlowDocumentStatus.REFUSED.name())
                                    || operation.toLowerCase().contains("ha rifiutato in firma")
                    );
                })
                .map(SignersDocumentDetails::getNote)
                .filter(Objects::nonNull)
                .filter(note -> !note.isEmpty())
                .findFirst()
                .orElse("Respinta da firma su HappySign");

        flowResult.setCommento(noteRespinta);
    }


}
