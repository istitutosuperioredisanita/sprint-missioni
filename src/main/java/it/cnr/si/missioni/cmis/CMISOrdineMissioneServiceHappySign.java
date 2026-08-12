package it.cnr.si.missioni.cmis;

import feign.FeignException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.flows.happySign.AutorizzazioneAnnulloService;
import it.cnr.si.missioni.cmis.flows.happySign.AutorizzazioneService;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.service.MailService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.spring.storage.StorageObject;
import it.iss.si.service.HappySignURLCondition;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Conditional(HappySignURLCondition.class)
public class CMISOrdineMissioneServiceHappySign extends AbstractCMISOrdineMissioneService {

    @Autowired
    AutorizzazioneService autorizzazioneService;

    @Autowired
    AutorizzazioneAnnulloService autorizzazioneAnnulloService;

    @Autowired(required = false)
    private MailService mailService;

    private static final Log logger = LogFactory.getLog(CMISOrdineMissioneServiceHappySign.class);

    protected void sendOrdineMissioneToSign(OrdineMissione ordineMissione, CMISOrdineMissione cmisOrdineMissione, Map<String, StorageObject> mapDocumentiMissione, List<StorageObject> allegati, OrdineMissioneAnticipo anticipo) {
        try {
            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                ordineMissioneService.popolaCoda(ordineMissione);
            } else {
                String idFlusso = autorizzazioneService.sendAutorizzazione(ordineMissione, mapDocumentiMissione.get(Costanti.DOCUMENTO_MISSIONE_KEY), getAllAllegati(mapDocumentiMissione, allegati, false));
                if (!StringUtils.isEmpty(idFlusso)) {
                    ordineMissione.setIdFlusso(idFlusso);
                    if (anticipo != null) {
                        anticipo.setIdFlusso(idFlusso);
                    }
                }
                ordineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            }
        } catch (Exception e) {
            String documentId = ordineMissione.getAnno() + "-" + ordineMissione.getNumero();
            handleHappySignException(e, "dell'ordine", documentId, "Missioni - Errore comunicazione HappySign per Ordine Missione");
        }
        logger.info("sendOrdineMissioneToSign completato");
    }

    protected void sendAnnullamentoOrdineMissioneToSign(AnnullamentoOrdineMissione annullamentoOrdineMissione, CMISOrdineMissione cmisOrdineMissione,
                                                        Map<String, StorageObject> mapDocumentiAnnulloMissione,
                                                        List<StorageObject> allegati) {
        try {
            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(annullamentoOrdineMissione.getOrdineMissione().getUoSpesa(),
                    annullamentoOrdineMissione.getOrdineMissione().getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                annullamentoOrdineMissioneService.popolaCoda(annullamentoOrdineMissione);
            } else {
                String idFlusso = autorizzazioneAnnulloService.sendAutorizzazione(annullamentoOrdineMissione, mapDocumentiAnnulloMissione.get(Costanti.DOCUMENTO_ANNULLAMENTO_MISSIONE_KEY), getAllAllegati(null, allegati, true));
                if (!StringUtils.isEmpty(idFlusso)) {
                    annullamentoOrdineMissione.setIdFlusso(idFlusso);
                }
                annullamentoOrdineMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            }
        } catch (Exception e) {
            String documentId = annullamentoOrdineMissione.getAnno() + "-" + annullamentoOrdineMissione.getNumero();
            handleHappySignException(e, "dell'annullamento", documentId, "Missioni - Errore comunicazione HappySign per Annullamento Ordine Missione");
        }
        logger.info("sendAnnullamentoOrdineMissioneToSign completato");
    }

    @Override
    public Boolean isActiveSignFlow() {
        return true;
    }

    @Override
    public void annullaFlusso(OrdineMissione ordineMissione) { }

    private List<StorageObject> getAllAllegati(Map<String, StorageObject> mapDocumentiMissione, List<StorageObject> allegati, boolean annullamento) {
        List<StorageObject> allAllegati = new ArrayList<>();
        if (allegati != null && !allegati.isEmpty()) {
            allAllegati.addAll(allegati);
        }
        if (mapDocumentiMissione != null && !mapDocumentiMissione.isEmpty()) {
            for (String s : mapDocumentiMissione.keySet()) {
                boolean addAllegato = annullamento || !Costanti.DOCUMENTO_MISSIONE_KEY.equalsIgnoreCase(s);
                if (annullamento && Costanti.DOCUMENTO_ANNULLAMENTO_MISSIONE_KEY.equalsIgnoreCase(s)) addAllegato = false;
                if (addAllegato) allAllegati.add(mapDocumentiMissione.get(s));
            }
        }
        return allAllegati;
    }

    private void handleHappySignException(Exception e, String contesto, String documentId, String subjectEmail) {
        logger.error("Errore durante l'invio " + contesto + " ad HappySign", e);

        String msgErrore = (e instanceof FeignException)
                ? "Servizio HappySign temporaneamente non raggiungibile."
                : "Impossibile contattare il servizio di firma.";

        String messaggioUtente = "Errore in fase di preparazione del flusso documentale. " + msgErrore;

        if (mailService != null) {
            String content = "<h3><b>Segnalazione Errore HappySign</b></h3>" +
                    "<p><b>Riferimento Documento:</b> " + documentId + "</p>" +
                    "<p><b>Dettagli Tecnici (Stack Trace):</b></p>" +
                    "<pre>" + ExceptionUtils.getStackTrace(e) + "</pre>";

            mailService.sendEmailError(subjectEmail, content, false, true);
        }

        throw new AwesomeException(CodiciErrore.ERRGEN, messaggioUtente);
    }
}