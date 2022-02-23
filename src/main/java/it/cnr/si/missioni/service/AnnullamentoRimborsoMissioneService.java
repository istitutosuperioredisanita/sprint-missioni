package it.cnr.si.missioni.service;

import java.io.InputStream;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.criterion.Subqueries;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.DatiFlusso;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoRimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.AnnullamentoRimborsoMissioneRepository;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.CommonService;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import net.bzdyl.ejb3.criteria.Criteria;
import net.bzdyl.ejb3.criteria.Order;
import net.bzdyl.ejb3.criteria.restrictions.Disjunction;
import net.bzdyl.ejb3.criteria.restrictions.Restrictions;


/**
 * Service class for managing users.
 */
@Service
public class AnnullamentoRimborsoMissioneService {

    private final Logger log = LoggerFactory.getLogger(AnnullamentoRimborsoMissioneService.class);

	@Autowired
	private CRUDComponentSession crudServiceBean;

    @Autowired
    private Environment env;

	@Autowired
	private AccountService accountService;

	@Autowired
	private RimborsoMissioneService rimborsoMissioneService;

	@Autowired
	private AnnullamentoRimborsoMissioneRepository annullamentoRimborsoMissioneRepository;

//	@Autowired
//	private PrintAnnullamentoOrdineMissioneService printAnnullamentoMissioneService;
//
	@Autowired
	CronService cronService;
	
	@Autowired
	CMISRimborsoMissioneService cmisRimborsoMissioneService; 
	
	@Autowired
	private MissioniCMISService missioniCMISService;

    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired
    private DatiSedeService datiSedeService;

	@Autowired
	private MailService mailService;

	@Autowired
    private CommonService commonService;
	
	@Autowired
	private UoService uoService;
    
    @Value("${spring.mail.messages.invioAnnullamentoRimborsoMissione.oggetto}")
    private String subjectSendToAdministrative;
    
    @Value("${spring.mail.messages.annullamentoRimborsoMittente.oggetto}")
    private String subjectUndo;

	@Autowired
	private SecurityService securityService;

	@Transactional(readOnly = true)
    public AnnullamentoRimborsoMissione getAnnullamentoRimborsoMissione(Long idAnnullamento) throws ComponentException {
    	RimborsoMissioneFilter filter = new RimborsoMissioneFilter();
    	filter.setDaId(idAnnullamento);
    	filter.setaId(idAnnullamento);
    	AnnullamentoRimborsoMissione annullamento = null;
		List<AnnullamentoRimborsoMissione> listaAnnullamentiMissione = getAnnullamenti(filter, false, true);
		if (listaAnnullamentiMissione != null && !listaAnnullamentiMissione.isEmpty()){
			annullamento = listaAnnullamentiMissione.get(0);
			RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById( RimborsoMissione.class, annullamento.getRimborsoMissione().getId());
			if (rimborsoMissione != null){
				Uo datiUo = uoService.recuperoUoSigla(annullamento.getRimborsoMissione().getUoSpesa());
				if (datiUo != null && Utility.nvl(datiUo.getOrdineDaValidare(),"N").equals("N")){
					annullamento.setIsUoDaValidare("N");
				} else {
					annullamento.setIsUoDaValidare("S");
				}
			}
		}
		return annullamento;
    }

