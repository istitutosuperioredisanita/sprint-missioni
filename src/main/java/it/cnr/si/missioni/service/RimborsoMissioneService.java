package it.cnr.si.missioni.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletResponse;

import it.cnr.jada.DetailedRuntimeException;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.UserContext;
import it.cnr.si.missioni.util.proxy.json.service.*;
import it.cnr.si.spring.storage.StorageDriver;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.cnr.jada.GenericPrincipal;
import it.cnr.jada.criterion.CriterionList;
import it.cnr.jada.criterion.Subqueries;
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.amq.domain.Missione;
import it.cnr.si.missioni.amq.domain.TypeMissione;
import it.cnr.si.missioni.amq.domain.TypeTipoMissione;
import it.cnr.si.missioni.amq.service.RabbitMQService;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Nazione;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompenso;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompensoJson;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.object.rimborso.MissioneBulk;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import it.cnr.si.spring.storage.StorageObject;
import net.bzdyl.ejb3.criteria.Order;
import net.bzdyl.ejb3.criteria.restrictions.Disjunction;
import net.bzdyl.ejb3.criteria.restrictions.Restrictions;


/**
 * Service class for managing users.
 */
@Service
public class RimborsoMissioneService {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneService.class);

	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired
	private UoService uoService;

    @Autowired
    private ParametriService parametriService;

    @Autowired
    private Environment env;

	@Autowired
	private AccountService accountService;

	@Autowired
	private OrdineMissioneService ordineMissioneService;

	@Autowired
	private RimborsoMissioneDettagliService rimborsoMissioneDettagliService;

	@Autowired
	private RimborsoImpegniService rimborsoImpegniService;

	@Autowired
	private PrintRimborsoMissioneService printRimborsoMissioneService;

	@Autowired
	UnitaOrganizzativaService unitaOrganizzativaService;
	
	@Autowired
	CdrService cdrService;
	
	@Autowired
	TerzoService terzoService;
	
	@Autowired
	CronService cronService;
	
	@Autowired
	ImpegnoGaeService impegnoGaeService;
	
	@Autowired
	ImpegnoService impegnoService;
	
	@Autowired
	NazioneService nazioneService;
	
	@Autowired
	TerzoPerCompensoService terzoPerCompensoService;
	
	@Autowired
	GaeService gaeService;
	
	@Autowired
	CMISRimborsoMissioneService cmisRimborsoMissioneService; 
	
	@Autowired
	ProgettoService progettoService;
	
    @Autowired
    private DatiIstitutoService datiIstitutoService;

    @Autowired
    private DatiSedeService datiSedeService;

    @Autowired
    private RabbitMQService rabbitMQService;

	@Autowired
	private MailService mailService;

	@Autowired
	private MissioneRespintaService missioneRespintaService;

	@Value("${spring.mail.messages.ritornoMissioneMittente.oggetto}")
    private String subjectReturnToSenderOrdine;
    
    @Value("${spring.mail.messages.invioRimborsoPerValidazioneDatiFinanziari.oggetto}")
    private String subjectSendToAdministrative;
    
    @Value("${spring.mail.messages.importoMissionePerResponsabileGruppo.oggetto}")
    private String oggettoImportoMissioneManager;
    
    @Value("${spring.mail.messages.approvazioneRimborsoMissione.oggetto}")
    private String approvazioneRimborsoMissione;

	@Value("${spring.mail.messages.erroreLetturaFlussoRimborso.oggetto}")
	private String subjectErrorFlowsRimborso;

	@Value("${spring.mail.messages.erroreLetturaFlussoRimborso.testo}")
	private String textErrorFlowsRimborso;

	@Autowired
    private MissioniCMISService missioniCMISService;

    public RimborsoMissione getRimborsoMissione(Principal principal, Long idMissione, Boolean retrieveDetail, Boolean retrieveDataFromFlows) throws ComponentException {
    	RimborsoMissioneFilter filter = new RimborsoMissioneFilter();
    	filter.setDaId(idMissione);
    	filter.setaId(idMissione);
    	RimborsoMissione rimborsoMissione = null;
		List<RimborsoMissione> listaRimborsiMissione = getRimborsiMissione(principal, filter, false, true);
		if (listaRimborsiMissione != null && !listaRimborsiMissione.isEmpty()){
			rimborsoMissione = listaRimborsiMissione.get(0);
			if (retrieveDetail){
				retrieveDetails(principal, rimborsoMissione);
			}
		}
		return rimborsoMissione;
    }

	@Transactional(propagation = Propagation.REQUIRED)
	public void retrieveDetails(Principal principal, RimborsoMissione rimborsoMissione) throws NumberFormatException, ComponentException {
		List<RimborsoMissioneDettagli> list = rimborsoMissioneDettagliService.getRimborsoMissioneDettagli(principal, new Long(rimborsoMissione.getId().toString()));
		rimborsoMissione.setRimborsoMissioneDettagli(list);
	}

	private boolean isDevProfile(){
		return env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT);
	}

    public List<RimborsoMissione> getRimborsiMissioneForValidateFlows(Principal principal, RimborsoMissioneFilter filter,  Boolean isServiceRest) throws ComponentException{
    	List<RimborsoMissione> lista = getRimborsiMissione(principal, filter, isServiceRest, true);
    	if (lista != null){
    		List<RimborsoMissione> listaNew = new ArrayList<RimborsoMissione>();
    		for (RimborsoMissione rimborsoMissione : lista){
    			if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()){
    				rimborsoMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_AUTORIZZARE_PER_HOME);
    				listaNew.add(rimborsoMissione);
    			} else {
    				if (rimborsoMissione.isMissioneDaValidare() && rimborsoMissione.isMissioneConfermata()){
    					rimborsoMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_VALIDARE_PER_HOME);
    					listaNew.add(rimborsoMissione);
    				} else if (rimborsoMissione.isMissioneInserita()){
    					rimborsoMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_CONFERMARE_PER_HOME);
    					listaNew.add(rimborsoMissione);
    				} else if (rimborsoMissione.isMissioneConfermata() && !rimborsoMissione.isMissioneDaValidare() && rimborsoMissione.isAllaValidazioneAmministrativa()){
    					rimborsoMissione.setStatoFlussoRitornoHome(Costanti.STATO_ALLA_VALIDAZIONE_AMM_PER_HOME);
    					listaNew.add(rimborsoMissione);
    				}
    			}
    		}
    		return listaNew;
    	}
    	return lista;
    }

    private void aggiornaRimborsoMissioneRespinto(Principal principal, FlowResult result,
			RimborsoMissione rimborsoMissioneDaAggiornare) throws ComponentException{
		aggiornaValidazione(principal, rimborsoMissioneDaAggiornare);
		rimborsoMissioneDaAggiornare.setCommentoFlusso(result.getCommento() == null ? null : (result.getCommento().length() > 1000 ? result.getCommento().substring(0, 1000) : result.getCommento()));
		rimborsoMissioneDaAggiornare.setStatoFlusso(FlowResult.STATO_FLUSSO_SCRIVANIA_MISSIONI.get(result.getStato()));
		rimborsoMissioneDaAggiornare.setStateFlows(result.getStato());
		rimborsoMissioneDaAggiornare.setStato(Costanti.STATO_INSERITO);
		rimborsoMissioneDaAggiornare.setValidato("N");
		updateRimborsoMissione(principal, rimborsoMissioneDaAggiornare, true, null);
		missioneRespintaService.inserisciMissioneRespinta(principal, result);
	}

	public void aggiornaRimborsoMissioneAnnullato(Principal principal, RimborsoMissione rimborsoMissioneDaAggiornare)
			throws ComponentException {
		rimborsoMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_ANNULLATO);
		rimborsoMissioneDaAggiornare.setStato(Costanti.STATO_ANNULLATO);
		updateRimborsoMissione(principal, rimborsoMissioneDaAggiornare, true, null);
	}

	public RimborsoMissione aggiornaRimborsoMissioneFirmato(Principal principal, RimborsoMissione rimborsoMissioneDaAggiornare)
			throws ComponentException {
		retrieveDetails(principal, rimborsoMissioneDaAggiornare);
		if (!rimborsoMissioneDaAggiornare.isTrattamentoAlternativoMissione()){
			if (rimborsoMissioneDaAggiornare.getTotaleRimborsoSenzaSpeseAnticipate().compareTo(BigDecimal.ZERO) == 0){
				rimborsoMissioneDaAggiornare.setStatoInvioSigla(Costanti.STATO_INVIO_DA_NON_COMUNICARE);
			} else {
				rimborsoMissioneDaAggiornare.setStatoInvioSigla(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE);
			}
		} else {
			rimborsoMissioneDaAggiornare.setStatoInvioSigla(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE);
		}
		LocalDate data = LocalDate.now();
		if (rimborsoMissioneDaAggiornare.getStatoInvioSigla().equals(Costanti.STATO_INVIO_SIGLA_DA_COMUNICARE) && data.getYear() > rimborsoMissioneDaAggiornare.getAnno()){
			ribaltaMissione(principal, rimborsoMissioneDaAggiornare, data.getYear());
		}

		gestioneMailResponsabileGruppo(principal, rimborsoMissioneDaAggiornare);
		List<UsersSpecial> listaUtenti = new ArrayList<>();
		DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(rimborsoMissioneDaAggiornare.getUoRich(), rimborsoMissioneDaAggiornare.getAnno());
		DatiIstituto datiIstitutoSpesa = null;
		if (!rimborsoMissioneDaAggiornare.getUoRich().equals(rimborsoMissioneDaAggiornare.getUoSpesa())){
			datiIstitutoSpesa = datiIstitutoService.getDatiIstituto(rimborsoMissioneDaAggiornare.getUoSpesa(), rimborsoMissioneDaAggiornare.getAnno());
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoRimborso(),"N").equals("U")){
			listaUtenti = accountService.getUserSpecialForUo(rimborsoMissioneDaAggiornare.getUoRich(), false);
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoRimborso(),"N").equals("V")){
			listaUtenti = accountService.getUserSpecialForUo(rimborsoMissioneDaAggiornare.getUoRich(), true);
		}
		if (datiIstitutoSpesa != null){
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoRimborso(),"N").equals("U")){
				List<UsersSpecial> listaUtentiSpesa = accountService.getUserSpecialForUo(rimborsoMissioneDaAggiornare.getUoSpesa(), false);
				listaUtenti.addAll(listaUtentiSpesa);
			}
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoRimborso(),"N").equals("V")){
				List<UsersSpecial> listaUtentiSpesa = accountService.getUserSpecialForUo(rimborsoMissioneDaAggiornare.getUoSpesa(), true);
				listaUtenti.addAll(listaUtentiSpesa);
			}
		}
		if (listaUtenti.size() > 0){
			mailService.sendEmail(approvazioneRimborsoMissione, getTextMailApprovazioneRimborso(rimborsoMissioneDaAggiornare), false, true, mailService.prepareTo(listaUtenti));
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoRimborso(),"N").equals("E") && !StringUtils.isEmpty(datiIstituto.getMailNotificheRimborso()) &&  !datiIstituto.getMailNotificheRimborso().equals("N")){
			mailService.sendEmail(approvazioneRimborsoMissione, getTextMailApprovazioneRimborso(rimborsoMissioneDaAggiornare), false, true, datiIstituto.getMailNotificheRimborso());
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoRimborso(),"N").equals("A") && !StringUtils.isEmpty(datiIstituto.getMailDopoRimborso())){
			mailService.sendEmail(approvazioneRimborsoMissione, getTextMailApprovazioneRimborso(rimborsoMissioneDaAggiornare), false, true, datiIstituto.getMailDopoRimborso());
		}
		if (datiIstitutoSpesa != null){
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoRimborso(),"N").equals("E") && !StringUtils.isEmpty(datiIstitutoSpesa.getMailNotificheRimborso()) && !datiIstitutoSpesa.getMailNotificheRimborso().equals("N")){
				mailService.sendEmail(approvazioneRimborsoMissione, getTextMailApprovazioneRimborso(rimborsoMissioneDaAggiornare), false, true, datiIstitutoSpesa.getMailNotificheRimborso());
			}
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoRimborso(),"N").equals("A") && !StringUtils.isEmpty(datiIstitutoSpesa.getMailDopoRimborso())){
				mailService.sendEmail(approvazioneRimborsoMissione, getTextMailApprovazioneRimborso(rimborsoMissioneDaAggiornare), false, true, datiIstitutoSpesa.getMailDopoRimborso());
			}
		}
		rimborsoMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		rimborsoMissioneDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
		RimborsoMissione rimborso = updateRimborsoMissione(principal, rimborsoMissioneDaAggiornare, true, null);
		popolaCoda(rimborso);
		return rimborso;
	}

	private RimborsoMissione aggiornaRimborsoMissionePrimaFirma(Principal principal, RimborsoMissione rimborsoMissioneDaAggiornare)
			throws ComponentException {
		rimborsoMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
		RimborsoMissione rimborso = updateRimborsoMissione(principal, rimborsoMissioneDaAggiornare, true, null);
		return rimborso;
	}

	public void popolaCoda(RimborsoMissione rimborsoMissione) {
    	if (!isDevProfile()){
			if (rimborsoMissione.getMatricola() != null){
				Account account = accountService.loadAccountFromRest(rimborsoMissione.getUid());
				String idSede = null;
				if (account != null){
					idSede = account.getCodice_sede();
				}
				Missione missione = new Missione(TypeMissione.RIMBORSO, new Long(rimborsoMissione.getId().toString()), idSede,
						rimborsoMissione.getMatricola(), rimborsoMissione.getDataInizioMissione(), rimborsoMissione.getDataFineMissione(), new Long(rimborsoMissione.getOrdineMissione().getId().toString()), rimborsoMissione.isMissioneEstera() ? TypeTipoMissione.ESTERA : TypeTipoMissione.ITALIA,
						rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
				rabbitMQService.send(missione);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public RimborsoMissione aggiornaRimborsoMissioneComunicata(Principal principal, RimborsoMissione rimborsoMissioneDaAggiornare, MissioneBulk missioneBulk)
			throws ComponentException {
		rimborsoMissioneDaAggiornare.setEsercizioSigla(missioneBulk.getEsercizio());
		rimborsoMissioneDaAggiornare.setPgMissioneSigla(missioneBulk.getPgMissione());
		rimborsoMissioneDaAggiornare.setCdCdsSigla(missioneBulk.getCdCds());
		rimborsoMissioneDaAggiornare.setCdUoSigla(missioneBulk.getCdUnitaOrganizzativa());
		rimborsoMissioneDaAggiornare.setStatoInvioSigla(Costanti.STATO_INVIO_SIGLA_COMUNICATA);
		return updateRimborsoMissione(principal, rimborsoMissioneDaAggiornare, true, null);
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissione updateRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissione, String basePath)  throws ComponentException{
    	return updateRimborsoMissione(principal, rimborsoMissione, false, basePath);
    }
    
    private RimborsoMissione updateRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissione, Boolean fromFlows, String basePath)  throws ComponentException{
    	return updateRimborsoMissione(principal, rimborsoMissione, fromFlows, false, basePath);
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissione updateRimborsoMissione (Principal principal, RimborsoMissione rimborsoMissione, Boolean fromFlows, Boolean confirm, String basePath)  throws ComponentException{

    	RimborsoMissione rimborsoMissioneDB = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, rimborsoMissione.getId());
       	boolean isRitornoMissioneMittente = false;
       	boolean isRitornoMissioneAmministrativi = false;

		if (rimborsoMissioneDB==null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso Missione da aggiornare inesistente.");
		}
    	try {
			crudServiceBean.lockBulk(principal, rimborsoMissioneDB);
		} catch (ComponentException | OptimisticLockException | PersistencyException | BusyResourceException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione in modifica. Ripetere l'operazione. Id "+rimborsoMissioneDB.getId());
		}
    	retrieveDetails(principal, rimborsoMissioneDB);
		if (rimborsoMissioneDB.isMissioneConfermata() && !fromFlows && !Utility.nvl(rimborsoMissione.getDaValidazione(), "N").equals("D")){
			rimborsoMissioneDB.setNoteRespingi(null);
			if (rimborsoMissioneDB.isStatoFlussoApprovato()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile modificare il rimborso della missione. E' già stato approvato.");
			}
			if (!rimborsoMissioneDB.isMissioneDaValidare() && !rimborsoMissioneDB.isAllaValidazioneAmministrativa()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile modificare il rimborso della missione. E' già stato avviato il flusso di approvazione.");
			}
		}
		
		if (Utility.nvl(rimborsoMissione.getDaValidazione(), "N").equals("S")){
			if (!rimborsoMissioneDB.getStato().equals(Costanti.STATO_CONFERMATO)){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione non confermato.");
			}
			if (!rimborsoMissioneDB.isMissioneDaValidare() && !rimborsoMissioneDB.isAllaValidazioneAmministrativa()){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso missione già validato.");
			}
			if (rimborsoMissioneDB.isMissioneDaValidare()){
				if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), rimborsoMissioneDB.getUoSpesa())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare i rimborsi di missione.");
				}
			}
			
			if (rimborsoMissioneDB.isAllaValidazioneAmministrativa()){
				if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), rimborsoMissioneDB.getUoContrAmm())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare i rimborsi di missione.");
				}
			}

			if (!confirm){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Operazione non possibile. Non è possibile modificare un rimborso di missione durante la fase di validazione. Rieseguire la ricerca.");
			}
			aggiornaDatiRimborsoMissione(principal, rimborsoMissione, confirm, rimborsoMissioneDB);
			rimborsoMissioneDB.setValidato("S");
			rimborsoMissioneDB.setNoteRespingi(null);
		} else if (Utility.nvl(rimborsoMissione.getDaValidazione(), "N").equals("D")){
			if (rimborsoMissione.getEsercizioOriginaleObbligazione() == null || rimborsoMissione.getPgObbligazione() == null ){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Per rendere definitivo il rimborso della missione è necessario valorizzare l'impegno.");
			}
			if (!StringUtils.isEmpty(rimborsoMissione.getGae())){
				rimborsoMissioneDB.setGae(rimborsoMissione.getGae());
			}
			if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
				rimborsoMissioneDB.setVoce(rimborsoMissione.getVoce());
			}
			rimborsoMissioneDB.setEsercizioOriginaleObbligazione(rimborsoMissione.getEsercizioOriginaleObbligazione());
			rimborsoMissioneDB.setPgObbligazione(rimborsoMissione.getPgObbligazione());
			rimborsoMissioneDB.setStato(Costanti.STATO_DEFINITIVO);
			rimborsoMissioneDB.setNoteRespingi(null);
		} else if (Utility.nvl(rimborsoMissione.getDaValidazione(), "N").equals("R")){
			if (rimborsoMissione.isMissioneDaValidare()){
				if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), rimborsoMissione.getUoSpesa())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare i rimborsi missione per la uo "+rimborsoMissione.getUoSpesa()+".");
				}
				if (rimborsoMissioneDB.isStatoNonInviatoAlFlusso() || rimborsoMissioneDB.isMissioneDaValidare()) {
					if (StringUtils.isEmpty(rimborsoMissione.getNoteRespingi())){
						throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile respingere un rimborso missione senza indicarne il motivo.");
					}
					rimborsoMissioneDB.setStato(Costanti.STATO_INSERITO);
					rimborsoMissioneDB.setNoteRespingi(rimborsoMissione.getNoteRespingi());
					isRitornoMissioneMittente = true;
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile respingere un rimborso missione se è stato già inviato al flusso.");
				}
			} else if (rimborsoMissione.getUoContrAmm() != null) {
				if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), rimborsoMissione.getUoContrAmm())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato alla validazione amministrativa per la uo "+rimborsoMissione.getUoSpesa()+".");
				}
				if (rimborsoMissioneDB.isStatoNonInviatoAlFlusso() || rimborsoMissioneDB.isAllaValidazioneAmministrativa()) {
					if (StringUtils.isEmpty(rimborsoMissione.getNoteRespingi())){
						throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile respingere un rimborso missione senza indicarne il motivo.");
					}
					rimborsoMissioneDB.setStato(Costanti.STATO_INSERITO);
					rimborsoMissioneDB.setNoteRespingi(rimborsoMissione.getNoteRespingi());
					rimborsoMissioneDB.setValidaAmm(null);
					isRitornoMissioneMittente = true;
					isRitornoMissioneAmministrativi = true;
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile respingere un rimborso missione se è stato già inviato al flusso.");
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Operazione non consentita.");
			}
		} else {
			aggiornaDatiRimborsoMissione(principal, rimborsoMissione, confirm, rimborsoMissioneDB);
		}
		
    	if (confirm){
//        	if (Utility.nvl(rimborsoMissioneDB.getTotaleRimborsoSenzaSpeseAnticipate()).subtract(Utility.nvl(rimborsoMissioneDB.getAnticipoImporto())).compareTo(BigDecimal.ZERO) < 0){
//				throw new AwesomeException(CodiciErrore.ERRGEN, "L'importo totale dei dettagli spesa non anticipati al netto dell'anticipo ricevuto è negativo.");
//        	}
    		
    		rimborsoMissioneDB.setStato(Costanti.STATO_CONFERMATO);
			rimborsoMissioneDB.setNoteRespingi(null);
    	} 

    	rimborsoMissioneDB.setToBeUpdated();
		if (confirm){
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(rimborsoMissioneDB.getUoSpesa(), rimborsoMissioneDB.getAnno());
			Boolean controlloEsistenzaAllegati = Utility.nvl(dati.getObbligoAllegatiValidazione(),"S").equals("S") || !rimborsoMissioneDB.isMissioneDaValidare();
			if (assenzaDettagli(rimborsoMissioneDB, controlloEsistenzaAllegati) && (Utility.nvl(rimborsoMissione.getRimborso0(),"N").equals("N"))){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile confermare un rimborso missione senza aver indicato nessun dettaglio di spesa.");
			}
			if (!assenzaDettagli(rimborsoMissione, controlloEsistenzaAllegati) && (Utility.nvl(rimborsoMissione.getRimborso0(),"N").equals("S"))){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile confermare un rimborso missione con dettagli e ad importo 0.");
			}
		}
