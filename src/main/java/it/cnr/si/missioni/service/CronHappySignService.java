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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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


    public void aggiornaEsistiMissioni(){
        MissioneFilter filtro = new MissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        //da testare anche per rimborso e annullamento
//        filtro.setListaStatiMissione(new ArrayList<>());
//        filtro.setListaStatiFlussoMissione(new ArrayList<>());
//
//        filtro.getListaStatiMissione().addAll(Arrays.asList(
//                Costanti.STATO_INSERITO,
//                Costanti.STATO_ANNULLATO,
//                Costanti.STATO_CONFERMATO
//        ));
//
//        filtro.getListaStatiFlussoMissione().addAll(Arrays.asList(
//                Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO,
//                Costanti.STATO_INVIATO_FLUSSO
//        ));
        filtro.setDaCron("S");
        List<OrdineMissione> listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(filtro, false, false);
        if ( Optional.ofNullable(listaOrdiniMissione).isPresent()){
            for ( OrdineMissione ordineMissione:listaOrdiniMissione) {
                GetStatusRequest request = new GetStatusRequest();
                request.setUuid(ordineMissione.getIdFlusso());
                EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus= null;
                try {
                    esitoFlowDocumentStatus = happySignService.getDocumentStatus(ordineMissione.getIdFlusso());

                      if ( EnumEsitoFlowDocumentStatus.SIGNED==esitoFlowDocumentStatus) {
                        GetDocumentResponse getDocumentResponse=happySignService.getDocument(ordineMissione.getIdFlusso());
                        if ( getDocumentResponse.getDocument()!=null){
                            StorageObject so=cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(
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
                    if ( EnumEsitoFlowDocumentStatus.REFUSED==esitoFlowDocumentStatus){
                        GetDocumentDetailResponse documentDetails = happySignService.getDocumentDetails(ordineMissione.getIdFlusso());
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(ordineMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                        Optional<SignersDocumentDetails> firstSigner = Arrays.stream(documentDetails.getSigners()).findFirst();
                        String noteRespinta = firstSigner.map(SignersDocumentDetails::getNote).orElse("Respinta da firma su HappySign");
                        flowResult.setCommento(noteRespinta);
                        flowResult.setUser("Utente Flusso Firma");
                        flowService.aggiornaMissioneFlows(flowResult);

                    }
                } catch (Exception e) {
                    log.error("ordineMissione:"+ordineMissione.getIdFlusso(),e);
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
        if ( Optional.ofNullable(listaRimborsiMissione).isPresent()){
            for ( RimborsoMissione rimborsoMissione:listaRimborsiMissione) {

                EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus= null;
                try {
                    esitoFlowDocumentStatus = happySignService.getDocumentStatus(rimborsoMissione.getIdFlusso());
                    if ( EnumEsitoFlowDocumentStatus.SIGNED==esitoFlowDocumentStatus) {
                        GetDocumentResponse getDocumentResponse=happySignService.getDocument(rimborsoMissione.getIdFlusso());
                        if ( getDocumentResponse.getDocument()!=null){

                            StorageObject so=cmisRimborsoMissioneService.salvaStampaRimborsoMissioneSuCMIS(
                                    getDocumentResponse.getDocument(),
                                    rimborsoMissione
                            );
                        }
                        //aggiorna file missione on Azure Cloud
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(rimborsoMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                        flowService.aggiornaMissioneFlows(flowResult);
                    }
                    if (  EnumEsitoFlowDocumentStatus.REFUSED==esitoFlowDocumentStatus) {
                        GetDocumentDetailResponse documentDetails = happySignService.getDocumentDetails(rimborsoMissione.getIdFlusso());
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(rimborsoMissione.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                        flowResult.setCommento(documentDetails.getCancelnote());
                        flowResult.setUser("Utente Flusso Firma");
                        flowService.aggiornaMissioneFlows(flowResult);

                    }
                } catch (Exception e) {
                    log.error("Rimborso Missione:"+rimborsoMissione.getIdFlusso(),e);
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
        if ( Optional.ofNullable(listaAnnullamentiOrdini).isPresent()){
            for ( AnnullamentoOrdineMissione annullamentoOrdine:listaAnnullamentiOrdini) {

                EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus= null;
                try {
                    esitoFlowDocumentStatus = happySignService.getDocumentStatus(annullamentoOrdine.getIdFlusso());
                    if ( EnumEsitoFlowDocumentStatus.SIGNED==esitoFlowDocumentStatus) {
                        GetDocumentResponse getDocumentResponse=happySignService.getDocument(annullamentoOrdine.getIdFlusso());
                        if ( getDocumentResponse.getDocument()!=null){

                            StorageObject so=cmisOrdineMissioneService.salvaStampaAnnullamentoOrdineMissioneSuCMIS(
                                    getDocumentResponse.getDocument(),
                                    annullamentoOrdine
                            );
                        }
                        //aggiorna file missione on Azure Cloud
                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(annullamentoOrdine.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                        flowService.aggiornaMissioneFlows(flowResult);
                    }
                    if (  EnumEsitoFlowDocumentStatus.REFUSED==esitoFlowDocumentStatus) {
                        GetDocumentDetailResponse documentDetails = happySignService.getDocumentDetails(annullamentoOrdine.getIdFlusso());

                        FlowResult flowResult = new FlowResult();
                        flowResult.setIdMissione(annullamentoOrdine.getId().toString());
                        flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                        flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                        flowResult.setCommento(documentDetails.getCancelnote());
                        flowResult.setUser("Utente Flusso Firma");
                        flowService.aggiornaMissioneFlows(flowResult);

                    }
                } catch (Exception e) {
                    log.error("Annullamento Ordine Missione:"+annullamentoOrdine.getIdFlusso(),e);
                }
            }
        }
    }
}
