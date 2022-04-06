package it.cnr.si.missioni.service;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.cache.service.CacheService;
import it.cnr.si.missioni.util.proxy.json.service.ComunicaRimborsoSiglaService;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class CronService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronService.class);

    @Value("${cron.comunicaDati.name}")
    private String lockKeyComunicaDati;

	@Value("${cron.comunicaDatiVecchiaScrivania.name}")
	private String lockKeyComunicaDatiVecchiaScrivania;

	@Value("${cron.evictCache.name}")
    private String lockKeyEvictCache;
    
    @Value("${cron.loadCache.name}")
    private String lockKeyLoadCache;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoAnnullamento.oggetto}")
    private String subjectErrorFlowsAnnullamento;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoAnnullamento.testo}")
    private String textErrorFlowsAnnullamento;
    
    @Value("${spring.mail.messages.erroreLetturaFlussoOrdine.oggetto}")
    private String subjectErrorFlowsOrdine;
    
    @Value("${spring.mail.messages.erroreBypassStepRespGruppo.oggetto}")
    private String subjectErrorBypassResp;
    
    @Value("${spring.mail.messages.erroreBypassStepRespGruppo.testo}")
    private String textErrorBypassResp;
    
    @Value("${spring.mail.messages.erroreBypassStepAmm.oggetto}")
    private String subjectErrorBypassAmm;
    
    @Value("${spring.mail.messages.erroreBypassStepAmm.testo}")
    private String textErrorBypassAmm;
    
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
	private StepService stepService;
	
	@Autowired
	private MailService mailService;
	
	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

	@Autowired
	private FlowsMissioniService flowsMissioniService;
	
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

	@CacheEvict(value = Costanti.NOME_CACHE_TERZO_COMPENSO_SERVICE, allEntries = true)
	public void evictCacheTerzoCompenso() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Terzo Compenso");
	}

	@CacheEvict(value = Costanti.NOME_CACHE_DATI_PERSONE, allEntries = true)
	public void evictCachePersone() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Persone");
	}

	@CacheEvict(value = Costanti.NOME_CACHE_RUOLI, allEntries = true)
	public void evictCacheRuoli() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Ruoli");
	}

	@CacheEvict(value = Costanti.NOME_CACHE_GRANT, allEntries = true)
	public void evictCacheGrant() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Grant");
	}

	@CacheEvict(value = Costanti.NOME_CACHE_DATI_ACCOUNT, allEntries = true)
	public void evictCacheAccount() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Account");
	}

	@CacheEvict(value = Costanti.NOME_CACHE_DATI_DIRETTORE, allEntries = true)
	public void evictCacheDirettore() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Direttore");
	}

	@CacheEvict(value = Costanti.NOME_CACHE_ID_SEDE, allEntries = true)
	public void evictCacheIdSede() throws ComponentException {
		LOGGER.info("Cron per Svuotare la Cache Id Sede");
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
	public void comunicaDatiRimborsoSigla() throws ComponentException {
        ILock lock = hazelcastInstance.getLock(lockKeyComunicaDati);
        LOGGER.info("requested lock: " + lock.getPartitionKey());

			try {
				if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				    LOGGER.info("got lock {}", lockKeyComunicaDati);

				    try {
						LOGGER.info("Cron per Aggiornamenti Ordine Missione");

						comunicaRimborsoSigla(false);

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
    
	public void aggiornaRimborsiMissioneFlows() throws ComponentException {
		LOGGER.info("Cron per Aggiornamenti Rimborso Missione");
		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
		filtroRimborso.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtroRimborso.setValidato("S");
		filtroRimborso.setDaCron("S");
		List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(filtroRimborso, false, true);
		if (listaRimborsiMissione != null){
			List<RimborsoMissione> listaRimborsiMissioneVecchiaScrivania = listaRimborsiMissione.stream()
					.filter(rimborsoMissione -> rimborsoMissione.getIdFlusso().startsWith(Costanti.INITIAL_NAME_OLD_FLOWS))
					.collect(Collectors.toList());

			for (RimborsoMissione rimborsoMissione : listaRimborsiMissioneVecchiaScrivania){
				try {
					flowsMissioniService.aggiornaRimborsoMissioneFlowsNewTransaction(rimborsoMissione.getId());
				} catch (Exception e) {
					String error = Utility.getMessageException(e);
					String testoErrore = getTextErrorRimborso(rimborsoMissione, error);
					LOGGER.error(testoErrore+" "+e);
					try {
						mailService.sendEmailError(subjectErrorFlowsRimborso, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
					}
				}
			}
		}
	}

	public void comunicaRimborsoSigla(Boolean isVecchiaScrivania) {
		LOGGER.info("Cron per Comunicazioni Rimborsi Missione");
		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
		filtroRimborso.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		filtroRimborso.setStatoInvioSigla(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE);
		filtroRimborso.setDaCron("S");
		List<RimborsoMissione> listaRimborsiMissione = null;
		try {
			listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(filtroRimborso, false, true);
		} catch (ComponentException e2) {
			String error = Utility.getMessageException(e2);
			LOGGER.error(error+" "+e2);
			try {
				mailService.sendEmailError(subjectGenericError + this.toString(), error, false, true);
			} catch (Exception e1) {
				LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
			}
		}
		List<RimborsoMissione> listaRimborsiMissioneDaComunicare = null;
		if (isVecchiaScrivania) {
			listaRimborsiMissioneDaComunicare = listaRimborsiMissione.stream()
					.filter(rimborsoMissione -> rimborsoMissione.getIdFlusso().startsWith(Costanti.INITIAL_NAME_OLD_FLOWS))
					.collect(Collectors.toList());
		} else {
			listaRimborsiMissioneDaComunicare = listaRimborsiMissione.stream()
					.filter(rimborsoMissione -> !rimborsoMissione.getIdFlusso().startsWith(Costanti.INITIAL_NAME_OLD_FLOWS))
					.collect(Collectors.toList());
		}
		if (listaRimborsiMissioneDaComunicare  != null){
			for (RimborsoMissione rimborsoMissione : listaRimborsiMissioneDaComunicare ){
				comunicaRimborsoSigla(rimborsoMissione);
			}
		}
	}

	private void comunicaRimborsoSigla(RimborsoMissione rimborsoMissione) {
		LOGGER.info("Comunica Missione: "+ rimborsoMissione.getId());
		if (rimborsoMissioneService.isMissioneComunicabileSigla(rimborsoMissione)){
			LOGGER.info("Missione Comunicabile: "+ rimborsoMissione.getId());
			try {
				comunicaRimborsoSiglaService.comunicaRimborsoSigla(rimborsoMissione.getId());
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

	private void aggiornaOrdiniMissioneFlows() {
		RimborsoMissioneFilter filtro = new RimborsoMissioneFilter();
		filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtro.setValidato("S");
		filtro.setDaCron("S");
		aggiornaOrdini(filtro);
		aggiornaAnnullamentoOrdini(filtro);
	}

	private void aggiornaOrdini(MissioneFilter filtro) {
		List<OrdineMissione> listaOrdiniMissione = null;
		try {
			listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(filtro, false, true);
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

			List<OrdineMissione> listaOrdiniMissioneVecchiaScrivania = listaOrdiniMissione.stream()
					.filter(ordineMissione -> ordineMissione.getIdFlusso().startsWith(Costanti.INITIAL_NAME_OLD_FLOWS))
					.collect(Collectors.toList());


			for (OrdineMissione ordineMissione : listaOrdiniMissioneVecchiaScrivania){
				try {
					flowsMissioniService.aggiornaOrdineMissioneFlowsNewTransaction(ordineMissione.getId());
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

	private void aggiornaAnnullamentoOrdini(RimborsoMissioneFilter filtro) {
		List<AnnullamentoOrdineMissione> listaAnnullamenti = null;
		try {
			listaAnnullamenti = annullamentoOrdineMissioneService.getAnnullamenti(filtro, false, true);
		} catch (ComponentException e2) {
			String error = Utility.getMessageException(e2);
			LOGGER.error(error + " "+e2);
			try {
				mailService.sendEmailError(subjectGenericError + this.toString(), error, false, true);
			} catch (Exception e1) {
				LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
			}
		}
		if (listaAnnullamenti != null){
			List<AnnullamentoOrdineMissione> listaAnnullamentiVecchiaScrivania = listaAnnullamenti.stream()
					.filter(annullamentoOrdineMissione -> annullamentoOrdineMissione.getIdFlusso().startsWith(Costanti.INITIAL_NAME_OLD_FLOWS))
					.collect(Collectors.toList());
			for (AnnullamentoOrdineMissione annullamento : listaAnnullamentiVecchiaScrivania){
				try {
					flowsMissioniService.aggiornaAnnullamentoOrdineMissioneFlowsNewTransaction(annullamento.getId());
				} catch (Exception e) {
					String error = Utility.getMessageException(e);
					String testoErrore = getTextErrorAnnullamento(annullamento, error);
					LOGGER.error(testoErrore + " "+e);
					try {
						mailService.sendEmailError(subjectErrorFlowsAnnullamento, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
					}
				}
			}
		}
	}

	private String getTextErrorAnnullamento(AnnullamentoOrdineMissione annullamentoOrdineMissione, String error) {
		return textErrorFlowsAnnullamento+" con id "+annullamentoOrdineMissione.getId()+" è andata in errore per il seguente motivo: " + error;
	}

	private String getTextErrorOrdineMissione(OrdineMissione ordineMissione, String error){
		return " con id "+ordineMissione.getId()+ " "+ ordineMissione.getAnno()+"-"+ordineMissione.getNumero()+ " di "+ ordineMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}
	private String getTextErrorOrdine(OrdineMissione ordineMissione, String error) {
		return textErrorFlowsOrdine+getTextErrorOrdineMissione(ordineMissione, error);
	}

	private String getTextErrorBypassResp(OrdineMissione ordineMissione, String error) {
		return textErrorBypassResp+getTextErrorOrdineMissione(ordineMissione, error);
	}

	private String getTextErrorBypassAmm(OrdineMissione ordineMissione, String error) {
		return textErrorBypassAmm+getTextErrorOrdineMissione(ordineMissione, error);
	}

	private String getTextErrorRimborso(RimborsoMissione rimborsoMissione, String error) {
		return textErrorFlowsRimborso+" con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}

	private String getTextErrorComunicaRimborso(RimborsoMissione rimborsoMissione, String error) {
		return textErrorComunicazioneRimborso+" con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}

	@Transactional
	public void verifyStep() throws ComponentException {
		ILock lock = hazelcastInstance.getLock(lockKeyLoadCache);
		LOGGER.info("requested lock: " + lock.getPartitionKey());

		try {
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				LOGGER.info("got lock {}", lockKeyLoadCache);

				try {
					LOGGER.info("Cron per Verificare gli step da eseguire");
					verifyStepRespGruppo();
					LOGGER.info("Fine Cron Resp Gruppo");
					verifyStepAmministrativo();
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
	private void verifyStepRespGruppo() {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStato(Costanti.STATO_INVIATO_RESPONSABILE);
		filtro.setDaCron("S");
		List<OrdineMissione> listaOrdiniMissione = null;
		try {
			listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(filtro, false, true);
		} catch (Exception e2) {
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
				DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno());
				if (istituto != null){
					if ((istituto.getMinutiPrimaInizioResp() != null && istituto.getMinutiMinimiResp() != null) || (istituto.getMinutiPassatiResp() != null)){
						try {
							stepService.verifyStepRespGruppoNewTransaction(ordineMissione.getId());
						} catch (Exception e) {
							String error = Utility.getMessageException(e);
							String testoErrore = getTextErrorBypassResp(ordineMissione, error);
							LOGGER.error(testoErrore + " "+e);
							try {
								mailService.sendEmailError(subjectErrorBypassResp, testoErrore, false, true);
							} catch (Exception e1) {
								LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
							}
						}
					}
					
				}
			}
		}
	}
	private void verifyStepAmministrativo() {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStato(Costanti.STATO_CONFERMATO);
		filtro.setValidato("N");
		filtro.setDaCron("S");
		List<OrdineMissione> listaOrdiniMissione = null;
		try {
			listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(filtro, false, true);
		} catch (Exception e2) {
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
				LOGGER.info("Ordine Amm");
				DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(), ordineMissione.getAnno());
				LOGGER.info("Dati Ist Amm");
				if (istituto != null){
					if ((istituto.getMinutiPrimaInizioAmm() != null && istituto.getMinutiMinimiAmm() != null) || (istituto.getMinutiPassatiAmm() != null)){
						try {
							LOGGER.info("Start verify Amm");
							stepService.verifyStepAmministrativoNewTransaction(ordineMissione.getId());
						} catch (Exception e) {
							String error = Utility.getMessageException(e);
							String testoErrore = getTextErrorBypassAmm(ordineMissione, error);
							LOGGER.error(testoErrore + " "+e);
							try {
								mailService.sendEmailError(subjectErrorBypassAmm, testoErrore, false, true);
							} catch (Exception e1) {
								LOGGER.error("Errore durante l'invio dell'e-mail: "+e1);
							}
						}
					}
				}
				
			}
		}
	}

	@Transactional
	public void verificaFlussoEComunicaDatiRimborsoSigla() throws ComponentException {
		ILock lock = hazelcastInstance.getLock(lockKeyComunicaDatiVecchiaScrivania);
		LOGGER.info("requested lock: " + lock.getPartitionKey());

		try {
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

				LOGGER.info("got lock {}", lockKeyComunicaDatiVecchiaScrivania);

				try {
					LOGGER.info("Cron per Aggiornamenti Ordine Missione Da Vecchia Scrivania");

					aggiornaOrdiniMissioneFlows();

					LOGGER.info("Cron per Aggiornamenti Rimborso Missione Da Vecchia Scrivania");

					aggiornaRimborsiMissioneFlows();

//						ribaltaMissione(principal);

					comunicaRimborsoSigla(true);

					LOGGER.info("work done.");
				} finally {
					LOGGER.info("unlocking {}", lockKeyComunicaDatiVecchiaScrivania);
					lock.unlock();
				}

			} else {
				LOGGER.warn("unable to get lock {}", lockKeyComunicaDatiVecchiaScrivania);
			}
		} catch (Exception e) {
			LOGGER.error("Errore", e);
			throw new ComponentException(e);
		}
	}

}