//		//effettuo controlli di validazione operazione CRUD
    	if (!Utility.nvl(rimborsoMissione.getDaValidazione(), "N").equals("R") && !fromFlows){
    		validaCRUD(principal, rimborsoMissioneDB);
    	}

		controlloCongruenzaTestataDettagli(rimborsoMissioneDB);
    	if (confirm && !rimborsoMissioneDB.isMissioneDaValidare() && rimborsoMissioneDB.isPassataValidazioneAmministrativa()){
			if (!accountService.isUserSpecialEnableToValidateOrder(principal.getName(), rimborsoMissione.getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Utente non abilitato a validare i rimborsi missione per la uo "+rimborsoMissione.getUoSpesa()+".");
			}
    		cmisRimborsoMissioneService.avviaFlusso((Principal) SecurityUtils.getCurrentUser(), rimborsoMissioneDB);
    		rimborsoMissioneDB.setStateFlows(Costanti.STATO_FLUSSO_RIMBORSO_FROM_CMIS.get(Costanti.STATO_FIRMA_UO_RIMBORSO_FROM_CMIS));
    	}
    	rimborsoMissioneDB.setRimborsoMissioneDettagli(null);
    	rimborsoMissioneDB = (RimborsoMissione)crudServiceBean.modificaConBulk(principal, rimborsoMissioneDB);
    	
    	log.debug("Updated Information for Rimborso Missione: {}", rimborsoMissioneDB);

	   if (confirm && rimborsoMissioneDB.isMissioneDaValidare()){
			sendMailToAdministrative(basePath, rimborsoMissioneDB);
	   } else if (confirm && rimborsoMissioneDB.isAllaValidazioneAmministrativa()){
			DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(rimborsoMissioneDB.getUoSpesa(), rimborsoMissioneDB.getAnno());
			if (datiIstituto != null && datiIstituto.getUoContrAmm() != null){
				sendMailToAdministrative(basePath, rimborsoMissioneDB, datiIstituto.getUoContrAmm());
			}
	   }
	   if (isRitornoMissioneMittente){
	   		missioneRespintaService.inserisciRimborsoMissioneRespinto(principal, rimborsoMissioneDB);
		   	List<String> listaMail = new ArrayList<>();
		   	listaMail.add(getEmail(rimborsoMissioneDB.getUidInsert()));
		   	if (isRitornoMissioneAmministrativi){
			   List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(rimborsoMissioneDB.getUoSpesa());
			   if (lista != null && lista.size() > 0){
				   List<String> listaMailAmm = mailService.preparaListaMail(lista);
				   listaMail.addAll(listaMailAmm);
			   }
		   	}
		   	String[] elencoMail = mailService.preparaElencoMail(listaMail);
		   	mailService.sendEmail(subjectReturnToSenderOrdine, getTextMailReturnToSender(principal, basePath, rimborsoMissioneDB), false, true, elencoMail);
	   }
    	return rimborsoMissioneDB;
    }

    private void gestioneMailResponsabileGruppo(Principal principal, RimborsoMissione rimborsoMissione) {
    	if (rimborsoMissione.getOrdineMissione() != null){
        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
        	if (ordineMissione != null && ordineMissione.getResponsabileGruppo() != null){
        		mailService.sendEmail(oggettoImportoMissioneManager+" "+getNominativo(rimborsoMissione.getUid()), 
        				getTestoMailAumentoMissioneResponsabileGruppo(rimborsoMissione, ordineMissione), false, true, accountService.getEmail(ordineMissione.getResponsabileGruppo()));
        	}
    	}
	}

    private void sendMailToAdministrative(String basePath, RimborsoMissione rimborsoMissioneDB) {
		sendMailToAdministrative(basePath, rimborsoMissioneDB, rimborsoMissioneDB.getUoSpesa());
	}

    private void sendMailToAdministrative(String basePath, RimborsoMissione rimborsoMissioneDB, String uo) {
		DatiIstituto dati = datiIstitutoService.getDatiIstituto(uo, rimborsoMissioneDB.getAnno());
		String subjectMail = subjectSendToAdministrative + " "+ getNominativo(rimborsoMissioneDB.getUid());
		String testoMail = getTextMailSendToAdministrative(basePath, rimborsoMissioneDB);
		if (dati != null && dati.getMailNotificheRimborso() != null  && !dati.getMailNotificheRimborso().equals("N")){
			mailService.sendEmail(subjectMail, testoMail, false, true, dati.getMailNotificheRimborso());
		} else {
			List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(uo);
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

	private String getTestoMailAumentoMissioneResponsabileGruppo(RimborsoMissione rimborsoMissione, OrdineMissione ordine) {
		return "Il rimborso missione "+rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+getNominativo(rimborsoMissione.getUid())+" per la missione a "+rimborsoMissione.getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataFineMissione())+ " avente per oggetto "+rimborsoMissione.getOggetto()+"  ha un importo totale di euro "+ Utility.numberFormat(rimborsoMissione.getTotaleRimborsoSenzaSpeseAnticipate());
	}

	private String getTextMailSendToAdministrative(String basePath, RimborsoMissione rimborsoMissione) {
		return "Il rimborso missione "+rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " della uo "+rimborsoMissione.getUoRich()+" "+rimborsoMissione.getDatoreLavoroRich()+ " di "+getNominativo(rimborsoMissione.getUid())+" per la missione a "+rimborsoMissione.getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataFineMissione())+ " avente per oggetto "+rimborsoMissione.getOggetto()+"  è stato inviato per la verifica/completamento dei dati finanziari."
				+ "Si prega di verificarlo attraverso il link "+basePath+"/#/rimborso-missione/"+rimborsoMissione.getId()+"/S";
	}

	private void aggiornaDatiRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissione, Boolean confirm,
			RimborsoMissione rimborsoMissioneDB) {
		rimborsoMissioneDB.setStato(rimborsoMissione.getStato());
		rimborsoMissioneDB.setStatoFlusso(rimborsoMissione.getStatoFlusso());
		rimborsoMissioneDB.setCdrSpesa(rimborsoMissione.getCdrSpesa());
		rimborsoMissioneDB.setCdsSpesa(rimborsoMissione.getCdsSpesa());
		if (rimborsoMissione.getUoSpesa() != null && rimborsoMissioneDB.getUoSpesa() != null && 
				!rimborsoMissione.getUoSpesa().equals(rimborsoMissioneDB.getUoSpesa())){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile modificare la uo di Spesa. Nel caso fosse necessaria la modifica è necessario cancellare il rimborso missione e reinserirlo.");
		}
		rimborsoMissioneDB.setCdsCompetenza(rimborsoMissione.getCdsCompetenza());
		rimborsoMissioneDB.setUoCompetenza(rimborsoMissione.getUoCompetenza());
		rimborsoMissioneDB.setCommentoFlusso(rimborsoMissione.getCommentoFlusso());
		rimborsoMissioneDB.setDomicilioFiscaleRich(rimborsoMissione.getDomicilioFiscaleRich());
		rimborsoMissioneDB.setDataInizioMissione(rimborsoMissione.getDataInizioMissione());
		rimborsoMissioneDB.setDataFineMissione(rimborsoMissione.getDataFineMissione());
		rimborsoMissioneDB.setDestinazione(rimborsoMissione.getDestinazione());
		rimborsoMissioneDB.setGae(rimborsoMissione.getGae());
		rimborsoMissioneDB.setNote(rimborsoMissione.getNote());
		rimborsoMissioneDB.setUoContrAmm(rimborsoMissione.getUoContrAmm());
		rimborsoMissioneDB.setNoteSegreteria(rimborsoMissione.getNoteSegreteria());
		if (confirm){
			aggiornaValidazione(principal, rimborsoMissioneDB);
		} else {
			rimborsoMissioneDB.setValidato(rimborsoMissione.getValidato());
			rimborsoMissioneDB.setValidaAmm(rimborsoMissione.getValidaAmm());
		}
		rimborsoMissioneDB.setOggetto(rimborsoMissione.getOggetto());
		rimborsoMissioneDB.setTipoMissione(rimborsoMissione.getTipoMissione());
		rimborsoMissioneDB.setVoce(rimborsoMissione.getVoce());
		rimborsoMissioneDB.setTrattamento(rimborsoMissione.getTrattamento());
		rimborsoMissioneDB.setNazione(rimborsoMissione.getNazione());

		rimborsoMissioneDB.setNoteUtilizzoTaxiNoleggio(rimborsoMissione.getNoteUtilizzoTaxiNoleggio());
		rimborsoMissioneDB.setUtilizzoAutoNoleggio(rimborsoMissione.getUtilizzoAutoNoleggio());
		rimborsoMissioneDB.setUtilizzoTaxi(rimborsoMissione.getUtilizzoTaxi());
		rimborsoMissioneDB.setPgProgetto(rimborsoMissione.getPgProgetto());
		rimborsoMissioneDB.setPgProgetto(rimborsoMissione.getPgProgetto());
		rimborsoMissioneDB.setEsercizioOriginaleObbligazione(rimborsoMissione.getEsercizioOriginaleObbligazione());
		rimborsoMissioneDB.setPgObbligazione(rimborsoMissione.getPgObbligazione());

		rimborsoMissioneDB.setDataInizioEstero(rimborsoMissione.getDataInizioEstero());
		rimborsoMissioneDB.setDataFineEstero(rimborsoMissione.getDataFineEstero());
		rimborsoMissioneDB.setCdTerzoSigla(rimborsoMissione.getCdTerzoSigla());
		rimborsoMissioneDB.setModpag(rimborsoMissione.getModpag());
		rimborsoMissioneDB.setIban(rimborsoMissione.getIban());
		rimborsoMissioneDB.setPgBanca(rimborsoMissione.getPgBanca());
		rimborsoMissioneDB.setTipoPagamento(rimborsoMissione.getTipoPagamento());
		rimborsoMissioneDB.setAnticipoRicevuto(rimborsoMissione.getAnticipoRicevuto());
		rimborsoMissioneDB.setAnticipoAnnoMandato(rimborsoMissione.getAnticipoAnnoMandato());
		rimborsoMissioneDB.setAnticipoNumeroMandato(rimborsoMissione.getAnticipoNumeroMandato());
		rimborsoMissioneDB.setAnticipoImporto(rimborsoMissione.getAnticipoImporto());
		rimborsoMissioneDB.setAltreSpeseAntDescrizione(rimborsoMissione.getAltreSpeseAntDescrizione());
		rimborsoMissioneDB.setAltreSpeseAntImporto(rimborsoMissione.getAltreSpeseAntImporto());
		rimborsoMissioneDB.setSpeseTerziImporto(rimborsoMissione.getSpeseTerziImporto());
		rimborsoMissioneDB.setSpeseTerziRicevute(rimborsoMissione.getSpeseTerziRicevute());
		rimborsoMissioneDB.setRimborso0(rimborsoMissione.getRimborso0());
//			rimborsoMissioneDB.setOrdineMissione(rimborsoMissione.getOrdineMissione());
		rimborsoMissioneDB.setInquadramento(rimborsoMissione.getInquadramento());
		rimborsoMissioneDB.setCdCdsSigla(rimborsoMissione.getCdCdsSigla());
		rimborsoMissioneDB.setCdUoSigla(rimborsoMissione.getCdUoSigla());
		rimborsoMissioneDB.setEsercizioSigla(rimborsoMissione.getEsercizioSigla());
		rimborsoMissioneDB.setPgMissioneSigla(rimborsoMissione.getPgMissioneSigla());
		rimborsoMissioneDB.setStatoInvioSigla(rimborsoMissione.getStatoInvioSigla());
		rimborsoMissioneDB.setCdTipoRapporto(rimborsoMissione.getCdTipoRapporto());
		rimborsoMissioneDB.setPersonaleAlSeguito(rimborsoMissione.getPersonaleAlSeguito());
		rimborsoMissioneDB.setUtilizzoAutoServizio(rimborsoMissione.getUtilizzoAutoServizio());
		rimborsoMissioneDB.setCug(rimborsoMissione.getCug());
		rimborsoMissioneDB.setPresidente(rimborsoMissione.getPresidente());
		rimborsoMissioneDB.setCup(rimborsoMissione.getCup());
//			rimborsoMissioneDB.setNoteDifferenzeOrdine(rimborsoMissione.getNoteDifferenzeOrdine());
	}

    private String getEmail(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getEmail_comunicazioni();
    }

    private String getNominativo(String user){
		Account utente = accountService.loadAccountFromRest(user);
		return utente.getCognome()+ " "+ utente.getNome();
    }

	private String getTextMailReturnToSender(Principal principal, String basePath, RimborsoMissione rimborsoMissione) {
		String baseMessage = "Il rimborso missione "+rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+getNominativo(rimborsoMissione.getUid())+" per la missione a "+rimborsoMissione.getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataFineMissione())+ " avente per oggetto "+rimborsoMissione.getOggetto()+" le è stata respinto da "+getNominativo(principal.getName())+" per il seguente motivo: "+rimborsoMissione.getNoteRespingi();
		if (basePath != null){
			return baseMessage;
		} else {
			return baseMessage+ ". Si prega di effettuare le opportune correzioni attraverso il link "+basePath+"/#/ordine-missione/"+rimborsoMissione.getId();
		}
	}

	private void controlloCongruenzaTestataDettagli(RimborsoMissione rimborsoMissione) {
		if (rimborsoMissione.getRimborsoMissioneDettagli() != null && !rimborsoMissione.getRimborsoMissioneDettagli().isEmpty() ){
			for (RimborsoMissioneDettagli dettaglio : rimborsoMissione.getRimborsoMissioneDettagli()){
				if (dettaglio.getDataSpesa().isAfter(DateUtils.getDateWithDefaultZoneId(rimborsoMissione.getDataFineMissione()).toLocalDate())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La Data di Fine Missione non può essere precedente alla data di una spesa indicata nei dettagli.");
				}
				if (dettaglio.getDataSpesa().isBefore(DateUtils.getDateWithDefaultZoneId(rimborsoMissione.getDataInizioMissione()).toLocalDate())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La Data di Inizio Missione non può essere successiva alla data di una spesa indicata nei dettagli.");
				}
			}
		}
	}

	private Boolean assenzaDettagli(RimborsoMissione rimborsoMissione, Boolean controlloEsistenzaAllegati) throws ComponentException {
		if (rimborsoMissione.getRimborsoMissioneDettagli() == null || rimborsoMissione.getRimborsoMissioneDettagli().isEmpty() && 
				!rimborsoMissione.isTrattamentoAlternativoMissione()){
			return true;
		} else {
			if (controlloEsistenzaAllegati){
				cmisRimborsoMissioneService.controlloEsitenzaGiustificativoDettaglio(rimborsoMissione);
			}
		}
		return false;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteRimborsoMissione(Principal principal, Long idRimborsoMissione) throws ComponentException{
    	RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
		if (rimborsoMissione != null){
			controlloOperazioniCRUDDaGui(rimborsoMissione);
			rimborsoMissioneDettagliService.cancellaRimborsoMissioneDettagli(principal, rimborsoMissione, false);
			rimborsoImpegniService.cancellaRimborsoImpegni(principal, rimborsoMissione);
			rimborsoMissione.setStato(Costanti.STATO_ANNULLATO);
			rimborsoMissione.setToBeUpdated();
			if (rimborsoMissione.isStatoRespintoFlusso() && !StringUtils.isEmpty(rimborsoMissione.getIdFlusso())){
				cmisRimborsoMissioneService.annullaFlusso(rimborsoMissione);
			}
			crudServiceBean.modificaConBulk(principal, rimborsoMissione);
		}
	}

	public void controlloOperazioniCRUDDaGui(RimborsoMissione rimborsoMissione) {
		if (!rimborsoMissione.isMissioneInserita()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile effettuare l'operazione su un Rimborso che non si trova in uno stato "+Costanti.STATO.get(Costanti.STATO_INSERITO));
		}
	}
	
    public RimborsoMissione getRimborsoMissione(Principal principal, Long idMissione, Boolean retrieveDetail) throws ComponentException {
		return getRimborsoMissione(principal, idMissione, retrieveDetail, false);
    }

    public List<RimborsoMissione> getRimborsoMissione(Principal principal, RimborsoMissioneFilter filter, Boolean isServiceRest) throws ComponentException {
		return getRimborsiMissione(principal, filter, isServiceRest, false);
    }

    public List<RimborsoMissione> getRimborsiMissione(Principal principal, RimborsoMissioneFilter filter, Boolean isServiceRest) throws ComponentException {
		return getRimborsiMissione(principal, filter, isServiceRest, false);
    }


    public List<RimborsoMissione> getRimborsiMissione(Principal principal, RimborsoMissioneFilter filter, Boolean isServiceRest, Boolean isForValidateFlows) throws ComponentException {
		CriterionList criterionList = new CriterionList();
		List<RimborsoMissione> rimborsoMissioneList=null;
		if (filter != null){
			if (filter.getUoRich() != null && filter.getUser() != null){
				filter.setUoRich(null);
			}
			if (filter.getAnno() != null){
				criterionList.add(Restrictions.eq("anno", filter.getAnno()));
			}
			if (filter.getDaId() != null){
				criterionList.add(Restrictions.ge("id", filter.getDaId()));
			}
			if (filter.getIdOrdineMissione() != null){
				criterionList.add(Restrictions.eq("ordineMissione.id", filter.getIdOrdineMissione()));
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
				criterionList.add(Restrictions.le("numero", filter.getaNumero()));
			}
			if (filter.getDaData() != null){
				criterionList.add(Restrictions.ge("dataInserimento", DateUtils.parseLocalDate(filter.getDaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getaData() != null){
				criterionList.add(Restrictions.le("dataInserimento", DateUtils.parseLocalDate(filter.getaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getDaDataMissione() != null) {
				Disjunction condizioneOr = Restrictions.disjunction();
				condizioneOr.add(Restrictions.conjunction().add(Restrictions.ge("dataInizioMissione",
						DateUtils.parseLocalDate(filter.getDaDataMissione(), DateUtils.PATTERN_DATE).atStartOfDay(ZoneId.of(DateUtils.ZONE_ID_DEFAULT)))));
				condizioneOr.add(Restrictions.conjunction().add(Restrictions.ge("dataFineMissione",
						DateUtils.parseLocalDate(filter.getDaDataMissione(), DateUtils.PATTERN_DATE).atStartOfDay(ZoneId.of(DateUtils.ZONE_ID_DEFAULT)))));
				criterionList.add(condizioneOr);
			}
			if (filter.getaDataMissione() != null) {
				Disjunction condizioneOr = Restrictions.disjunction();
				condizioneOr.add(Restrictions.conjunction().add(Restrictions.lt("dataInizioMissione",
						DateUtils.parseLocalDate(filter.getaDataMissione(), DateUtils.PATTERN_DATE).plusDays(1).atStartOfDay(ZoneId.of(DateUtils.ZONE_ID_DEFAULT)))));
				condizioneOr.add(Restrictions.conjunction().add(Restrictions.lt("dataFineMissione",
						DateUtils.parseLocalDate(filter.getaDataMissione(), DateUtils.PATTERN_DATE).plusDays(1).atStartOfDay(ZoneId.of(DateUtils.ZONE_ID_DEFAULT)))));
				criterionList.add(condizioneOr);
			}
			if (filter.getCdsRich() != null){
				criterionList.add(Restrictions.eq("cdsRich", filter.getCdsRich()));
			}
			if (filter.getUoRich() != null){
				if (accountService.isUserEnableToWorkUo(principal, filter.getUoRich()) && !filter.isDaCron()){
					Disjunction condizioneOr = Restrictions.disjunction();
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoRich", filter.getUoRich())));
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoSpesa", filter.getUoRich())));
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoCompetenza", filter.getUoRich())));
		    		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoContrAmm", filter.getUoRich())));
					criterionList.add(condizioneOr);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente "+principal.getName()+"  non è abilitato a vedere i dati della uo "+filter.getUoRich());
				}
			}
			if (filter.getAnnoOrdine() != null){
				criterionList.add(Subqueries.exists("select ord.id from OrdineMissione AS ord where ord.id = this.ordineMissione.id and ord.stato != 'ANN' and ord.anno = "+filter.getAnnoOrdine()));
			}
			if (filter.getDaNumeroOrdine() != null){
				criterionList.add(Subqueries.exists("select ord.id from OrdineMissione AS ord where ord.id = this.ordineMissione.id and ord.stato != 'ANN' and ord.numero >= "+filter.getDaNumeroOrdine()));
			}
			if (filter.getaNumeroOrdine() != null){
				criterionList.add(Subqueries.exists("select ord.id from OrdineMissione AS ord where ord.id = this.ordineMissione.id and ord.stato != 'ANN' and ord.numero <= "+filter.getaNumeroOrdine()));
			}
			if (filter.getStatoInvioSigla() != null){
				criterionList.add(Restrictions.eq("statoInvioSigla", filter.getStatoInvioSigla()));
			}
			if (filter.getCup() != null) {
				criterionList.add(Restrictions.eq("cup", filter.getCup()));
			}
		}
		if (filter != null && Utility.nvl(filter.getDaCron(), "N").equals("S")){
			return crudServiceBean.findByCriterion(principal, RimborsoMissione.class, criterionList, Order.asc("dataInserimento"), Order.asc("anno"), Order.asc("numero"));
		} else if (filter != null && Utility.nvl(filter.getToFinal(), "N").equals("S")){
			if (StringUtils.isEmpty(filter.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stata selezionata la uo per rendere definitivo il rimborso della missione.");
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
			rimborsoMissioneList = crudServiceBean.findByProjection(principal, RimborsoMissione.class, RimborsoMissione.getProjectionForElencoMissioni(), criterionList, true, Order.desc("dataInserimento"), Order.desc("anno"), Order.desc("numero"));
			return rimborsoMissioneList;
			
		} else {
			if (!isForValidateFlows){
				if (!StringUtils.isEmpty(filter.getUser())){
					criterionList.add(Restrictions.eq("uid", filter.getUser()));
				} else {
					if (StringUtils.isEmpty(filter.getUoRich())){
						criterionList.add(Restrictions.eq("uid", principal.getName()));
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
					    		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoRich", uoService.getUoSigla(uoUser))));
					    		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoSpesa", uoService.getUoSigla(uoUser))));
					    		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoCompetenza", uoService.getUoSigla(uoUser))));
					    		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoContrAmm", uoService.getUoSigla(uoUser))));
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
				criterionList.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE)));
			}

			if (isServiceRest) {
				if (isForValidateFlows){
					List<String> listaStatiFlusso = new ArrayList<String>();
					listaStatiFlusso.add(Costanti.STATO_INVIATO_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_INSERITO);
					listaStatiFlusso.add(Costanti.STATO_RESPINTO_UO_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO);
					criterionList.add(Restrictions.disjunction().add(Restrictions.disjunction().add(Restrictions.in("statoFlusso", listaStatiFlusso)).add(Restrictions.conjunction().add(Restrictions.eq("stato", Costanti.STATO_INSERITO)))));
				}
				rimborsoMissioneList = crudServiceBean.findByProjection(principal, RimborsoMissione.class, RimborsoMissione.getProjectionForElencoMissioni(), criterionList, true, Order.desc("dataInserimento"), Order.desc("anno"), Order.desc("numero"));
			} else
				rimborsoMissioneList = crudServiceBean.findByCriterion(principal, RimborsoMissione.class, criterionList, Order.desc("dataInserimento"), Order.desc("anno"), Order.desc("numero"));
			return rimborsoMissioneList;
		}
    }

    
    @Transactional(propagation = Propagation.REQUIRED)
    public RimborsoMissione createRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissione)  throws ComponentException{
    	controlloDatiObbligatoriDaGUI(rimborsoMissione);
    	inizializzaCampiPerInserimento(principal, rimborsoMissione);
		validaCRUD(principal, rimborsoMissione);
		rimborsoMissione = (RimborsoMissione)crudServiceBean.creaConBulk(principal, rimborsoMissione);
    	if (!StringUtils.isEmpty(rimborsoMissione.getPgObbligazione())){
    		creaRimborsoImpegni(principal, rimborsoMissione);
    	}
		
    	log.info("Creato Rimborso Missione", rimborsoMissione.getId());
    	return rimborsoMissione;
    }

    private void creaRimborsoImpegni(Principal principal,
    		RimborsoMissione rimborsoMissione) throws ComponentException{
    	RimborsoImpegni rimborsoImpegni = new RimborsoImpegni();
    	rimborsoImpegni.setEsercizioObbligazione(rimborsoMissione.getEsercizioObbligazione());
    	rimborsoImpegni.setCdCdsObbligazione(rimborsoMissione.getCdCdsObbligazione());
    	rimborsoImpegni.setEsercizioOriginaleObbligazione(rimborsoMissione.getEsercizioOriginaleObbligazione());
    	rimborsoImpegni.setPgObbligazione(rimborsoMissione.getPgObbligazione());
    	rimborsoImpegni.setRimborsoMissione(rimborsoMissione);
    	
    	rimborsoImpegniService.createRimborsoImpegni(principal, rimborsoImpegni);
    }
    
    private void inizializzaCampiPerInserimento(Principal principal,
    		RimborsoMissione rimborsoMissione) throws ComponentException{
    	rimborsoMissione.setUidInsert(principal.getName());
    	rimborsoMissione.setUser(principal.getName());
    	if (StringUtils.isEmpty(rimborsoMissione.getTrattamento())){
    		rimborsoMissione.setTrattamento("R");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoAutoNoleggio())){
    		rimborsoMissione.setUtilizzoAutoNoleggio("N");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoTaxi())){
    		rimborsoMissione.setUtilizzoTaxi("N");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoAutoServizio())){
    		rimborsoMissione.setUtilizzoAutoServizio("N");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getPersonaleAlSeguito())){
    		rimborsoMissione.setPersonaleAlSeguito("N");
    	}
    	if (StringUtils.isEmpty(rimborsoMissione.getAnticipoRicevuto())){
    		rimborsoMissione.setAnticipoRicevuto("N");
    	}
    	
    	if (StringUtils.isEmpty(rimborsoMissione.getSpeseTerziRicevute())){
    		rimborsoMissione.setSpeseTerziRicevute("N");
    	}
    	
    	if (StringUtils.isEmpty(rimborsoMissione.getRimborso0())){
    		rimborsoMissione.setRimborso0("N");
    	}
    	
    	Integer anno = recuperoAnno(rimborsoMissione);

    	DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
		
    	if (datiIstituto == null){
    		throw new ComponentException("Dati uo non presenti per il codice: "+rimborsoMissione.getUoSpesa()); 
    	}

		bloccoInserimentoRimborsi(rimborsoMissione, datiIstituto);
    	if (datiIstituto.getUoContrAmm() != null){
    		rimborsoMissione.setUoContrAmm(datiIstituto.getUoContrAmm());
    	}

    	rimborsoMissione.setNumero(datiIstitutoService.getNextPG(principal, rimborsoMissione.getUoSpesa(), anno , Costanti.TIPO_RIMBORSO_MISSIONE));
    	rimborsoMissione.setAnnoIniziale(rimborsoMissione.getAnno());
    	rimborsoMissione.setNumeroIniziale(rimborsoMissione.getNumero());
    	aggiornaValidazione(principal, rimborsoMissione);
    	
    	rimborsoMissione.setStato(Costanti.STATO_INSERITO);
    	rimborsoMissione.setStatoFlusso(Costanti.STATO_INSERITO);
    	if (rimborsoMissione.getOrdineMissione() != null){
        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
        	if (ordineMissione != null){
        		rimborsoMissione.setOrdineMissione(ordineMissione);
        	} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "L'ordine di missione con ID: "+rimborsoMissione.getOrdineMissione().getId()+" non esiste");
        	}
    	}
		if (StringUtils.isEmpty(rimborsoMissione.getMatricola()) && StringUtils.isEmpty(rimborsoMissione.getQualificaRich())) {
			Account account = accountService.loadAccountFromRest(rimborsoMissione.getUid());
			if (account != null && account.getCodice_fiscale() != null) {
				TerzoPerCompensoJson terzoJson = terzoPerCompensoService.getTerzi(account.getCodice_fiscale(),
						rimborsoMissione.getDataInizioMissione(), rimborsoMissione.getDataFineMissione());
				for (TerzoPerCompenso terzo : terzoJson.getElements()) {
					rimborsoMissione.setQualificaRich(terzo.getDsTipoRapporto());
					break;
				}
			}
		}
    	rimborsoMissione.setToBeCreated();
    }

	private void bloccoInserimentoRimborsi(RimborsoMissione rimborsoMissione, DatiIstituto datiIstituto) {
		if (rimborsoMissione.isTrattamentoAlternativoMissione()){
	    	if (datiIstituto.getDataBloccoInsRimborsiTam() != null && datiIstituto.getDataBloccoInsRimborsiTam().compareTo(rimborsoMissione.getDataInserimento()) < 0){
	    		throw new ComponentException("Inserimento rimborsi missione di tipo TAM bloccato. Non è possibile inserire nuovi rimborsi TAM per l'anno in corso."); 
	    	}
		} else {
	    	if (datiIstituto.getDataBloccoInsRimborsi() != null && datiIstituto.getDataBloccoInsRimborsi().compareTo(rimborsoMissione.getDataInserimento()) < 0){
	    		throw new ComponentException("Inserimento rimborsi missione bloccato. Non è possibile inserire nuovi rimborsi per l'anno in corso."); 
	    	}
		}
	}

    private OrdineMissioneAutoPropria getAutoPropriaOrdineMissione(Principal principal, RimborsoMissione rimborsoMissione){
    	if (rimborsoMissione.getOrdineMissione() != null){
        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
        	if (ordineMissione != null){
        		return ordineMissioneService.getAutoPropria(ordineMissione);
        	}
    	}
    	return null;
    }
    
    private Integer recuperoAnno(RimborsoMissione rimborsoMissione) {
    	rimborsoMissione.setDataInserimento(LocalDate.now());
		return rimborsoMissione.getDataInserimento().getYear();
	}

    private void controlloDatiObbligatoriDaGUI(RimborsoMissione rimborsoMissione){
		if (rimborsoMissione != null){
			if (!StringUtils.isEmpty(rimborsoMissione.getMatricola())){
				if (StringUtils.isEmpty(rimborsoMissione.getInquadramento()))
					throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stato possibile recuperare il valore dell'Inquadramento SIGLA. Verificare i dati");
			} 
			if (StringUtils.isEmpty(rimborsoMissione.getCdsRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getCdsSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cds Spesa");
			} else if (StringUtils.isEmpty(rimborsoMissione.getUoRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getUoSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Uo Spesa");
			} else if (StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Cdr Spesa");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDataInizioMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Inizio Missione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDataFineMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Fine Missione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDatoreLavoroRich())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Datore di Lavoro Richiedente");
			} else if (StringUtils.isEmpty(rimborsoMissione.getDestinazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Destinazione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getOggetto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Oggetto");
			} else if (StringUtils.isEmpty(rimborsoMissione.getTipoMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Tipo Missione");
			} else if (StringUtils.isEmpty(rimborsoMissione.getModpag())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Modalità di Pagamento");
			} else if (StringUtils.isEmpty(rimborsoMissione.getPgBanca())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Dati Banca");
			} else if (Costanti.TIPO_PAGAMENTO_BONIFICO.equals(rimborsoMissione.getModpag()) && !StringUtils.hasLength(rimborsoMissione.getIban())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Codice IBAN");
			} else if (StringUtils.isEmpty(rimborsoMissione.getAnticipoRicevuto())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anticipo Ricevuto");
			} else if (StringUtils.isEmpty(rimborsoMissione.getSpeseTerziRicevute())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Altri anticipi ricevuti");
			} else if (StringUtils.isEmpty(rimborsoMissione.getRimborso0())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Importo 0");
			} 
			if (StringUtils.isEmpty(rimborsoMissione.getInquadramento())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Per la data della missione indicata non è stato possibile recuperare l'inquadramento.");
			} 
			if (rimborsoMissione.isMissioneEstera()){
				if (StringUtils.isEmpty(rimborsoMissione.getNazione()) || Costanti.NAZIONE_ITALIA_SIGLA.compareTo(rimborsoMissione.getNazione()) == 0){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Nazione");
				} 
				if (StringUtils.isEmpty(rimborsoMissione.getTrattamento())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Trattamento");
				} 
				if (rimborsoMissione.isTrattamentoAlternativoMissione()){
					if (rimborsoMissione.getDataInizioEstero() == null){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Imbarco di Partenza");
					}
					if (rimborsoMissione.getDataFineEstero() == null){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Data Sbarco di Ritorno");
					}
				}
			} else {
				if (rimborsoMissione.getDataInizioEstero() != null){
					rimborsoMissione.setDataInizioEstero(null);
				}
				if (rimborsoMissione.getDataFineEstero() != null){
					rimborsoMissione.setDataFineEstero(null);
				}
			}
			if (rimborsoMissione.isMissioneDipendente()){
				if (StringUtils.isEmpty(rimborsoMissione.getComuneResidenzaRich())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Comune di Residenza del Richiedente");
				} else if (StringUtils.isEmpty(rimborsoMissione.getIndirizzoResidenzaRich())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Indirizzo di Residenza del Richiedente");
				}
			}
		}
    }

	private void aggiornaValidazione(Principal principal, RimborsoMissione rimborsoMissione) {
		if (accountService.isUserSpecialEnableToValidateOrder(principal.getName(), rimborsoMissione.getUoSpesa())){
			aggiornaValidazioneAmministrativa(principal, rimborsoMissione);
			rimborsoMissione.setValidato("S");
		} else {
			if (!rimborsoMissione.isMissioneDaValidare()){
				aggiornaValidazioneAmministrativa(principal, rimborsoMissione);
			} 
		}
	}

	protected void aggiornaValidazioneAmministrativa(Principal principal, RimborsoMissione rimborsoMissione) {
		DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
		if (datiIstituto != null && datiIstituto.getUoContrAmm() != null){
			if (accountService.isUserSpecialEnableToValidateOrder(principal.getName(), datiIstituto.getUoContrAmm())){
				rimborsoMissione.setValidaAmm("S");
			} else {
				if (Utility.nvl(rimborsoMissione.getValidaAmm(),"S").equals("N") && (!rimborsoMissione.isMissioneDaValidare() && !rimborsoMissione.isMissioneInserita())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Rimborso Missione al controllo amministrativo, conferma non possibile.");
				}
				rimborsoMissione.setValidaAmm("N");
			}
		} else {
			rimborsoMissione.setValidaAmm("S");
		}
	}

	private void validaCRUD(Principal principal, RimborsoMissione rimborsoMissione) {
		if (rimborsoMissione != null){
			controlloCampiObbligatori(rimborsoMissione); 
			controlloCongruenzaDatiInseriti(principal, rimborsoMissione);
			controlloDatiFinanziari(principal, rimborsoMissione);
		}
	}

	private void controlloDatiFinanziari(Principal principal, RimborsoMissione rimborsoMissione) {
		LocalDate data = LocalDate.now();
		int anno = data.getYear();

    	UnitaOrganizzativa uo = unitaOrganizzativaService.loadUo(rimborsoMissione.getUoSpesa(), rimborsoMissione.getCdsSpesa(), anno);
    	if (uo == null){
    		throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La UO "+ rimborsoMissione.getUoSpesa() + " non è corretta rispetto al CDS "+rimborsoMissione.getCdsSpesa());
    	}
		if (!StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
			Cdr cdr = cdrService.loadCdr(rimborsoMissione.getCdrSpesa(), rimborsoMissione.getUoSpesa());
			if (cdr == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Il CDR "+ rimborsoMissione.getCdrSpesa() + " non è corretto rispetto alla UO "+rimborsoMissione.getUoSpesa());
			}
		}
		if (!StringUtils.isEmpty(rimborsoMissione.getPgProgetto())){
			Progetto progetto = progettoService.loadModulo(rimborsoMissione.getPgProgetto(), anno, rimborsoMissione.getUoSpesa());
			if (progetto == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Il modulo indicato non è corretto rispetto alla UO "+rimborsoMissione.getUoSpesa());
			}
		}
		if (!StringUtils.isEmpty(rimborsoMissione.getGae())){
			if (StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile indicare la GAE senza il centro di responsabilità");
			}
			Gae gae = gaeService.loadGae(rimborsoMissione);
			if (gae == null){
				throw new AwesomeException(CodiciErrore.ERRGEN, "La GAE "+ rimborsoMissione.getGae()+" indicata non esiste");
			} else {
				boolean progettoCdrIndicato = false;
				if (!StringUtils.isEmpty(rimborsoMissione.getPgProgetto()) && !StringUtils.isEmpty(gae.getPg_progetto())){
					progettoCdrIndicato = true;
					if (gae.getPg_progetto().compareTo(rimborsoMissione.getPgProgetto()) != 0){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La GAE indicata "+ rimborsoMissione.getGae()+" non corrisponde al progetto indicato.");
					}
				}
				if (!StringUtils.isEmpty(rimborsoMissione.getCdrSpesa())){
					progettoCdrIndicato = true;
					if (!gae.getCd_centro_responsabilita().equals(rimborsoMissione.getCdrSpesa()) ){
						throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": La GAE indicata "+ rimborsoMissione.getGae()+" non corrisponde con il CDR "+rimborsoMissione.getCdrSpesa() +" indicato.");
					}
				}
				if (!progettoCdrIndicato){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Non è possibile indicare solo La GAE senza il modulo o il CDR.");
				}
			}
		}
		
//inizio e fine modifiche multimpegni		if (rimborsoMissione.isToBeCreated()){
			if (!StringUtils.isEmpty(rimborsoMissione.getPgObbligazione())){
				if (StringUtils.isEmpty(rimborsoMissione.getEsercizioOriginaleObbligazione())){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Oltre al numero dell'impegno è necessario indicare anche l'anno dell'impegno");
				}
				if (!StringUtils.isEmpty(rimborsoMissione.getGae())){
					ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoMissione);
					if (impegnoGae == null){
						throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non corrisponde con la GAE "+ rimborsoMissione.getGae()+" indicata oppure non esiste");
					} else {
						if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
							if (!impegnoGae.getCdElementoVoce().equals(rimborsoMissione.getVoce())){
								throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoMissione.getVoce());
							}
// inizio modifiche multimpegni
						} else {
							rimborsoMissione.setVoce(impegnoGae.getCdElementoVoce());
// fine modifiche multimpegni
						}
						rimborsoMissione.setCdCdsObbligazione(impegnoGae.getCdCds());
						rimborsoMissione.setEsercizioObbligazione(impegnoGae.getEsercizio());
					}
				} else {
					Impegno impegno = impegnoService.loadImpegno(rimborsoMissione);
					if (impegno == null){
						throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non esiste");
					} else {
						if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
							if (!impegno.getCdElementoVoce().equals(rimborsoMissione.getVoce())){
								throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoMissione.getEsercizioOriginaleObbligazione() + "-" + rimborsoMissione.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoMissione.getVoce());
							}
						}
						rimborsoMissione.setCdCdsObbligazione(impegno.getCdCds());
						rimborsoMissione.setEsercizioObbligazione(impegno.getEsercizio());
					}
				}
			} else {
				//			if (!StringUtils.isEmpty(rimborsoMissione.getEsercizioOriginaleObbligazione())){
				//				throw new AwesomeException(CodiciErrore.ERRGEN, "Oltre all'anno dell'impegno è necessario indicare anche il numero dell'impegno");
				//			}
				rimborsoMissione.setCdCdsObbligazione(null);
				rimborsoMissione.setEsercizioObbligazione(null);
			}
