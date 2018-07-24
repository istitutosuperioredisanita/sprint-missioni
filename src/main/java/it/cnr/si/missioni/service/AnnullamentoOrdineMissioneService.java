package it.cnr.si.missioni.service;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.amq.domain.Missione;
import it.cnr.si.missioni.amq.domain.TypeMissione;
import it.cnr.si.missioni.amq.domain.TypeTipoMissione;
import it.cnr.si.missioni.amq.service.RabbitMQService;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import net.bzdyl.ejb3.criteria.Criteria;
import net.bzdyl.ejb3.criteria.Order;
import net.bzdyl.ejb3.criteria.restrictions.Disjunction;
import net.bzdyl.ejb3.criteria.restrictions.Restrictions;


/**
 * Service class for managing users.
 */
@Service
public class AnnullamentoOrdineMissioneService {

    private final Logger log = LoggerFactory.getLogger(AnnullamentoOrdineMissioneService.class);

	@Autowired
	private CRUDComponentSession crudServiceBean;

    @Autowired
    private Environment env;

	@Autowired
	private AccountService accountService;

	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private PrintAnnullamentoOrdineMissioneService printAnnullamentoMissioneService;

	@Autowired
	CronService cronService;
	
	@Autowired
	CMISOrdineMissioneService cmisOrdineMissioneService; 
	
    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired
    private DatiSedeService datiSedeService;

    @Autowired
    private RabbitMQService rabbitMQService;

	@Autowired
	private MailService mailService;
    
	@Autowired
	private UoService uoService;
    
    @Value("${spring.mail.messages.invioAnnullamentoOrdineMissione.oggetto}")
    private String subjectSendToAdministrative;
    
    @Value("${spring.mail.messages.ritornoAnnullamentoOrdineMittente.oggetto}")
    private String subjectReturnToSender;
    
    @Transactional(readOnly = true)
    public AnnullamentoOrdineMissione getAnnullamentoOrdineMissione(Principal principal, Long idAnnullamento, Boolean retrieveDataFromFlows) throws ComponentException {
    	RimborsoMissioneFilter filter = new RimborsoMissioneFilter();
    	filter.setDaId(idAnnullamento);
    	filter.setaId(idAnnullamento);
    	AnnullamentoOrdineMissione annullamento = null;
		List<AnnullamentoOrdineMissione> listaAnnullamentiMissione = getAnnullamenti(principal, filter, false, true);
		if (listaAnnullamentiMissione != null && !listaAnnullamentiMissione.isEmpty()){
			annullamento = listaAnnullamentiMissione.get(0);
			if (retrieveDataFromFlows){
				if (annullamento.isStatoInviatoAlFlusso()){
	    			ResultFlows result = retrieveDataFromFlows(annullamento);
	    			if (result != null){
	    				annullamento.setStateFlows(retrieveStateFromFlows(result));
	    				annullamento.setCommentFlows(result.getComment());
	    			}
				}
			}
		}
		return annullamento;
    }

