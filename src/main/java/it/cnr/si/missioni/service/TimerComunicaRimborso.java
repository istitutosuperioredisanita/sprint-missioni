package it.cnr.si.missioni.service;

import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.service.ComunicaRimborsoSiglaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.security.Principal;
import java.util.TimerTask;

public class TimerComunicaRimborso extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimerComunicaRimborso.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private ComunicaRimborsoSiglaService comunicaRimborsoSiglaService;

    @Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.oggetto}")
    private String subjectErrorComunicazioneRimborso;

    @Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.testo}")
    private String textErrorComunicazioneRimborso;

    private RimborsoMissione rimborsoMissione;
    private Principal principal;
    TimerComunicaRimborso(RimborsoMissione rimborsoMissione, Principal principal){
        this.principal = principal;
        this.rimborsoMissione = rimborsoMissione;
    }

    @Override
    public void run() {
        try {
            comunicaRimborsoSiglaService.comunicaRimborsoSigla(principal, rimborsoMissione.getId());
        } catch (Exception e) {
            String error = Utility.getMessageException(e);
            String testoErrore = "Errore nel lancio della comunicazione del rimborso a SIGLA";
            LOGGER.error(testoErrore+" "+e);
            try {
                mailService.sendEmailError("errore", testoErrore, false, true);
            } catch (Exception e1) {
                LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
            }
        }
    }
    private String getTextErrorComunicaRimborso(RimborsoMissione rimborsoMissione, String error) {
        return textErrorComunicazioneRimborso+" con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
    }
}