//inizio modifiche multimpegni		} else {
//			List<RimborsoImpegni> lista = rimborsoImpegniService.getRimborsoImpegni(principal, new Long(rimborsoMissione.getId().toString()));
//			if (lista != null && !lista.isEmpty()){
//				for (RimborsoImpegni rimborsoImpegni : lista){
//					if (!StringUtils.isEmpty(rimborsoMissione.getGae())){
//						ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(rimborsoMissione.getCdsSpesa(), rimborsoMissione.getUoSpesa(), rimborsoImpegni.getEsercizioOriginaleObbligazione(), rimborsoImpegni.getPgObbligazione(), rimborsoMissione.getGae());
//						if (impegnoGae == null){
//							throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non corrisponde con la GAE "+ rimborsoMissione.getGae());
//						} else {
//							if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
//								if (!impegnoGae.getCdElementoVoce().equals(rimborsoMissione.getVoce())){
//									throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoMissione.getVoce());
//								}
//							}
//						}
//					} else {
//						Impegno impegno = impegnoService.loadImpegno(rimborsoMissione.getCdsSpesa(), rimborsoMissione.getUoSpesa(), rimborsoImpegni.getEsercizioOriginaleObbligazione(), rimborsoImpegni.getPgObbligazione());
//						if (impegno == null){
//							throw new AwesomeException(CodiciErrore.ERRGEN, "L'impegno indicato "+ rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non esiste");
//						} else {
//							if (!StringUtils.isEmpty(rimborsoMissione.getVoce())){
//								if (!impegno.getCdElementoVoce().equals(rimborsoMissione.getVoce())){
//									throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": L'impegno indicato "+rimborsoImpegni.getEsercizioOriginaleObbligazione() + "-" + rimborsoImpegni.getPgObbligazione() +" non corrisponde con la voce di Bilancio indicata."+rimborsoMissione.getVoce());
//								}
//							}
//						}
//					}
//				}
//			}
//fine modifiche multimpegni		}
    	if (rimborsoMissione.getOrdineMissione() != null){
        	OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
        	if (ordineMissione != null){
        		StringBuilder buffer = new StringBuilder();
        		aggiungiDifferenzaOrdineRimborsoDatiFin(principal, rimborsoMissione, buffer, ordineMissione);
        		if (buffer.length() > 0 && StringUtils.isEmpty(rimborsoMissione.getNote())){
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Se si cambiano i dati finanziari è necessario indicarne nel campo Note il motivo.");
        		}
        	}
    	}
    }
	
    private void controlloCongruenzaDatiInseriti(Principal principal, RimborsoMissione rimborsoMissione) {
		if (rimborsoMissione.getDataFineMissione().isBefore(rimborsoMissione.getDataInizioMissione())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": La data di fine missione non può essere precedente alla data di inizio missione");
		}
		if (DateUtils.getDateAsString(rimborsoMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC).equals(DateUtils.getDateAsString(rimborsoMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC))){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": Le date di inizio e fine missione non possono essere uguali");
		}
		if (!StringUtils.isEmpty(rimborsoMissione.getNoteUtilizzoTaxiNoleggio())){
			if (rimborsoMissione.getUtilizzoTaxi().equals("N") && rimborsoMissione.getUtilizzoAutoNoleggio().equals("N") && rimborsoMissione.getUtilizzoAutoServizio().equals("N") && rimborsoMissione.getPersonaleAlSeguito().equals("N")){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": Non è possibile indicare le note all'utilizzo del taxi o dell'auto a noleggio o dell'auto di servizio o del personale al seguito se non si è scelto il loro utilizzo");
			}
		}
		if ((Utility.nvl(rimborsoMissione.getUtilizzoAutoNoleggio()).equals("S") || Utility.nvl(rimborsoMissione.getUtilizzoAutoServizio()).equals("S") || Utility.nvl(rimborsoMissione.getPersonaleAlSeguito()).equals("S") || Utility.nvl(rimborsoMissione.getUtilizzoTaxi()).equals("S")) && StringUtils.isEmpty(rimborsoMissione.getNoteUtilizzoTaxiNoleggio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI+": E' obbligatorio indicare le note all'utilizzo del taxi o dell'auto a noleggio o dell'auto di servizio o del personale al seguito se si è scelto il loro utilizzo");
		}
		if (rimborsoMissione.isMissioneEstera() && rimborsoMissione.isTrattamentoAlternativoMissione()) {
			if (rimborsoMissione.isAssociato()){
				throw new AwesomeException(CodiciErrore.ERRGEN,CodiciErrore.DATI_INCONGRUENTI+": Per gli associati non è previsto il trattamento alternativo di missione.");
			}
			
			if (rimborsoMissione.getDataInizioEstero().isBefore(rimborsoMissione.getDataInizioMissione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": La Data Imbarco di Partenza non può essere precedente alla data di inizio missione");
			}
			if (rimborsoMissione.getDataFineMissione().isBefore(rimborsoMissione.getDataFineEstero())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": La Data Sbarco di Ritorno non può essere successiva alla data di fine missione");
			}
			if (rimborsoMissione.getDataFineEstero().isBefore(rimborsoMissione.getDataInizioEstero())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI+": La Data Sbarco di Ritorno non può essere precedente alla Data Imbarco di Partenza ");
			}
		}
		if ((!StringUtils.isEmpty(rimborsoMissione.getAnticipoImporto()) ||  Utility.nvl(rimborsoMissione.getAnticipoRicevuto()).equals("S") ||
				!StringUtils.isEmpty(rimborsoMissione.getAnticipoAnnoMandato()) ||  !StringUtils.isEmpty(rimborsoMissione.getAnticipoNumeroMandato())) &&
				(StringUtils.isEmpty(rimborsoMissione.getAnticipoImporto()) ||  Utility.nvl(rimborsoMissione.getAnticipoRicevuto(),"N").equals("N") ||
						StringUtils.isEmpty(rimborsoMissione.getAnticipoAnnoMandato()) ||  StringUtils.isEmpty(rimborsoMissione.getAnticipoNumeroMandato())) ){
			throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare tutti i campi dell'anticipo ricevuto e del mandato, oppure nessuno.");
		} 
		if (StringUtils.isEmpty(rimborsoMissione.getIdFlusso()) &&  rimborsoMissione.isStatoInviatoAlFlusso()){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile avere lo stato Inviato al flusso e non avere l'ID del flusso");
		} 
		if (!rimborsoMissione.isMissioneEstera()){
			rimborsoMissione.setNazione(new Long("1"));
		}
		if (!StringUtils.hasLength(rimborsoMissione.getMatricola())){
			rimborsoMissione.setMatricola(null);
		}
		OrdineMissione ordine = rimborsoMissione.getOrdineMissione();
		if (ordine != null){
			if (ordine.getCdsRich() != null){
				ordine = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, ordine.getId());
			}
			if (rimborsoMissione.isTrattamentoAlternativoMissione()){
				RimborsoMissioneFilter filter = new RimborsoMissioneFilter();
				filter.setIdOrdineMissione(new Long(ordine.getId().toString()));
				List<RimborsoMissione> rimborsi = getRimborsiMissione(principal, filter, false);
				if (rimborsi != null && !rimborsi.isEmpty()){
					for (RimborsoMissione rimb : rimborsi){
						if (rimb.isTrattamentoAlternativoMissione()){
							if (rimborsoMissione.getId() == null || rimb.getId().toString().compareTo(rimborsoMissione.getId().toString()) != 0){
								throw new AwesomeException(CodiciErrore.ERRGEN, "E' stato già inserito un rimborso missione con la richiesta di trattamento alternativo di missione. Cambiare il trattamento.");
							}
						}
					}
				}
				long oreDifferenza = ChronoUnit.HOURS.between(rimborsoMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES), rimborsoMissione.getDataFineMissione().truncatedTo(ChronoUnit.MINUTES));
				if (oreDifferenza < 24 ){
					throw new AwesomeException(CodiciErrore.ERRGEN, "Per il trattamento alternativo di missione è necessario avere una durata non inferiore a 24 ore.");
				}
			}
			if (Utility.nvl(ordine.getPresidente(),"N").equals("S")){
				if (!Utility.nvl(rimborsoMissione.getPresidente(),"N").equals("S")){
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'ordine di missione è per la presidenza, quindi anche il rimborso missione deve essere per la presidenza.");
				}
			}
		}
	}

	private void controlloCampiObbligatori(RimborsoMissione rimborsoMissione) {
		if (!rimborsoMissione.isToBeCreated()){
			controlloDatiObbligatoriDaGUI(rimborsoMissione);
			DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
			bloccoInserimentoRimborsi(rimborsoMissione, datiIstituto);
		}
		if (rimborsoMissione.getCdTerzoSigla() == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stato recuperato il codice terzo SIGLA, verificare il rimborso.");
		}
		if (StringUtils.isEmpty(rimborsoMissione.getAnno())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anno");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUid())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utente");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoTaxi())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo del Taxi");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoAutoServizio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo dell'auto di servizio");
		} else if (StringUtils.isEmpty(rimborsoMissione.getPersonaleAlSeguito())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Personale al seguito");
		} else if (StringUtils.isEmpty(rimborsoMissione.getUtilizzoAutoNoleggio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo auto a noleggio");
		} else if (StringUtils.isEmpty(rimborsoMissione.getStato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Stato");
		} else if (StringUtils.isEmpty(rimborsoMissione.getValidato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Validato");
		} else if (StringUtils.isEmpty(rimborsoMissione.getNumero())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Numero");
		}
	}

   	public Map<String, byte[]> printRimborsoMissione(Authentication auth, Long idMissione) throws ComponentException {
    	Principal principal = (Principal)auth;
    	RimborsoMissione rimborsoMissione = getRimborsoMissione(principal, idMissione, true);
    	byte[] printRimborsoMissione = null;
    	String fileName = null;
    	if ((rimborsoMissione.isStatoInviatoAlFlusso()  && !rimborsoMissione.isMissioneInserita() && !rimborsoMissione.isMissioneDaValidare()) || (rimborsoMissione.isStatoFlussoApprovato())){
    		return cmisRimborsoMissioneService.getFileRimborsoMissione(rimborsoMissione);
    	} else {
    		return stampaRimborso(principal, rimborsoMissione);
    	}
    }

	public Map<String, byte[]> stampaRimborso(Principal principal, RimborsoMissione rimborsoMissione)
			throws ComponentException {
		byte[] printRimborsoMissione;
		String fileName;
		retrieveDetails(principal, rimborsoMissione);
		if (assenzaDettagli(rimborsoMissione, false) && (Utility.nvl(rimborsoMissione.getRimborso0(),"N").equals("N"))){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile stampare un rimborso missione senza aver indicato nessun dettaglio di spesa.");
		}
		if (!assenzaDettagli(rimborsoMissione, false) && (Utility.nvl(rimborsoMissione.getRimborso0(),"N").equals("S"))){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile stampare un rimborso missione con dettagli e ad importo 0.");
		}
		fileName = "RimborsoMissione"+rimborsoMissione.getId()+".pdf";
		printRimborsoMissione = printRimborsoMissioneService.printRimborsoMissione(rimborsoMissione, principal.getName());
		if (rimborsoMissione.isMissioneInserita()){
			cmisRimborsoMissioneService.salvaStampaRimborsoMissioneSuCMIS(principal, printRimborsoMissione, rimborsoMissione);
		}
		Map<String, byte[]> map = new HashMap<String, byte[]>();
		map.put(fileName, printRimborsoMissione);
		return map;
	}

	public List<CMISFileAttachment> getAttachments(Principal principal, Long idRimborsoMissione)
			throws ComponentException {
		if (idRimborsoMissione != null) {
			RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
			if (rimborsoMissione != null){
				List<CMISFileAttachment> lista = cmisRimborsoMissioneService.getAttachmentsRimborsoMissione(rimborsoMissione, idRimborsoMissione);
				return lista;
			}
		}
		return null;
	}

	public CMISFileAttachment uploadAllegato(Principal principal, Long idRimborsoMissione,
			InputStream inputStream, String name, MimeTypes mimeTypes) throws ComponentException {
		RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
		if (rimborsoMissione != null) {
			return cmisRimborsoMissioneService.uploadAttachmentRimborsoMissione(principal, rimborsoMissione,idRimborsoMissione,
					inputStream, name, mimeTypes);
		}
		return null;
	}

	public String getDifferenzeRimborsoOrdine(Principal principal, RimborsoMissione rimborso) throws ComponentException{
		StringBuilder buffer = new StringBuilder();
		OrdineMissione ordine = rimborso.getOrdineMissione();
		if (ordine.getCdsRich() != null){
			ordine = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, ordine.getId());
		}
		if (isDiverso(rimborso.getCdsCompetenza(), ordine.getCdsCompetenza())){
			aggiungiDifferenza(buffer, "CDS Competenza. ", null);
		}
		if (isDiverso(rimborso.getUoCompetenza(), ordine.getUoCompetenza())){
			aggiungiDifferenza(buffer, "UO Competenza. ", null);
		}
		if (isDiverso(rimborso.getOggetto(), ordine.getOggetto())){
			aggiungiDifferenza(buffer, "Oggetto. ", null);
		}
		if (isDiverso(rimborso.getDestinazione(), ordine.getDestinazione())){
			aggiungiDifferenza(buffer, "Destinazione. ", null);
		}
		if (isDiverso(rimborso.getTipoMissione(), ordine.getTipoMissione())){
			aggiungiDifferenza(buffer, "Tipo Missione. ", null);
		}
		if (isDiverso(rimborso.getNazione(), ordine.getNazione())){
			Nazione nazione;
			try {
				nazione = nazioneService.loadNazione(rimborso.getNazione());
				if (nazione != null){
					aggiungiDifferenza(buffer, "Nazione. ", null);
				}
			} catch (Exception e) {
				throw new ComponentException("Errore durante il recupero dei dati della Nazione", e); 
			}
		}
		if (isDiverso(rimborso.getTrattamento(), ordine.getTrattamento())){
			aggiungiDifferenza(buffer, "Trattamento. ", null);
		}
		if (isDiverso(rimborso.getUtilizzoTaxi(), ordine.getUtilizzoTaxi())){
			aggiungiDifferenza(buffer, "Utilizzo Taxi. ", null);
		}
		if (isDiverso(rimborso.getUtilizzoAutoServizio(), ordine.getUtilizzoAutoServizio())){
			aggiungiDifferenza(buffer, "Utilizzo Auto Servizio. ", null);
		}
		if (isDiverso(rimborso.getPersonaleAlSeguito(), ordine.getPersonaleAlSeguito())){
			aggiungiDifferenza(buffer, "Personale Al Seguito. ", null);
		}
		if (isDiverso(rimborso.getUtilizzoAutoNoleggio(), ordine.getUtilizzoAutoNoleggio())){
			aggiungiDifferenza(buffer, "Utilizzo Auto Noleggio. ", null);
		}
		if (isDiverso(rimborso.getNoteUtilizzoTaxiNoleggio(), ordine.getNoteUtilizzoTaxiNoleggio())){
			aggiungiDifferenza(buffer, "Note Utilizzo Taxi-Noleggio. ", null);
		}
		if (isDiverso(rimborso.getDataInizioMissione(), ordine.getDataInizioMissione())){
			aggiungiDifferenza(buffer, "Data Inizio Missione. ", null);
		}
		if (isDiverso(rimborso.getDataFineMissione(), ordine.getDataFineMissione())){
			aggiungiDifferenza(buffer, "Data Fine Missione. ", null);
		}
		
		aggiungiDifferenzaOrdineRimborsoDatiFin(principal,rimborso, buffer, ordine);
		try {
			OrdineMissioneAnticipo anticipo = ordineMissioneService.getAnticipo(principal, ordine);
			String anticipoOrdine = "N";
			BigDecimal importoAnticipoOrdine = BigDecimal.ZERO;
			if (anticipo != null){
				anticipoOrdine = "S";
				importoAnticipoOrdine = anticipo.getImporto();
			}
			
			if (isDiverso(Utility.nvl(rimborso.getAnticipoRicevuto(),"N"), anticipoOrdine)){
				if (anticipoOrdine.equals("S")){
					aggiungiDifferenza(buffer, "Anticipo: ", "Autorizzato in fase d'ordine ma non indicato in fase di rimborso. ");
				} else {
					aggiungiDifferenza(buffer, "Anticipo: ", "Non autorizzato in fase d'ordine. ");
				}
			}
			if (isDiverso(Utility.nvl(rimborso.getAnticipoImporto()), importoAnticipoOrdine)){
				if (rimborso.getAnticipoImporto() != null){
					aggiungiDifferenza(buffer, "Importo Anticipo. ", null);
				} else {
					aggiungiDifferenza(buffer, "Importo Anticipo. ", "Non valorizzato");
				}
			}
		} catch (ComponentException e) {
			throw new ComponentException("Errore durante il recupero dei dati dell'anticipo", e); 
		}
		try {
			OrdineMissioneAutoPropria autoPropria = ordineMissioneService.getAutoPropria(ordine);
			String autoPropriaOrdine = "N";
			String autoPropriaRimborso = "N";
			if (autoPropria != null && Utility.nvl(autoPropria.getUtilizzoMotiviIspettivi(),"N").equals("S")){
				autoPropriaOrdine = "S";
			}
			
			if (rimborso.getRimborsoMissioneDettagli() != null && !rimborso.getRimborsoMissioneDettagli().isEmpty()){
				for (RimborsoMissioneDettagli dettaglio : rimborso.getRimborsoMissioneDettagli()){
					if (dettaglio.isDettaglioIndennitaKm()){
						autoPropriaRimborso = "S";
					}
				}
			}
			
			if (isDiverso(autoPropriaRimborso, autoPropriaOrdine)){
				if (autoPropriaRimborso.equals("S")){
					aggiungiDifferenza(buffer, "Rimborso KM: ", "Non autorizzato in fase d'ordine");
				} else {
					aggiungiDifferenza(buffer, "Rimborso KM: ", "Autorizzato in fase d'ordine ma non utilizzato in fase di rimborso");
				}
			}
		} catch (ComponentException e) {
			throw new ComponentException("Errore durante il recupero dei dati dell'auto propria", e); 
		}
		return buffer.toString();
	}

	private void aggiungiDifferenzaOrdineRimborsoDatiFin(Principal principal, RimborsoMissione rimborso, StringBuilder buffer,
			OrdineMissione ordine) {
		LocalDate data = LocalDate.now();
		int anno = data.getYear();

		if (isDiverso(rimborso.getUoSpesa(), ordine.getUoSpesa())){
			aggiungiDifferenza(buffer, "UO Spesa. ", null);
		}
		if (isDiverso(rimborso.getCdrSpesa(), ordine.getCdrSpesa())){
			aggiungiDifferenza(buffer, "CDR Spesa. ", null);
		}
		if (isDiverso(rimborso.getVoce(), ordine.getVoce())){
			aggiungiDifferenza(buffer, "Voce. ", null);
		}
		if (isDiverso(rimborso.getGae(), ordine.getGae())){
			aggiungiDifferenza(buffer, "GAE. ", null);
		}
		if (isDiverso(rimborso.getPgProgetto(), ordine.getPgProgetto())){
			Progetto progetto = progettoService.loadModulo(rimborso.getPgProgetto(), anno, rimborso.getUoSpesa());
			if (progetto != null){
				aggiungiDifferenza(buffer, "Progetto. ", null);
			}
		}

		if (rimborso.isToBeCreated()){
			if (isDiverso(rimborso.getEsercizioOriginaleObbligazione(), ordine.getEsercizioOriginaleObbligazione())){
				aggiungiDifferenza(buffer, "Anno Impegno. ", null);
			}
			if (isDiverso(rimborso.getPgObbligazione(), ordine.getPgObbligazione())){
				aggiungiDifferenza(buffer, "Numero Impegno. ", null);
			}
		} else {
			List<RimborsoImpegni> lista = rimborsoImpegniService.getRimborsoImpegni(principal, new Long(rimborso.getId().toString()));
			if (lista != null && !lista.isEmpty()){
				for (RimborsoImpegni rimborsoImpegni : lista){
					if (isDiverso(rimborsoImpegni.getEsercizioOriginaleObbligazione(), ordine.getEsercizioOriginaleObbligazione())){
						aggiungiDifferenza(buffer, "Anno Impegno. ", null);
					}
					if (isDiverso(rimborsoImpegni.getPgObbligazione(), ordine.getPgObbligazione())){
						aggiungiDifferenza(buffer, "Numero Impegno. ", null);
					}
				}
			}
		}
	}
	private Boolean isDiverso(Object obj1, Object obj2){
		if (obj1 == null && obj2 == null){
			return false;
		} else if ((obj1 != null && obj2 == null) || 
				(obj1 == null)){
			return true;
		} 
		if (obj1 instanceof String){
			String str1 = (String)obj1; 
			String str2 = (String)obj2;
			if (!str1.equals(str2)){
				return true;
			}
		} else if (obj1 instanceof ZonedDateTime){
			ZonedDateTime zd1 = (ZonedDateTime)obj1; 
			ZonedDateTime zd2 = (ZonedDateTime)obj2;
			String str1 = DateUtils.getDateAsString(zd1, DateUtils.PATTERN_DATETIME_FOR_DOCUMENTALE);
			String str2 = DateUtils.getDateAsString(zd2, DateUtils.PATTERN_DATETIME_FOR_DOCUMENTALE);
			if (!str1.equals(str2)){
				return true;
			}
		} else if (obj1 instanceof LocalDate){
			LocalDate str1 = (LocalDate)obj1; 
			LocalDate str2 = (LocalDate)obj2;
			if (str1.compareTo(str2) != 0){
				return true;
			}
		} else if (obj1 instanceof Long){
			Long str1 = (Long)obj1; 
			Long str2 = (Long)obj2;
			if (str1.compareTo(str2) != 0){
				return true;
			}
		} else if (obj1 instanceof Integer){
			Integer str1 = (Integer)obj1; 
			Integer str2 = (Integer)obj2;
			if (str1.compareTo(str2) != 0){
				return true;
			}
		}
		return false;
	}
	private StringBuilder aggiungiDifferenza(StringBuilder buffer, String label, String value){
		if (buffer.length() > 0){
			buffer.append(" - ");
		}
		buffer.append(label+Utility.nvl(value));
		return buffer;
	}
	public Boolean isMissioneComunicabileSigla(Principal principal, RimborsoMissione rimborsoMissione){
    	if (rimborsoMissione.isMissioneDaComunicareSigla()){
			LocalDate data = LocalDate.now();
			if (data.getYear() == rimborsoMissione.getAnno()){
				DatiIstituto dati = datiIstitutoService.getDatiIstituto(rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno());
				if (dati == null){
					return false;
				}
				if (rimborsoMissione.isTrattamentoAlternativoMissione()){
					if (dati.getDataBloccoRimborsiTam() != null){
						if (dati.getDataBloccoRimborsiTam().compareTo(data) < 0){
							ribaltaMissione(principal, rimborsoMissione, data.getYear() + 1);
							return false;
						}
					}
				}
				if (dati.getDataBloccoRimborsi() != null){
					if (dati.getDataBloccoRimborsi().compareTo(data) < 0){
						ribaltaMissione(principal, rimborsoMissione, data.getYear() + 1);
						return false;
					}
					return true;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}
    	return false;
	}

	protected void ribaltaMissione(Principal principal, RimborsoMissione rimborsoMissione, Integer anno) {
		retrieveDetails(principal, rimborsoMissione);
		if (rimborsoMissione.isTrattamentoAlternativoMissione() || rimborsoMissione.getTotaleRimborsoSenzaSpeseAnticipate().compareTo(BigDecimal.ZERO) > 0){
			rimborsoMissione.setAnno(anno);
			rimborsoMissione.setNumero(datiIstitutoService.getNextPG(principal, rimborsoMissione.getUoSpesa(), rimborsoMissione.getAnno(), Costanti.TIPO_RIMBORSO_MISSIONE));
			rimborsoMissione.setToBeUpdated();
	    	crudServiceBean.modificaConBulk(principal, rimborsoMissione);
		}
	}
	private String getTextMailApprovazioneRimborso(RimborsoMissione rimborsoMissione) {
		return "Il rimborso missione "+rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+getNominativo(rimborsoMissione.getUid())+" per la missione a "+rimborsoMissione.getDestinazione() + " dal "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataInizioMissione())+ " al "+DateUtils.getDefaultDateAsString(rimborsoMissione.getDataFineMissione())+ " avente per oggetto "+rimborsoMissione.getOggetto()+" è stata approvata.";
	}
	public void gestioneCancellazioneAllegati(Principal principal, String idNodo, Long idRimborsoMissione){
		if (idRimborsoMissione != null) {
			RimborsoMissione rimborsoMissione = (RimborsoMissione) crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
			controlloAllegatoDettaglioModificabile(rimborsoMissione);
			if (rimborsoMissione != null && StringUtils.hasLength(rimborsoMissione.getIdFlusso())){
				StorageObject storage = cmisRimborsoMissioneService.recuperoFolderRimborsoMissione(rimborsoMissione);
				missioniCMISService.eliminaFilePresenteNelFlusso(principal, idNodo, storage);
			} else {
        		missioniCMISService.deleteNode(idNodo);
			}
		}
	}
	
	public void controlloAllegatoDettaglioModificabile(RimborsoMissione rimborso) {
		DatiIstituto dati = datiIstitutoService.getDatiIstituto(rimborso.getUoSpesa(), rimborso.getAnno());
		Boolean controlloEsistenzaAllegati = Utility.nvl(dati.getObbligoAllegatiValidazione(),"S").equals("S") || !rimborso.isMissioneDaValidare();
		if (controlloEsistenzaAllegati){
			controlloOperazioniCRUDDaGui(rimborso);
		}
	}

	public void popolaCoda(String id){
		RimborsoMissione missione = (RimborsoMissione)crudServiceBean.findById(new GenericPrincipal("app.missioni"), RimborsoMissione.class, new Long(id));
		popolaCoda(missione);
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public RimborsoMissione aggiornaRimborsoMissione(Principal principal, RimborsoMissione rimborsoMissioneDaAggiornare, FlowResult flowResult) {
		try {
			if (rimborsoMissioneDaAggiornare != null){
				if (rimborsoMissioneDaAggiornare.isStatoInviatoAlFlusso() && rimborsoMissioneDaAggiornare.isMissioneConfermata() &&
						!rimborsoMissioneDaAggiornare.isMissioneDaValidare())	{
					switch (flowResult.getStato() ) {
						case FlowResult.ESITO_FLUSSO_FIRMATO:
							RimborsoMissione rimborsoMissione = aggiornaRimborsoMissioneFirmato(principal, rimborsoMissioneDaAggiornare);
							if (isMissioneComunicabileSigla(principal, rimborsoMissione)){
								return rimborsoMissione;
							}
							break;
						case FlowResult.ESITO_FLUSSO_FIRMA_UO:
							aggiornaRimborsoMissionePrimaFirma(principal, rimborsoMissioneDaAggiornare);
							break;
						case FlowResult.ESITO_FLUSSO_RESPINTO_UO:
							aggiornaRimborsoMissioneRespinto(principal, flowResult, rimborsoMissioneDaAggiornare);
							break;
						case FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA:
							aggiornaRimborsoMissioneRespinto(principal, flowResult, rimborsoMissioneDaAggiornare);
							break;
					}
				} else {
					erroreRimborsoMissione(rimborsoMissioneDaAggiornare, flowResult);
				}
			}
		return null;
		} catch (Exception e ){
//			mailService.sendEmailError(subjectErrorFlowsRimborso, "Errore in aggiornaRimborsoMissione: "+e.getMessage(), false, true);
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in aggiornaRimborsoMissione:" + Utility.getMessageException(e));
		}
	}


	private void erroreRimborsoMissione(RimborsoMissione rimborsoMissioneDaAggiornare, FlowResult flowResult) {
		String errore = "Esito flusso non corrispondente con lo stato del rimborso.";
		String testoErrore = getTextErrorRimborso(rimborsoMissioneDaAggiornare, flowResult, errore);
		throw new AwesomeException(CodiciErrore.ERRGEN, errore + " "+testoErrore);
	}

	private String getTextErrorRimborso(RimborsoMissione rimborsoMissione, FlowResult flow, String error) {
		return textErrorFlowsRimborso+getTextErrorRimborsoMissione(rimborsoMissione, flow, error);
	}

	private String getTextErrorRimborsoMissione(RimborsoMissione rimborsoMissione, FlowResult flow, String error){
		return " con id "+rimborsoMissione.getId()+ " "+ rimborsoMissione.getAnno()+"-"+rimborsoMissione.getNumero()+ " di "+ rimborsoMissione.getDatoreLavoroRich()+" collegato al flusso "+flow.getProcessInstanceId()+" con esito "+flow.getStato()+" è andata in errore per il seguente motivo: " + error;
	}
/*
	private void addToZip(DocumentiContabiliService documentiContabiliService, ZipOutputStream zos, String path, StatoTrasmissione statoTrasmissione) {
		documentiContabiliService.getChildren(documentiContabiliService.getStorageObjectByPath(path).getKey())
				.stream()
				.forEach(storageObject -> {
					try {
						if (!Optional.ofNullable(storageObject.getPropertyValue(StoragePropertyNames.BASE_TYPE_ID.value()))
								.map(String.class::cast)
								.filter(s -> s.equals(StoragePropertyNames.CMIS_FOLDER.value()))
								.isPresent()) {
							ZipEntry zipEntryChild = new ZipEntry(statoTrasmissione.getCMISFolderName()
									.concat(
											Optional.ofNullable(storageObject.getPath())
													.map(s -> s.substring(statoTrasmissione.getStorePath().length()))
													.orElse(StorageDriver.SUFFIX)
									));
							zos.putNextEntry(zipEntryChild);
							IOUtils.copyLarge(documentiContabiliService.getResource(storageObject), zos);
						} else {
							addToZip(documentiContabiliService, zos, storageObject.getPath(), statoTrasmissione);
						}
					} catch (IOException e) {
						throw new DetailedRuntimeException(e);
					}
				});
	}

*/
	public List<StorageObject> getAllDocumentsMissione(Principal principal, RimborsoMissione rimborsoMissione){
		List<StorageObject> listaDocumentiRimborso = cmisRimborsoMissioneService.getDocumentsRimborsoMissione(rimborsoMissione);
		if (listaDocumentiRimborso != null && !listaDocumentiRimborso.isEmpty()){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, rimborsoMissione.getOrdineMissione().getId());
			listaDocumentiRimborso.addAll(ordineMissioneService.getDocumentsOrdineMissione(ordineMissione));
			return listaDocumentiRimborso;
		}
		return null;
	}
	public InputStream getResource(StorageObject so){
		return cmisRimborsoMissioneService.getResource(so);
	}
}

