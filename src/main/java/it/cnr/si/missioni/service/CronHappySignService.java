package it.cnr.si.missioni.service;

import feign.FeignException;
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
import it.iss.si.signature.flow.condition.SignatureDriverIsHappySign;
import it.iss.si.signature.flow.model.SignatureStatus;
import it.iss.si.signature.flow.model.SignatureStatusDetail;
import it.iss.si.signature.flow.service.SignatureService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Conditional(SignatureDriverIsHappySign.class)
public class CronHappySignService {
    private static final Logger log = LoggerFactory.getLogger(CronHappySignService.class);

    /**
     * Motivo di respinta usato quando signature-flow non espone la nota dei firmatari.
     */
    private static final String MOTIVO_RESPINTA_DEFAULT = "Respinta da firma su HappySign";

    /**
     * Ultimo errore notificato per ogni documento (chiave: tipo-identificativo).
     * Serve a non reinviare la mail di errore ad ogni esecuzione del cron.
     * Stato in memoria: al riavvio dell'applicazione le notifiche vengono reinviate una volta.
     */
    private final Map<String, String> ultimoErroreNotificato = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private SignatureService signatureService;
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
    @Autowired(required = false)
    private MailService mailService;

    private boolean isTransientError(Throwable e) {
        Throwable current = e;
        while (current != null) {
            if (current instanceof SocketTimeoutException || current instanceof ConnectException || current instanceof RetryableException) return true;
            if (current instanceof FeignException && (((FeignException) current).status() == 404 || ((FeignException) current).status() >= 500)) return true;
            if (current instanceof org.springframework.dao.OptimisticLockingFailureException ||
                    current instanceof jakarta.persistence.OptimisticLockException ||
                    current instanceof org.springframework.dao.PessimisticLockingFailureException ||
                    current instanceof jakarta.persistence.PessimisticLockException ||
                    current instanceof org.springframework.dao.CannotAcquireLockException) return true;
            if (current.getMessage() != null && current.getMessage().contains("in modifica. Ripetere l'operazione")) return true;
            current = current.getCause();
        }
        return false;
    }

