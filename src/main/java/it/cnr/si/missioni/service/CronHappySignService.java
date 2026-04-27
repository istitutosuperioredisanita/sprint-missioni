package it.cnr.si.missioni.service;

import feign.RetryableException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import it.iss.si.dto.happysign.base.EnumEsitoFlowDocumentStatus;
import it.iss.si.dto.happysign.base.SignersDocumentDetails;
import it.iss.si.dto.happysign.response.GetDocumentDetailResponse;
import it.iss.si.dto.happysign.response.GetDocumentResponse;
import it.iss.si.service.HappySignURLCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Conditional(HappySignURLCondition.class)
public class CronHappySignService {
    private static final Logger log = LoggerFactory.getLogger(CronHappySignService.class);

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

    // Errori di rete = controllo rimandato
    private boolean isTransientHappySignError(Throwable e) {
        Throwable current = e;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof ConnectException) {
                return true;
            }
            current = current.getCause();
        }
        return e instanceof RetryableException;
    }

    private EnumEsitoFlowDocumentStatus safeGetDocumentStatus(String idFlusso) throws Exception {
        try {
            return happySignService.getDocumentStatus(idFlusso);
        } catch (Exception e) {
            if (isTransientHappySignError(e)) {
                log.debug("HappySign non raggiungibile, controllo rimandato. idFlusso={}", idFlusso);
                return null;
            }

            throw e;
        }
    }

    public void aggiornaEsitiMissioni() {
        MissioneFilter filtro = new MissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");

        List<OrdineMissione> listaOrdiniMissione =
                ordineMissioneService.getOrdiniMissione(filtro, false, false);

        if (listaOrdiniMissione == null) {
            return;
        }

        for (OrdineMissione ordineMissione : listaOrdiniMissione) {
            try {
                if (ordineMissione.getIdFlusso() == null || ordineMissione.getIdFlusso().isEmpty()) {
                    log.warn("OrdineMissione {} ha idFlusso null o vuoto, salto elaborazione",
                            ordineMissione.getId());
                    continue;
                }

                if (happySignService == null) {
                    log.warn("HappySignService non disponibile per ordineMissione {}",
                            ordineMissione.getId());
                    continue;
                }

                EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus =
                        safeGetDocumentStatus(ordineMissione.getIdFlusso());

                if (esitoFlowDocumentStatus == null) {
                    continue;
                }

                if (EnumEsitoFlowDocumentStatus.TOSIGN == esitoFlowDocumentStatus) {
                    continue;
                }

                if (EnumEsitoFlowDocumentStatus.SIGNED == esitoFlowDocumentStatus) {
                    GetDocumentResponse getDocumentResponse =
                            safeGetDocument(ordineMissione.getIdFlusso());

                    if (getDocumentResponse == null) {
                        continue;
                    }

                    if (getDocumentResponse.getDocument() != null) {
                        cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(
                                getDocumentResponse.getDocument(),
                                ordineMissione
                        );
                    }

                    FlowResult flowResult = new FlowResult();
                    flowResult.setIdMissione(ordineMissione.getId().toString());
                    flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                    flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);

                    flowService.aggiornaMissioneFlows(flowResult);
                }

                if (EnumEsitoFlowDocumentStatus.REFUSED == esitoFlowDocumentStatus) {
                    GetDocumentDetailResponse documentDetails =
                            safeGetDocumentDetails(ordineMissione.getIdFlusso());

                    if (documentDetails == null) {
                        continue;
                    }

                    FlowResult flowResult = new FlowResult();
                    flowResult.setIdMissione(ordineMissione.getId().toString());
                    flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                    flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                    flowResult.setUser("Utente Flusso Firma");

                    setNoteMissioneRespinta(documentDetails, flowResult);

                    flowService.aggiornaMissioneFlows(flowResult);
                }

            } catch (Exception e) {
                if (isTransientHappySignError(e)) {
                    log.debug("Elaborazione rimandata per ordineMissione id={}, HappySign non raggiungibile",
                            ordineMissione.getId());
                } else {
                    log.error("Errore reale durante elaborazione ordineMissione id={}",
                            ordineMissione.getId(), e);
                }
            }
        }
    }

    public void aggiornaEsitiRimborsiMissioni() {
        RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");

        List<RimborsoMissione> listaRimborsiMissione =
                rimborsoMissioneService.getRimborsiMissione(filtro, false, false);

        if (listaRimborsiMissione == null) {
            return;
        }

        for (RimborsoMissione rimborsoMissione : listaRimborsiMissione) {
            try {
                if (rimborsoMissione.getIdFlusso() == null || rimborsoMissione.getIdFlusso().isEmpty()) {
                    log.warn("RimborsoMissione {} ha idFlusso null o vuoto, salto elaborazione",
                            rimborsoMissione.getId());
                    continue;
                }

                if (happySignService == null) {
                    log.warn("HappySignService non disponibile per rimborsoMissione {}",
                            rimborsoMissione.getId());
                    continue;
                }

                EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus =
                        safeGetDocumentStatus(rimborsoMissione.getIdFlusso());

                if (esitoFlowDocumentStatus == null) {
                    continue;
                }

                if (EnumEsitoFlowDocumentStatus.TOSIGN == esitoFlowDocumentStatus) {
                    continue;
                }

                if (EnumEsitoFlowDocumentStatus.SIGNED == esitoFlowDocumentStatus) {
                    GetDocumentResponse getDocumentResponse =
                            safeGetDocument(rimborsoMissione.getIdFlusso());

                    if (getDocumentResponse == null) {
                        continue;
                    }

                    if (getDocumentResponse.getDocument() != null) {
                        cmisRimborsoMissioneService.salvaStampaRimborsoMissioneSuCMIS(
                                getDocumentResponse.getDocument(),
                                rimborsoMissione
                        );
                    }

                    FlowResult flowResult = new FlowResult();
                    flowResult.setIdMissione(rimborsoMissione.getId().toString());
                    flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                    flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);

                    flowService.aggiornaMissioneFlows(flowResult);
                }

                if (EnumEsitoFlowDocumentStatus.REFUSED == esitoFlowDocumentStatus) {
                    GetDocumentDetailResponse documentDetails =
                            safeGetDocumentDetails(rimborsoMissione.getIdFlusso());

                    if (documentDetails == null) {
                        continue;
                    }

                    FlowResult flowResult = new FlowResult();
                    flowResult.setIdMissione(rimborsoMissione.getId().toString());
                    flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                    flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                    flowResult.setUser("Utente Flusso Firma");

                    setNoteMissioneRespinta(documentDetails, flowResult);

                    flowService.aggiornaMissioneFlows(flowResult);
                }

            } catch (Exception e) {
                if (isTransientHappySignError(e)) {
                    log.debug("Elaborazione rimandata per rimborsoMissione id={}, HappySign non raggiungibile",
                            rimborsoMissione.getId());
                } else {
                    log.error("Errore reale durante elaborazione rimborsoMissione id={}",
                            rimborsoMissione.getId(), e);
                }
            }
        }
    }

    public void aggiornaEsitiAnnullamentiMissioni() {
        RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");

        List<AnnullamentoOrdineMissione> listaAnnullamentiOrdini =
                annullamentoOrdineMissioneService.getAnnullamenti(filtro, false, false);

        if (listaAnnullamentiOrdini == null) {
            return;
        }

        for (AnnullamentoOrdineMissione annullamentoOrdine : listaAnnullamentiOrdini) {
            try {
                if (annullamentoOrdine.getIdFlusso() == null || annullamentoOrdine.getIdFlusso().isEmpty()) {
                    log.warn("AnnullamentoOrdineMissione {} ha idFlusso null o vuoto, salto elaborazione",
                            annullamentoOrdine.getId());
                    continue;
                }

                if (happySignService == null) {
                    log.warn("HappySignService non disponibile per annullamentoOrdine {}",
                            annullamentoOrdine.getId());
                    continue;
                }

                EnumEsitoFlowDocumentStatus esitoFlowDocumentStatus =
                        safeGetDocumentStatus(annullamentoOrdine.getIdFlusso());

                if (esitoFlowDocumentStatus == null) {
                    continue;
                }

                if (EnumEsitoFlowDocumentStatus.TOSIGN == esitoFlowDocumentStatus) {
                    continue;
                }

                if (EnumEsitoFlowDocumentStatus.SIGNED == esitoFlowDocumentStatus) {
                    GetDocumentResponse getDocumentResponse =
                            safeGetDocument(annullamentoOrdine.getIdFlusso());

                    if (getDocumentResponse == null) {
                        continue;
                    }

                    if (getDocumentResponse.getDocument() != null) {
                        cmisOrdineMissioneService.salvaStampaAnnullamentoOrdineMissioneSuCMIS(
                                getDocumentResponse.getDocument(),
                                annullamentoOrdine
                        );
                    }

                    FlowResult flowResult = new FlowResult();
                    flowResult.setIdMissione(annullamentoOrdine.getId().toString());
                    flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                    flowResult.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);

                    flowService.aggiornaMissioneFlows(flowResult);
                }

                if (EnumEsitoFlowDocumentStatus.REFUSED == esitoFlowDocumentStatus) {
                    GetDocumentDetailResponse documentDetails =
                            safeGetDocumentDetails(annullamentoOrdine.getIdFlusso());

                    if (documentDetails == null) {
                        continue;
                    }

                    FlowResult flowResult = new FlowResult();
                    flowResult.setIdMissione(annullamentoOrdine.getId().toString());
                    flowResult.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                    flowResult.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                    flowResult.setUser("Utente Flusso Firma");

                    setNoteMissioneRespinta(documentDetails, flowResult);

                    flowService.aggiornaMissioneFlows(flowResult);
                }

            } catch (Exception e) {
                if (isTransientHappySignError(e)) {
                    log.debug("Elaborazione rimandata per annullamentoOrdine id={}, HappySign non raggiungibile",
                            annullamentoOrdine.getId());
                } else {
                    log.error("Errore reale durante elaborazione annullamentoOrdine id={}",
                            annullamentoOrdine.getId(), e);
                }
            }
        }
    }

    /**
     * Imposta nel FlowResult la nota del primo firmatario che ha rifiutato la firma.
     * Controlla il campo operation e usa la nota se presente.
     */
    private void setNoteMissioneRespinta(GetDocumentDetailResponse documentDetails, FlowResult flowResult) {

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

    private GetDocumentResponse safeGetDocument(String idFlusso) {
        try {
            return happySignService.getDocument(idFlusso);
        } catch (Exception e) {
            if (isTransientHappySignError(e)) {
                log.debug("HappySign non raggiungibile durante download documento, controllo rimandato. idFlusso={}", idFlusso);
                return null;
            }
            throw e;
        }
    }

    private GetDocumentDetailResponse safeGetDocumentDetails(String idFlusso) {
        try {
            return happySignService.getDocumentDetails(idFlusso);
        } catch (Exception e) {
            if (isTransientHappySignError(e)) {
                log.debug("HappySign non raggiungibile durante lettura dettagli documento, controllo rimandato. idFlusso={}", idFlusso);
                return null;
            }
            throw e;
        }
    }
}