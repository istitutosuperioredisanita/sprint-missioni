package it.cnr.si.missioni.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.Banca;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.DivisaTappa;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneBulk;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneSigla;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.ModalitaPagamento;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.Nazione;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.RifInquadramento;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.SpeseMissioneColl;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.TappeMissioneColl;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.TipoRapporto;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.UserContext;
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
					LOGGER.error(testoErrore);
					try {
						mailService.sendEmailError(subjectErrorFlowsOrdine, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+Utility.getMessageException(e1));
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
		List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(principal, filtroRimborso, false, true);
		if (listaRimborsiMissione != null){
			for (RimborsoMissione rimborsoMissione : listaRimborsiMissione){
				try {
					comunicaRimborsoSigla(principal, rimborsoMissione);
				} catch (Exception e) {
					LOGGER.error("comunicaRimborsoSigla",e);
					try {
						mailService.sendEmailError(subjectErrorFlowsOrdine, getTextErrorRimborso(rimborsoMissione, error), false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+Utility.getMessageException(e1));
					}
				}
			}
		}
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
	private void comunicaRimborsoSigla(Principal principal, RimborsoMissione rimborsoApprovato) throws Exception {
		rimborsoMissioneService.retrieveDetails(principal, rimborsoApprovato);
		MissioneSigla missioneSigla = new MissioneSigla();
		impostaUserContext(principal, rimborsoApprovato, missioneSigla);
		MissioneBulk oggettoBulk = new MissioneBulk();
		oggettoBulk.setCdCds(rimborsoApprovato.getCdsSpesa());
		
		impostaBanca(rimborsoApprovato, oggettoBulk);
		
		if (rimborsoApprovato.getCdTerzoSigla() != null){
		    oggettoBulk.setCdTerzo(rimborsoApprovato.getCdTerzoSigla().intValue());
		}
		oggettoBulk.setCdUnitaOrganizzativa(rimborsoApprovato.getUoSpesa());
		oggettoBulk.setDsMissione(rimborsoApprovato.getOggetto());
		oggettoBulk.setDtFineMissione(DateUtils.getDateAsString(rimborsoApprovato.getDataFineMissione(), DateUtils.PATTERN_DATETIME_WITH_TIMEZONE));
		oggettoBulk.setDtInizioMissione(DateUtils.getDateAsString(rimborsoApprovato.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_WITH_TIMEZONE));
/*GGGG TODO...VERIFICARE QUALE ESERCIZIO PASSARE*/                                oggettoBulk.setEsercizio(rimborsoApprovato.getAnno());
		oggettoBulk.setFlAssociatoCompenso(false);
		if (rimborsoApprovato.isMissioneEstera()){
			oggettoBulk.setFlComuneAltro(false);
			oggettoBulk.setFlComuneEstero(true);
		} else {
			oggettoBulk.setFlComuneAltro(true);
			oggettoBulk.setFlComuneEstero(false);
		}
		oggettoBulk.setFlComuneProprio(false);
		oggettoBulk.setImDiariaLorda(BigDecimal.ZERO);
		oggettoBulk.setImDiariaNetto(BigDecimal.ZERO);
		oggettoBulk.setImLordoPercepiente(BigDecimal.ZERO);
		oggettoBulk.setImNettoPecepiente(BigDecimal.ZERO);
		oggettoBulk.setImQuotaEsente(BigDecimal.ZERO);
		oggettoBulk.setImRimborso(BigDecimal.ZERO);
		oggettoBulk.setImSpese(BigDecimal.ZERO);
		oggettoBulk.setImSpeseAnticipate(Utility.nvl(rimborsoApprovato.getAltreSpeseAntImporto()));
		oggettoBulk.setImTotaleMissione(rimborsoApprovato.getTotaleRimborso());
		if (rimborsoApprovato.getAnticipoAnnoMandato() != null){
			oggettoBulk.setAnnoMandatoAnticipo(rimborsoApprovato.getAnticipoAnnoMandato());
		}
		if (rimborsoApprovato.getAnticipoNumeroMandato() != null){
			oggettoBulk.setNumeroMandatoAnticipo(rimborsoApprovato.getAnticipoNumeroMandato());
		}
		if (rimborsoApprovato.getAnticipoImporto() != null){
			oggettoBulk.setImportoMandatoAnticipo(rimborsoApprovato.getAnticipoImporto());
		}
//						oggettoBulk.setIdRimborsoMissione(new Long (rimborsoApprovato.getId().toString()));
//						oggettoBulk.setIdFlusso(rimborsoApprovato.getIdFlusso());
		if (StringUtils.hasLength(rimborsoApprovato.getCdCdsObbligazione())){
			oggettoBulk.setCdsObblGeMis(rimborsoApprovato.getCdCdsObbligazione());
		}
		if (rimborsoApprovato.getEsercizioObbligazione() != null){
			oggettoBulk.setEsercizioObblGeMis(rimborsoApprovato.getEsercizioObbligazione());
		}
		if (rimborsoApprovato.getEsercizioOriginaleObbligazione() != null){
			oggettoBulk.setEsercizioOriObblGeMis(rimborsoApprovato.getEsercizioOriginaleObbligazione());
		}
		if (rimborsoApprovato.getPgObbligazione() != null){
			oggettoBulk.setPgObblGeMis(rimborsoApprovato.getPgObbligazione());
		}
		if (rimborsoApprovato.getGae() != null){
			oggettoBulk.setGaeGeMis(rimborsoApprovato.getGae());
		}
		impostaModalitaPagamento(rimborsoApprovato, oggettoBulk);
		
		impostaInquadramento(rimborsoApprovato, oggettoBulk);
		impostaTappe(rimborsoApprovato, oggettoBulk);

		TipoRapporto tipoRapporto = new TipoRapporto();
		if (StringUtils.hasLength(rimborsoApprovato.getMatricola())){
			tipoRapporto.setCdTipoRapporto("DIP");
			oggettoBulk.setTiAnagrafico("D");
		} else {
			tipoRapporto.setCdTipoRapporto(rimborsoApprovato.getCdTipoRapporto());
			oggettoBulk.setTiAnagrafico("A");
		}

		oggettoBulk.setTipoRapporto(tipoRapporto);

		List<SpeseMissioneColl> speseMissioneColl = new ArrayList<SpeseMissioneColl>();
		for (RimborsoMissioneDettagli dettaglio : rimborsoApprovato.getRimborsoMissioneDettagli()){
			SpeseMissioneColl spesaMissione = new SpeseMissioneColl();
			spesaMissione.setCdTiSpesa(dettaglio.getCdTiSpesa());
			spesaMissione.setDsTiSpesa(dettaglio.getDsTiSpesa());
			String dataTappa = recuperoDataTappa(oggettoBulk.getTappeMissioneColl(), dettaglio);
			if (dataTappa != null){
				spesaMissione.setDtInizioTappa(dataTappa);
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa "+ dettaglio.getDsTiSpesa()+" del "+ DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa())+ " del rimborso missione con id "+ rimborsoApprovato.getId() + " della uo "+rimborsoApprovato.getUoRich()+", anno "+rimborsoApprovato.getAnno()+", numero "+rimborsoApprovato.getNumero()+"  non esiste una tappa utile. Possibile incongruenza con le date di inizio e di fine missione.");
			}
			
			spesaMissione.setFlDiariaManuale(false);
			spesaMissione.setFlSpesaAnticipata(dettaglio.getFlSpesaAnticipata().equals("S") ? true : false);
			spesaMissione.setImBaseMaggiorazione(BigDecimal.ZERO);
			spesaMissione.setImDiariaLorda(BigDecimal.ZERO);
			spesaMissione.setImDiariaNetto(BigDecimal.ZERO);
			spesaMissione.setImMaggiorazione(BigDecimal.ZERO);
			spesaMissione.setImMaggiorazioneEuro(BigDecimal.ZERO);
			spesaMissione.setImQuotaEsente(BigDecimal.ZERO);
			spesaMissione.setPercentualeMaggiorazione(BigDecimal.ZERO);
			spesaMissione.setImRimborso(BigDecimal.ZERO);
			spesaMissione.setImSpesaMax(BigDecimal.ZERO);
			spesaMissione.setImSpesaMaxDivisa(BigDecimal.ZERO);
			spesaMissione.setImSpesaDivisa(dettaglio.getImportoDivisa());
			spesaMissione.setImSpesaEuro(dettaglio.getImportoEuro());
			spesaMissione.setImTotaleSpesa(dettaglio.getImportoEuro());
			spesaMissione.setPgRiga(dettaglio.getRiga().intValue());
			spesaMissione.setTiAuto("A");
			String idFolderDettaglio = cmisRimborsoMissioneService.getNodeRefFolderDettaglioRimborso(new Long (dettaglio.getId().toString()));
			if (idFolderDettaglio != null){
				spesaMissione.setDsGiustificativo(idFolderDettaglio);
				spesaMissione.setIdGiustificativo(dettaglio.getRiga().toString());
			} else {
				if (dettaglio.isGiustificativoObbligatorio()){
					if (dettaglio.getDsNoGiustificativo() != null){
		    			spesaMissione.setDsNoGiustificativo(dettaglio.getDsNoGiustificativo());
					} else {
						throw new AwesomeException(CodiciErrore.ERRGEN, "Per il dettaglio spesa "+ dettaglio.getDsTiSpesa()+" del "+ DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa())+ " del rimborso missione con id "+ rimborsoApprovato.getId() + " della uo "+rimborsoApprovato.getUoRich()+", anno "+rimborsoApprovato.getAnno()+", numero "+rimborsoApprovato.getNumero()+" è obbligatorio allegare almeno un giustificativo oppure indicare il motivo della mancanza.");
					}
				}
			}
			if (dettaglio.isDettaglioPasto()){
				spesaMissione.setCdTiPasto(dettaglio.getCdTiPasto());
			}
			if (dettaglio.isDettaglioIndennitaKm()){
				spesaMissione.setChilometri(dettaglio.getKmPercorsi());
				spesaMissione.setLocalitaSpostamento(dettaglio.getLocalitaSpostamento());
			}
			if (dettaglio.getDsSpesa() != null){
				if (dettaglio.getDsSpesa().length() > 100){
					spesaMissione.setDsSpesa(dettaglio.getDsSpesa().substring(0, 100));
				} else {
					spesaMissione.setDsSpesa(dettaglio.getDsSpesa());
				}
			}
			spesaMissione.setTiSpesaDiaria("S");
			speseMissioneColl.add(spesaMissione);
		}

		oggettoBulk.setSpeseMissioneColl(speseMissioneColl);
		oggettoBulk.setStatoCoan("N");
		oggettoBulk.setStatoCofi("I");
		oggettoBulk.setStatoCoge("N");
		oggettoBulk.setStatoLiquidazione("SOSP");
		oggettoBulk.setStatoPagamentoFondoEco("N");

		oggettoBulk.setTiAssociatoManrev("N");
		oggettoBulk.setTiIstituzCommerc("I");
		oggettoBulk.setTiProvvisorioDefinitivo("P");
		missioneSigla.setOggettoBulk(oggettoBulk);
		MissioneBulk missioneBulk = comunicaRimborsoSiglaService.comunica(oggettoBulk);
		if (missioneBulk != null){
			rimborsoMissioneService.aggiornaRimborsoMissioneComunicata(principal, rimborsoApprovato, missioneBulk);
		}
	}
	
	private String recuperoDataTappa(List<TappeMissioneColl> tappe, RimborsoMissioneDettagli dettaglio){
		for (TappeMissioneColl tappa : tappe){
			if (dettaglio.getDataSpesa().isEqual(DateUtils.parseLocalDate(tappa.getDtInizioTappa().substring(0, 10), DateUtils.PATTERN_DATE_FOR_DOCUMENTALE))){
    			return tappa.getDtInizioTappa();
			}
		}
		return null;
	}

	private void aggiornaOrdiniMissioneFlows(Principal principal) {
		MissioneFilter filtro = new MissioneFilter();
		filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
		filtro.setValidato("S");
		List<OrdineMissione> listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(principal, filtro, false, true);
		if (listaOrdiniMissione != null){
			for (OrdineMissione ordineMissione : listaOrdiniMissione){
				try {
					flowsService.aggiornaOrdineMissioneFlowsNewTransaction(principal,ordineMissione);
				} catch (Exception e) {
					String error = Utility.getMessageException(e);
					String testoErrore = getTextErrorOrdine(ordineMissione, error);
					LOGGER.error(testoErrore);
					try {
						mailService.sendEmailError(subjectErrorFlowsOrdine, testoErrore, false, true);
					} catch (Exception e1) {
						LOGGER.error("Errore durante l'invio dell'e-mail: "+Utility.getMessageException(e1));
					}
				}
			}
		}
	}

	private String getTextErrorOrdine(OrdineMissione ordineMissione, String error) {
		return textErrorFlowsOrdine+" con id "+ordineMissione.getId()+ " "+ ordineMissione.getAnno()+ordineMissione.getNumero()+ " di "+ ordineMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}

	private String getTextErrorRimborso(RimborsoMissione rimborsoMissione, String error) {
		return textErrorFlowsRimborso+" con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" è andata in errore per il seguente motivo: " + error;
	}

	private List<String> getTosMail(OrdineMissione ordineMissione) {
		List<String> mails = new ArrayList<>();
		Account utenteMissione = accountService.loadAccountFromRest(ordineMissione.getUid());
		mails.add(utenteMissione.getEmailComunicazioni());
		if (!ordineMissione.getUid().equals(ordineMissione.getUidInsert())){
			Account utenteInserimentoMissione = accountService.loadAccountFromRest(ordineMissione.getUid());
			mails.add(utenteInserimentoMissione.getEmailComunicazioni());
		}
		return mails;
	}

	private void impostaTappe(RimborsoMissione rimborsoApprovato, MissioneBulk oggettoBulk)
			throws Exception {
		List<TappeMissioneColl> tappeMissioneColl = new ArrayList<TappeMissioneColl>();
		TappeMissioneColl tappa = new TappeMissioneColl();
		impostaDivisaTappa(tappa);
		if (rimborsoApprovato.isTrattamentoAlternativoMissione()){
			tappa.setFlRimborso(false);
			tappa.setFlNoDiaria(false);
		} else {
			tappa.setFlRimborso(true);
			tappa.setFlNoDiaria(true);
		}
		tappa.setFlComuneAltro(oggettoBulk.getFlComuneAltro());
		tappa.setFlComuneEstero(oggettoBulk.getFlComuneEstero());
		tappa.setFlComuneProprio(oggettoBulk.getFlComuneProprio());
//TODO: GGGG Da verificare sul db Gestione vitto e alloggio gratuito con gli abbattimenti
		tappa.setFlAlloggioGratuito(false);
		tappa.setFlNavigazione(false);
		tappa.setFlVittoAlloggioGratuito(false);
		tappa.setFlVittoGratuito(false);
//TODO: GGGG Fine da verificare sul db Gestione vitto e alloggio gratuito con gli abbattimenti 								
		if (!rimborsoApprovato.isMissioneEstera()){
			impostaNazioneRimborso(rimborsoApprovato, tappa);
			tappeMissioneColl = impostaTappeDaDate(rimborsoApprovato.getDataInizioMissione(), rimborsoApprovato.getDataFineMissione(), tappa, tappeMissioneColl);
		} else {
			if (DateUtils.truncate(rimborsoApprovato.getDataInizioMissione()).compareTo(DateUtils.truncate(rimborsoApprovato.getDataInizioEstero())) == 0 && 
					DateUtils.truncate(rimborsoApprovato.getDataFineMissione()).compareTo(DateUtils.truncate(rimborsoApprovato.getDataFineEstero())) == 0){
				impostaNazioneRimborso(rimborsoApprovato, tappa);
				tappeMissioneColl = impostaTappeDaDate(rimborsoApprovato.getDataInizioMissione(), rimborsoApprovato.getDataFineMissione(), tappa, tappeMissioneColl);
			} else {
				impostaNazioneRimborso(rimborsoApprovato, tappa);
				tappeMissioneColl = impostaTappeDaDate(rimborsoApprovato.getDataInizioEstero(), rimborsoApprovato.getDataFineEstero(), tappa, tappeMissioneColl, rimborsoApprovato.getDataInizioMissione(), rimborsoApprovato.getDataFineMissione());
			}
		}
		oggettoBulk.setTappeMissioneColl(tappeMissioneColl);
	}

	private List<TappeMissioneColl> impostaTappeDaDate(ZonedDateTime daData, ZonedDateTime aData, TappeMissioneColl tappa, List<TappeMissioneColl> tappeMissioneColl) throws Exception {
		return impostaTappeDaDate(daData, aData, tappa, tappeMissioneColl, null, null);
	}
	private List<TappeMissioneColl> impostaTappeDaDate(ZonedDateTime daData, ZonedDateTime aData, TappeMissioneColl tappa, List<TappeMissioneColl> tappeMissioneColl, ZonedDateTime dataInizioMissione, ZonedDateTime dataFineMissione) throws Exception {
		ZonedDateTime ultimaDataInizioUsata = null;
		if (dataInizioMissione != null && !DateUtils.truncate(daData).equals(DateUtils.truncate(dataInizioMissione))){
			for (ZonedDateTime data = dataInizioMissione; DateUtils.truncate(data).isBefore(DateUtils.truncate(daData)); data = data.plusDays(1))
			{
				ultimaDataInizioUsata = data;
				TappeMissioneColl newDayTappa = (TappeMissioneColl)tappa.clone();
				impostaNazione(Costanti.NAZIONE_ITALIA_SIGLA, newDayTappa);
				ZonedDateTime dataFine = data.plusDays(1);
				if (dataFine.isAfter(dataFineMissione)){
					dataFine = dataFineMissione;
				}
				impostaDateTappa(data, dataFine, newDayTappa);
				tappeMissioneColl.add(newDayTappa);
			}
		}
		if (ultimaDataInizioUsata != null){
			daData = ultimaDataInizioUsata.plusDays(1);
		}
		for (ZonedDateTime data = daData; DateUtils.truncate(data).isBefore(DateUtils.truncate(aData)) || DateUtils.truncate(data).isEqual(DateUtils.truncate(aData)); data = data.plusDays(1))
		{
			ultimaDataInizioUsata = data;
			TappeMissioneColl newDayTappa = (TappeMissioneColl)tappa.clone();
			ZonedDateTime dataFine = data.plusDays(1);
			if (dataFine.isAfter(aData)){
				if (dataFineMissione == null){
					dataFine = aData;
				} else {
					if (dataFine.isAfter(dataFineMissione)){
						dataFine = dataFineMissione;
					}
				}
			}
			impostaDateTappa(data, dataFine, newDayTappa);
			tappeMissioneColl.add(newDayTappa);
		}
		if (dataFineMissione != null && !DateUtils.truncate(aData).equals(DateUtils.truncate(dataFineMissione))){
			ultimaDataInizioUsata = ultimaDataInizioUsata.plusDays(1);
			impostaNazione(Costanti.NAZIONE_ITALIA_SIGLA, tappa);
			for (ZonedDateTime data = ultimaDataInizioUsata; DateUtils.truncate(data).isBefore(DateUtils.truncate(dataFineMissione)) || DateUtils.truncate(data).isEqual(DateUtils.truncate(dataFineMissione)); data = data.plusDays(1))
			{
				ZonedDateTime dataInizio = data;
				if (dataInizio.isAfter(dataFineMissione)){
					dataInizio = dataFineMissione;
				}
				TappeMissioneColl newDayTappa = (TappeMissioneColl)tappa.clone();
				ZonedDateTime dataFine = dataInizio.plusDays(1);
				if (dataFine.isAfter(dataFineMissione)){
					dataFine = dataFineMissione;
				}
				impostaDateTappa(dataInizio, dataFine, newDayTappa);
				tappeMissioneColl.add(newDayTappa);
			}
		}

		
		return tappeMissioneColl;
	}

	private void impostaDateTappa(ZonedDateTime dataInizio, ZonedDateTime dataFine, TappeMissioneColl tappa) {
		tappa.setDtInizioTappa(DateUtils.getDateAsString(dataInizio, DateUtils.PATTERN_DATETIME_WITH_TIMEZONE));
		tappa.setDtFineTappa(DateUtils.getDateAsString(dataFine, DateUtils.PATTERN_DATETIME_WITH_TIMEZONE));
	}

	private void impostaNazioneRimborso(RimborsoMissione rimborsoApprovato, TappeMissioneColl tappa) {
		impostaNazione(rimborsoApprovato.getNazione(), tappa);
	}

	private void impostaNazione(Long idNazione, TappeMissioneColl tappa) {
		Nazione nazione = new Nazione();
		nazione.setPgNazione(idNazione.intValue());
		tappa.setNazione(nazione);
	}

	private void impostaDivisaTappa(TappeMissioneColl tappa) {
		DivisaTappa divisa = new DivisaTappa();
		divisa.setCdDivisa(Costanti.CODICE_DIVISA_DEFAULT_SIGLA);
		tappa.setDivisaTappa(divisa);
		tappa.setCambioTappa(1);
	}

	private void impostaInquadramento(RimborsoMissione rimborsoApprovato, MissioneBulk oggettoBulk) {
		if (rimborsoApprovato.getInquadramento() != null){
			RifInquadramento rifInquadramento = new RifInquadramento();
			rifInquadramento.setPgRifInquadramento(rimborsoApprovato.getInquadramento().intValue());
			oggettoBulk.setRifInquadramento(rifInquadramento);
		}
	}

	private void impostaModalitaPagamento(RimborsoMissione rimborsoApprovato, MissioneBulk oggettoBulk) {
		ModalitaPagamento modalitaPagamento = new ModalitaPagamento();
		modalitaPagamento.setCdModalitaPag(rimborsoApprovato.getModpag());
		oggettoBulk.setModalitaPagamento(modalitaPagamento);
	}

	private void impostaBanca(RimborsoMissione rimborsoApprovato, MissioneBulk oggettoBulk) {
		Banca banca = new Banca();
		banca.setPgBanca(rimborsoApprovato.getPgBanca());
		oggettoBulk.setBanca(banca);
	}

	private void impostaUserContext(Principal principal, RimborsoMissione rimborsoApprovato,
			MissioneSigla missioneSigla) {
		UserContext userContext = new UserContext();
		userContext.setCdCds(rimborsoApprovato.getCdsSpesa());
		userContext.setUser(principal.getName());
		missioneSigla.setUserContext(userContext);
	}
}