	private boolean isDevProfile(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return true;
   		}
   		return false;
	}

    public List<AnnullamentoOrdineMissione> getAnnullamentiForValidateFlows(Principal principal, RimborsoMissioneFilter filter,  Boolean isServiceRest) throws ComponentException{
    	List<AnnullamentoOrdineMissione> lista = getAnnullamenti(principal, filter, isServiceRest, true);
    	if (lista != null){
    		List<AnnullamentoOrdineMissione> listaNew = new ArrayList<AnnullamentoOrdineMissione>();
    		for (AnnullamentoOrdineMissione annullamento : lista){
    			if (annullamento.isStatoInviatoAlFlusso() && !annullamento.isMissioneDaValidare()){
    				annullamento.setStatoFlussoRitornoHome(Costanti.STATO_DA_AUTORIZZARE_PER_HOME);
    				listaNew.add(annullamento);
    			} else {
    				if (annullamento.isMissioneDaValidare() && annullamento.isMissioneConfermata()){
    					annullamento.setStatoFlussoRitornoHome(Costanti.STATO_DA_VALIDARE_PER_HOME);
    					listaNew.add(annullamento);
    				} else if (annullamento.isMissioneInserita()){
    					annullamento.setStatoFlussoRitornoHome(Costanti.STATO_DA_CONFERMARE_PER_HOME);
    					listaNew.add(annullamento);
    				}
    			}
    		}
    		return listaNew;
    	}
    	return lista;
    }

	public void aggiornaAnnullamentoRespinto(Principal principal, ResultFlows result,
			AnnullamentoOrdineMissione annullamentoDaAggiornare) throws ComponentException{
		aggiornaValidazione(principal, annullamentoDaAggiornare);
		annullamentoDaAggiornare.setCommentFlows(result.getComment());
		annullamentoDaAggiornare.setStateFlows(retrieveStateFromFlows(result));
		annullamentoDaAggiornare.setStato(Costanti.STATO_INSERITO);
		updateAnnullamentoOrdineMissione(principal, annullamentoDaAggiornare, true, null);
	}

	public void aggiornaAnnullamentoOrdineMissioneAnnullato(Principal principal, AnnullamentoOrdineMissione annullamentoDaAggiornare)
			throws ComponentException {
		annullamentoDaAggiornare.setStatoFlusso(Costanti.STATO_ANNULLATO);
		updateAnnullamentoOrdineMissione(principal, annullamentoDaAggiornare, true, null);
	}

	public AnnullamentoOrdineMissione aggiornaAnnullamentoMissioneApprovato(Principal principal, AnnullamentoOrdineMissione annullamentoDaAggiornare, OrdineMissione ordineMissione)
			throws ComponentException {
		annullamentoDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		annullamentoDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
		AnnullamentoOrdineMissione annullamento = updateAnnullamentoOrdineMissione(principal, annullamentoDaAggiornare, true, null);
		ordineMissione.setStato(Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE);
		ordineMissione = ordineMissioneService.updateOrdineMissione(principal, ordineMissione, true, false);
		popolaCoda(annullamento);
		ordineMissioneService.gestioneEmailDopoApprovazione(ordineMissione, true);
		return annullamento;
	}

	public void popolaCoda(AnnullamentoOrdineMissione annullamento) {
		if (annullamento.getMatricola() != null){
			Account account = accountService.loadAccountFromRest(annullamento.getUid());
			String idSede = null;
			if (account != null){
				idSede = account.getCodiceSede();
			}
			Missione missione = new Missione(TypeMissione.ANNULLAMENTO, new Long(annullamento.getId().toString()), idSede, 
					annullamento.getOrdineMissione().getMatricola(), annullamento.getOrdineMissione().getDataInizioMissione(), 
					annullamento.getOrdineMissione().getDataFineMissione(), new Long(annullamento.getOrdineMissione().getId().toString()), annullamento.getOrdineMissione().isMissioneEstera() ? TypeTipoMissione.ESTERA : TypeTipoMissione.ITALIA);
			rabbitMQService.send(missione);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoOrdineMissione updateAnnullamentoOrdineMissione(Principal principal, AnnullamentoOrdineMissione annullamento, String basePath)  throws ComponentException{
    	return updateAnnullamentoOrdineMissione(principal, annullamento, false, basePath);
    }
    
    private AnnullamentoOrdineMissione updateAnnullamentoOrdineMissione(Principal principal, AnnullamentoOrdineMissione annullamento, Boolean fromFlows, String basePath)  throws ComponentException{
    	return updateAnnullamentoOrdineMissione(principal, annullamento, fromFlows, false, basePath);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoOrdineMissione updateAnnullamentoOrdineMissione (Principal principal, AnnullamentoOrdineMissione annullamento, Boolean fromFlows, Boolean confirm, String basePath)  throws ComponentException{

    	AnnullamentoOrdineMissione annullamentoDB = (AnnullamentoOrdineMissione)crudServiceBean.findById(principal, AnnullamentoOrdineMissione.class, annullamento.getId());
       	boolean isRitornoMissioneMittente = false;

		if (annullamentoDB==null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Annullamento Ordine Missione da aggiornare inesistente.");
		}
		
    	if (annullamentoDB.getOrdineMissione() != null){
        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamentoDB.getOrdineMissione().getId());
        	if (ordineMissione != null){
        		annullamento.setOrdineMissione(ordineMissione);
        	}
    	}
		if (Utility.nvl(annullamento.getDaValidazione(), "N").equals("S")){
			if (!annullamentoDB.getStato().equals(Costanti.STATO_CONFERMATO)){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione non confermato.");
			}
			if (!annullamentoDB.isMissioneDaValidare()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione già validato.");
			}
			if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), annullamento.getOrdineMissione().getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare gli ordini di missione per la uo "+annullamento.getOrdineMissione().getUoSpesa()+".");
			}
			
			if (!confirm){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Operazione non possibile. Non è possibile modificare un annullamento ordine di missione durante la fase di validazione. Rieseguire la ricerca.");
			}

			aggiornaDatiAnnullamentoOrdineMissione(principal, annullamento, confirm, annullamentoDB);
			annullamentoDB.setValidato("S");
		} else if (Utility.nvl(annullamento.getDaValidazione(), "N").equals("R")){
			if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), annullamentoDB.getOrdineMissione().getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare gli ordini di missione per la uo "+annullamentoDB.getOrdineMissione().getUoSpesa()+".");
			}
			if (annullamentoDB.isStatoNonInviatoAlFlusso() || annullamentoDB.isMissioneDaValidare()) {
				annullamentoDB.setStato(Costanti.STATO_INSERITO);
				isRitornoMissioneMittente = true;
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile sbloccare un ordine di missione se è stato già inviato al flusso.");
			}
		} else {
			aggiornaDatiAnnullamentoOrdineMissione(principal, annullamento, confirm, annullamentoDB);
		}

		if (confirm){
			annullamentoDB.setStato(Costanti.STATO_CONFERMATO);
    	} 

		annullamentoDB.setToBeUpdated();
