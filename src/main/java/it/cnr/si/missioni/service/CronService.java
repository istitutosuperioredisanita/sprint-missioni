package it.cnr.si.missioni.service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
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

import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.Banca;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneSigla;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.ModalitaPagamento;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.OggettoBulk;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.RifInquadramento;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.SpeseMissioneColl;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.UserContext;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

@Service
@SuppressWarnings({"rawtypes", "unchecked"})
public class CronService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CronService.class);

    @Value("${cron.comunicaDati.name}")
    private String lockKey;
    
    @Autowired
    private HazelcastInstance hazelcastInstance;

	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private RimborsoMissioneService rimborsoMissioneService;

    @Transactional
	public void comunicaDati(Principal principal) throws Exception {
        ILock lock = hazelcastInstance.getLock(lockKey);
        LOGGER.info("requested lock: " + lock.getPartitionKey());

        if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {

            LOGGER.info("got lock {}", lockKey);

            try {
        		LOGGER.info("doing comunicaDati");

        		MissioneFilter filtro = new MissioneFilter();
        		filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        		filtro.setValidato("S");
        		List<OrdineMissione> listaOrdiniMissione = ordineMissioneService.getOrdiniMissione(principal, filtro, false, true);
            	if (listaOrdiniMissione != null){
            		for (OrdineMissione ordineMissione : listaOrdiniMissione){
            			if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
                			ResultFlows result = ordineMissioneService.retrieveDataFromFlows(ordineMissione);
            				if (result.isApprovato()){
                                LOGGER.info("Trovato Ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
            					ordineMissioneService.aggiornaOrdineMissioneApprovato(principal, ordineMissione);
            				}
            			}
            		}
            	}

        		RimborsoMissioneFilter filtroRimborso = new RimborsoMissioneFilter();
        		filtro.setStatoFlusso(Costanti.STATO_INVIATO_FLUSSO);
        		filtro.setValidato("S");
        		List<RimborsoMissione> listaRimborsiMissione = rimborsoMissioneService.getRimborsiMissione(principal, filtroRimborso, false, true);
            	if (listaRimborsiMissione != null){
            		for (RimborsoMissione rimborsoMissione : listaRimborsiMissione){
            			if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()){
                			ResultFlows result = rimborsoMissioneService.retrieveDataFromFlows(rimborsoMissione);
            				if (result.isApprovato()){
                                LOGGER.info("Trovato Rimborso missione con id {} della uo {}, anno {}, numero {} approvato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
                                RimborsoMissione rimborsoApprovato = rimborsoMissioneService.aggiornaRimborsoMissioneApprovato(principal, rimborsoMissione);
                                rimborsoMissioneService.retrieveDetails(principal, rimborsoApprovato);
                                MissioneSigla missioneSigla = new MissioneSigla();
                                impostaUserContext(principal, rimborsoApprovato, missioneSigla);
                                OggettoBulk oggettoBulk = new OggettoBulk();
                                oggettoBulk.setCdCds(rimborsoApprovato.getCdsSpesa());
                                
                                impostaBanca(rimborsoApprovato, oggettoBulk);
                                
                                if (rimborsoApprovato.getCdTerzoSigla() != null){
                                    oggettoBulk.setCdTerzo(rimborsoApprovato.getCdTerzoSigla().intValue());
                                }
                                oggettoBulk.setCdUnitaOrganizzativa(rimborsoApprovato.getUoSpesa());
                                oggettoBulk.setDsMissione(rimborsoApprovato.getOggetto());
                                oggettoBulk.setDtFineMissione(DateUtils.getDefaultDateAsString(rimborsoApprovato.getDataFineMissione()));
                                oggettoBulk.setDtInizioMissione(DateUtils.getDefaultDateAsString(rimborsoApprovato.getDataInizioMissione()));
/*GGGG TODO...VERIFICARE QUALE ESERCIZIO PASSARE*/                                oggettoBulk.setEsercizio(rimborsoApprovato.getAnno());
								oggettoBulk.setFlAssociatoCompenso(false);
/*								oggettoBulk.setFlComuneAltro();
								oggettoBulk.setFlComuneEstero();
								oggettoBulk.setFlComuneProprio();
								oggettoBulk.setImDiariaLorda(0);
								oggettoBulk.setImDiariaNetto(0);
								oggettoBulk.setImLordoPercepiente();
								oggettoBulk.setImNettoPecepiente();
								oggettoBulk.setImQuotaEsente();
								oggettoBulk.setImRimborso();
								oggettoBulk.setImSpese();
								oggettoBulk.setImSpeseAnticipate(Utility.nvl(rimborsoMissione.getAltreSpeseAntImporto()));
								oggettoBulk.setImTotaleMissione();
								
								impostaModalitaPagamento(rimborsoApprovato, oggettoBulk);
								
								impostaInquadramento(rimborsoApprovato, oggettoBulk);
								
								List<SpeseMissioneColl> speseMissioneColl = new ArrayList<SpeseMissioneColl>();
					    		for (RimborsoMissioneDettagli dettaglio : rimborsoApprovato.getRimborsoMissioneDettagli()){
					    			SpeseMissioneColl spesaMissione = new SpeseMissioneColl();
					    			spesaMissione.setCdTiSpesa(dettaglio.getCdTiSpesa());
					    			spesaMissione.setDsTiSpesa(dettaglio.getDsTiSpesa());
					    			spesaMissione.setDtInizioTappa(DateUtils.getDefaultDateAsString(dettaglio.getDataSpesa()));
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
					    			spesaMissione.setCdTiPasto(dettaglio.getCdTiPasto());
					    			spesaMissione.setTiAuto("A");
					    			spesaMissione.setChilometri(dettaglio.getKmPercorsi());
					    			spesaMissione.setDsGiustificativo(dsGiustificativo);
					    			spesaMissione.setDsNoGiustificativo(dsNoGiustificativo);
					    			spesaMissione.setIdGiustificativo(idGiustificativo);
					    			spesaMissione.setIndennitaChilometrica(indennitaChilometrica);
					    			spesaMissione.setLocalitaSpostamento(localitaSpostamento);
					    			if (dettaglio.getDsSpesa() != null){
					    				if (dettaglio.getDsSpesa().length() > 100){
							    			spesaMissione.setDsSpesa(dettaglio.getDsSpesa().substring(0, 100));
					    				} else {
							    			spesaMissione.setDsSpesa(dettaglio.getDsSpesa());
					    				}
					    			}
					    			spesaMissione.setTiSpesaDiaria("S");
					    		}

								oggettoBulk.setSpeseMissioneColl(speseMissioneColl);
								oggettoBulk.setStatoCoan("N");
								oggettoBulk.setStatoCofi("I");
								oggettoBulk.setStatoCoge("N");
								oggettoBulk.setStatoLiquidazione("SOSP");
								oggettoBulk.setStatoPagamentoFondoEco("N");
								oggettoBulk.setTappeMissioneColl(tappeMissioneColl);
								oggettoBulk.setTiAnagrafico(tiAnagrafico);
								oggettoBulk.setTipoRapporto(tipoRapporto);
								oggettoBulk.setTiAssociatoManrev("N");
								oggettoBulk.setTiIstituzCommerc("I");
								oggettoBulk.setTiProvvisorioDefinitivo("P");
								missioneSigla.setOggettoBulk(oggettoBulk);*/
            				}
            			}
            		}
            	}
        		

//        		for (Attestato attestato : resultList) {
//                    LOGGER.info("doing verificaFlussoDocumentale for sede {} e periodo {}", attestato.getSede().getCodiceSede(), attestato.getPeriodo());
//    				attestato = utilityService.verificaFlussoDocumentale(principal, attestato);
//
//        			if (attestato.isFlussoValidato()) {
//    					String userNameRichiedente = (String)utilityService.getFieldValueFlussoDocumentale(principal, attestato.getSede().getCodiceSede(), attestato.getAnnoPeriodo(), attestato.getMesePeriodo(), UtilityService.FIELD_FLUSSO_USERNAME_RICHIEDENTE);
//        				if (userNameRichiedente!=null) {
//        					JsonFactory jsonFactory = new JsonFactory();
//        					ObjectMapper mapper = new ObjectMapper(jsonFactory);
//        					try {
//            					LOGGER.info("doing comunicaAttestatoToNsip for sede {} e periodo {}", attestato.getSede().getCodiceSede(), attestato.getPeriodo());
//            					Response responseRichiedente = cmisService.invokeGET(new UrlBuilder(cmisService.getRepositoryURL()+"service/cnr/person/person/"+userNameRichiedente));
//            					TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
//            					HashMap<String,Object> mapFirmatario = mapper.readValue(responseRichiedente.getStream(), typeRef);
//
//            					String emailRichiedente = (String)mapFirmatario.get("email");
//
//           						stralcioService.comunicaAttestatoToNsip(principal, attestato.getSede().getCodiceSede(), attestato.getAnnoPeriodo(), attestato.getMesePeriodo(), true);
//                               	mailService.sendEmail(emailRichiedente, "Comunicazione Dati a NSIP - Sede "+attestato.getSede().getCodiceSede()+" - Periodo "+attestato.getPeriodo()+ " - COMUNICAZIONE EFFETTUATA", "Si segnala che è stata effettuata in automatico la comunicazione dati a NSIP per la sede ed il periodo indicato in oggetto.", false, true);
//                            } catch (Exception e) {
//                               	mailService.sendEmailError("Errore CronAttestati Sede "+attestato.getSede().getCodiceSede()+" - Periodo "+attestato.getPeriodo(), "Errore: "+ExceptionUtils.getStackTrace(e), false, true);
//                            }
//        				}
//        			}
//        		}
                LOGGER.info("work done.");
            } finally {
                LOGGER.info("unlocking {}", lockKey);
				lock.unlock();
			}

        } else {
            LOGGER.warn("unable to get lock {}", lockKey);
        }
    }

	private void impostaInquadramento(RimborsoMissione rimborsoApprovato, OggettoBulk oggettoBulk) {
		if (rimborsoApprovato.getInquadramento() != null){
			RifInquadramento rifInquadramento = new RifInquadramento();
			rifInquadramento.setPgRifInquadramento(rimborsoApprovato.getInquadramento().intValue());
			oggettoBulk.setRifInquadramento(rifInquadramento);
		}
	}

	private void impostaModalitaPagamento(RimborsoMissione rimborsoApprovato, OggettoBulk oggettoBulk) {
		ModalitaPagamento modalitaPagamento = new ModalitaPagamento();
		modalitaPagamento.setCdModalitaPag(rimborsoApprovato.getModpag());
		oggettoBulk.setModalitaPagamento(modalitaPagamento);
	}

	private void impostaBanca(RimborsoMissione rimborsoApprovato, OggettoBulk oggettoBulk) {
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
