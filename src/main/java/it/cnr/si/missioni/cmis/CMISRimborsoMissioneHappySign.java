package it.cnr.si.missioni.cmis;

import feign.FeignException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.flows.happySign.AutorizzazioneRimborsoService;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
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

@Service
@Conditional(HappySignURLCondition.class)
public class CMISRimborsoMissioneHappySign extends AbstractCMISRimborsoMissioneService {
    private static final Log logger = LogFactory.getLog(CMISRimborsoMissioneHappySign.class);

    @Autowired
    AutorizzazioneRimborsoService autorizzazioneRimborsoService;

    @Autowired(required = false)
    private MailService mailService;

    @Override
    void sendRimborsoOrdineMissioneToSign(RimborsoMissione rimborsoMissione, CMISRimborsoMissione cmisRimborsoMissione, StorageObject documento, List<StorageObject> allegati, List<StorageObject> giustificativi) {
        try {
            if (isDevProfile() && Utility.nvl(datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno()).getTipoMailDopoOrdine(), "N").equals("C")) {
                rimborsoMissioneService.popolaCoda(rimborsoMissione);
            } else {
                List<StorageObject> signAllegati = new ArrayList<>();
                if (giustificativi != null && !giustificativi.isEmpty()) {
                    signAllegati.addAll(giustificativi);
                }
                if (allegati != null && !allegati.isEmpty()) {
                    signAllegati.addAll(allegati);
                }

                String idFlusso = autorizzazioneRimborsoService.sendAutorizzazione(rimborsoMissione, documento, signAllegati);

                if (!StringUtils.isEmpty(idFlusso)) {
                    rimborsoMissione.setIdFlusso(idFlusso);
                }
                rimborsoMissione.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
            }
        } catch (Exception e) {
            String documentId = rimborsoMissione.getAnno() + "-" + rimborsoMissione.getNumero();
            handleHappySignException(e, "del rimborso", documentId, "Missioni - Errore comunicazione HappySign per Rimborso Missione");
        }
        logger.info("sendRimborsoOrdineMissioneToSign completato");
    }

    @Override
    public void annullaFlusso(RimborsoMissione rimborsoMissione) throws AwesomeException {

    }

    private void handleHappySignException(Exception e, String contesto, String documentId, String subjectEmail) {
        logger.error("Errore durante l'invio " + contesto + " ad HappySign", e);

        String msgErrore = (e instanceof FeignException)
                ? "Servizio HappySign temporaneamente non raggiungibile."
                : "Impossibile contattare il servizio di firma.";

        String messaggioUtente = "Errore in fase di avvio del flusso documentale. " + msgErrore;

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