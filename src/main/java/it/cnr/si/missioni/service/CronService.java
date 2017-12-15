package it.cnr.si.missioni.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.service.ComunicaRimborsoSiglaService;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.security.Principal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class CronService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronService.class);

    @Value("${cron.comunicaDati.name}")
    private String lockKeyComunicaDati;
    
    @Value("${cron.evictCache.name}")
    private String lockKeyEvictCache;
    
    @Value("${cron.loadCache.name}")
    private String lockKeyLoadCache;
    
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
	private ComunicaRimborsoSiglaService comunicaRimborsoSiglaService;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private FlowsService flowsService;
	
    @Autowired
    private DatiIstitutoService datiIstitutoService;

	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private RimborsoMissioneService rimborsoMissioneService;

	@CacheEvict(value = Costanti.NOME_CACHE_PROXY, allEntries = true)
	public void evictCache() throws ComponentException {
		ILock lock = hazelcastInstance.getLock(lockKeyEvictCache);
		LOGGER.info("requested lock: " + lock.getPartitionKey());

		try {
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				LOGGER.info("got lock {}", lockKeyEvictCache);
				LOGGER.info("Cron per Svuotare la Cache");
			}


		} catch (Exception e) {
			LOGGER.error("Errore", e);
			throw new ComponentException(e);
		}
	}

	@Transactional
	public void loadCache() throws ComponentException {
		ILock lock = hazelcastInstance.getLock(lockKeyLoadCache);
		LOGGER.info("requested lock: " + lock.getPartitionKey());

		try {
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				LOGGER.info("got lock {}", lockKeyLoadCache);

				try {
					LOGGER.info("Cron per Caricare la Cache");

					cacheService.loadInCache();

					LOGGER.info("Cron per Caricare la Cache terminato");
				} finally {
					LOGGER.info("unlocking {}", lockKeyLoadCache);
					lock.unlock();
				}

			} else {
				LOGGER.warn("unable to get lock {}", lockKeyLoadCache);
			}
		} catch (Exception e) {
			LOGGER.error("Errore", e);
			throw new ComponentException(e);
		}
	}

    @Transactional
	public void verificaFlussoEComunicaDatiRimborsoSigla(Principal principal) throws ComponentException {
        ILock lock = hazelcastInstance.getLock(lockKeyComunicaDati);
        LOGGER.info("requested lock: " + lock.getPartitionKey());

			try {
				if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				    LOGGER.info("got lock {}", lockKeyComunicaDati);

				    try {
						LOGGER.info("Cron per Aggiornamenti Ordine Missione");

						aggiornaOrdiniMissioneFlows(principal);

						aggiornaRimborsiMissioneFlows(principal);

						comunicaRimborsoSigla(principal);

				        LOGGER.info("work done.");
				    } finally {
				        LOGGER.info("unlocking {}", lockKeyComunicaDati);
						lock.unlock();
					}

				} else {
				    LOGGER.warn("unable to get lock {}", lockKeyComunicaDati);
				}
			} catch (Exception e) {
				LOGGER.error("Errore", e);
				throw new ComponentException(e);
			}
    }

	public void aggiornaRimborsiMissioneFlows(Principal principal) throws ComponentException {
		LOGGER.info("Cron per Aggiornamenti Rimborso Missione");
		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
		filtroRimborso.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtroRimborso.setValidato("S");
		filtroRimborso.setDaCron("S");
		List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(principal, filtroRimborso, false, true);
		if (listaRimborsiMissione != null){
			for (RimborsoMissione rimborsoMissione : listaRimborsiMissione){
				try {
					flowsService.aggiornaRimborsoMissioneFlowsNewTransaction(principal, rimborsoMissione.getId());
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

	public void comunicaRimborsoSigla(Principal principal) {
		LOGGER.info("Cron per Comunicazioni Rimborsi Missione");
		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
		filtroRimborso.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		filtroRimborso.setStatoInvioSigla(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE);
		filtroRimborso.setDaCron("S");
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
				if (rimborsoMissioneService.isMissioneComunicabileSigla(rimborsoMissione)){
					try {
						comunicaRimborsoSiglaService.comunicaRimborsoSigla(principal, rimborsoMissione.getId());
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
	}

	private void aggiornaOrdiniMissioneFlows(Principal principal) {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtro.setValidato("S");
		filtro.setDaCron("S");
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
					flowsService.aggiornaOrdineMissioneFlowsNewTransaction(principal,ordineMissione.getId());
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

	@Transactional
	public void verifyStep(Principal principal) throws ComponentException {
		ILock lock = hazelcastInstance.getLock(lockKeyLoadCache);
		LOGGER.info("requested lock: " + lock.getPartitionKey());

		try {
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				LOGGER.info("got lock {}", lockKeyLoadCache);

				try {
					LOGGER.info("Cron per Verificare gli step da eseguire");

					LOGGER.info("Cron per Verificare gli step da eseguire terminato");
				} finally {
					LOGGER.info("unlocking {}", lockKeyLoadCache);
					lock.unlock();
				}

			} else {
				LOGGER.warn("unable to get lock {}", lockKeyLoadCache);
			}
		} catch (Exception e) {
			LOGGER.error("Errore", e);
			throw new ComponentException(e);
		}
	}
}
