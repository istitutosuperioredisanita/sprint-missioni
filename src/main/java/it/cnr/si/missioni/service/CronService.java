package it.cnr.si.missioni.service;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneBulk;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.ComunicaRimborsoSiglaService;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class CronService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronService.class);

    @Value("${cron.comunicaDati.name}")
    private String lockKey;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoOrdine.oggetto}")
    private String subjectErrorFlowsOrdine;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoOrdine.testo}")
    private String textErrorFlowsOrdine;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoRimborso.oggetto}")
    private String subjectErrorFlowsRimborso;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoRimborso.testo}")
    private String textErrorFlowsRimborso;
    
    @Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.oggetto}")
    private String subjectErrorComunicazioneRimborso;
    
    @Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.testo}")
    private String textErrorComunicazioneRimborso;
    
    @Value("${spring.mail.messages.erroreGenerico.oggetto}")
    private String subjectGenericError;
    
    @Autowired
    private HazelcastInstance hazelcastInstance;

	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired
	private ComunicaRimborsoSiglaService comunicaRimborsoSiglaService;

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private FlowsService flowsService;
	
	@Autowired
	private RimborsoMissioneService rimborsoMissioneService;

	@Autowired
	private CMISRimborsoMissioneService cmisRimborsoMissioneService;

    @Transactional
	public void verificaFlussoEComunicaDatiRimborsoSigla(Principal principal) throws Exception {
        ILock lock = hazelcastInstance.getLock(lockKey);
        LOGGER.info("requested lock: " + lock.getPartitionKey());

        if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

            LOGGER.info("got lock {}", lockKey);

            try {
        		LOGGER.info("Cron per Aggiornamenti Ordine Missione");

        		aggiornaOrdiniMissioneFlows(principal);

        		aggiornaRimborsiMissioneFlows(principal);

        		comunicaRimborsoSigla(principal);

                LOGGER.info("work done.");
            } finally {
                LOGGER.info("unlocking {}", lockKey);
				lock.unlock();
			}

        } else {
            LOGGER.warn("unable to get lock {}", lockKey);
        }
    }

	public void aggiornaRimborsiMissioneFlows(Principal principal) throws Exception, CloneNotSupportedException {
		LOGGER.info("Cron per Aggiornamenti Rimborso Missione");
		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
		filtroRimborso.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtroRimborso.setValidato("S");
		List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(principal, filtroRimborso, false, true);
		if (listaRimborsiMissione != null){
			for (RimborsoMissione rimborsoMissione : listaRimborsiMissione){
				try {
					flowsService.aggiornaRimborsoMissioneFlowsNewTransaction(principal, rimborsoMissione);
				} catch (Exception e) {
					String error = Utility.getMessageException(e);
					String testoErrore = getTextErrorRimborso(rimborsoMissione, error);
					LOGGER.error(testoErrore+" "+e);
					try {
						mailService.sendEmailError(subjectErrorFlowsOrdine, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
					}
				}
			}
		}
	}

	public void comunicaRimborsoSigla(Principal principal) throws CloneNotSupportedException {
		LOGGER.info("Cron per Comunicazioni Rimborsi Missione");
		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
		filtroRimborso.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		filtroRimborso.setStatoInvioSigla(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE);
		List<RimborsoMissione> listaRimborsiMissione = null;
		try {
			listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(principal, filtroRimborso, false, true);
		} catch (ComponentException e2) {
			String error = Utility.getMessageException(e2);
			LOGGER.error(error+" "+e2);
			try {
				mailService.sendEmailError(subjectGenericError + this.toString(), error, false, true);
			} catch (Exception e1) {
				LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
			}
		}
		if (listaRimborsiMissione != null){
			for (RimborsoMissione rimborsoMissione : listaRimborsiMissione){
				try {
					MissioneBulk missione = comunicaRimborsoSiglaService.comunicaRimborsoSigla(principal, rimborsoMissione);
				} catch (Exception e) {
					String error = Utility.getMessageException(e);
					String testoErrore = getTextErrorComunicaRimborso(rimborsoMissione, error);
					LOGGER.error(testoErrore+" "+e);
					try {
						mailService.sendEmailError(subjectErrorComunicazioneRimborso, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
					}
				}
			}
		}
	}

	private void aggiornaOrdiniMissioneFlows(Principal principal) {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtro.setValidato("S");
		List<OrdineMissione> listaOrdiniMissione = null;
		try {
			listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(principal, filtro, false, true);
		} catch (ComponentException e2) {
			String error = Utility.getMessageException(e2);
			LOGGER.error(error + " "+e2);
			try {
				mailService.sendEmailError(subjectGenericError + this.toString(), error, false, true);
			} catch (Exception e1) {
				LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
			}
		}
		if (listaOrdiniMissione != null){
			for (OrdineMissione ordineMissione : listaOrdiniMissione){
				try {
					flowsService.aggiornaOrdineMissioneFlowsNewTransaction(principal,ordineMissione);
				} catch (Exception e) {
					String error = Utility.getMessageException(e);
					String testoErrore = getTextErrorOrdine(ordineMissione, error);
					LOGGER.error(testoErrore + " "+e);
					try {
						mailService.sendEmailError(subjectErrorFlowsOrdine, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
					}
				}
			}
		}
	}

	private String getTextErrorOrdine(OrdineMissione ordineMissione, String error) {
		return textErrorFlowsOrdine+" con id "+ordineMissione.getId()+ " "+ ordineMissione.getAnno()+"-"+ordineMissione.getNumero()+ " di "+ ordineMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}

	private String getTextErrorRimborso(RimborsoMissione rimborsoMissione, String error) {
		return textErrorFlowsRimborso+" con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}

	private String getTextErrorComunicaRimborso(RimborsoMissione rimborsoMissione, String error) {
		return textErrorComunicazioneRimborso+" con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}
}
