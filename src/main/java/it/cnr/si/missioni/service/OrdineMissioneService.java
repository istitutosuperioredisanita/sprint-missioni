package it.cnr.si.missioni.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.OptimisticLockException;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneAutoPropriaRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.CdrService;
import it.cnr.si.missioni.util.proxy.json.service.GaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import net.bzdyl.ejb3.criteria.Order;
import net.bzdyl.ejb3.criteria.restrictions.Disjunction;
import net.bzdyl.ejb3.criteria.restrictions.Restrictions;


/**
 * Service class for managing users.
 */
@Service
public class OrdineMissioneService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);

    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired
    private PrintOrdineMissioneService printOrdineMissioneService;

    @Autowired
    private CMISOrdineMissioneService cmisOrdineMissioneService;

    @Autowired
    private UoService uoService;

    @Autowired
    OrdineMissioneAutoPropriaRepository OrdineMissioneAutoPropriaRepository;

    @Autowired
    private UnitaOrganizzativaService unitaOrganizzativaService;

    @Autowired
    private CdrService cdrService;
    
    @Autowired
    private GaeService gaeService;
    
    @Autowired
    private ProgettoService progettoService;
    
    @Autowired
    private ImpegnoService impegnoService;
    
    @Autowired
    private ImpegnoGaeService impegnoGaeService;

    @Autowired
    private OrdineMissioneAnticipoService ordineMissioneAnticipoService;

    @Autowired
    private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

    @Autowired
    private ConfigService configService;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired
	private AccountService accountService;
	
    @Value("${spring.mail.messages.invioResponsabileGruppo.oggetto}")
    private String subjectSendToManagerOrdine;
    
    @Value("${spring.mail.messages.ritornoMissioneMittente.oggetto}")
    private String subjectReturnToSenderOrdine;
    
    @Transactional(readOnly = true)
    public OrdineMissione getOrdineMissione(Principal principal, Long idMissione, Boolean retrieveDataFromFlows) throws ComponentException {
    	MissioneFilter filter = new MissioneFilter();
    	filter.setDaId(idMissione);
    	filter.setaId(idMissione);
    	OrdineMissione ordineMissione = null;
		List<OrdineMissione> listaOrdiniMissione = getOrdiniMissione(principal, filter, false, true);
		if (listaOrdiniMissione != null && !listaOrdiniMissione.isEmpty()){
			ordineMissione = listaOrdiniMissione.get(0);
			if (retrieveDataFromFlows){
				if (ordineMissione.isStatoInviatoAlFlusso()){
	    			ResultFlows result = cmisOrdineMissioneService.getFlowsOrdineMissione(ordineMissione.getIdFlusso());
	    			if (result != null){
		    			ordineMissione.setStateFlows(retrieveStateFromFlows(result));
		    			ordineMissione.setCommentFlows(result.getComment());
	    			}
				}
			}
			
		}
		return ordineMissione;
    }

    @Transactional(readOnly = true)
    public OrdineMissione getOrdineMissione(Principal principal, Long idMissione) throws ComponentException {
		return getOrdineMissione(principal, idMissione, false);
    }

	private void caricaDatiDerivati(Principal principal, OrdineMissione ordineMissione) throws ComponentException {
		if (ordineMissione != null){
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissione.getCdsSpesa(), ordineMissione.getAnno());
			if (dati == null){
				dati = datiIstitutoService.creaDatiIstitutoOrdine(principal, ordineMissione.getCdsSpesa(), ordineMissione.getAnno());
			}
			ordineMissione.setDatiIstituto(dati);
			if (ordineMissione.getDatiIstituto() == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non esistono i dati per istituto per il codice "+ordineMissione.getCdsSpesa()+" nell'anno "+ordineMissione.getAnno());
			}
		}
	}

    @Transactional(readOnly = true)
   	public Map<String, byte[]> printOrdineMissione(Authentication auth, Long idMissione) throws ComponentException {
    	String username = SecurityUtils.getCurrentUserLogin();
    	Principal principal = (Principal)auth;
    	OrdineMissione ordineMissione = getOrdineMissione(principal, idMissione);
		Map<String, byte[]> map = new HashMap<String, byte[]>();
    	byte[] printOrdineMissione = null;
    	String fileName = null;
    	if (!ordineMissione.isStatoNonInviatoAlFlusso()){
    		ContentStream content = null;
			try {
				content = cmisOrdineMissioneService.getContentStreamOrdineMissione(ordineMissione);
			} catch (ComponentException e1) {
				throw new ComponentException("Errore nel recupero del contenuto del file sul documentale (" + Utility.getMessageException(e1) + ")",e1);
			}
    		if (content != null){
        		fileName = content.getFileName();
        		InputStream is = null;
    			try {
    				is = content.getStream();
    			} catch (Exception e) {
    				throw new ComponentException("Errore nel recupero dello stream del file sul documentale (" + Utility.getMessageException(e) + ")",e);
    			}
        		if (is != null){
            		try {
    					printOrdineMissione = IOUtils.toByteArray(is);
    				} catch (IOException e) {
    					throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")",e);
    				}
        		}
    		} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file sul documentale");
    		}
    		map.put(fileName, printOrdineMissione);
    	} else {
    		fileName = "OrdineMissione"+idMissione+".pdf";
    		printOrdineMissione = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);
    		if (ordineMissione.isMissioneInserita()){
    			cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(principal, printOrdineMissione, ordineMissione);
    		}
    		map.put(fileName, printOrdineMissione);
    	}
		return map;
    }
    
    @Transactional(readOnly = true)
	public String jsonForPrintOrdineMissione(Principal principal, Long idMissione) throws ComponentException {
    	OrdineMissione ordineMissione = getOrdineMissione(principal, idMissione);
    	return printOrdineMissioneService.createJsonPrintOrdineMissione(ordineMissione, principal.getName());
    }

    public List<OrdineMissione> getOrdiniMissioneForValidateFlows(Principal principal, MissioneFilter filter,  Boolean isServiceRest) throws ComponentException{
    	List<OrdineMissione> lista = getOrdiniMissione(principal, filter, isServiceRest, true);
    	if (lista != null){
        	List<OrdineMissione> listaNew = new ArrayList<OrdineMissione>();
    		for (OrdineMissione ordineMissione : lista){
    			if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
        			ResultFlows result = cmisOrdineMissioneService.getFlowsOrdineMissione(ordineMissione.getIdFlusso());
        			if (result != null){
    			    	OrdineMissione ordineMissioneDaAggiornare = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, ordineMissione.getId());
        				if (result.isApprovato()){
        					aggiornaOrdineMissioneApprovato(principal, ordineMissioneDaAggiornare);
        					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_APPROVATO_PER_HOME);
        					listaNew.add(ordineMissione);
        				} else if (result.isStateReject()){
        					ordineMissione.setCommentFlows(result.getComment());
        					ordineMissione.setStateFlows(retrieveStateFromFlows(result));
        			    	aggiornaOrdineMissioneRespinto(principal, result, ordineMissioneDaAggiornare);
        					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_RESPINTO_PER_HOME);
        					listaNew.add(ordineMissione);
        				} else if (result.isAnnullato()){
        					aggiornaOrdineMissioneAnnullato(principal, ordineMissioneDaAggiornare);
        					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_ANNULLATO_PER_HOME);
        					listaNew.add(ordineMissione);
        				} else {
        					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_AUTORIZZARE_PER_HOME);
        					listaNew.add(ordineMissione);
        				}
        			}
    			} else {
    				if (ordineMissione.isMissioneDaValidare() && ordineMissione.isMissioneConfermata()){
    					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_VALIDARE_PER_HOME);
    					listaNew.add(ordineMissione);
    				} else if (ordineMissione.isMissioneInserita()){
    					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_CONFERMARE_PER_HOME);
    					listaNew.add(ordineMissione);
    				} else if (ordineMissione.isMissioneInviataResponsabile()){
    					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_PER_RESPONSABILE_GRUPPO_PER_HOME);
    					listaNew.add(ordineMissione);
    				}
    			}
    		}
    		return listaNew;
    	}
    	return lista;
    }

	public void aggiornaOrdineMissioneRespinto(Principal principal, ResultFlows result,
			OrdineMissione ordineMissioneDaAggiornare) throws ComponentException {
		aggiornaValidazione(ordineMissioneDaAggiornare);
		ordineMissioneDaAggiornare.setCommentFlows(result.getComment());
		ordineMissioneDaAggiornare.setStateFlows(retrieveStateFromFlows(result));
		ordineMissioneDaAggiornare.setStato(Costanti.STATO_INSERITO);
		OrdineMissioneAnticipo anticipo = getAnticipo(principal, ordineMissioneDaAggiornare);
		if (anticipo != null){
			anticipo.setStato(Costanti.STATO_INSERITO);
			ordineMissioneAnticipoService.updateAnticipo(principal, anticipo, false);
		}
		updateOrdineMissione(principal, ordineMissioneDaAggiornare, true);
	}

	public OrdineMissioneAnticipo getAnticipo(Principal principal, OrdineMissione ordineMissioneDaAggiornare)
			throws ComponentException {
		return ordineMissioneAnticipoService.getAnticipo(principal, new Long(ordineMissioneDaAggiornare.getId().toString()));
	}

	public void aggiornaOrdineMissioneAnnullato(Principal principal, OrdineMissione ordineMissioneDaAggiornare){
		ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_ANNULLATO);
		updateOrdineMissione(principal, ordineMissioneDaAggiornare, true);
	}

	public void aggiornaOrdineMissioneApprovato(Principal principal, OrdineMissione ordineMissioneDaAggiornare){
		ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		ordineMissioneDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
		updateOrdineMissione(principal, ordineMissioneDaAggiornare, true);
	}

	private void aggiornaValidazione(OrdineMissione ordineMissione) {
		Uo uo = uoService.recuperoUoSigla(ordineMissione.getUoRich());
		if (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("N")){
			ordineMissione.setValidato("S");
		} else {
			ordineMissione.setValidato("N");
		}
	}

	public String retrieveStateFromFlows(ResultFlows result) {
		return result.getState();
	}

    @Transactional(readOnly = true)
    public void uploadAllegatoOrdineMissione(Principal principal, Long idMissione, InputStream uploadedAllegatoInputStream, String fileName, String contentType) throws ComponentException {
    	OrdineMissione ordineMissione = getOrdineMissione(principal, idMissione);
    	cmisOrdineMissioneService.uploadAllegatoOrdineMissione(principal, ordineMissione, uploadedAllegatoInputStream, fileName, contentType);
    }

    @Transactional(readOnly = true)
    public List<OrdineMissione> getOrdiniMissione(Principal principal, MissioneFilter filter, Boolean isServiceRest) throws ComponentException {
		return getOrdiniMissione(principal, filter, isServiceRest, false);
    }

    @Transactional(readOnly = true)
    public List<OrdineMissione> getOrdiniMissione(Principal principal, MissioneFilter filter, Boolean isServiceRest, Boolean isForValidateFlows) throws ComponentException {
		CriterionList criterionList = new CriterionList();
		List<OrdineMissione> ordineMissioneList=null;
		if (filter != null){
			if (filter.getAnno() != null){
				criterionList.add(Restrictions.eq("anno", filter.getAnno()));
			}
			if (filter.getDaId() != null){
				criterionList.add(Restrictions.ge("id", filter.getDaId()));
			}
			if (filter.getStato() != null){
				criterionList.add(Restrictions.eq("stato", filter.getStato()));
			}
			if (filter.getStatoFlusso() != null){
				criterionList.add(Restrictions.eq("statoFlusso", filter.getStatoFlusso()));
			}
			if (filter.getValidato() != null){
				criterionList.add(Restrictions.eq("validato", filter.getValidato()));
			}
			if (filter.getListaStatiMissione() != null && !filter.getListaStatiMissione().isEmpty()){
				criterionList.add(Restrictions.in("stato", filter.getListaStatiMissione()));
			}
			if (filter.getListaStatiFlussoMissione() != null && !filter.getListaStatiFlussoMissione().isEmpty()){
				criterionList.add(Restrictions.in("statoFlusso", filter.getListaStatiFlussoMissione()));
			}
			
			if (filter.getaId() != null){
				criterionList.add(Restrictions.le("id", filter.getaId()));
			}
			if (filter.getDaNumero() != null){
				criterionList.add(Restrictions.ge("numero", filter.getDaNumero()));
			}
			if (filter.getaNumero() != null){
				criterionList.add(Restrictions.le("numero", filter.getaNumero()));
			}
			if (filter.getDaData() != null){
				criterionList.add(Restrictions.ge("dataInserimento", filter.getDaData()));
			}
			if (filter.getaData() != null){
				criterionList.add(Restrictions.le("dataInserimento", filter.getaData()));
			}
			if (filter.getCdsRich() != null){
				criterionList.add(Restrictions.eq("cdsRich", filter.getCdsRich()));
			}
			if (filter.getUoRich() != null){
				criterionList.add(Restrictions.eq("uoRich", filter.getUoRich()));
			}
		}
		if (filter != null && Utility.nvl(filter.getToFinal(), "N").equals("S")){
			if (StringUtils.isEmpty(filter.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stata selezionata la uo per rendere definitivi ordini di missione.");
			}
//			criterionList.add(Restrictions.isNull("pgObbligazione"));
			UsersSpecial userSpecial = accountService.getUoForUsersSpecial(principal.getName());
			boolean uoAbilitata = false;
			if (userSpecial != null){
				if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
					if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
				    	for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()){
				    		if (uoService.getUoSigla(uoUser).equals(filter.getUoRich())){
				    			uoAbilitata = true;
				    			if (!Utility.nvl(uoUser.getRendi_definitivo(),"N").equals("S")){
									throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente non è abilitato a rendere definitivi ordini di missione.");
					    		}
				    		}
				    	} 
					}
				}
			}
			if (!uoAbilitata){
				throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente non è abilitato a rendere definitivi ordini di missione.");
			}
			criterionList.add(Restrictions.eq("statoFlusso", Costanti.STATO_APPROVATO_FLUSSO));
			criterionList.add(Restrictions.eq("stato", Costanti.STATO_CONFERMATO));
			criterionList.add(Restrictions.eq("validato", "S"));
			ordineMissioneList = crudServiceBean.findByProjection(principal, OrdineMissione.class, OrdineMissione.getProjectionForElencoMissioni(), criterionList, true, Order.asc("dataInserimento"));
			return ordineMissioneList;
			
		} else {
			if (!isForValidateFlows){
				if (!StringUtils.isEmpty(filter.getUser())){
					criterionList.add(Restrictions.eq("uid", filter.getUser()));
				} else {
					criterionList.add(Restrictions.eq("uid", principal.getName()));
				}
			} else {
				UsersSpecial userSpecial = accountService.getUoForUsersSpecial(principal.getName());
				if (userSpecial != null){
					if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
						if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
							boolean esisteUoConValidazioneConUserNonAbilitato = false;
							Disjunction condizioneOr = Restrictions.disjunction();
							List<String> listaUoUtente = new ArrayList<String>();
					    	for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()){
					    		Uo uo = uoService.recuperoUo(uoUser.getCodice_uo());
					    		if (uo != null){
						    		listaUoUtente.add(uoService.getUoSigla(uoUser));
						    		if (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("S")){
						    			if (Utility.nvl(uoUser.getOrdine_da_validare(),"N").equals("S")){
							    			condizioneOr.add(Restrictions.eq("uoRich", uoService.getUoSigla(uoUser)));
							    		} else {
							    			esisteUoConValidazioneConUserNonAbilitato = true;
							    			condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoRich", uoService.getUoSigla(uoUser))).add(Restrictions.eq("validato", "S")));
						    			}
						    		}
					    		}
					    	}
					    	condizioneResponsabileGruppo(principal, condizioneOr);
					    	if (esisteUoConValidazioneConUserNonAbilitato){
					    		criterionList.add(condizioneOr);
					    	} else {
								Disjunction condizioneNuovaOr = Restrictions.disjunction();
					    		condizioneNuovaOr.add(Restrictions.in("uoRich", listaUoUtente));
					    		criterionList.add(condizioneNuovaOr);
					    	}
						} else {
							condizioneOrdineDellUtenteConResponsabileGruppo(principal, criterionList);
						}
					}
				} else {
					condizioneOrdineDellUtenteConResponsabileGruppo(principal, criterionList);
				}
			}
			criterionList.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO)));

			if (isServiceRest) {
				if (isForValidateFlows){
					List<String> listaStatiFlusso = new ArrayList<String>();
					listaStatiFlusso.add(Costanti.STATO_INVIATO_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_NON_INVIATO_FLUSSO);
					criterionList.add(Restrictions.disjunction().add(Restrictions.disjunction().add(Restrictions.in("statoFlusso", listaStatiFlusso)).add(Restrictions.eq("stato", Costanti.STATO_INSERITO))));
				}
				ordineMissioneList = crudServiceBean.findByProjection(principal, OrdineMissione.class, OrdineMissione.getProjectionForElencoMissioni(), criterionList, true, Order.asc("dataInserimento"));
			} else
				ordineMissioneList = crudServiceBean.findByCriterion(principal, OrdineMissione.class, criterionList, Order.asc("dataInserimento"));
			return ordineMissioneList;
		}
    }

	private void condizioneOrdineDellUtenteConResponsabileGruppo(Principal principal, CriterionList criterionList) {
		Disjunction condizioneOr = Restrictions.disjunction();
		condizioneResponsabileGruppo(principal, condizioneOr);
		condizioneOr.add(Restrictions.eq("uid", principal.getName()));
		criterionList.add(condizioneOr);
	}

	private void condizioneResponsabileGruppo(Principal principal, Disjunction condizioneOr) {
		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("responsabileGruppo", principal.getName())).add(Restrictions.eq("stato", "INR")));
	}

    @Transactional(readOnly = true)
    public OrdineMissioneAutoPropria getAutoPropria(OrdineMissione ordineMissione) {
        return OrdineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione createOrdineMissione(Principal principal, OrdineMissione ordineMissione)  throws ComponentException{
    	controlloDatiObbligatoriDaGUI(ordineMissione);
    	inizializzaCampiPerInserimento(principal, ordineMissione);
		validaCRUD(principal, ordineMissione);
		ordineMissione = (OrdineMissione)crudServiceBean.creaConBulk(principal, ordineMissione);
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for User: {}", ordineMissione);
    	return ordineMissione;
    }


    private void inizializzaCampiPerInserimento(Principal principal,
    		OrdineMissione ordineMissione) throws ComponentException {
    	ordineMissione.setUidInsert(principal.getName());
    	ordineMissione.setUser(principal.getName());
    	Integer anno = recuperoAnno(ordineMissione);
    	ordineMissione.setAnno(anno);
    	ordineMissione.setNumero(datiIstitutoService.getNextPG(principal, ordineMissione.getCdsRich(), anno , Costanti.TIPO_ORDINE_DI_MISSIONE));
    	if (StringUtils.isEmpty(ordineMissione.getTrattamento())){
    		ordineMissione.setTrattamento("R");
    	}
    	if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())){
    		ordineMissione.setObbligoRientro("S");
    	}
    	if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())){
    		ordineMissione.setUtilizzoAutoNoleggio("N");
    	}
    	if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())){
    		ordineMissione.setUtilizzoTaxi("N");
    	}
    	
    	aggiornaValidazione(ordineMissione);
    	
    	ordineMissione.setStato(Costanti.STATO_INSERITO);
    	ordineMissione.setStatoFlusso(Costanti.STATO_INSERITO);

    	ordineMissione.setToBeCreated();
    }

	private Integer recuperoAnno(OrdineMissione ordineMissione) {
		if (ordineMissione.getDataInserimento() == null){
			ordineMissione.setDataInserimento(LocalDate.now());
		}
		return ordineMissione.getDataInserimento().getYear();
	}

	private Boolean isInvioOrdineAlResponsabileGruppo(OrdineMissione ordineMissione){
		return Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("M");
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione updateOrdineMissione(Principal principal, OrdineMissione ordineMissione)  throws ComponentException{
    	return updateOrdineMissione(principal, ordineMissione, false);
    }
    
    private OrdineMissione updateOrdineMissione(Principal principal, OrdineMissione ordineMissione, Boolean fromFlows)  throws ComponentException{
    	return updateOrdineMissione(principal, ordineMissione, fromFlows, false);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public OrdineMissione updateOrdineMissione(Principal principal, OrdineMissione ordineMissione, Boolean fromFlows, Boolean confirm)  {

    	OrdineMissione ordineMissioneDB = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, ordineMissione.getId());
    	boolean isCambioResponsabileGruppo = false;
       	boolean isRitornoMissioneMittente = false;
		if (ordineMissioneDB==null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di Missione da aggiornare inesistente.");
		}
		
		if (ordineMissione.getResponsabileGruppo() != null && ordineMissioneDB.getResponsabileGruppo() != null && 
				!ordineMissione.getResponsabileGruppo().equals(ordineMissioneDB.getResponsabileGruppo())){
			isCambioResponsabileGruppo = true;
		}
		if (ordineMissioneDB.isMissioneConfermata() && !fromFlows && !Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("D")){
			if (ordineMissioneDB.isStatoFlussoApprovato()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile modificare l'ordine di missione. E' già stato approvato.");
			}
			if (!ordineMissioneDB.isMissioneDaValidare()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile modificare l'ordine di missione. E' già stato avviato il flusso di approvazione.");
			}
		}
		
		if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("S")){
			if (!ordineMissioneDB.getStato().equals(Costanti.STATO_CONFERMATO)){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione non confermato.");
			}
			if (!ordineMissioneDB.isMissioneDaValidare()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione già validato.");
			}
			if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), ordineMissioneDB.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare gli ordini di missione.");
			}
			
			if (!confirm){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Operazione non possibile. Non è possibile modificare un ordine di missione durante la fase di validazione. Rieseguire la ricerca.");
			}
			ordineMissioneDB.setValidato("S");
		} else if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("D")){
			if (ordineMissione.getEsercizioOriginaleObbligazione() == null || ordineMissione.getPgObbligazione() == null ){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Per rendere definitivo l'ordine di missione è necessario valorizzare l'impegno.");
			}
			if (!StringUtils.isEmpty(ordineMissione.getGae())){
				ordineMissioneDB.setGae(ordineMissione.getGae());
			}
			if (!StringUtils.isEmpty(ordineMissione.getVoce())){
				ordineMissioneDB.setVoce(ordineMissione.getVoce());
			}
			ordineMissioneDB.setEsercizioOriginaleObbligazione(ordineMissione.getEsercizioOriginaleObbligazione());
			ordineMissioneDB.setPgObbligazione(ordineMissione.getPgObbligazione());
			ordineMissioneDB.setStato(Costanti.STATO_DEFINITIVO);
		} else if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("R")){
			if (ordineMissioneDB.isStatoNonInviatoAlFlusso() || ordineMissioneDB.isMissioneDaValidare()) {
				ordineMissioneDB.setStato(Costanti.STATO_INSERITO);
				isRitornoMissioneMittente = true;
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile sbloccare un ordine di missione se è stato già inviato al flusso.");
			}
		} else if (isInvioOrdineAlResponsabileGruppo(ordineMissione)){
			if (ordineMissione.getResponsabileGruppo() != null){
				if (ordineMissioneDB.isMissioneInserita()) {
					ordineMissioneDB.setResponsabileGruppo(ordineMissione.getResponsabileGruppo());
					ordineMissioneDB.setStato(Costanti.STATO_INVIATO_RESPONSABILE);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile inviare al responsabile una missione in stato diverso da 'Inserito'.");
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "E' obbligatorio indicare il responsabile del gruppo.");
			}
			if (StringUtils.isEmpty(ordineMissioneDB.getPgProgetto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il Progetto.");
			}

		} else {
			ordineMissioneDB.setStato(ordineMissione.getStato());
			ordineMissioneDB.setStatoFlusso(ordineMissione.getStatoFlusso());
			ordineMissioneDB.setCdrSpesa(ordineMissione.getCdrSpesa());
			ordineMissioneDB.setCdsSpesa(ordineMissione.getCdsSpesa());
			ordineMissioneDB.setUoSpesa(ordineMissione.getUoSpesa());
			ordineMissioneDB.setCdsCompetenza(ordineMissione.getCdsCompetenza());
			ordineMissioneDB.setUoCompetenza(ordineMissione.getUoCompetenza());
			ordineMissioneDB.setDomicilioFiscaleRich(ordineMissione.getDomicilioFiscaleRich());
			ordineMissioneDB.setDataInizioMissione(ordineMissione.getDataInizioMissione());
			ordineMissioneDB.setDataFineMissione(ordineMissione.getDataFineMissione());
			ordineMissioneDB.setDestinazione(ordineMissione.getDestinazione());
			ordineMissioneDB.setDistanzaDallaSede(ordineMissione.getDistanzaDallaSede());
			ordineMissioneDB.setGae(ordineMissione.getGae());
			ordineMissioneDB.setImportoPresunto(ordineMissione.getImportoPresunto());
			ordineMissioneDB.setModulo(ordineMissione.getModulo());
			ordineMissioneDB.setNote(ordineMissione.getNote());
			ordineMissioneDB.setObbligoRientro(ordineMissione.getObbligoRientro());
			if (confirm){
				aggiornaValidazione(ordineMissioneDB);
			} else {
				ordineMissioneDB.setValidato(ordineMissione.getValidato());
			}
			ordineMissioneDB.setOggetto(ordineMissione.getOggetto());
			ordineMissioneDB.setPartenzaDa(ordineMissione.getPartenzaDa());
			ordineMissioneDB.setPriorita(ordineMissione.getPriorita());
			ordineMissioneDB.setTipoMissione(ordineMissione.getTipoMissione());
			ordineMissioneDB.setVoce(ordineMissione.getVoce());
			ordineMissioneDB.setTrattamento(ordineMissione.getTrattamento());
			ordineMissioneDB.setNazione(ordineMissione.getNazione());

			ordineMissioneDB.setNoteUtilizzoTaxiNoleggio(ordineMissione.getNoteUtilizzoTaxiNoleggio());
			ordineMissioneDB.setUtilizzoAutoNoleggio(ordineMissione.getUtilizzoAutoNoleggio());
			ordineMissioneDB.setUtilizzoTaxi(ordineMissione.getUtilizzoTaxi());
			ordineMissioneDB.setPgProgetto(ordineMissione.getPgProgetto());
			ordineMissioneDB.setEsercizioOriginaleObbligazione(ordineMissione.getEsercizioOriginaleObbligazione());
			ordineMissioneDB.setPgObbligazione(ordineMissione.getPgObbligazione());
			ordineMissioneDB.setResponsabileGruppo(ordineMissione.getResponsabileGruppo());
		}
		
    	if (confirm){
    		DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getCdsSpesa(), ordineMissione.getAnno());
    		if (istituto.isAttivaGestioneResponsabileModulo()){
    			if (StringUtils.isEmpty(ordineMissioneDB.getResponsabileGruppo())){
    				throw new AwesomeException(CodiciErrore.ERRGEN, "Per il cds di spesa indicato è attiva la gestione del responsabile del gruppo ma non è stato inserito il responsabile del gruppo.");
    			}
    			if (StringUtils.isEmpty(ordineMissioneDB.getPgProgetto())){
    				throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il Progetto.");
    			}
    			if (ordineMissioneDB.isMissioneInserita() && !ordineMissioneDB.getResponsabileGruppo().equals(ordineMissione.getUid())){
    				throw new AwesomeException(CodiciErrore.ERRGEN, "Per il cds di spesa indicato è attiva la gestione del responsabile del gruppo ma l'ordine di missione non è stato inviato alla sua approvazione.");
    			}
    		}
        	ordineMissioneDB.setStato(Costanti.STATO_CONFERMATO);
    	} 

    	ordineMissioneDB.setToBeUpdated();