//		//effettuo controlli di validazione operazione CRUD
    	if (confirm && !annullamentoDB.isMissioneDaValidare()){
    		cmisOrdineMissioneService.avviaFlusso((Principal) SecurityUtils.getCurrentUser(), annullamentoDB);
    		annullamentoDB.setStateFlows(Costanti.STATO_FLUSSO_RIMBORSO_FROM_CMIS.get(Costanti.STATO_FIRMA_UO_RIMBORSO_FROM_CMIS));
    	}
    	annullamentoDB = (AnnullamentoOrdineMissione)crudServiceBean.modificaConBulk(principal, annullamentoDB);
    	
    	if (confirm && annullamentoDB.isMissioneDaValidare()){
    		sendMailToAdministrative(basePath, annullamentoDB);
    	}
    	if (isRitornoMissioneMittente){
    		mailService.sendEmail(subjectReturnToSender, getTextMailReturnToSender(principal, basePath, annullamentoDB), false, true, accountService.getEmail(annullamentoDB.getUidInsert()));
    	}
    	log.debug("Updated Information for Annullamento Ordine Missione: {}", annullamentoDB);

    	return annullamentoDB;
    }

	private String getTextMailReturnToSender(Principal principal, String basePath, AnnullamentoOrdineMissione annullamento) {
		return "L'annullamento ordine di missione "+annullamento.getAnno()+"-"+annullamento.getNumero()+ " di "+getNominativo(annullamento.getUid())+" per la missione a "+annullamento.getOrdineMissione().getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(annullamento.getOrdineMissione().getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(annullamento.getOrdineMissione().getDataFineMissione())+ " avente per oggetto "+annullamento.getOrdineMissione().getOggetto()+" le è stata respinto da "+getNominativo(principal.getName());
	}

    private void sendMailToAdministrative(String basePath, AnnullamentoOrdineMissione annullamento) {
		DatiIstituto dati = datiIstitutoService.getDatiIstituto(annullamento.getOrdineMissione().getUoSpesa(), annullamento.getOrdineMissione().getAnno());
		String subjectMail = subjectSendToAdministrative + " "+ getNominativo(annullamento.getUid());
		String testoMail = getTextMailSendToAdministrative(basePath, annullamento);
		if (dati != null && dati.getMailNotifiche() != null){
			mailService.sendEmail(subjectMail, testoMail, false, true, dati.getMailNotifiche());
		} else {
			List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(annullamento.getOrdineMissione().getUoSpesa());
			sendMailToAdministrative(lista, testoMail, subjectMail);
		}
	}

	private void sendMailToAdministrative(List<UsersSpecial> lista, String testoMail, String oggetto) {
		if (lista != null && lista.size() > 0){
			String[] elencoMail = mailService.prepareTo(lista);
			if (elencoMail != null && elencoMail.length > 0){
				mailService.sendEmail(oggetto, testoMail, false, true, elencoMail);
			}
		}
	}

	private String getTextMailSendToAdministrative(String basePath, AnnullamentoOrdineMissione annullamento) {
		return "L'annullamento dell'ordine di missione "+annullamento.getOrdineMissione().getAnno()+"-"+annullamento.getOrdineMissione().getNumero()+ " della uo "+annullamento.getOrdineMissione().getUoRich()+" "+annullamento.getOrdineMissione().getDatoreLavoroRich()+ " di "+getNominativo(annullamento.getOrdineMissione().getUid())+" per la missione a "+annullamento.getOrdineMissione().getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(annullamento.getOrdineMissione().getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(annullamento.getOrdineMissione().getDataFineMissione())+ " avente per oggetto "+annullamento.getOrdineMissione().getOggetto()+"  è stato inviato per la verifica/completamento dei dati finanziari."
				+ "Si prega di verificarlo attraverso il link "+basePath+"/#/annullamentoOrdineMissione/"+annullamento.getOrdineMissione().getId()+"/S";
	}

	private void aggiornaDatiAnnullamentoOrdineMissione(Principal principal, AnnullamentoOrdineMissione annullamento, Boolean confirm,
			AnnullamentoOrdineMissione annullamentoDB) {
		annullamentoDB.setStato(annullamento.getStato());
		annullamentoDB.setStatoFlusso(annullamento.getStatoFlusso());
		annullamentoDB.setMotivoAnnullamento(annullamento.getMotivoAnnullamento());
		if (confirm){
			aggiornaValidazione(principal, annullamentoDB);
		}
	}

    private String getEmail(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getEmailComunicazioni();
    }

    private String getNominativo(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getCognome()+ " "+ utente.getNome();
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteAnnullamento(Principal principal, Long idAnnullamento) throws ComponentException{
    	AnnullamentoOrdineMissione annullamento = (AnnullamentoOrdineMissione)crudServiceBean.findById(principal, AnnullamentoOrdineMissione.class, idAnnullamento);
		if (annullamento != null){
			controlloOperazioniCRUDDaGui(annullamento);
			annullamento.setStato(Costanti.STATO_ANNULLATO);
			annullamento.setToBeUpdated();
//			if (annullamento.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(annullamento.getIdFlusso())){
//				cmisRimborsoMissioneService.annullaFlusso(annullamento);
//			}
			crudServiceBean.modificaConBulk(principal, annullamento);
		}
	}

	public void controlloOperazioniCRUDDaGui(AnnullamentoOrdineMissione annullamento) {
		if (!annullamento.isMissioneInserita()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile effettuare l'operazione su un Annullamento Ordine di Missione che non si trova in uno stato "+Costanti.STATO.get(Costanti.STATO_INSERITO));
		}
	}
	
	public String retrieveStateFromFlows(ResultFlows result) {
		return result.getState();
	}

	public ResultFlows retrieveDataFromFlows(AnnullamentoOrdineMissione annullamento)
			throws ComponentException {
		ResultFlows result = cmisOrdineMissioneService.getFlowsOrdineMissione(annullamento.getIdFlusso());
		return result;
	}

    @Transactional(readOnly = true)
    public AnnullamentoOrdineMissione getAnnullamentoMissione(Principal principal, Long idMissione) throws ComponentException {
		return getAnnullamentoOrdineMissione(principal, idMissione, false);
    }

    @Transactional(readOnly = true)
    public List<AnnullamentoOrdineMissione> getAnnullamenti(Principal principal, RimborsoMissioneFilter filter, Boolean isServiceRest) throws ComponentException {
		return getAnnullamenti(principal, filter, isServiceRest, false);
    }

    @Transactional(readOnly = true)
    public List<AnnullamentoOrdineMissione> getAnnullamenti(Principal principal, RimborsoMissioneFilter filter, Boolean isServiceRest, Boolean isForValidateFlows) throws ComponentException {
		CriterionList criterionList = new CriterionList();
		List<AnnullamentoOrdineMissione> annullamentiList=null;
		String aliasOrdineMissione = "ordine";
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
			
			if (filter.getaId() != null){
				criterionList.add(Restrictions.le("id", filter.getaId()));
			}
			if (filter.getDaNumero() != null){
				criterionList.add(Restrictions.ge("numero", filter.getDaNumero()));
			}
			if (filter.getaNumero() != null){
				criterionList.add(Restrictions.le("ordine.numero", filter.getaNumero()));
			}
			if (filter.getDaData() != null){
				criterionList.add(Restrictions.ge("dataInserimento", DateUtils.parseLocalDate(filter.getDaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getaData() != null){
				criterionList.add(Restrictions.le("dataInserimento", DateUtils.parseLocalDate(filter.getaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getCdsRich() != null){
				criterionList.add(Restrictions.eq("ordine.cdsRich", filter.getCdsRich()));
			}
			if (filter.getUoRich() != null){
				if (accountService.isUserEnableToWorkUo(principal, filter.getUoRich()) && !filter.isDaCron()){
					Disjunction condizioneOr = Restrictions.disjunction();
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("ordine.uoRich", filter.getUoRich())));
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("ordine.uoSpesa", filter.getUoRich())));
					criterionList.add(condizioneOr);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente "+principal.getName()+"  non è abilitato a vedere i dati della uo "+filter.getUoRich());
				}
			}
			if (filter.getAnnoOrdine() != null){
				criterionList.add(Restrictions.eq(aliasOrdineMissione+".anno", filter.getAnnoOrdine()));
			}
			if (filter.getDaNumeroOrdine() != null){
				criterionList.add(Restrictions.ge(aliasOrdineMissione+".numero", filter.getDaNumeroOrdine()));
			}
			if (filter.getaNumeroOrdine() != null){
				criterionList.add(Restrictions.le(aliasOrdineMissione+".numero", filter.getaNumeroOrdine()));
			}
			if (filter.getStatoInvioSigla() != null){
				criterionList.add(Restrictions.eq("statoInvioSigla", filter.getStatoInvioSigla()));
			}
		}
		if (filter != null && Utility.nvl(filter.getDaCron(), "N").equals("S")){
			Criteria criteria = crudServiceBean.preparaCriteria(principal, AnnullamentoOrdineMissione.class, criterionList, null, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
			criteria.createAlias("ordineMissione", aliasOrdineMissione);
			return  crudServiceBean.eseguiQuery(criteria);
		} else if (filter != null && Utility.nvl(filter.getToFinal(), "N").equals("S")){
			if (StringUtils.isEmpty(filter.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stata selezionata la uo per rendere definitivo l'annullamento dell'ordine di missione.");
			}
			UsersSpecial userSpecial = accountService.getUoForUsersSpecial(principal.getName());
			boolean uoAbilitata = false;
			if (userSpecial != null){
				if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
					if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
				    	for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()){
				    		if (uoService.getUoSigla(uoUser).equals(filter.getUoRich())){
				    			uoAbilitata = true;
				    			if (!Utility.nvl(uoUser.getRendi_definitivo(),"N").equals("S")){
									throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente non è abilitato a rendere definitivi rimborsi di missione.");
					    		}
				    		}
				    	} 
					}
				}
			}
			if (!uoAbilitata){
				throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente non è abilitato a rendere definitivi rimborsi di missione.");
			}
			criterionList.add(Restrictions.eq("statoFlusso", Costanti.STATO_APPROVATO_FLUSSO));
			criterionList.add(Restrictions.eq("stato", Costanti.STATO_CONFERMATO));
			criterionList.add(Restrictions.eq("validato", "S"));
			annullamentiList = crudServiceBean.findByProjection(principal, AnnullamentoOrdineMissione.class, AnnullamentoOrdineMissione.getProjectionForElencoMissioni(), criterionList, true, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
			return annullamentiList;
			
		} else {
			if (!isForValidateFlows){
				if (!StringUtils.isEmpty(filter.getUser())){
					criterionList.add(Restrictions.eq("uid", filter.getUser()));
				} else {
					if (StringUtils.isEmpty(filter.getUoRich())){
						criterionList.add(Restrictions.eq("uid", principal.getName()));
//					} else {
//						criterionList.add(Restrictions.eq("uoRich", filter.getUoRich()));
					}
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
					    			condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("ordine.uoRich", uoService.getUoSigla(uoUser))));
						    		if (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("S")){
						    			if (Utility.nvl(uoUser.getOrdine_da_validare(),"N").equals("S")){
							    			condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("ordine.uoSpesa", uoService.getUoSigla(uoUser))));
						    			}
						    		}
					    		}
					    	}
					    	criterionList.add(condizioneOr);
						} else {
							criterionList.add(Restrictions.eq("uid", principal.getName()));
						}
					}
				} else {
					criterionList.add(Restrictions.eq("uid", principal.getName()));
				}
			}
			if (!Utility.nvl(filter.getIncludiMissioniAnnullate()).equals("S") && (!(filter.getDaId() != null && filter.getaId() != null && filter.getDaId().compareTo(filter.getaId()) == 0))){
				criterionList.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO)));
			}

			if (isServiceRest) {
				if (isForValidateFlows){
					List<String> listaStatiFlusso = new ArrayList<String>();
					listaStatiFlusso.add(Costanti.STATO_INVIATO_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_NON_INVIATO_FLUSSO);
					criterionList.add(Restrictions.disjunction().add(Restrictions.disjunction().add(Restrictions.in("statoFlusso", listaStatiFlusso)).add(Restrictions.conjunction().add(Restrictions.eq("stato", Costanti.STATO_INSERITO)))));
				}
				

				Criteria criteria = crudServiceBean.preparaCriteria(principal, AnnullamentoOrdineMissione.class, criterionList, AnnullamentoOrdineMissione.getProjectionForElencoMissioni(), Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
				criteria.createAlias("ordineMissione", aliasOrdineMissione);
				annullamentiList = crudServiceBean.eseguiQuery(criteria);
			} else{
				Criteria criteria = crudServiceBean.preparaCriteria(principal, AnnullamentoOrdineMissione.class, criterionList, null, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
				criteria.createAlias("ordineMissione", aliasOrdineMissione);
				annullamentiList = crudServiceBean.eseguiQuery(criteria);
			}
			return annullamentiList;
		}
    }

    
    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoOrdineMissione createAnnullamentoOrdineMissione(Principal principal, AnnullamentoOrdineMissione annullamento)  throws ComponentException{
    	controlloDatiObbligatoriDaGUI(annullamento);
    	inizializzaCampiPerInserimento(principal, annullamento);
		validaCRUD(principal, annullamento);
		annullamento = (AnnullamentoOrdineMissione)crudServiceBean.creaConBulk(principal, annullamento);
    	log.info("Creato Annullamento Ordine Missione", annullamento.getId());
    	return annullamento;
    }

    private void inizializzaCampiPerInserimento(Principal principal,
    		AnnullamentoOrdineMissione annullamento) throws ComponentException{
    	annullamento.setUidInsert(principal.getName());
    	annullamento.setUser(principal.getName());
    	Integer anno = recuperoAnno(annullamento);
    	annullamento.setAnno(anno);
    	annullamento.setNumero(datiIstitutoService.getNextPG(principal, annullamento.getOrdineMissione().getUoRich(), anno , Costanti.TIPO_ANNULLAMENTO_ORDINE_MISSIONE));

    	aggiornaValidazione(principal, annullamento);
    	
    	annullamento.setStato(Costanti.STATO_INSERITO);
    	annullamento.setStatoFlusso(Costanti.STATO_INSERITO);
    	if (annullamento.getOrdineMissione() != null){
        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
        	if (ordineMissione != null){
        		annullamento.setOrdineMissione(ordineMissione);
        	} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "L'ordine di missione con ID: "+annullamento.getOrdineMissione().getId()+" non esiste");
        	}
    	}
    	annullamento.setToBeCreated();
    }

    private Integer recuperoAnno(AnnullamentoOrdineMissione annullamento) {
		if (annullamento.getDataInserimento() == null){
			annullamento.setDataInserimento(LocalDate.now());
		}
		return annullamento.getDataInserimento().getYear();
	}

    private void controlloDatiObbligatoriDaGUI(AnnullamentoOrdineMissione annullamento){
    	if (annullamento != null){
    		if (StringUtils.isEmpty(annullamento.getMotivoAnnullamento())){
    			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Motivo di Annullamento");
    		}
    		if (annullamento.isMissioneDipendente()){
    			if (StringUtils.isEmpty(annullamento.getComuneResidenzaRich())){
    				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Comune di Residenza del Richiedente");
    			} else if (StringUtils.isEmpty(annullamento.getIndirizzoResidenzaRich())){
    				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Indirizzo di Residenza del Richiedente");
    			}
    		}
    	}
    }

    private void aggiornaValidazione(Principal principal, AnnullamentoOrdineMissione annullamento) {
		if (accountService.isUserSpecialEnableToValidateOrder(principal.getName(), annullamento.getOrdineMissione().getUoSpesa())){
			annullamento.setValidato("S");
		} else {
			annullamento.setValidato("N");
		}
    }

	private void validaCRUD(Principal principal, AnnullamentoOrdineMissione annullamento) {
		if (annullamento != null){
			controlloCampiObbligatori(annullamento); 
			controlloCongruenzaDatiInseriti(principal, annullamento);
		}
	}

    private void controlloCongruenzaDatiInseriti(Principal principal, AnnullamentoOrdineMissione annullamento) {
		if (StringUtils.isEmpty(annullamento.getIdFlusso()) &&  annullamento.isStatoInviatoAlFlusso()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile avere lo stato Inviato al flusso e non avere l'ID del flusso");
		} 
	}
	
	private void controlloCampiObbligatori(AnnullamentoOrdineMissione annullamento) {
		if (!annullamento.isToBeCreated()){
			controlloDatiObbligatoriDaGUI(annullamento);
		}
		if (StringUtils.isEmpty(annullamento.getAnno())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anno");
		} else if (StringUtils.isEmpty(annullamento.getUid())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utente");
		} else if (StringUtils.isEmpty(annullamento.getStato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Stato");
		} else if (StringUtils.isEmpty(annullamento.getValidato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Validato");
		} else if (StringUtils.isEmpty(annullamento.getNumero())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Numero");
		}
	}

	@Transactional(readOnly = true)
   	public Map<String, byte[]> printAnnullamentoMissione(Authentication auth, Long idMissione) throws ComponentException {
    	Principal principal = (Principal)auth;
    	AnnullamentoOrdineMissione annullamento = getAnnullamentoOrdineMissione(principal, idMissione, true);
    	byte[] printAnnullamentoMissione = null;
    	String fileName = null;
    	if (!annullamento.isStatoNonInviatoAlFlusso()){
    		ContentStream content = null;
			try {
				content = cmisOrdineMissioneService.getContentStreamAnnullamentoOrdineMissione(annullamento);
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
    					printAnnullamentoMissione = IOUtils.toByteArray(is);
    					is.close();
    				} catch (IOException e) {
    					throw new ComponentException("Errore nella conversione dello stream in byte del file (" + Utility.getMessageException(e) + ")",e);
					}
        		}
    		} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel recupero del contenuto del file sul documentale");
    		}
    		Map<String, byte[]> map = new HashMap<String, byte[]>();
    		map.put(fileName, printAnnullamentoMissione);
    		return map;
    	} else {
    		return stampaAnnullamento(principal, annullamento);
    	}
    }

	public Map<String, byte[]> stampaAnnullamento(Principal principal, AnnullamentoOrdineMissione annullamento)
			throws ComponentException {
		byte[] printAnnullamento;
		String fileName;
		fileName = "Annullamento"+annullamento.getId()+".pdf";
		printAnnullamento = printAnnullamentoMissioneService.printOrdineMissione(annullamento, principal.getName());
		if (annullamento.isMissioneInserita()){
			cmisOrdineMissioneService.salvaStampaAnnullamentoOrdineMissioneSuCMIS(principal, printAnnullamento, annullamento);
		}
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		map.put(fileName, printAnnullamento);
		return map;
	}

}