    public List<AnnullamentoRimborsoMissione> getAnnullamentiForValidateFlows(RimborsoMissioneFilter filter,  Boolean isServiceRest) throws ComponentException{
    	filter.setStato(Costanti.STATO_INSERITO);
    	List<AnnullamentoRimborsoMissione> lista = getAnnullamenti(filter, isServiceRest, true);
    	if (lista != null){
    		List<AnnullamentoRimborsoMissione> listaNew = new ArrayList<AnnullamentoRimborsoMissione>();
    		for (AnnullamentoRimborsoMissione annullamento : lista){
    			if (annullamento.isMissioneInserita()){
    				listaNew.add(annullamento);
    			}
    		}
    		return listaNew;
    	}
    	return lista;
    }

//	public void popolaCoda(AnnullamentoOrdineMissione annullamento) {
//		if (annullamento.getMatricola() != null){
//			Account account = accountService.loadAccountFromRest(annullamento.getUid());
//			String idSede = null;
//			if (account != null){
//				idSede = account.getCodice_sede();
//			}
//			Missione missione = new Missione(TypeMissione.ANNULLAMENTO, new Long(annullamento.getId().toString()), idSede, 
//					annullamento.getOrdineMissione().getMatricola(), annullamento.getOrdineMissione().getDataInizioMissione(), 
//					annullamento.getOrdineMissione().getDataFineMissione(), new Long(annullamento.getOrdineMissione().getId().toString()), annullamento.getOrdineMissione().isMissioneEstera() ? TypeTipoMissione.ESTERA : TypeTipoMissione.ITALIA);
//			rabbitMQService.send(missione);
//		}
//	}
//
	@Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoRimborsoMissione updateAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, String basePath)  throws ComponentException{
    	return updateAnnullamentoRimborsoMissione(annullamento, false, basePath);
    }
    
    private AnnullamentoRimborsoMissione updateAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, Boolean fromFlows, String basePath)  throws ComponentException{
    	return updateAnnullamentoRimborsoMissione(annullamento, fromFlows, false, basePath);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoRimborsoMissione updateAnnullamentoRimborsoMissione (AnnullamentoRimborsoMissione annullamento, Boolean fromFlows, Boolean confirm, String basePath)  throws ComponentException{

    	AnnullamentoRimborsoMissione annullamentoDB = (AnnullamentoRimborsoMissione)crudServiceBean.findById( AnnullamentoRimborsoMissione.class, annullamento.getId());
       	boolean isRitornoMissioneMittente = false;

		if (annullamentoDB==null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Annullamento Rimborso Missione da aggiornare inesistente.");
		}
		
    	if (annullamentoDB.getRimborsoMissione() != null){
    		RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById( RimborsoMissione.class, annullamentoDB.getRimborsoMissione().getId());
        	if (rimborsoMissione != null){
        		annullamento.setRimborsoMissione(rimborsoMissione);
        	}
    	}
		if (confirm){
			if (annullamentoDB.isMissioneConfermata()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione già annullato.");
			}
			List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsAnnullamentoRimborsoMissione(annullamento.getRimborsoMissione(), new Long(annullamento.getId().toString()));
			if (lista == null || lista.isEmpty()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario allegare almeno un documento prima di procedere all'annullamento del rimborso.");
			}
			annullamento.setStato(Costanti.STATO_CONFERMATO);
		} else {
			if (!accountService.isUserEnableToWorkUo(annullamentoDB.getRimborsoMissione().getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato ad inserire gli annullamenti rimborso missione per la uo "+annullamentoDB.getRimborsoMissione().getUoSpesa()+".");
			}
		}
		aggiornaDatiAnnullamentoRimborsoMissione(annullamento, confirm, annullamentoDB);
		annullamentoDB.setToBeUpdated();
    	annullamentoDB = (AnnullamentoRimborsoMissione)crudServiceBean.modificaConBulk( annullamentoDB);

    	if (confirm){
    		RimborsoMissione rimborsoMissione = annullamento.getRimborsoMissione();
    		rimborsoMissione.setStato(Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE);
    		rimborsoMissione.setStatoFlusso(Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE);
    		rimborsoMissione.setToBeUpdated();
    		rimborsoMissione = (RimborsoMissione)crudServiceBean.modificaConBulk( rimborsoMissione);
    		if (rimborsoMissione.getPgMissioneSigla() != null){
        		JSONBody body = new JSONBody();

    			String app = Costanti.APP_SIGLA;
    			String url = Costanti.REST_COMUNICA_RIMBORSO_SIGLA+"/"+rimborsoMissione.getId();
    			commonService.process(body, app, url, true, HttpMethod.DELETE);
    		}
    		
    		sendMail(annullamentoDB);

    	}
    	
    	log.debug("Updated Information for Annullamento Rimborso Missione: {}", annullamentoDB);

    	return annullamentoDB;
    }

	private String getTextMail(AnnullamentoRimborsoMissione annullamento) {
		return "Il rimborso missione approvato "+annullamento.getRimborsoMissione().getAnno()+"-"+annullamento.getRimborsoMissione().getNumero()+ " di "+getNominativo(annullamento.getRimborsoMissione().getUid())+" per la missione a "+annullamento.getRimborsoMissione().getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(annullamento.getRimborsoMissione().getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(annullamento.getRimborsoMissione().getDataFineMissione())+ " avente per oggetto "+annullamento.getRimborsoMissione().getOggetto()+" è stato annullato da "+getNominativo(securityService.getCurrentUserLogin());
	}

    private void sendMail(AnnullamentoRimborsoMissione annullamento) {
		DatiIstituto dati = datiIstitutoService.getDatiIstituto(annullamento.getRimborsoMissione().getUoSpesa(), annullamento.getRimborsoMissione().getAnno());
		String subjectMail = subjectUndo + " "+ getNominativo(annullamento.getUid());
		String testoMail = getTextMail(annullamento);

		Account account = accountService.loadAccountFromRest(annullamento.getRimborsoMissione().getUid());
		LocalDate data = LocalDate.now();
		int anno = data.getYear();

		List<String> listaMail = new ArrayList<>();
		listaMail.add(account.getEmail_comunicazioni());
		if (dati != null && dati.getMailNotificheRimborso() != null && !dati.getMailNotificheRimborso().equals("N")){
			listaMail.add(dati.getMailNotificheRimborso());
		} else {
			List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(annullamento.getRimborsoMissione().getUoSpesa());
			listaMail.addAll(mailService.preparaListaMail(lista));
		}
		sendMail(listaMail, testoMail, subjectMail);
    }

	private void sendMail(List<String> lista, String testoMail, String oggetto) {
		if (lista != null && lista.size() > 0){
			String[] elencoMail = mailService.preparaElencoMail(lista);
			if (elencoMail != null && elencoMail.length > 0){
				mailService.sendEmail(oggetto, testoMail, false, true, elencoMail);
			}
		}
	}


	private void aggiornaDatiAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento, Boolean confirm,
			AnnullamentoRimborsoMissione annullamentoDB) {
		annullamentoDB.setStato(annullamento.getStato());
		annullamentoDB.setMotivoAnnullamento(annullamento.getMotivoAnnullamento());
	}

    private String getNominativo(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getCognome()+ " "+ utente.getNome();
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteAnnullamento(Long idAnnullamento) throws ComponentException{
    	AnnullamentoRimborsoMissione annullamento = (AnnullamentoRimborsoMissione)crudServiceBean.findById( AnnullamentoRimborsoMissione.class, idAnnullamento);
		if (annullamento != null){
			controlloOperazioniCRUDDaGui(annullamento);
			annullamento.setStato(Costanti.STATO_ANNULLATO);
			annullamento.setToBeUpdated();
//			if (annullamento.isStatoInviatoAlFlusso() && !StringUtils.isEmpty(annullamento.getIdFlusso())){
//				cmisRimborsoMissioneService.annullaFlusso(annullamento);
//			}
			crudServiceBean.modificaConBulk( annullamento);
		}
	}

	public void controlloOperazioniCRUDDaGui(AnnullamentoRimborsoMissione annullamento) {
		if (!annullamento.isMissioneInserita()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile effettuare l'operazione su un Annullamento Rimborso issione che non si trova in uno stato "+Costanti.STATO.get(Costanti.STATO_INSERITO));
		}
	}


    @Transactional(readOnly = true)
    public AnnullamentoRimborsoMissione getAnnullamentoMissione(Long idMissione) throws ComponentException {
		return getAnnullamentoRimborsoMissione(idMissione);
    }

    @Transactional(readOnly = true)
    public List<AnnullamentoRimborsoMissione> getAnnullamenti(RimborsoMissioneFilter filter, Boolean isServiceRest) throws ComponentException {
		return getAnnullamenti(filter, isServiceRest, false);
    }

    @Transactional(readOnly = true)
    public List<AnnullamentoRimborsoMissione> getAnnullamenti(RimborsoMissioneFilter filter, Boolean isServiceRest, Boolean isForValidateFlows) throws ComponentException {
		CriterionList criterionList = new CriterionList();
		List<AnnullamentoRimborsoMissione> annullamentiList=null;
		String aliasRimborsoMissione = "rimborso";
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
			if (filter.getListaStatiMissione() != null && !filter.getListaStatiMissione().isEmpty()){
				criterionList.add(Restrictions.in("stato", filter.getListaStatiMissione()));
			}
			
			if (filter.getaId() != null){
				criterionList.add(Restrictions.le("id", filter.getaId()));
			}
			if (filter.getDaNumero() != null){
				criterionList.add(Restrictions.ge("numero", filter.getDaNumero()));
			}
			if (filter.getDaData() != null){
				criterionList.add(Restrictions.ge("dataInserimento", DateUtils.parseLocalDate(filter.getDaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getaData() != null){
				criterionList.add(Restrictions.le("dataInserimento", DateUtils.parseLocalDate(filter.getaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getUoRich() != null){
				if (accountService.isUserEnableToWorkUo(filter.getUoRich()) && !filter.isDaCron()){
					criterionList.add(Subqueries.exists("select rim.id from RimborsoMissione AS rim where rim.id = this.rimborsoMissione.id and (rim.uoRich = '"+filter.getUoRich()+"' or rim.uoSpesa = '"+filter.getUoRich()+"') "));
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente "+securityService.getCurrentUserLogin()+"  non è abilitato a vedere i dati della uo "+filter.getUoRich());
				}
			}
			if (filter.getAnnoOrdine() != null){
				criterionList.add(Restrictions.eq(aliasRimborsoMissione+".anno", filter.getAnnoOrdine()));
			}
			if (filter.getDaNumeroOrdine() != null){
				criterionList.add(Restrictions.ge(aliasRimborsoMissione+".numero", filter.getDaNumeroOrdine()));
			}
			if (filter.getaNumeroOrdine() != null){
				criterionList.add(Restrictions.le(aliasRimborsoMissione+".numero", filter.getaNumeroOrdine()));
			}
			if (filter.getStatoInvioSigla() != null){
				criterionList.add(Restrictions.eq("statoInvioSigla", filter.getStatoInvioSigla()));
			}
		}
		if (filter != null && Utility.nvl(filter.getDaCron(), "N").equals("S")){
			Criteria criteria = crudServiceBean.preparaCriteria( AnnullamentoRimborsoMissione.class, criterionList, null, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
			return  crudServiceBean.eseguiQuery(criteria);
		} else {
			if (!isForValidateFlows){
				if (!StringUtils.isEmpty(filter.getUser())){
					criterionList.add(Restrictions.eq("uid", filter.getUser()));
				} else {
					if (StringUtils.isEmpty(filter.getUoRich())){
						criterionList.add(Restrictions.eq("uid", securityService.getCurrentUserLogin()));
					}
				}
			} else {
				UsersSpecial userSpecial = accountService.getUoForUsersSpecial(securityService.getCurrentUserLogin());
				if (userSpecial != null){
					if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
						if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
							boolean esisteUoConValidazioneConUserNonAbilitato = false;
							Disjunction condizioneOr = Restrictions.disjunction();
							List<String> listaUoUtente = new ArrayList<String>();
							boolean primoGiro = true;
							String subQuery = "";		
					    	for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()){
					    		Uo uo = uoService.recuperoUo(uoUser.getCodice_uo());
					    		if (uo != null){
					    			String uoFilter = uoService.getUoSigla(uoUser);
					    			if (primoGiro){
					    				subQuery = "select rim.id from RimborsoMissione AS rim where rim.id = this.rimborsoMissione.id and (rim.uoRich = '"+uoFilter+"' ";
					    				primoGiro = false;
					    			} else {
					    				subQuery += " or rim.uoRich = '"+uoFilter+"' ";
					    			}
						    		if (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("S")){
						    			if (Utility.nvl(uoUser.getOrdine_da_validare(),"N").equals("S")){
						    				subQuery += " or rim.uoSpesa = '"+uoFilter+"' ";
						    			}
						    		}
					    		}
					    	}
							if (!subQuery.isEmpty()){
			    				subQuery += " )";
								criterionList.add(Subqueries.exists(subQuery));
							}
						} else {
							criterionList.add(Restrictions.eq("uid", securityService.getCurrentUserLogin()));
						}
					}
				} else {
					criterionList.add(Restrictions.eq("uid", securityService.getCurrentUserLogin()));
				}
			}
			if (!Utility.nvl(filter.getIncludiMissioniAnnullate()).equals("S") && (!(filter.getDaId() != null && filter.getaId() != null && filter.getDaId().compareTo(filter.getaId()) == 0))){
				criterionList.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO)));
			}

			if (isServiceRest) {
				if (isForValidateFlows){
					criterionList.add(Restrictions.eq("stato", Costanti.STATO_INSERITO));
				}
				

				Criteria criteria = crudServiceBean.preparaCriteria( AnnullamentoRimborsoMissione.class, criterionList, AnnullamentoRimborsoMissione.getProjectionForElencoMissioni(), Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
				annullamentiList = crudServiceBean.eseguiQuery(criteria);
			} else{
				Criteria criteria = crudServiceBean.preparaCriteria( AnnullamentoRimborsoMissione.class, criterionList, null, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
				annullamentiList = crudServiceBean.eseguiQuery(criteria);
			}
			return annullamentiList;
		}
    }

    
    @Transactional(propagation = Propagation.REQUIRED)
    public AnnullamentoRimborsoMissione createAnnullamentoRimborsoMissione(AnnullamentoRimborsoMissione annullamento)  throws ComponentException{
    	controlloDatiObbligatoriDaGUI(annullamento);
    	inizializzaCampiPerInserimento(annullamento);
		validaCRUD(annullamento);
		annullamento = (AnnullamentoRimborsoMissione)crudServiceBean.creaConBulk(annullamento);
    	log.info("Creato Annullamento Rimborso Missione", annullamento.getId());
    	return annullamento;
    }

    private void inizializzaCampiPerInserimento(
    		AnnullamentoRimborsoMissione annullamento) throws ComponentException{
    	annullamento.setUidInsert(securityService.getCurrentUserLogin());
    	annullamento.setUser(securityService.getCurrentUserLogin());
    	Integer anno = recuperoAnno(annullamento);
    	annullamento.setAnno(anno);
    	annullamento.setNumero(datiIstitutoService.getNextPG(annullamento.getRimborsoMissione().getUoSpesa(), anno , Costanti.TIPO_ANNULLAMENTO_RIMBORSO_MISSIONE));

    	annullamento.setStato(Costanti.STATO_INSERITO);
    	if (annullamento.getRimborsoMissione() != null){
    		RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById( RimborsoMissione.class, annullamento.getRimborsoMissione().getId());
        	if (rimborsoMissione != null){
        		AnnullamentoRimborsoMissione ann = annullamentoRimborsoMissioneRepository.getAnnullamentoRimborsoMissione(rimborsoMissione);
        		if (ann != null){
    				throw new AwesomeException(CodiciErrore.ERRGEN, "Esiste già un annullamento per il rimborso missione "+annullamento.getRimborsoMissione().getAnno()+"-"+annullamento.getRimborsoMissione().getNumero());
        		}
        		annullamento.setRimborsoMissione(rimborsoMissione);
        	} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Il rimborso missione con ID: "+annullamento.getRimborsoMissione().getId()+" non esiste");
        	}
    	}
    	annullamento.setToBeCreated();
    }

    private Integer recuperoAnno(AnnullamentoRimborsoMissione annullamento) {
		if (annullamento.getDataInserimento() == null){
			annullamento.setDataInserimento(LocalDate.now());
		}
		return annullamento.getDataInserimento().getYear();
	}

    private void controlloDatiObbligatoriDaGUI(AnnullamentoRimborsoMissione annullamento){
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

	private void validaCRUD(AnnullamentoRimborsoMissione annullamento) {
		if (annullamento != null){
			controlloCampiObbligatori(annullamento); 
		}
	}

	
	private void controlloCampiObbligatori(AnnullamentoRimborsoMissione annullamento) {
		if (!annullamento.isToBeCreated()){
			controlloDatiObbligatoriDaGUI(annullamento);
		}
		if (StringUtils.isEmpty(annullamento.getAnno())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anno");
		} else if (StringUtils.isEmpty(annullamento.getUid())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utente");
		} else if (StringUtils.isEmpty(annullamento.getStato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Stato");
		} else if (StringUtils.isEmpty(annullamento.getNumero())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Numero");
		}
	}

	public List<CMISFileAttachment> getAttachments(Long idAnnullamentoRimborsoMissione)
			throws ComponentException {
		if (idAnnullamentoRimborsoMissione != null) {
			AnnullamentoRimborsoMissione annullamento = (AnnullamentoRimborsoMissione) crudServiceBean.findById( AnnullamentoRimborsoMissione.class, idAnnullamentoRimborsoMissione);
			if (annullamento != null){
				RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById( RimborsoMissione.class, annullamento.getRimborsoMissione().getId());
				if (rimborsoMissione != null){
					List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsAnnullamentoRimborsoMissione(rimborsoMissione, idAnnullamentoRimborsoMissione);
					return lista;
				}
			}
		}
		return null;
	}

	public List<CMISFileAttachment> getAttachmentsFromRimborso(Long idRimborsoMissione)
			throws ComponentException {
		RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById( RimborsoMissione.class, idRimborsoMissione);
		if (rimborsoMissione != null) {
			AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneRepository.getAnnullamentoRimborsoMissione(rimborsoMissione);
			return getAttachments(new Long(annullamento.getId().toString()));
		}
		return null;
	}

	public CMISFileAttachment uploadAllegato(Long idAnnullamentoRimborsoMissione,
			InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
		if (idAnnullamentoRimborsoMissione != null) {
			AnnullamentoRimborsoMissione annullamento = (AnnullamentoRimborsoMissione) crudServiceBean.findById( AnnullamentoRimborsoMissione.class, idAnnullamentoRimborsoMissione);
			if (annullamento != null){
				RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById( RimborsoMissione.class, annullamento.getRimborsoMissione().getId());
				if (rimborsoMissione != null) {
					return cmisRimborsoMissioneService.uploadAttachmentAnnullamentoRimborsoMissione(rimborsoMissione,idAnnullamentoRimborsoMissione,
							inputStream, name, mimeTypes);
				}
			}
		}
		return null;
	}
	
	public void gestioneCancellazioneAllegati(String idNodo, Long idAnnullamentoRimborsoMissione){
		if (idAnnullamentoRimborsoMissione != null) {
			AnnullamentoRimborsoMissione annullamento = (AnnullamentoRimborsoMissione) crudServiceBean.findById( AnnullamentoRimborsoMissione.class, idAnnullamentoRimborsoMissione);
			if (annullamento != null){
				if (annullamento.isMissioneConfermata()){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione già annullato.");
				}
				missioniCMISService.deleteNode(idNodo);
			}
		}
	}
}