//		//effettuo controlli di validazione operazione CRUD
    	if (!Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("R")){
        	validaCRUD(principal, ordineMissioneDB);
    	}

    	if (confirm && !ordineMissioneDB.isMissioneDaValidare()){
    		cmisOrdineMissioneService.avviaFlusso((Principal) SecurityUtils.getCurrentUser(), ordineMissioneDB);
    	}
		ordineMissioneDB = (OrdineMissione)crudServiceBean.modificaConBulk(principal, ordineMissioneDB);
    	
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for Ordine di Missione: {}", ordineMissioneDB);
    	if (isInvioOrdineAlResponsabileGruppo(ordineMissione) || (isCambioResponsabileGruppo && ordineMissione.isMissioneInviataResponsabile())){
    		mailService.sendEmail(subjectSendToManagerOrdine, getTextMailSendToManager(ordineMissioneDB), false, true, getEmail(ordineMissione.getResponsabileGruppo()));
    	}
    	if (isRitornoMissioneMittente){
    		mailService.sendEmail(subjectReturnToSenderOrdine, getTextMailReturnToSender(ordineMissioneDB), false, true, getEmail(ordineMissione.getUidInsert()));
    	}
		return ordineMissioneDB;
    }

    private String getEmail(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getEmailComunicazioni();
    }

    private String getNominativo(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getCognome()+ " "+ utente.getNome();
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

	private String getTextMailSendToManager(OrdineMissione ordineMissione) {
		return "L'ordine di missione "+ordineMissione.getAnno()+"-"+ordineMissione.getNumero()+ " di "+getNominativo(ordineMissione.getUid())+" per la missione a "+ordineMissione.getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione())+ " avente per oggetto "+ordineMissione.getOggetto()+" le è stata inviata per l'approvazione in quanto responsabile del gruppo.";
	}

	private String getTextMailReturnToSender(OrdineMissione ordineMissione) {
		return "L'ordine di missione "+ordineMissione.getAnno()+"-"+ordineMissione.getNumero()+ " di "+getNominativo(ordineMissione.getUid())+" per la missione a "+ordineMissione.getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione())+ " avente per oggetto "+ordineMissione.getOggetto()+" le è stata restituito dal responsabile del gruppo "+getNominativo(ordineMissione.getResponsabileGruppo())+" per apportare delle correzioni.";
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteOrdineMissione(Principal principal, Long idOrdineMissione) {
		OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idOrdineMissione);
		if (ordineMissione != null){
			controlloOperazioniCRUDDaGui(ordineMissione);
			ordineMissioneAnticipoService.deleteAnticipo(principal, ordineMissione);
			ordineMissioneAutoPropriaService.deleteAutoPropria(principal, ordineMissione);
			//effettuo controlli di validazione operazione CRUD
			ordineMissione.setStato(Costanti.STATO_ANNULLATO);
			ordineMissione.setToBeUpdated();
			if (ordineMissione.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(ordineMissione.getIdFlusso())){
				cmisOrdineMissioneService.annullaFlusso(ordineMissione);
			}
			crudServiceBean.modificaConBulk(principal, ordineMissione);
		}
	}

	public void controlloOperazioniCRUDDaGui(OrdineMissione ordineMissione) {
		if (!ordineMissione.isMissioneInserita()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile effettuare l'operazione su un ordine di missione che non si trova in uno stato "+Costanti.STATO.get(Costanti.STATO_INSERITO));
		}
	}
	
    private void controlloDatiObbligatoriDaGUI(OrdineMissione ordineMissione){
		if (ordineMissione != null){
			if (StringUtils.isEmpty(ordineMissione.getCdsRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getCdsSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getDataInizioMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Inizio Missione");
			} else if (StringUtils.isEmpty(ordineMissione.getDataFineMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Fine Missione");
			} else if (StringUtils.isEmpty(ordineMissione.getDataInserimento())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Inserimento");
			} else if (StringUtils.isEmpty(ordineMissione.getDatoreLavoroRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Datore di Lavoro Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getDestinazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Destinazione");
			} else if (StringUtils.isEmpty(ordineMissione.getOggetto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Oggetto");
			} else if (StringUtils.isEmpty(ordineMissione.getPriorita())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Priorità");
			} else if (StringUtils.isEmpty(ordineMissione.getTipoMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Tipo Missione");
			} 
			if (ordineMissione.isMissioneEstera()){
				if (StringUtils.isEmpty(ordineMissione.getNazione())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Nazione");
				} 
				if (StringUtils.isEmpty(ordineMissione.getTrattamento())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Trattamento");
				} 
			}

			if (ordineMissione.isMissioneConGiorniDivervi()){
				if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Obbligo di Rientro");
				}
			} 
			if (ordineMissione.isMissioneDipendente()){
				if (StringUtils.isEmpty(ordineMissione.getComuneResidenzaRich())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Comune di Residenza del Richiedente");
				} else if (StringUtils.isEmpty(ordineMissione.getIndirizzoResidenzaRich())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Indirizzo di Residenza del Richiedente");
				}
			}
		}
    }
	private void controlloCongruenzaDatiInseriti(Principal principal, OrdineMissione ordineMissione) {
		if (ordineMissione.getDataFineMissione().isBefore(ordineMissione.getDataInizioMissione())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": La data di fine missione non può essere precedente alla data di inizio missione");
		}
		if (!StringUtils.isEmpty(ordineMissione.getNoteUtilizzoTaxiNoleggio())){
			if (ordineMissione.getUtilizzoTaxi().equals("N") && ordineMissione.getUtilizzoAutoNoleggio().equals("N")){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Non è possibile indicare le note all'utilizzo del taxi o dell'auto a noleggio se non si è scelto il loro utilizzo");
			}
		}
		if (StringUtils.isEmpty(ordineMissione.getIdFlusso()) &&  ordineMissione.isStatoInviatoAlFlusso()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile avere lo stato Inviato al flusso e non avere l'ID del flusso");
		} 
		if (!ordineMissione.isMissioneEstera()){
			ordineMissione.setNazione(new Long("1"));
		} 
        if (ordineMissione.getUtilizzoAutoNoleggio() != null && ordineMissione.getUtilizzoAutoNoleggio().equals("S") && 
            getAutoPropria(ordineMissione) != null ){
            throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile salvare una missione con la richiesta di utilizzo dell'auto a noleggio e dell'auto propria.");
        } 
		if ((Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S") || Utility.nvl(ordineMissione.getUtilizzoTaxi()).equals("S")) && StringUtils.isEmpty(ordineMissione.getNoteUtilizzoTaxiNoleggio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": E' obbligatorio indicare le note all'utilizzo del taxi o dell'auto a noleggio se si è scelto il loro utilizzo");
		}
        if (ordineMissione.isFondiCompetenza() && !StringUtils.isEmpty(ordineMissione.getEsercizioObbligazione()) && !StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione()) 
        		&&  ordineMissione.getEsercizioObbligazione().compareTo(ordineMissione.getEsercizioOriginaleObbligazione()) != 0){
                throw new AwesomeException(CodiciErrore.ERRGEN, "Incongruenza tra fondi e esercizio obbligazione.");
        } 
        if (ordineMissione.isFondiResiduo() && !StringUtils.isEmpty(ordineMissione.getEsercizioObbligazione()) && !StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione()) 
        		&&  ordineMissione.getEsercizioObbligazione().compareTo(ordineMissione.getEsercizioOriginaleObbligazione()) <= 0){
                throw new AwesomeException(CodiciErrore.ERRGEN, "Incongruenza tra fondi e esercizio obbligazione.");
        } 
	}
	
	private void controlloDatiFinanziari(Principal principal, OrdineMissione ordineMissione) {
    	UnitaOrganizzativa uo = unitaOrganizzativaService.loadUo(ordineMissione.getUoSpesa(), ordineMissione.getCdsSpesa(), ordineMissione.getAnno());
    	if (uo == null){
    		throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La UO "+ ordineMissione.getUoSpesa() + " non è corretta rispetto al CDS "+ordineMissione.getCdsSpesa());
    	}
		if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())){
			Cdr cdr = cdrService.loadCdr(ordineMissione.getCdrSpesa(), ordineMissione.getUoSpesa());
			if (cdr == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Il CDR "+ ordineMissione.getCdrSpesa() + " non è corretto rispetto alla UO "+ordineMissione.getUoSpesa());
			}
		}
		if (!StringUtils.isEmpty(ordineMissione.getPgProgetto())){
			Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), ordineMissione.getAnno(), ordineMissione.getUoSpesa());
			if (progetto == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Il modulo indicato non è corretto rispetto alla UO "+ordineMissione.getUoSpesa());
			}
		}
		if (!StringUtils.isEmpty(ordineMissione.getGae())){
			if (StringUtils.isEmpty(ordineMissione.getCdrSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile indicare la GAE senza il centro di responsabilità");
			}
			Gae gae = gaeService.loadGae(ordineMissione);
			if (gae == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "La GAE "+ ordineMissione.getGae()+" indicata non esiste");
			} else {
				boolean progettoCdrIndicato = false;
				if (!StringUtils.isEmpty(ordineMissione.getPgProgetto()) && !StringUtils.isEmpty(gae.getPg_progetto())){
					progettoCdrIndicato = true;
					if (gae.getPg_progetto().compareTo(ordineMissione.getPgProgetto()) != 0){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La GAE indicata "+ ordineMissione.getGae()+" non corrisponde al modulo indicato.");
					}
				}
				if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())){
					progettoCdrIndicato = true;
					if (!gae.getCd_centro_responsabilita().equals(ordineMissione.getCdrSpesa()) ){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La GAE indicata "+ ordineMissione.getGae()+" non corrisponde con il CDR "+ordineMissione.getCdrSpesa() +" indicato.");
					}
				}
				if (!progettoCdrIndicato){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Non è possibile indicare solo La GAE senza il modulo o il CDR.");
				}
			}
		}

		if (!StringUtils.isEmpty(ordineMissione.getPgObbligazione())){
			if (StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Oltre al numero dell'impegno è necessario indicare anche l'anno dell'impegno");
			}
			if (!StringUtils.isEmpty(ordineMissione.getGae())){
				ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(ordineMissione);
				if (impegnoGae == null){
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ ordineMissione.getEsercizioOriginaleObbligazione() + "-" + ordineMissione.getPgObbligazione() +" non corrisponde con la GAE "+ ordineMissione.getGae()+" indicata oppure non esiste");
				} else {
					if (!StringUtils.isEmpty(ordineMissione.getVoce())){
						if (!impegnoGae.getCdElementoVoce().equals(ordineMissione.getVoce())){
							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+ordineMissione.getEsercizioOriginaleObbligazione() + "-" + ordineMissione.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+ordineMissione.getVoce());
						}
					} else {
						ordineMissione.setVoce(impegnoGae.getCdElementoVoce());
					}
					ordineMissione.setCdCdsObbligazione(impegnoGae.getCdCds());
					ordineMissione.setEsercizioObbligazione(impegnoGae.getEsercizio());
				}
			} else {
				Impegno impegno = impegnoService.loadImpegno(ordineMissione);
				if (impegno == null){
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ ordineMissione.getEsercizioOriginaleObbligazione() + "-" + ordineMissione.getPgObbligazione() +" non esiste");
				} else {
					if (!StringUtils.isEmpty(ordineMissione.getVoce())){
						if (!impegno.getCdElementoVoce().equals(ordineMissione.getVoce())){
							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+ordineMissione.getEsercizioOriginaleObbligazione() + "-" + ordineMissione.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+ordineMissione.getVoce());
						}
					} else {
						ordineMissione.setVoce(impegno.getCdElementoVoce());
					}
					ordineMissione.setCdCdsObbligazione(impegno.getCdCds());
					ordineMissione.setEsercizioObbligazione(impegno.getEsercizio());
				}
			}
		} else {
			if (!StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione()) && StringUtils.isEmpty(ordineMissione.getFondi())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Oltre all'anno dell'impegno è necessario indicare anche il numero dell'impegno");
			}
			ordineMissione.setCdCdsObbligazione(null);
			ordineMissione.setEsercizioObbligazione(null);
		}
    
    }
	
	private void validaCRUD(Principal principal, OrdineMissione ordineMissione) {
		if (ordineMissione != null){
			controlloCampiObbligatori(ordineMissione); 
			controlloCongruenzaDatiInseriti(principal, ordineMissione);
			controlloDatiFinanziari(principal, ordineMissione);
		}
	}
    
	private void controlloCampiObbligatori(OrdineMissione ordineMissione) {
		if (!ordineMissione.isToBeCreated()){
			controlloDatiObbligatoriDaGUI(ordineMissione);
		}
		if (StringUtils.isEmpty(ordineMissione.getAnno())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anno");
		} else if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Obbligo di Rientro");
		} else if (StringUtils.isEmpty(ordineMissione.getUid())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utente");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo del Taxi");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo auto a noleggio");
		} else if (StringUtils.isEmpty(ordineMissione.getStato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Stato");
		} else if (StringUtils.isEmpty(ordineMissione.getValidato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Validato");
		} else if (StringUtils.isEmpty(ordineMissione.getNumero())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Numero");
		}
	}
}