    private void inviaMailErroreCron(String tipoDocumento, String identificativo, Exception e) {
        if (mailService != null) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            String body = "<h3>Errore Elaborazione HappySign</h3><p><b>Documento:</b> " + tipoDocumento + " " + identificativo + "</p><p>L'aggiornamento automatico ha incontrato un errore grave non risolvibile automaticamente.</p><h4>Stack Trace:</h4><pre>" + stackTrace + "</pre>";
            mailService.sendEmailError("Errore Cron HappySign - " + tipoDocumento, body, false, true);
        }
    }

    /**
     * Invia la mail di errore solo se l'errore è nuovo per quel documento.
     * Se lo stesso errore si ripresenta ad ogni esecuzione del cron, la mail non viene ripetuta.
     */
    private void notificaErroreUnaSola(String tipoDocumento, String identificativo, Exception e) {
        String chiave = tipoDocumento + "-" + identificativo;
        String firma = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
        if (firma.equals(ultimoErroreNotificato.put(chiave, firma))) {
            log.debug("Errore già notificato per {}, mail non reinviata", chiave);
            return;
        }
        inviaMailErroreCron(tipoDocumento, identificativo, e);
    }

    /**
     * Stato e nota in un'unica chiamata a signature-flow.
     * Restituisce null se HappySign non è raggiungibile o c'è un lock temporaneo: il controllo viene rimandato.
     */
    private SignatureStatusDetail safeGetSignatureStatusDetail(String idFlusso) throws Exception {
        try {
            return signatureService.getSignatureStatusDetail(idFlusso);
        } catch (Exception e) {
            if (isTransientError(e)) {
                log.debug("API HappySign non raggiungibile o lock temporaneo. Controllo rimandato. idFlusso={}", idFlusso);
                return null;
            }
            throw e;
        }
    }

    // Unico metodo pubblico richiamato dal CronService che fa girare i 3 flussi all'interno della stessa transazione

    public void aggiornaTuttiEsitiHappySign() {
        log.info("Inizio aggiornamento massivo esiti HappySign...");
        aggiornaOrdini();
        aggiornaRimborsi();
        aggiornaAnnullamenti();
        log.info("Fine aggiornamento massivo esiti HappySign.");
    }

    @Transactional
    private void aggiornaOrdini() {
        MissioneFilter filtro = new MissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");

        List<OrdineMissione> lista = ordineMissioneService.getOrdiniMissione(filtro, false, false);
        if (lista == null) return;

        for (OrdineMissione ordine : lista) {
            String identificativo = ordine.getAnno() + "-" + ordine.getNumero();
            try {
                if (ordine.getIdFlusso() == null || ordine.getIdFlusso().isEmpty() || signatureService == null) continue;
                SignatureStatusDetail dettaglio = safeGetSignatureStatusDetail(ordine.getIdFlusso());
                if (dettaglio == null || SignatureStatus.INVIATO == dettaglio.status()) continue;

                if (SignatureStatus.FIRMATO == dettaglio.status()) {
                    byte[] documento = signatureService.getSignedDocument(ordine.getIdFlusso());
                    cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(documento, ordine);
                    FlowResult fr = new FlowResult();
                    fr.setProcessInstanceId(ordine.getIdFlusso());
                    fr.setIdMissione(ordine.getId().toString());
                    fr.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                    fr.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                    fr.setCommento("");
                    flowService.aggiornaMissioneFlows(fr);
                } else if (SignatureStatus.RIFIUTATO == dettaglio.status()) {
                    FlowResult fr = new FlowResult();
                    fr.setIdMissione(ordine.getId().toString());
                    fr.setTipologiaMissione(FlowResult.TIPO_FLUSSO_ORDINE);
                    fr.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                    fr.setUser("Utente Flusso Firma");
                    fr.setCommento(motivoRespinta(dettaglio));
                    flowService.aggiornaMissioneFlows(fr);
                }
                // Elaborazione riuscita: l'eventuale errore precedente non è più attivo
                ultimoErroreNotificato.remove("Ordine Missione-" + identificativo);
            } catch (Exception e) {
                if (!isTransientError(e)) {
                    log.error("Errore elaborazione ordine id={}", ordine.getId(), e);
                    notificaErroreUnaSola("Ordine Missione", identificativo, e);
                }
            }
        }
    }

    @Transactional
    private void aggiornaRimborsi() {
        RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");

        List<RimborsoMissione> lista = rimborsoMissioneService.getRimborsiMissione(filtro, false, false);
        if (lista == null) return;

        for (RimborsoMissione rimborso : lista) {
            String identificativo = rimborso.getAnno() + "-" + rimborso.getNumero();
            try {
                if (rimborso.getIdFlusso() == null || rimborso.getIdFlusso().isEmpty() || signatureService == null) continue;
                SignatureStatusDetail dettaglio = safeGetSignatureStatusDetail(rimborso.getIdFlusso());
                if (dettaglio == null || SignatureStatus.INVIATO == dettaglio.status()) continue;

                if (SignatureStatus.FIRMATO == dettaglio.status()) {
                    byte[] documento = signatureService.getSignedDocument(rimborso.getIdFlusso());
                    cmisRimborsoMissioneService.salvaStampaRimborsoMissioneSuCMIS(documento, rimborso);
                    FlowResult fr = new FlowResult();
                    fr.setProcessInstanceId(rimborso.getIdFlusso());
                    fr.setIdMissione(rimborso.getId().toString());
                    fr.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                    fr.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                    fr.setCommento("");
                    flowService.aggiornaMissioneFlows(fr);
                } else if (SignatureStatus.RIFIUTATO == dettaglio.status()) {
                    FlowResult fr = new FlowResult();
                    fr.setIdMissione(rimborso.getId().toString());
                    fr.setTipologiaMissione(FlowResult.TIPO_FLUSSO_RIMBORSO);
                    fr.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                    fr.setUser("Utente Flusso Firma");
                    fr.setCommento(motivoRespinta(dettaglio));
                    flowService.aggiornaMissioneFlows(fr);
                }
                ultimoErroreNotificato.remove("Rimborso Missione-" + identificativo);
            } catch (Exception e) {
                if (!isTransientError(e)) {
                    log.error("Errore elaborazione rimborso id={}", rimborso.getId(), e);
                    notificaErroreUnaSola("Rimborso Missione", identificativo, e);
                }
            }
        }
    }

    @Transactional
    private void aggiornaAnnullamenti() {
        RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
        filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        filtro.setStato(Costanti.STATO_CONFERMATO);
        filtro.setDaCron("S");

        List<AnnullamentoOrdineMissione> lista = annullamentoOrdineMissioneService.getAnnullamenti(filtro, false, false);
        if (lista == null) return;

        for (AnnullamentoOrdineMissione annullamento : lista) {
            String identificativo = annullamento.getAnno() + "-" + annullamento.getNumero();
            try {
                if (annullamento.getIdFlusso() == null || annullamento.getIdFlusso().isEmpty() || signatureService == null) continue;
                SignatureStatusDetail dettaglio = safeGetSignatureStatusDetail(annullamento.getIdFlusso());
                if (dettaglio == null || SignatureStatus.INVIATO == dettaglio.status()) continue;

                if (SignatureStatus.FIRMATO == dettaglio.status()) {
                    byte[] documento = signatureService.getSignedDocument(annullamento.getIdFlusso());
                    cmisOrdineMissioneService.salvaStampaAnnullamentoOrdineMissioneSuCMIS(documento, annullamento);
                    FlowResult fr = new FlowResult();
                    fr.setProcessInstanceId(annullamento.getIdFlusso());
                    fr.setIdMissione(annullamento.getId().toString());
                    fr.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                    fr.setStato(FlowResult.ESITO_FLUSSO_FIRMATO);
                    fr.setCommento("");
                    flowService.aggiornaMissioneFlows(fr);
                } else if (SignatureStatus.RIFIUTATO == dettaglio.status()) {
                    FlowResult fr = new FlowResult();
                    fr.setIdMissione(annullamento.getId().toString());
                    fr.setTipologiaMissione(FlowResult.TIPO_FLUSSO_REVOCA);
                    fr.setStato(FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA);
                    fr.setUser("Utente Flusso Firma");
                    fr.setCommento(motivoRespinta(dettaglio));
                    flowService.aggiornaMissioneFlows(fr);
                }
                ultimoErroreNotificato.remove("Annullamento Ordine-" + identificativo);
            } catch (Exception e) {
                if (!isTransientError(e)) {
                    log.error("Errore elaborazione annullamento id={}", annullamento.getId(), e);
                    notificaErroreUnaSola("Annullamento Ordine", identificativo, e);
                }
            }
        }
    }

    private String motivoRespinta(SignatureStatusDetail dettaglio) {
        return dettaglio.note() != null && !dettaglio.note().isEmpty() ? dettaglio.note() : MOTIVO_RESPINTA_DEFAULT;
    }
}
