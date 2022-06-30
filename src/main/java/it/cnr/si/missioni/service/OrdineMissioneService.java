package it.cnr.si.missioni.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import javax.persistence.OptimisticLockException;

import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.domain.custom.persistence.*;
import it.cnr.si.service.SecurityService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.OrdineMissioneAutoPropriaRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.Cdr;
import it.cnr.si.missioni.util.proxy.json.object.Gae;
import it.cnr.si.missioni.util.proxy.json.object.Impegno;
import it.cnr.si.missioni.util.proxy.json.object.ImpegnoGae;
import it.cnr.si.missioni.util.proxy.json.object.Progetto;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompenso;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompensoJson;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.missioni.util.proxy.json.service.CdrService;
import it.cnr.si.missioni.util.proxy.json.service.GaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoGaeService;
import it.cnr.si.missioni.util.proxy.json.service.ImpegnoService;
import it.cnr.si.missioni.util.proxy.json.service.ProgettoService;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
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
	SecurityService securityService;

	@Autowired
	private Environment env;

	@Autowired
	private DatiSedeService datiSedeService;

    @Autowired
    private TerzoPerCompensoService terzoPerCompensoService;
    
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
	private ParametriService parametriService;

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

	@Autowired(required = false)
	private MailService mailService;

	@Autowired
	private CRUDComponentSession crudServiceBean;

	@Autowired(required = false)
	private RabbitMQService rabbitMQService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private MissioniCMISService missioniCMISService;

	@Autowired
	private MissioneRespintaService missioneRespintaService;

	@Value("${spring.mail.messages.invioResponsabileGruppo.oggetto}")
	private String subjectSendToManagerOrdine;

	@Value("${spring.mail.messages.invioOrdinePerValidazioneDatiFinanziari.oggetto}")
	private String subjectSendToAdministrative;

	@Value("${spring.mail.messages.ritornoOrdineMissioneMittente.oggetto}")
	private String subjectReturnToSenderOrdine;

	@Value("${spring.mail.messages.approvazioneAnticipo.oggetto}")
	private String subjectAnticipo;

	@Value("${spring.mail.messages.approvazioneOrdineMissione.oggetto}")
	private String approvazioneOrdineMissione;

	@Value("${spring.mail.messages.approvazioneAnnullamentoOrdineMissione.oggetto}")
	private String approvazioneAnnullamentoOrdineMissione;

	@Value("${spring.mail.messages.erroreLetturaFlussoOrdine.oggetto}")
	private String subjectErrorFlowsOrdine;

	@Value("${spring.mail.messages.erroreLetturaFlussoOrdine.testo}")
	private String textErrorFlowsOrdine;

	@Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.oggetto}")
	private String subjectErrorComunicazioneRimborso;

	@Value("${spring.mail.messages.erroreComunicazioneRimborsoSigla.testo}")
	private String textErrorComunicazioneRimborso;

	@Value("${spring.mail.messages.erroreGenerico.oggetto}")
	private String subjectGenericError;


	public Boolean isUserEnabledToViewMissione(OrdineMissione ordineMissione){
		if (ordineMissione.getUid().equals(securityService.getCurrentUserLogin()) || (ordineMissione.getResponsabileGruppo() != null && ordineMissione.getResponsabileGruppo().equals(securityService.getCurrentUserLogin()))
				|| accountService.isUserEnableToWorkUo(ordineMissione.getUoRich()) || accountService.isUserEnableToWorkUo(ordineMissione.getUoSpesa())){
			return true;
		}
		return false;
	}

	public OrdineMissione getOrdineMissione(Long idMissione, Boolean retrieveDataFromFlows)
			throws ComponentException {
		MissioneFilter filter = new MissioneFilter();
		filter.setDaId(idMissione);
		filter.setaId(idMissione);
		OrdineMissione ordineMissione = null;
		List<OrdineMissione> listaOrdiniMissione = getOrdiniMissione(filter, false, true);
		if (listaOrdiniMissione != null && !listaOrdiniMissione.isEmpty()) {
			ordineMissione = listaOrdiniMissione.get(0);
			if (retrieveDataFromFlows) {
				OrdineMissioneAutoPropria autoPropria = getAutoPropria(ordineMissione);
				if (autoPropria != null) {
					ordineMissione.setUtilizzoAutoPropria("S");
				}
				OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissione);
				if (anticipo != null) {
					ordineMissione.setRichiestaAnticipo("S");
				}
			}

		}
		return ordineMissione;
	}

	public OrdineMissione getOrdineMissione(Long idMissione) throws ComponentException {
		return getOrdineMissione(idMissione, false);
	}

	public Map<String, byte[]> printOrdineMissione(Long idMissione) throws ComponentException {
		String username = securityService.getCurrentUserLogin();
		OrdineMissione ordineMissione = getOrdineMissione(idMissione);
		if (ordineMissione != null){
			Map<String, byte[]> map = new HashMap<String, byte[]>();
			byte[] printOrdineMissione = null;
			String fileName = null;
			if ((ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneInserita()
					&& !ordineMissione.isMissioneDaValidare()) || (ordineMissione.isStatoFlussoApprovato())) {
				StorageObject storage = null;
				try {
					storage = cmisOrdineMissioneService.getStorageObjectOrdineMissione(ordineMissione);
				} catch (ComponentException e1) {
					throw new ComponentException("Errore nel recupero del contenuto del file sul documentale ("
							+ Utility.getMessageException(e1) + ")", e1);
				}
				if (storage != null) {
					fileName = storage.getPropertyValue(StoragePropertyNames.NAME.value());
					InputStream is = null;
					try {
						is = missioniCMISService.getResource(storage);
					} catch (Exception e) {
						throw new ComponentException("Errore nel recupero dello stream del file sul documentale ("
								+ Utility.getMessageException(e) + ")", e);
					}
					if (is != null) {
						try {
							printOrdineMissione = IOUtils.toByteArray(is);
							is.close();
						} catch (IOException e) {
							throw new ComponentException("Errore nella conversione dello stream in byte del file ("
									+ Utility.getMessageException(e) + ")", e);
						}
					}
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"Errore nel recupero del contenuto del file sul documentale");
				}
				map.put(fileName, printOrdineMissione);
			} else {
				fileName = "OrdineMissione" + idMissione + ".pdf";
				printOrdineMissione = printOrdineMissioneService.printOrdineMissione(ordineMissione, username);
				if (ordineMissione.isMissioneInserita()) {
					cmisOrdineMissioneService.salvaStampaOrdineMissioneSuCMIS(printOrdineMissione,
							ordineMissione);
				}
				map.put(fileName, printOrdineMissione);
			}
			return map;
		}
		return null;
	}

	private boolean isDevProfile() {
		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
			return true;
		}
		return false;
	}

	public String jsonForPrintOrdineMissione(Long idMissione) throws ComponentException {
		OrdineMissione ordineMissione = getOrdineMissione(idMissione);
		return printOrdineMissioneService.createJsonPrintOrdineMissione(ordineMissione, securityService.getCurrentUserLogin());
	}

	public List<OrdineMissione> getOrdiniMissioneForValidateFlows(MissioneFilter filter,
			Boolean isServiceRest) throws ComponentException {
		List<OrdineMissione> lista = getOrdiniMissione(filter, isServiceRest, true);
		if (lista != null) {
			List<OrdineMissione> listaNew = new ArrayList<OrdineMissione>();
			for (OrdineMissione ordineMissione : lista) {
				if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()) {
					ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_AUTORIZZARE_PER_HOME);
					listaNew.add(ordineMissione);
				} else {
					if (ordineMissione.isMissioneDaValidare() && ordineMissione.isMissioneConfermata()) {
						ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_VALIDARE_PER_HOME);
						listaNew.add(ordineMissione);
					} else if (ordineMissione.isMissioneInserita()) {
						ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_DA_CONFERMARE_PER_HOME);
						listaNew.add(ordineMissione);
					} else if (ordineMissione.isMissioneInviataResponsabile()) {
						ordineMissione.setStatoFlussoRitornoHome(Costanti.STATO_PER_RESPONSABILE_GRUPPO_PER_HOME);
						listaNew.add(ordineMissione);
					}
				}
			}
			return listaNew;
		}
		return lista;
	}

	public void aggiornaOrdineMissioneRespinto(FlowResult result,
			OrdineMissione ordineMissioneDaAggiornare) throws ComponentException {
		aggiornaValidazione(ordineMissioneDaAggiornare);
		ordineMissioneDaAggiornare.setCommentoFlusso(result.getCommento() == null ? null : (result.getCommento().length() > 1000 ? result.getCommento().substring(0, 1000) : result.getCommento()));
		ordineMissioneDaAggiornare.setStatoFlusso(FlowResult.STATO_FLUSSO_SCRIVANIA_MISSIONI.get(result.getStato()));
		ordineMissioneDaAggiornare.setStato(Costanti.STATO_INSERITO);
		ordineMissioneDaAggiornare.setDataInvioAmministrativo(null);
		ordineMissioneDaAggiornare.setDataInvioFirma(null);
		ordineMissioneDaAggiornare.setDataInvioRespGruppo(null);
		ordineMissioneDaAggiornare.setBypassAmministrativo(null);
		ordineMissioneDaAggiornare.setBypassRespGruppo(null);
		OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissioneDaAggiornare);
		if (anticipo != null) {
			anticipo.setStato(Costanti.STATO_INSERITO);
			ordineMissioneAnticipoService.updateAnticipo(anticipo, false);
		}
		updateOrdineMissione(ordineMissioneDaAggiornare, true);

		missioneRespintaService.inserisciMissioneRespinta(result);
	}

	public OrdineMissioneAnticipo getAnticipo(OrdineMissione ordineMissioneDaAggiornare) throws ComponentException {
		return ordineMissioneAnticipoService.getAnticipo(ordineMissioneDaAggiornare, false);
	}

	public void aggiornaOrdineMissioneAnnullato(OrdineMissione ordineMissioneDaAggiornare) {
		ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_ANNULLATO);
		ordineMissioneDaAggiornare.setStato(Costanti.STATO_ANNULLATO);
		updateOrdineMissione(ordineMissioneDaAggiornare, true);
	}

	private String getTextErrorOrdine(OrdineMissione ordineMissione, FlowResult flow, String error) {
		return textErrorFlowsOrdine+getTextErrorOrdineMissione(ordineMissione, flow, error);
	}

	private String getTextErrorOrdineMissione(OrdineMissione ordineMissione, FlowResult flow, String error){
		return " con id "+ordineMissione.getId()+ " "+ ordineMissione.getAnno()+"-"+ordineMissione.getNumero()+ " di "+ ordineMissione.getDatoreLavoroRich()+" collegato al flusso "+flow.getProcessInstanceId()+" con esito "+flow.getStato()+" è andata in errore per il seguente motivo: " + error;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void aggiornaOrdineMissione(OrdineMissione ordineMissioneDaAggiornare, FlowResult flowResult) {
		try {
			if (ordineMissioneDaAggiornare != null){
				if (ordineMissioneDaAggiornare.isStatoInviatoAlFlusso() && ordineMissioneDaAggiornare.isMissioneConfermata() &&
						!ordineMissioneDaAggiornare.isMissioneDaValidare())	{
					switch (flowResult.getStato() ) {
						case FlowResult.ESITO_FLUSSO_FIRMATO:
							aggiornaOrdineMissioneFirmato(ordineMissioneDaAggiornare);
							break;
						case FlowResult.ESITO_FLUSSO_FIRMA_UO:
							aggiornaOrdineMissionePrimaFirma(ordineMissioneDaAggiornare);
							break;
						case FlowResult.ESITO_FLUSSO_RESPINTO_UO:
							aggiornaOrdineMissioneRespinto(flowResult, ordineMissioneDaAggiornare);
							break;
						case FlowResult.ESITO_FLUSSO_RESPINTO_UO_SPESA:
							aggiornaOrdineMissioneRespinto(flowResult, ordineMissioneDaAggiornare);
							break;
					}
				} else {
					erroreOrdineMissione(ordineMissioneDaAggiornare, flowResult);
				}
			}
		} catch (Exception e){
//			mailService.sendEmailError(subjectErrorFlowsOrdine, "Errore in aggiornaOrdineMissione: "+e.getMessage(), false, true);
			throw new AwesomeException(CodiciErrore.ERRGEN, Utility.getMessageException(e));
		}
	}

	private void erroreOrdineMissione(OrdineMissione ordineMissioneDaAggiornare, FlowResult flowResult) {
		String errore = "Esito flusso non corrispondente con lo stato dell'ordine.";
		String testoErrore = getTextErrorOrdine(ordineMissioneDaAggiornare, flowResult, errore);
		throw new AwesomeException(CodiciErrore.ERRGEN, errore+" "+ testoErrore);
	}

	private void aggiornaOrdineMissioneFirmato(OrdineMissione ordineMissioneDaAggiornare) {
		ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		ordineMissioneDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
		gestioneEmailDopoApprovazione(ordineMissioneDaAggiornare);
		OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissioneDaAggiornare);
		if (anticipo != null) {
			anticipo.setStato(Costanti.STATO_DEFINITIVO);
			ordineMissioneAnticipoService.updateAnticipo(anticipo, false);
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoSpesa(),
					ordineMissioneDaAggiornare.getAnno());
			if (dati != null && dati.getMailNotifiche() != null) {
				if (!dati.getMailNotifiche().equals("N")){
					mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo), false,
							true, dati.getMailNotifiche());
				}
			} else {
				List<UsersSpecial> lista = accountService
						.getUserSpecialForUoPerValidazione(ordineMissioneDaAggiornare.getUoSpesa());
				if (lista != null && lista.size() > 0) {
					mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo),
							false, true, mailService.prepareTo(lista));
				}
			}
		}
		updateOrdineMissione(ordineMissioneDaAggiornare, true);
		popolaCoda(ordineMissioneDaAggiornare);
	}

	private void aggiornaOrdineMissionePrimaFirma(OrdineMissione ordineMissioneDaAggiornare) {
		ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
		updateOrdineMissione(ordineMissioneDaAggiornare, true);
	}

	public void gestioneEmailDopoApprovazione(OrdineMissione ordineMissioneDaAggiornare) {
		gestioneEmailDopoApprovazione(ordineMissioneDaAggiornare, false);
	}

	public void gestioneEmailDopoApprovazione(OrdineMissione ordineMissioneDaAggiornare, Boolean isAnnullamento) {
		List<UsersSpecial> listaUtenti = new ArrayList<>();
		DatiSede datiSede = null;
		Account account = accountService.loadAccountFromUsername(ordineMissioneDaAggiornare.getUid());
		if (account != null && account.getCodice_sede() != null) {
			datiSede = datiSedeService.getDatiSede(account.getCodice_sede(),LocalDate.now());
		}

		DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoRich(),
				ordineMissioneDaAggiornare.getAnno());
		DatiIstituto datiIstitutoSpesa = null;
		if (!ordineMissioneDaAggiornare.getUoRich().equals(ordineMissioneDaAggiornare.getUoSpesa())) {
			datiIstitutoSpesa = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoSpesa(),
					ordineMissioneDaAggiornare.getAnno());
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("U")) {
			listaUtenti = accountService.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoRich(), false);
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("V")) {
			listaUtenti = accountService.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoRich(), true);
		}
		if (datiIstitutoSpesa != null) {
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("V")) {
				List<UsersSpecial> listaUtentiSpesa = accountService
						.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoSpesa(), true);
				listaUtenti.addAll(listaUtentiSpesa);
			}
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("U")) {
				List<UsersSpecial> listaUtentiSpesa = accountService
						.getUserSpecialForUo(ordineMissioneDaAggiornare.getUoSpesa(), false);
				listaUtenti.addAll(listaUtentiSpesa);
			}
		}
		String oggetto = isAnnullamento ? approvazioneAnnullamentoOrdineMissione : approvazioneOrdineMissione;
		String testo = isAnnullamento ? getTextMailApprovazioneAnnullamentoOrdine(ordineMissioneDaAggiornare)
				: getTextMailApprovazioneOrdine(ordineMissioneDaAggiornare);
		if (listaUtenti.size() > 0) {
			mailService.sendEmail(oggetto, testo, false, true, mailService.prepareTo(listaUtenti));
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("E")
				&& !StringUtils.isEmpty(datiIstituto.getMailNotifiche()) && !datiIstituto.getMailNotifiche().equals("N")) {
			mailService.sendEmail(oggetto, testo, false, true, datiIstituto.getMailNotifiche());
		}
		if (datiSede != null && Utility.nvl(datiSede.getTipoMailDopoOrdine(), "N").equals("A")
				&& !StringUtils.isEmpty(datiSede.getMailDopoOrdine())) {
			mailService.sendEmail(oggetto, testo, false, true, datiSede.getMailDopoOrdine());
		}
		if (Utility.nvl(datiIstituto.getTipoMailDopoOrdine(), "N").equals("A")
				&& !StringUtils.isEmpty(datiIstituto.getMailDopoOrdine())) {
			mailService.sendEmail(oggetto, testo, false, true, datiIstituto.getMailDopoOrdine());
		}
		if (datiIstitutoSpesa != null) {
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("E")
					&& !StringUtils.isEmpty(datiIstitutoSpesa.getMailNotifiche()) && !datiIstitutoSpesa.getMailNotifiche().equals("N")) {
				mailService.sendEmail(oggetto, testo, false, true, datiIstitutoSpesa.getMailNotifiche());
			}
			if (Utility.nvl(datiIstitutoSpesa.getTipoMailDopoOrdine(), "N").equals("A")
					&& !StringUtils.isEmpty(datiIstitutoSpesa.getMailDopoOrdine())) {
				mailService.sendEmail(oggetto, testo, false, true, datiIstitutoSpesa.getMailDopoOrdine());
			}
		}
	}

	public void popolaCoda(OrdineMissione ordineMissione) {
		if (ordineMissione.getMatricola() != null && !isDevProfile()) {
			Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
			String idSede = null;
			if (account != null) {
				idSede = account.getCodice_sede();
			}
			Missione missione = new Missione(TypeMissione.ORDINE, new Long(ordineMissione.getId().toString()), idSede,
					ordineMissione.getMatricola(), ordineMissione.getDataInizioMissione(),
					ordineMissione.getDataFineMissione(), null,
					ordineMissione.isMissioneEstera() ? TypeTipoMissione.ESTERA : TypeTipoMissione.ITALIA,
					ordineMissione.getAnno(), ordineMissione.getNumero());
			rabbitMQService.send(missione);
		}
	}

	private void aggiornaValidazione(OrdineMissione ordineMissione) {
		if (accountService.isUserSpecialEnableToValidateOrder(securityService.getCurrentUserLogin(), ordineMissione.getUoSpesa())) {
			ordineMissione.setValidato("S");
		} else {
			ordineMissione.setValidato("N");
		}
	}

	public String retrieveStateFromFlows(ResultFlows result) {
		return result.getState();
	}

	public List<OrdineMissione> getOrdiniMissione(MissioneFilter filter, Boolean isServiceRest)
			throws ComponentException {
		return getOrdiniMissione(filter, isServiceRest, false);
	}

	public List<OrdineMissione> getOrdiniMissione(MissioneFilter filter, Boolean isServiceRest,
			Boolean isForValidateFlows) throws ComponentException {
		CriterionList criterionList = new CriterionList();
		List<OrdineMissione> ordineMissioneList = null;
		if (filter != null) {
			if (filter.getUoRich() != null && filter.getUser() != null) {
				filter.setUoRich(null);
			}
			if (filter.getAnno() != null) {
				criterionList.add(Restrictions.eq("anno", filter.getAnno()));
			}
			if (filter.getDaId() != null) {
				criterionList.add(Restrictions.ge("id", filter.getDaId()));
			}
			if (filter.getStato() != null) {
				criterionList.add(Restrictions.eq("stato", filter.getStato()));
			}
			if (filter.getStatoFlusso() != null) {
				criterionList.add(Restrictions.eq("statoFlusso", filter.getStatoFlusso()));
			}
			if (filter.getValidato() != null) {
				criterionList.add(Restrictions.eq("validato", filter.getValidato()));
			}
			if (filter.getListaStatiMissione() != null && !filter.getListaStatiMissione().isEmpty()) {
				criterionList.add(Restrictions.in("stato", filter.getListaStatiMissione()));
			}
			if (filter.getListaStatiFlussoMissione() != null && !filter.getListaStatiFlussoMissione().isEmpty()) {
				criterionList.add(Restrictions.in("statoFlusso", filter.getListaStatiFlussoMissione()));
			}

			if (filter.getaId() != null) {
				criterionList.add(Restrictions.le("id", filter.getaId()));
			}
			if (filter.getDaNumero() != null) {
				criterionList.add(Restrictions.ge("numero", filter.getDaNumero()));
			}
			if (filter.getaNumero() != null) {
				criterionList.add(Restrictions.le("numero", filter.getaNumero()));
			}
			if (filter.getDaData() != null) {
				criterionList.add(Restrictions.ge("dataInserimento",
						DateUtils.parseLocalDate(filter.getDaData(), DateUtils.PATTERN_DATE)));
			}
			if (filter.getaData() != null) {
				criterionList.add(Restrictions.le("dataInserimento",
						DateUtils.parseLocalDate(filter.getaData(), DateUtils.PATTERN_DATE)));
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
			if (filter.getCdsRich() != null) {
				criterionList.add(Restrictions.eq("cdsRich", filter.getCdsRich()));
			}
			if (filter.getUoRich() != null) {
				if (accountService.isUserEnableToWorkUo(filter.getUoRich()) && !filter.isDaCron()) {
					Disjunction condizioneOr = Restrictions.disjunction();
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoRich", filter.getUoRich())));
					condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uoSpesa", filter.getUoRich())));
					condizioneOr
							.add(Restrictions.conjunction().add(Restrictions.eq("uoCompetenza", filter.getUoRich())));
					criterionList.add(condizioneOr);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN, "L'utente " + securityService.getCurrentUserLogin()
							+ "  non è abilitato a vedere i dati della uo " + filter.getUoRich());
				}
			}
			if (filter.getSoloMissioniNonGratuite()) {
				criterionList.add(Restrictions.disjunction().add(Restrictions.isNull("missioneGratuita"))
						.add(Restrictions.eq("missioneGratuita", "N")));
			}
			if (Utility.nvl(filter.getGiaRimborsato(), "A").equals("N")) {
				criterionList.add(Subqueries.notExists(
						"select rim.id from RimborsoMissione AS rim where rim.ordineMissione.id = this.id and rim.stato != 'ANN' and rim.stato != 'ANA' "));
			} else if (Utility.nvl(filter.getGiaRimborsato(), "A").equals("R")) {
				criterionList.add(Subqueries.exists(
						"select rim.id from RimborsoMissione AS rim where rim.ordineMissione.id = this.id and rim.stato != 'ANN' and rim.stato != 'ANA' "));
			}
			if (Utility.nvl(filter.getDaAnnullare(), "N").equals("S") ) {
				String notExists = "select ann.id from AnnullamentoOrdineMissione AS ann where ann.ordineMissione.id = this.id and ann.stato != 'ANN' and ann.stato != 'ANA' and ann.stato != 'DEF' " ;
				criterionList.add(Subqueries.notExists(notExists));
			}
			if (filter.getCup() != null) {
				criterionList.add(Restrictions.eq("cup", filter.getCup()));
			}
		}
		if (filter != null && Utility.nvl(filter.getDaCron(), "N").equals("S")) {
			return crudServiceBean.findByCriterion(OrdineMissione.class, criterionList,
					Order.desc("dataInserimento"), Order.desc("anno"), Order.desc("numero"));
		} else if (filter != null && Utility.nvl(filter.getToFinal(), "N").equals("S")) {
			if (StringUtils.isEmpty(filter.getUoRich())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Non è stata selezionata la uo per rendere definitivi ordini di missione.");
			}
			// criterionList.add(Restrictions.isNull("pgObbligazione"));
			UsersSpecial userSpecial = accountService.getUoForUsersSpecial(securityService.getCurrentUserLogin());
			boolean uoAbilitata = false;
			if (userSpecial != null) {
				if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")) {
					if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
						for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
							if (uoService.getUoSigla(uoUser).equals(filter.getUoRich())) {
								uoAbilitata = true;
								if (!Utility.nvl(uoUser.getRendi_definitivo(), "N").equals("S")) {
									throw new AwesomeException(CodiciErrore.ERRGEN,
											"L'utente non è abilitato a rendere definitivi ordini di missione.");
								}
							}
						}
					}
				}
			}
			if (!uoAbilitata) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"L'utente non è abilitato a rendere definitivi ordini di missione.");
			}
			criterionList.add(Restrictions.eq("statoFlusso", Costanti.STATO_APPROVATO_FLUSSO));
			criterionList.add(Restrictions.eq("stato", Costanti.STATO_CONFERMATO));
			criterionList.add(Restrictions.eq("validato", "S"));
			ordineMissioneList = crudServiceBean.findByProjection(OrdineMissione.class,
					OrdineMissione.getProjectionForElencoMissioni(), criterionList, true, Order.desc("dataInserimento"),
					Order.desc("anno"), Order.desc("numero"));
			if (Utility.nvl(filter.getRecuperoAutoPropria()).equals("S")) {
				for (OrdineMissione ordineMissione : ordineMissioneList) {
					OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(new Long(ordineMissione.getId().toString()));
					if (autoPropria != null) {
						ordineMissione.setUtilizzoAutoPropria("S");
					} else {
						ordineMissione.setUtilizzoAutoPropria("N");
					}
				}
			}

			return ordineMissioneList;

		} else {
			if (!isForValidateFlows) {
				if (!StringUtils.isEmpty(filter.getUser())) {
					criterionList.add(Restrictions.eq("uid", filter.getUser()));
				} else {
					if (StringUtils.isEmpty(filter.getUoRich())) {
						if (Utility.nvl(filter.getRespGruppo(), "N").equals("S")) {
							criterionList.add(Restrictions.disjunction()
									.add(Restrictions.conjunction()
											.add(Restrictions.eq("responsabileGruppo", securityService.getCurrentUserLogin()))
											.add(Restrictions.not(Restrictions.eq("stato", "INS")))));
						} else {
							criterionList.add(Restrictions.eq("uid", securityService.getCurrentUserLogin()));
						}
					}
				}
			} else {
				UsersSpecial userSpecial = accountService.getUoForUsersSpecial(securityService.getCurrentUserLogin());
				if (userSpecial != null) {
					if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")) {
						if (userSpecial.getUoForUsersSpecials() != null
								&& !userSpecial.getUoForUsersSpecials().isEmpty()) {
							boolean esisteUoConValidazioneConUserNonAbilitato = false;
							Disjunction condizioneOr = Restrictions.disjunction();
							List<String> listaUoUtente = new ArrayList<String>();
							for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
								// Uo uo =
								// uoService.recuperoUo(uoUser.getCodice_uo());
								// if (uo != null){
								condizioneOr.add(Restrictions.conjunction()
										.add(Restrictions.eq("uoRich", uoService.getUoSigla(uoUser))));
								// if
								// (Utility.nvl(uo.getOrdineDaValidare(),"N").equals("S")){
								// if
								// (Utility.nvl(uoUser.getOrdine_da_validare(),"N").equals("S")){
								condizioneOr.add(Restrictions.conjunction()
										.add(Restrictions.eq("uoSpesa", uoService.getUoSigla(uoUser))));
								condizioneOr.add(Restrictions.conjunction()
										.add(Restrictions.eq("uoCompetenza", uoService.getUoSigla(uoUser))));
								// }
								// }
								// }
							}
							condizioneResponsabileGruppo(condizioneOr, filter);
							criterionList.add(condizioneOr);
						} else {
							condizioneOrdineDellUtenteConResponsabileGruppo(criterionList, filter);
						}
					}
				} else {
					condizioneOrdineDellUtenteConResponsabileGruppo(criterionList, filter);
				}
			}
			if (!Utility.nvl(filter.getIncludiMissioniAnnullate()).equals("S") && (!(filter.getDaId() != null
					&& filter.getaId() != null && filter.getDaId().compareTo(filter.getaId()) == 0))) {
				criterionList.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO)));
				criterionList
						.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE)));
				if (StringUtils.isEmpty(filter.getGiaRimborsato())){
					criterionList
							.add(Restrictions.not(Restrictions.eq("stato", Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE_CONSENTITO_RIMBORSO)));
				}
			}

			if (isServiceRest) {
				if (isForValidateFlows) {
					List<String> listaStatiFlusso = new ArrayList<String>();
					listaStatiFlusso.add(Costanti.STATO_INVIATO_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_FIRMATO_PRIMA_FIRMA_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_INSERITO);
					listaStatiFlusso.add(Costanti.STATO_RESPINTO_UO_FLUSSO);
					listaStatiFlusso.add(Costanti.STATO_RESPINTO_UO_SPESA_FLUSSO);
					criterionList.add(Restrictions.disjunction().add(Restrictions.disjunction()
							.add(Restrictions.in("statoFlusso", listaStatiFlusso))
							.add(Restrictions.conjunction().add(Restrictions.eq("stato", Costanti.STATO_INSERITO)))));
				}
				ordineMissioneList = crudServiceBean.findByProjection(OrdineMissione.class,
						OrdineMissione.getProjectionForElencoMissioni(), criterionList, true,
						Order.desc("dataInserimento"), Order.desc("anno"), Order.desc("numero"));
				if (isServiceRest && !isForValidateFlows) {
					for (OrdineMissione ordineMissione : ordineMissioneList) {
						OrdineMissioneAnticipo anticipo = ordineMissioneAnticipoService
								.getAnticipo(new Long(ordineMissione.getId().toString()));
						if (anticipo != null) {
							ordineMissione.setRichiestaAnticipo(Costanti.SI_NO.get("S"));
						} else {
							ordineMissione.setRichiestaAnticipo(Costanti.SI_NO.get("N"));
						}
					}
				}
			} else
				ordineMissioneList = crudServiceBean.findByCriterion(OrdineMissione.class, criterionList,
						Order.desc("dataInserimento"), Order.desc("anno"), Order.desc("numero"));

			if (Utility.nvl(filter.getRecuperoAutoPropria()).equals("S")) {
				for (OrdineMissione ordineMissione : ordineMissioneList) {
					OrdineMissioneAutoPropria autoPropria = ordineMissioneAutoPropriaService.getAutoPropria(new Long(ordineMissione.getId().toString()));
					if (autoPropria != null) {
						ordineMissione.setUtilizzoAutoPropria("S");
					} else {
						ordineMissione.setUtilizzoAutoPropria("N");
					}
				}
			}

			return ordineMissioneList;
		}
	}

	private void condizioneOrdineDellUtenteConResponsabileGruppo(CriterionList criterionList,
			MissioneFilter filter) {
		Disjunction condizioneOr = Restrictions.disjunction();
		condizioneResponsabileGruppo(condizioneOr, filter);
		condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("uid", securityService.getCurrentUserLogin())));
		criterionList.add(condizioneOr);
	}

	private void condizioneResponsabileGruppo(Disjunction condizioneOr, MissioneFilter filter) {
		if (filter.getaId() != null && filter.getaId() != null && filter.getDaId().compareTo(filter.getaId()) == 0) {
			condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("responsabileGruppo", securityService.getCurrentUserLogin()))
					.add(Restrictions.not(Restrictions.eq("stato", "INS"))));
		} else {
			condizioneOr.add(Restrictions.conjunction().add(Restrictions.eq("responsabileGruppo", securityService.getCurrentUserLogin()))
					.add(Restrictions.eq("stato", "INR")));
		}
	}

	public OrdineMissioneAutoPropria getAutoPropria(OrdineMissione ordineMissione) {
		return OrdineMissioneAutoPropriaRepository.getAutoPropria(ordineMissione);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrdineMissione createOrdineMissione(OrdineMissione ordineMissione)
			throws ComponentException {
		controlloDatiObbligatoriDaGUI(ordineMissione);
		inizializzaCampiPerInserimento(ordineMissione);
		validaCRUD(ordineMissione);
		ordineMissione = (OrdineMissione) crudServiceBean.creaConBulk(ordineMissione);
		// autoPropriaRepository.save(autoPropria);
		log.debug("Created Information for User: {}", ordineMissione);
		return ordineMissione;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void verifyStepRespGruppo(OrdineMissione ordineMissione) throws ComponentException {
		log.info("Start 2 Resp gruppo");
		DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
				ordineMissione.getAnno());
		ZonedDateTime oggi = ZonedDateTime.now();
		long minutiDifferenza = 10000;
		long minutiDifferenzaDaInizioMissione = 0;
		if (oggi.isBefore(ordineMissione.getDataInizioMissione())) {
			minutiDifferenzaDaInizioMissione = ChronoUnit.MINUTES.between(oggi.truncatedTo(ChronoUnit.MINUTES),
					ordineMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES));
		}
		log.info("Start 2.1 Resp gruppo");
		if (ordineMissione.getDataInvioRespGruppo() != null) {
			minutiDifferenza = ChronoUnit.MINUTES.between(
					ordineMissione.getDataInvioRespGruppo().truncatedTo(ChronoUnit.MINUTES),
					oggi.truncatedTo(ChronoUnit.MINUTES));
		}
		log.info("Start 2.2 Resp gruppo");
		if (istituto.getMinutiPrimaInizioResp() != null
				&& minutiDifferenzaDaInizioMissione < istituto.getMinutiPrimaInizioResp()) {
			if (istituto.getMinutiMinimiResp() != null && minutiDifferenza > istituto.getMinutiMinimiResp()) {
				bypassRespGruppo(ordineMissione);
			}
		}
		log.info("Start 2.3 Resp gruppo");
		if (istituto.getMinutiPassatiResp() != null && minutiDifferenza > istituto.getMinutiPassatiResp()) {
			bypassRespGruppo(ordineMissione);
		}

	}

	private void bypassRespGruppo(OrdineMissione ordineMissione) {
		ZonedDateTime oggi = ZonedDateTime.now();
		ordineMissione.setBypassRespGruppo("S");
		ordineMissione.setDaChron("S");
		updateOrdineMissione(ordineMissione, false, true, null);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void verifyStepAmministrativo(OrdineMissione ordineMissione) throws ComponentException {
		log.info("Start 2 Amm");
		DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
				ordineMissione.getAnno());
		ZonedDateTime oggi = ZonedDateTime.now();
		long minutiDifferenza = 10000;
		long minutiDifferenzaDaInizioMissione = 0;
		log.info("Start 2.1 Amm");
		if (oggi.isBefore(ordineMissione.getDataInizioMissione())) {
			minutiDifferenzaDaInizioMissione = ChronoUnit.MINUTES.between(oggi.truncatedTo(ChronoUnit.MINUTES),
					ordineMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES));
		}
		log.info("Start 2.2 Amm");
		if (ordineMissione.getDataInvioAmministrativo() != null) {
			minutiDifferenza = ChronoUnit.MINUTES.between(
					ordineMissione.getDataInvioAmministrativo().truncatedTo(ChronoUnit.MINUTES),
					oggi.truncatedTo(ChronoUnit.MINUTES));
		}

		log.info("Start 2.3 Amm");
		if (istituto.getMinutiPrimaInizioAmm() != null
				&& minutiDifferenzaDaInizioMissione < istituto.getMinutiPrimaInizioAmm()) {
			if (istituto.getMinutiMinimiAmm() != null && minutiDifferenza > istituto.getMinutiMinimiAmm()) {
				bypassVerificaAmministrativo(ordineMissione);
			}
		}
		log.info("Start 2.4 Amm");
		if (istituto.getMinutiPassatiAmm() != null && minutiDifferenza > istituto.getMinutiPassatiAmm()) {
			bypassVerificaAmministrativo(ordineMissione);
		}
		log.info("Start 2.5 Amm");

	}

	private void bypassVerificaAmministrativo(OrdineMissione ordineMissione) {
		ordineMissione.setBypassAmministrativo("S");
		ordineMissione.setDaValidazione("S");
		ordineMissione.setDaChron("S");
		ordineMissione.setToBeUpdated();
		updateOrdineMissione(ordineMissione, false, true);
	}

	private void inizializzaCampiPerInserimento(OrdineMissione ordineMissione)
			throws ComponentException {
		ordineMissione.setUidInsert(securityService.getCurrentUserLogin());
		ordineMissione.setUser(securityService.getCurrentUserLogin());
		Integer anno = recuperoAnno(ordineMissione);
		ordineMissione.setAnno(anno);
		ordineMissione.setNumero(datiIstitutoService.getNextPG(ordineMissione.getUoRich(), anno,
				Costanti.TIPO_ORDINE_DI_MISSIONE));
		if (StringUtils.isEmpty(ordineMissione.getTrattamento())) {
			ordineMissione.setTrattamento("R");
		}
		if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())) {
			ordineMissione.setObbligoRientro("S");
		}
		if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())) {
			ordineMissione.setUtilizzoAutoNoleggio("N");
		}
		if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())) {
			ordineMissione.setUtilizzoTaxi("N");
		}
		if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoServizio())) {
			ordineMissione.setUtilizzoAutoServizio("N");
		}
		if (StringUtils.isEmpty(ordineMissione.getPersonaleAlSeguito())) {
			ordineMissione.setPersonaleAlSeguito("N");
		}

		aggiornaValidazione(ordineMissione);

		ordineMissione.setStato(Costanti.STATO_INSERITO);
		ordineMissione.setStatoFlusso(Costanti.STATO_INSERITO);
		if (StringUtils.isEmpty(ordineMissione.getMatricola()) && StringUtils.isEmpty(ordineMissione.getQualificaRich())) {
			Account account = accountService.loadAccountFromUsername(ordineMissione.getUid());
			if (account != null && account.getCodice_fiscale() != null) {
				TerzoPerCompensoJson terzoJson = terzoPerCompensoService.getTerzi(account.getCodice_fiscale(),
						ordineMissione.getDataInizioMissione(), ordineMissione.getDataFineMissione());
				for (TerzoPerCompenso terzo : terzoJson.getElements()) {
					ordineMissione.setQualificaRich(terzo.getDsTipoRapporto());
					break;
				}
			}
		}
		ordineMissione.setToBeCreated();
	}

	private Integer recuperoAnno(OrdineMissione ordineMissione) {
		if (ordineMissione.getDataInserimento() == null) {
			ordineMissione.setDataInserimento(LocalDate.now());
		}
		return ordineMissione.getDataInserimento().getYear();
	}

	private Boolean isInvioOrdineAlResponsabileGruppo(OrdineMissione ordineMissione) {
		return Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("M");
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione)
			throws ComponentException {
		return updateOrdineMissione(ordineMissione, false);
	}

	private OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione, Boolean fromFlows)
			throws ComponentException {
		return updateOrdineMissione(ordineMissione, fromFlows, false);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione, Boolean fromFlows,
			Boolean confirm) {
		return updateOrdineMissione(ordineMissione, fromFlows, confirm, null);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public OrdineMissione updateOrdineMissione(OrdineMissione ordineMissione, Boolean fromFlows,
			Boolean confirm, String basePath) {
		ZonedDateTime oggi = ZonedDateTime.now();
		if (!fromFlows && !isUserEnabledToViewMissione(ordineMissione) && Utility.nvl(ordineMissione.getDaChron(),"N").equals("N")){
			throw new AwesomeException(CodiciErrore.ERRGEN,
					"Non Autorizzato");
		}
		OrdineMissione ordineMissioneDB = (OrdineMissione) crudServiceBean.findById(OrdineMissione.class,
				ordineMissione.getId());
		try {
			crudServiceBean.lockBulk(ordineMissioneDB);
		} catch (ComponentException | OptimisticLockException | PersistencyException | BusyResourceException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					"Ordine in modifica. Ripetere l'operazione. ID: " + ordineMissioneDB.getId());
		}
		boolean isCambioResponsabileGruppo = false;
		boolean isRitornoMissioneMittente = false;
		if (ordineMissioneDB == null) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di Missione da aggiornare inesistente.");
		}

		// try {
		// crudServiceBean.lockBulk(ordineMissioneDB);
		// } catch (OptimisticLockException | PersistencyException |
		// BusyResourceException e) {
		// throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione
		// in modifica. Ripetere l'operazione.");
		// }
		if (ordineMissione.getResponsabileGruppo() != null && ordineMissioneDB.getResponsabileGruppo() != null
				&& !ordineMissione.getResponsabileGruppo().equals(ordineMissioneDB.getResponsabileGruppo())) {
			isCambioResponsabileGruppo = true;
			ordineMissioneDB.setNoteRespingi(null);
		}
		if (ordineMissioneDB.isMissioneConfermata() && !fromFlows
				&& !Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("D")) {
			if (ordineMissioneDB.isStatoFlussoApprovato()) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Non è possibile modificare l'ordine di missione. E' già stato approvato.");
			}
			if (!ordineMissioneDB.isMissioneDaValidare()) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Non è possibile modificare l'ordine di missione. E' già stato avviato il flusso di approvazione.");
			}
			ordineMissioneDB.setNoteRespingi(null);
		}

		if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("S")) {
			if (!ordineMissioneDB.getStato().equals(Costanti.STATO_CONFERMATO)) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione non confermato.");
			}
			if (!ordineMissioneDB.isMissioneDaValidare()) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione già validato.");
			}
			if (!accountService.isUserSpecialEnableToValidateOrder(securityService.getCurrentUserLogin(), ordineMissioneDB.getUoSpesa())  && Utility.nvl(ordineMissione.getDaChron(),"N").equals("N")) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Utente non abilitato a validare gli ordini di missione per la uo "
								+ ordineMissioneDB.getUoSpesa() + ".");
			}

			if (!confirm) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Operazione non possibile. Non è possibile modificare un ordine di missione durante la fase di validazione. Rieseguire la ricerca.");
			}

			aggiornaDatiOrdineMissione(ordineMissione, confirm, ordineMissioneDB);
			ordineMissioneDB.setValidato("S");
			ordineMissioneDB.setNoteRespingi(null);
		} else if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("D")) {
			if (ordineMissione.getEsercizioOriginaleObbligazione() == null
					|| ordineMissione.getPgObbligazione() == null) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Per rendere definitivo l'ordine di missione è necessario valorizzare l'impegno.");
			}
			if (!StringUtils.isEmpty(ordineMissione.getGae())) {
				ordineMissioneDB.setGae(ordineMissione.getGae());
			}
			if (!StringUtils.isEmpty(ordineMissione.getVoce())) {
				ordineMissioneDB.setVoce(ordineMissione.getVoce());
			}
			ordineMissioneDB.setEsercizioOriginaleObbligazione(ordineMissione.getEsercizioOriginaleObbligazione());
			ordineMissioneDB.setPgObbligazione(ordineMissione.getPgObbligazione());
			ordineMissioneDB.setNoteRespingi(null);
			ordineMissioneDB.setStato(Costanti.STATO_DEFINITIVO);
		} else if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("R")) {
			if (!ordineMissione.isMissioneInviataResponsabile() && !accountService
					.isUserSpecialEnableToValidateOrder(securityService.getCurrentUserLogin(), ordineMissione.getUoSpesa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Utente non abilitato a validare gli ordini di missione per la uo "
								+ ordineMissione.getUoSpesa() + ".");
			}
			if (ordineMissioneDB.isStatoNonInviatoAlFlusso() || ordineMissioneDB.isMissioneDaValidare()) {
				if (StringUtils.isEmpty(ordineMissione.getNoteRespingi())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"Non è possibile respingere un ordine di missione senza indicarne il motivo.");
				}
				ordineMissioneDB.setDataInvioAmministrativo(null);
				ordineMissioneDB.setDataInvioRespGruppo(null);
				ordineMissioneDB.setBypassRespGruppo(null);
				ordineMissioneDB.setStato(Costanti.STATO_INSERITO);
				ordineMissioneDB.setNoteRespingi(ordineMissione.getNoteRespingi());
				isRitornoMissioneMittente = true;
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Non è possibile sbloccare un ordine di missione se è stato già inviato al flusso.");
			}
		} else if (isInvioOrdineAlResponsabileGruppo(ordineMissione)) {
			if (ordineMissione.getResponsabileGruppo() != null) {
				if (ordineMissioneDB.isMissioneInserita()) {
					ordineMissioneDB.setResponsabileGruppo(ordineMissione.getResponsabileGruppo());
					ordineMissioneDB.setStato(Costanti.STATO_INVIATO_RESPONSABILE);
					ordineMissioneDB.setDataInvioRespGruppo(oggi);
					ordineMissioneDB.setNoteRespingi(null);
				} else {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"Non è possibile inviare al responsabile una missione in stato diverso da 'Inserito'.");
				}
			} else {
				throw new AwesomeException(CodiciErrore.ERRGEN, "E' obbligatorio indicare il responsabile del gruppo.");
			}
			if (StringUtils.isEmpty(ordineMissioneDB.getPgProgetto())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il Progetto.");
			}

		} else {
			aggiornaDatiOrdineMissione(ordineMissione, confirm, ordineMissioneDB);
		}

		if (confirm) {
			DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
					ordineMissione.getAnno());
			if (istituto != null && istituto.isAttivaGestioneResponsabileModulo()
					&& !ordineMissione.isMissioneGratuita()) {
				if (StringUtils.isEmpty(ordineMissioneDB.getResponsabileGruppo())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"Per il cds di spesa indicato è attiva la gestione del responsabile del gruppo ma non è stato inserito il responsabile del gruppo.");
				}
				if (StringUtils.isEmpty(ordineMissioneDB.getPgProgetto())) {
					throw new AwesomeException(CodiciErrore.ERRGEN, "E' necessario indicare il Progetto.");
				}
				if (ordineMissioneDB.isMissioneInserita()
						&& !ordineMissioneDB.getResponsabileGruppo().equals(ordineMissione.getUid())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"Per il cds di spesa indicato è attiva la gestione del responsabile del gruppo ma l'ordine di missione non è stato inviato alla sua approvazione.");
				}
			}
			if (Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("N") && ordineMissione.isMissioneConfermata()
					&& ordineMissione.isMissioneDaValidare()) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Ordine di missione già confermato.");
			}
			ordineMissioneDB.setStato(Costanti.STATO_CONFERMATO);
			ordineMissioneDB.setNoteRespingi(null);
		}

		ordineMissioneDB.setToBeUpdated();

		// //effettuo controlli di validazione operazione CRUD
		if (!Utility.nvl(ordineMissione.getDaValidazione(), "N").equals("R") && !fromFlows) {
			validaCRUD(ordineMissioneDB);
		}
		if (confirm) {
			Parametri parametri = parametriService.getParametri();
			if (parametri != null && StringUtils.hasLength(parametri.getDipendenteCda())
					&& Utility.nvl(ordineMissione.getUid(), "N").equals(parametri.getDipendenteCda())
					&& Utility.nvl(ordineMissione.getPresidente(), "N").equals("S")) {
				ordineMissioneDB.setStateFlows(Costanti.STATO_FLUSSO_FROM_CMIS.get(Costanti.STATO_FIRMATO_FROM_CMIS));
				ordineMissioneDB.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
				ordineMissioneDB.setValidato("S");
				ordineMissioneDB.setStato(Costanti.STATO_DEFINITIVO);

			} else if (!ordineMissioneDB.isMissioneDaValidare()) {
				DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
						ordineMissione.getAnno());
				if (Utility.nvl(istituto.getCreaImpegnoAut(), "N").equals("S")) {

				}
				cmisOrdineMissioneService.avviaFlusso(ordineMissioneDB);
				ordineMissioneDB.setStateFlows(Costanti.STATO_FLUSSO_FROM_CMIS.get(Costanti.STATO_FIRMA_UO_FROM_CMIS));
				ordineMissioneDB.setDataInvioFirma(oggi);
				if (istituto.isAttivaGestioneResponsabileModulo()) {
					if (ordineMissioneDB.getDataInvioRespGruppo() == null) {
						ordineMissioneDB.setDataInvioRespGruppo(oggi);
					}
				}
				if (ordineMissioneDB.getDataInvioAmministrativo() == null) {
					ordineMissioneDB.setDataInvioAmministrativo(oggi);
				}
			} else if (ordineMissioneDB.isMissioneDaValidare()) {
				DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
						ordineMissione.getAnno());
				if (istituto.isAttivaGestioneResponsabileModulo()) {
					if (ordineMissioneDB.getDataInvioRespGruppo() == null) {
						ordineMissioneDB.setDataInvioRespGruppo(oggi);
					}
				}
				ordineMissioneDB.setDataInvioAmministrativo(oggi);
			}

		}

		ordineMissioneDB = (OrdineMissione) crudServiceBean.modificaConBulk( ordineMissioneDB);

		// autoPropriaRepository.save(autoPropria);
		log.debug("Updated Information for Ordine di Missione: {}", ordineMissioneDB);
		if (isInvioOrdineAlResponsabileGruppo(ordineMissione)
				|| (isCambioResponsabileGruppo && ordineMissioneDB.isMissioneInviataResponsabile())
						&& basePath != null) {
			mailService.sendEmail(subjectSendToManagerOrdine + " " + getNominativo(ordineMissioneDB.getUid()),
					getTextMailSendToManager(basePath, ordineMissioneDB), false, true,
					accountService.getEmail(ordineMissione.getResponsabileGruppo()));
		} else if (confirm && ordineMissioneDB.isMissioneDaValidare()) {
			sendMailToAdministrative(basePath, ordineMissioneDB);
		}
		if (isRitornoMissioneMittente) {
			missioneRespintaService.inserisciOrdineMissioneRespinto(ordineMissioneDB, ordineMissione.isMissioneInviataResponsabile() ? MissioneRespinta.FASE_RESPINGI_RESP_GRUPPO : MissioneRespinta.FASE_RESPINGI_AMMINISTRATIVI);
			mailService.sendEmail(subjectReturnToSenderOrdine,
					getTextMailReturnToSender(basePath, ordineMissioneDB), false, true,
					accountService.getEmail(ordineMissioneDB.getUidInsert()));
		}
		return ordineMissioneDB;
	}

	private void aggiornaDatiOrdineMissione(OrdineMissione ordineMissione, Boolean confirm,
			OrdineMissione ordineMissioneDB) {
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
		ordineMissioneDB.setNoteSegreteria(ordineMissione.getNoteSegreteria());
		ordineMissioneDB.setObbligoRientro(ordineMissione.getObbligoRientro());
		if (confirm) {
			aggiornaValidazione(ordineMissioneDB);
		} else {
			ordineMissioneDB.setValidato(ordineMissione.getValidato());
		}
		ordineMissioneDB.setOggetto(ordineMissione.getOggetto());
		ordineMissioneDB.setPartenzaDa(ordineMissione.getPartenzaDa());
		ordineMissioneDB.setPartenzaDaAltro(ordineMissione.getPartenzaDaAltro());
		if (!ordineMissioneDB.getPartenzaDa().equals("A")) {
			ordineMissioneDB.setPartenzaDaAltro(null);
		}
		ordineMissioneDB.setPriorita(ordineMissione.getPriorita());
		ordineMissioneDB.setTipoMissione(ordineMissione.getTipoMissione());
		ordineMissioneDB.setVoce(ordineMissione.getVoce());
		ordineMissioneDB.setTrattamento(ordineMissione.getTrattamento());
		ordineMissioneDB.setNazione(ordineMissione.getNazione());

		ordineMissioneDB.setNoteUtilizzoTaxiNoleggio(ordineMissione.getNoteUtilizzoTaxiNoleggio());
		ordineMissioneDB.setUtilizzoAutoNoleggio(ordineMissione.getUtilizzoAutoNoleggio());
		ordineMissioneDB.setUtilizzoTaxi(ordineMissione.getUtilizzoTaxi());
		ordineMissioneDB.setPersonaleAlSeguito(ordineMissione.getPersonaleAlSeguito());
		ordineMissioneDB.setUtilizzoAutoServizio(ordineMissione.getUtilizzoAutoServizio());
		ordineMissioneDB.setPgProgetto(ordineMissione.getPgProgetto());
		ordineMissioneDB.setEsercizioOriginaleObbligazione(ordineMissione.getEsercizioOriginaleObbligazione());
		ordineMissioneDB.setPgObbligazione(ordineMissione.getPgObbligazione());
		ordineMissioneDB.setResponsabileGruppo(ordineMissione.getResponsabileGruppo());
		ordineMissioneDB.setFondi(ordineMissione.getFondi());
		ordineMissioneDB.setCup(ordineMissione.getCup());
		ordineMissioneDB.setMissioneGratuita(ordineMissione.getMissioneGratuita());
		ordineMissioneDB.setCug(ordineMissione.getCug());
		ordineMissioneDB.setPresidente(ordineMissione.getPresidente());
		ordineMissioneDB.setBypassAmministrativo(ordineMissione.getBypassAmministrativo());
		ordineMissioneDB.setBypassRespGruppo(ordineMissione.getBypassRespGruppo());
		ordineMissioneDB.setDataInvioAmministrativo(ordineMissione.getDataInvioAmministrativo());
		ordineMissioneDB.setDataInvioRespGruppo(ordineMissione.getDataInvioRespGruppo());
		ordineMissioneDB.setDataInvioFirma(ordineMissione.getDataInvioFirma());
		ordineMissioneDB.setCommentoFlusso(ordineMissione.getCommentoFlusso());
	}

	private void sendMailToAdministrative(String basePath, OrdineMissione ordineMissioneDB) {
		DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissioneDB.getUoSpesa(),
				ordineMissioneDB.getAnno());
		String testoMail = getTextMailSendToAdministrative(basePath, ordineMissioneDB);
		String subjectMail = subjectSendToAdministrative + " " + getNominativo(ordineMissioneDB.getUid());
		if (dati != null && dati.getMailNotifiche() != null) {
			if (!dati.getMailNotifiche().equals("N")){
				mailService.sendEmail(subjectMail, testoMail, false, true, dati.getMailNotifiche());
			}
		} else {
			log.info("Ricerca amministrativi per mail. Uo: " + ordineMissioneDB.getUoSpesa());
			List<UsersSpecial> lista = accountService.getUserSpecialForUoPerValidazione(ordineMissioneDB.getUoSpesa());
			sendMailToAdministrative(lista, testoMail, subjectMail);
		}
	}

	private void sendMailToAdministrative(List<UsersSpecial> lista, String testoMail, String oggetto) {
		if (lista != null && lista.size() > 0) {
			String[] elencoMail = mailService.prepareTo(lista);
			if (elencoMail != null && elencoMail.length > 0) {
				mailService.sendEmail(oggetto, testoMail, false, true, elencoMail);
			}
		}
	}

	private String getNominativo(String user) {
		Account utente = accountService.loadAccountFromUsername(user);
		return utente.getCognome() + " " + utente.getNome();
	}

	private List<String> getTosMail(OrdineMissione ordineMissione) {
		List<String> mails = new ArrayList<>();
		Account utenteMissione = accountService.loadAccountFromUsername(ordineMissione.getUid());
		mails.add(utenteMissione.getEmail_comunicazioni());
		if (!ordineMissione.getUid().equals(ordineMissione.getUidInsert())) {
			Account utenteInserimentoMissione = accountService.loadAccountFromUsername(ordineMissione.getUid());
			mails.add(utenteInserimentoMissione.getEmail_comunicazioni());
		}
		return mails;
	}

	private String getTextMailSendToManager(String basePath, OrdineMissione ordineMissione) {
		return "L'ordine di missione " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + " di "
				+ getNominativo(ordineMissione.getUid()) + " per la missione a " + ordineMissione.getDestinazione()
				+ " dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto "
				+ ordineMissione.getOggetto()
				+ " le è stata inviata per l'approvazione in quanto responsabile del gruppo. "
				+ "Si prega di confermarlo attraverso il link " + basePath + "/#/ordine-missione/"
				+ ordineMissione.getId();
	}

	private String getTextMailApprovazioneOrdine(OrdineMissione ordineMissione) {
		return "L'ordine di missione " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + " di "
				+ getNominativo(ordineMissione.getUid()) + " per la missione a " + ordineMissione.getDestinazione()
				+ " dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto "
				+ ordineMissione.getOggetto() + " è stata approvata.";
	}

	private String getTextMailApprovazioneAnnullamentoOrdine(OrdineMissione ordineMissione) {
		return "L'ordine di missione " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + " di "
				+ getNominativo(ordineMissione.getUid()) + " per la missione a " + ordineMissione.getDestinazione()
				+ " dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto "
				+ ordineMissione.getOggetto() + " è stata annullato.";
	}

	private String getTextMailAnticipo(OrdineMissione ordineMissione, OrdineMissioneAnticipo anticipo) {
		return "E'  stata approvata la richiesta di anticipo di € " + Utility.numberFormat(anticipo.getImporto())
				+ " relativa all'ordine di missione " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero()
				+ " di " + getNominativo(ordineMissione.getUid()) + " per la missione a "
				+ ordineMissione.getDestinazione() + " dal "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto "
				+ ordineMissione.getOggetto();
	}

	private String getTextMailSendToAdministrative(String basePath, OrdineMissione ordineMissione) {
		return "L'ordine di missione " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + " della uo "
				+ ordineMissione.getUoRich() + " " + ordineMissione.getDatoreLavoroRich() + " di "
				+ getNominativo(ordineMissione.getUid()) + " per la missione a " + ordineMissione.getDestinazione()
				+ " dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto "
				+ ordineMissione.getOggetto() + " è stato inviato per la verifica/completamento dei dati finanziari."
				+ "Si prega di verificarlo attraverso il link " + basePath + "/#/ordine-missione/"
				+ ordineMissione.getId() + "/S";
	}

	private String getTextMailReturnToSender(String basePath, OrdineMissione ordineMissione) {
		return "L'ordine di missione " + ordineMissione.getAnno() + "-" + ordineMissione.getNumero() + " di "
				+ getNominativo(ordineMissione.getUid()) + " per la missione a " + ordineMissione.getDestinazione()
				+ " dal " + DateUtils.getDefaultDateAsString(ordineMissione.getDataInizioMissione()) + " al "
				+ DateUtils.getDefaultDateAsString(ordineMissione.getDataFineMissione()) + " avente per oggetto "
				+ ordineMissione.getOggetto() + " le è stata respinto da " + getNominativo(securityService.getCurrentUserLogin())
				+ " per il seguente motivo: " + ordineMissione.getNoteRespingi()
				+ ". Si prega di effettuare le opportune correzioni attraverso il link " + basePath
				+ "/#/ordine-missione/" + ordineMissione.getId();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteOrdineMissione(Long idOrdineMissione) {
		OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById( OrdineMissione.class,
				idOrdineMissione);
		if (ordineMissione != null && isUserEnabledToViewMissione(ordineMissione)) {
			controlloOperazioniCRUDDaGui(ordineMissione);
			ordineMissioneAnticipoService.deleteAnticipo(ordineMissione);
			ordineMissioneAutoPropriaService.deleteAutoPropria(ordineMissione);
			// effettuo controlli di validazione operazione CRUD
			ordineMissione.setStato(Costanti.STATO_ANNULLATO);
			ordineMissione.setToBeUpdated();
			if (ordineMissione.isStatoRespintoFlusso() && !StringUtils.isEmpty(ordineMissione.getIdFlusso())) {
				cmisOrdineMissioneService.annullaFlusso(ordineMissione);
			}
			crudServiceBean.modificaConBulk( ordineMissione);
		}
	}

	public void controlloOperazioniCRUDDaGui(OrdineMissione ordineMissione) {
		if (!ordineMissione.isMissioneInserita()) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					"Non è possibile effettuare l'operazione su un ordine di missione che non si trova in uno stato "
							+ Costanti.STATO.get(Costanti.STATO_INSERITO));
		}
	}

	private void controlloDatiObbligatoriDaGUI(OrdineMissione ordineMissione) {
		if (ordineMissione != null) {
			if (StringUtils.isEmpty(ordineMissione.getCdsRich())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Cds Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getCdsSpesa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Cds Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getUoRich())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Uo Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getUoSpesa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Uo Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Cdr Spesa");
			} else if (StringUtils.isEmpty(ordineMissione.getDataInizioMissione())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Inizio Missione");
			} else if (StringUtils.isEmpty(ordineMissione.getDataFineMissione())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Fine Missione");
			} else if (StringUtils.isEmpty(ordineMissione.getDataInserimento())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Data Inserimento");
			} else if (StringUtils.isEmpty(ordineMissione.getDatoreLavoroRich())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						CodiciErrore.CAMPO_OBBLIGATORIO + ": Datore di Lavoro Richiedente");
			} else if (StringUtils.isEmpty(ordineMissione.getDestinazione())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Destinazione");
			} else if (StringUtils.isEmpty(ordineMissione.getOggetto())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Oggetto");
			} else if (StringUtils.isEmpty(ordineMissione.getPriorita())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Priorità");
			} else if (StringUtils.isEmpty(ordineMissione.getTipoMissione())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Tipo Missione");
			}
			if (ordineMissione.isMissioneEstera()) {
				if (StringUtils.isEmpty(ordineMissione.getNazione())
						|| Costanti.NAZIONE_ITALIA_SIGLA.compareTo(ordineMissione.getNazione()) == 0) {
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Nazione");
				}
				if (StringUtils.isEmpty(ordineMissione.getTrattamento())) {
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Trattamento");
				}
			}

			if (StringUtils.isEmpty(ordineMissione.getPartenzaDa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Partenza Da");
			}

			if (Utility.nvl(ordineMissione.getPartenzaDa(), "N").equals("A")) {
				if (StringUtils.isEmpty(ordineMissione.getPartenzaDaAltro())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							CodiciErrore.CAMPO_OBBLIGATORIO + ": Specificare il luogo di partenza");
				}
			}

			if (ordineMissione.isMissioneConGiorniDivervi()) {
				if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							CodiciErrore.CAMPO_OBBLIGATORIO + ": Obbligo di Rientro");
				}
			}
			if (ordineMissione.isMissioneDipendente()) {
				if (StringUtils.isEmpty(ordineMissione.getComuneResidenzaRich())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							CodiciErrore.CAMPO_OBBLIGATORIO + ": Comune di Residenza del Richiedente");
				} else if (StringUtils.isEmpty(ordineMissione.getIndirizzoResidenzaRich())) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							CodiciErrore.CAMPO_OBBLIGATORIO + ": Indirizzo di Residenza del Richiedente");
				}
			}
		}
	}

	private void controlloCongruenzaDatiInseriti(OrdineMissione ordineMissione) {
		if (ordineMissione.getDataFineMissione().isBefore(ordineMissione.getDataInizioMissione())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI
					+ ": La data di fine missione non può essere precedente alla data di inizio missione");
		}
		if (DateUtils.getDateAsString(ordineMissione.getDataFineMissione(), DateUtils.PATTERN_DATETIME_NO_SEC).equals(
				DateUtils.getDateAsString(ordineMissione.getDataInizioMissione(), DateUtils.PATTERN_DATETIME_NO_SEC))) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.ERR_DATE_INCONGRUENTI
					+ ": Le date di inizio e fine missione non possono essere uguali");
		}

		if (StringUtils.isEmpty(ordineMissione.getIdFlusso()) && ordineMissione.isStatoInviatoAlFlusso()) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					"Non è possibile avere lo stato Inviato al flusso e non avere l'ID del flusso");
		}
		if (!ordineMissione.isMissioneEstera()) {
			ordineMissione.setNazione(new Long("1"));
		}
		// if (ordineMissione.getUtilizzoAutoNoleggio() != null &&
		// ordineMissione.getUtilizzoAutoNoleggio().equals("S") &&
		// !ordineMissione.isToBeCreated() && getAutoPropria(ordineMissione) !=
		// null ){
		// throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile
		// salvare una missione con la richiesta di utilizzo dell'auto a
		// noleggio e dell'auto propria.");
		// }
		// if (ordineMissione.getUtilizzoTaxi() != null &&
		// ordineMissione.getUtilizzoTaxi().equals("S") &&
		// !ordineMissione.isToBeCreated() && getAutoPropria(ordineMissione) !=
		// null ){
		// throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile
		// salvare una missione con la richiesta di utilizzo del taxi e
		// dell'auto propria.");
		// }
		if (!StringUtils.isEmpty(ordineMissione.getNoteUtilizzoTaxiNoleggio())) {
			if (ordineMissione.getUtilizzoTaxi().equals("N") && ordineMissione.getUtilizzoAutoNoleggio().equals("N")
					&& ordineMissione.getUtilizzoAutoServizio().equals("N")
					&& ordineMissione.getPersonaleAlSeguito().equals("N")) {
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
						+ ": Non è possibile indicare le note all'utilizzo del taxi o dell'auto a noleggio o dell'auto di servizio o del personale al seguito se non si è scelto il loro utilizzo");
			}
		}
		// if (ordineMissione.getUtilizzoAutoServizio() != null &&
		// ordineMissione.getUtilizzoAutoServizio().equals("S") &&
		// !ordineMissione.isToBeCreated() && getAutoPropria(ordineMissione) !=
		// null ){
		// throw new AwesomeException(CodiciErrore.ERRGEN, "Non è possibile
		// salvare una missione con la richiesta di utilizzo dell'auto di
		// servizio e dell'auto propria.");
		// }
		if ((Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S")
				|| Utility.nvl(ordineMissione.getUtilizzoAutoServizio()).equals("S")
				|| Utility.nvl(ordineMissione.getPersonaleAlSeguito()).equals("S")
				|| Utility.nvl(ordineMissione.getUtilizzoTaxi()).equals("S"))
				&& StringUtils.isEmpty(ordineMissione.getNoteUtilizzoTaxiNoleggio())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
					+ ": E' obbligatorio indicare le note all'utilizzo del taxi o dell'auto a noleggio o dell'auto di servizio o del personale al seguito se si è scelto il loro utilizzo");
		}
		// if
		// ((Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S")
		// && Utility.nvl(ordineMissione.getUtilizzoAutoServizio()).equals("S"))
		// ||
		// (Utility.nvl(ordineMissione.getUtilizzoTaxi()).equals("S") &&
		// Utility.nvl(ordineMissione.getUtilizzoAutoServizio()).equals("S")) ||
		// (Utility.nvl(ordineMissione.getUtilizzoTaxi()).equals("S") &&
		// Utility.nvl(ordineMissione.getUtilizzoAutoNoleggio()).equals("S"))){
		// throw new AwesomeException(CodiciErrore.ERRGEN,
		// CodiciErrore.DATI_INCONGRUENTI+": Scegliere solo un utilizzo
		// dell'auto ");
		// }
		if (ordineMissione.isFondiCompetenza() && !StringUtils.isEmpty(ordineMissione.getEsercizioObbligazione())
				&& !StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())
				&& ordineMissione.getEsercizioObbligazione()
						.compareTo(ordineMissione.getEsercizioOriginaleObbligazione()) != 0) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Incongruenza tra fondi e esercizio obbligazione.");
		}
		if (ordineMissione.isFondiResiduo() && !StringUtils.isEmpty(ordineMissione.getEsercizioObbligazione())
				&& !StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())
				&& ordineMissione.getEsercizioObbligazione()
						.compareTo(ordineMissione.getEsercizioOriginaleObbligazione()) <= 0) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Incongruenza tra fondi e esercizio obbligazione.");
		}
		if (ordineMissione.isTrattamentoAlternativoMissione()) {
			long oreDifferenza = ChronoUnit.HOURS.between(
					ordineMissione.getDataInizioMissione().truncatedTo(ChronoUnit.MINUTES),
					ordineMissione.getDataFineMissione().truncatedTo(ChronoUnit.MINUTES));
			if (oreDifferenza < 24) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Per il trattamento alternativo di missione è necessario avere una durata non inferiore a 24 ore.");
			}
		}
		if (Utility.nvl(ordineMissione.getMissioneGratuita()).equals("S") && ordineMissione.getImportoPresunto() != null
				&& ordineMissione.getImportoPresunto().compareTo(BigDecimal.ZERO) != 0) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					"Non è possibile inserire una missione con spese a carico di altro ente e l'importo presunto");
		}
		if (!StringUtils.hasLength(ordineMissione.getMatricola())) {
			ordineMissione.setMatricola(null);
		}
	}

	private void controlloDatiFinanziari(OrdineMissione ordineMissione) {
		UnitaOrganizzativa uo = unitaOrganizzativaService.loadUo(ordineMissione.getUoSpesa(),
				ordineMissione.getCdsSpesa(), ordineMissione.getAnno());
		if (uo == null) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI + ": La UO "
					+ ordineMissione.getUoSpesa() + " non è corretta rispetto al CDS " + ordineMissione.getCdsSpesa());
		}
		if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
			Cdr cdr = cdrService.loadCdr(ordineMissione.getCdrSpesa(), ordineMissione.getUoSpesa());
			if (cdr == null) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						CodiciErrore.DATI_INCONGRUENTI + ": Il CDR " + ordineMissione.getCdrSpesa()
								+ " non è corretto rispetto alla UO " + ordineMissione.getUoSpesa());
			}
		}
		LocalDate data = LocalDate.now();
		int anno = data.getYear();
		if (!StringUtils.isEmpty(ordineMissione.getPgProgetto())) {
			Progetto progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), anno,
					ordineMissione.getUoSpesa());
			if (progetto == null) {
				progetto = progettoService.loadModulo(ordineMissione.getPgProgetto(), ordineMissione.getAnno(),
						ordineMissione.getUoSpesa());
				if (progetto == null) {
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
							+ ": Il modulo indicato non è corretto rispetto alla UO " + ordineMissione.getUoSpesa());
				}
			}
		}
		if (!StringUtils.isEmpty(ordineMissione.getGae())) {
			if (StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Non è possibile indicare la GAE senza il centro di responsabilità");
			}
			Gae gae = gaeService.loadGae(ordineMissione);
			if (gae == null) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"La GAE " + ordineMissione.getGae() + " indicata non esiste");
			} else {
				boolean progettoCdrIndicato = false;
				if (!StringUtils.isEmpty(ordineMissione.getPgProgetto())
						&& !StringUtils.isEmpty(gae.getPg_progetto())) {
					progettoCdrIndicato = true;
					if (gae.getPg_progetto().compareTo(ordineMissione.getPgProgetto()) != 0) {
						throw new AwesomeException(CodiciErrore.ERRGEN,
								CodiciErrore.DATI_INCONGRUENTI + ": La GAE indicata " + ordineMissione.getGae()
										+ " non corrisponde al modulo indicato.");
					}
				}
				if (!StringUtils.isEmpty(ordineMissione.getCdrSpesa())) {
					progettoCdrIndicato = true;
					if (!gae.getCd_centro_responsabilita().equals(ordineMissione.getCdrSpesa())) {
						throw new AwesomeException(CodiciErrore.ERRGEN,
								CodiciErrore.DATI_INCONGRUENTI + ": La GAE indicata " + ordineMissione.getGae()
										+ " non corrisponde con il CDR " + ordineMissione.getCdrSpesa() + " indicato.");
					}
				}
				if (!progettoCdrIndicato) {
					throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
							+ ": Non è possibile indicare solo La GAE senza il modulo o il CDR.");
				}
			}
		}

		if (!StringUtils.isEmpty(ordineMissione.getPgObbligazione())) {
			if (StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Oltre al numero dell'impegno è necessario indicare anche l'anno dell'impegno");
			}
			if (!StringUtils.isEmpty(ordineMissione.getGae())) {
				ImpegnoGae impegnoGae = impegnoGaeService.loadImpegno(ordineMissione);
				if (impegnoGae == null) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
									+ ordineMissione.getPgObbligazione() + " non corrisponde con la GAE "
									+ ordineMissione.getGae() + " indicata oppure non esiste");
				} else {
					if (!StringUtils.isEmpty(ordineMissione.getVoce())) {
						if (!impegnoGae.getCdElementoVoce().equals(ordineMissione.getVoce())) {
							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
									+ ": L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
									+ ordineMissione.getPgObbligazione()
									+ " non corrisponde con la voce di Bilancio indicata." + ordineMissione.getVoce());
						}
					} else {
						ordineMissione.setVoce(impegnoGae.getCdElementoVoce());
					}
					ordineMissione.setCdCdsObbligazione(impegnoGae.getCdCds());
					ordineMissione.setEsercizioObbligazione(impegnoGae.getEsercizio());
				}
			} else {
				Impegno impegno = impegnoService.loadImpegno(ordineMissione);
				if (impegno == null) {
					throw new AwesomeException(CodiciErrore.ERRGEN,
							"L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
									+ ordineMissione.getPgObbligazione() + " non esiste");
				} else {
					if (!StringUtils.isEmpty(ordineMissione.getVoce())) {
						if (!impegno.getCdElementoVoce().equals(ordineMissione.getVoce())) {
							throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.DATI_INCONGRUENTI
									+ ": L'impegno indicato " + ordineMissione.getEsercizioOriginaleObbligazione() + "-"
									+ ordineMissione.getPgObbligazione()
									+ " non corrisponde con la voce di Bilancio indicata." + ordineMissione.getVoce());
						}
					} else {
						ordineMissione.setVoce(impegno.getCdElementoVoce());
					}
					ordineMissione.setCdCdsObbligazione(impegno.getCdCds());
					ordineMissione.setEsercizioObbligazione(impegno.getEsercizio());
				}
			}
		} else {
			if (!StringUtils.isEmpty(ordineMissione.getEsercizioOriginaleObbligazione())
					&& StringUtils.isEmpty(ordineMissione.getFondi())) {
				throw new AwesomeException(CodiciErrore.ERRGEN,
						"Oltre all'anno dell'impegno è necessario indicare anche il numero dell'impegno");
			}
			ordineMissione.setCdCdsObbligazione(null);
			ordineMissione.setEsercizioObbligazione(null);
		}

	}

	private void validaCRUD(OrdineMissione ordineMissione) {
		if (ordineMissione != null) {
			controlloCampiObbligatori(ordineMissione);
			controlloCongruenzaDatiInseriti(ordineMissione);
			controlloDatiFinanziari(ordineMissione);
			DatiIstituto istituto = datiIstitutoService.getDatiIstituto(ordineMissione.getUoSpesa(),
					ordineMissione.getAnno());
			if (istituto == null) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Dati uo non presenti per il codice "
						+ ordineMissione.getUoSpesa() + " nell'anno " + ordineMissione.getAnno());
			}
		}
	}

	private void controlloCampiObbligatori(OrdineMissione ordineMissione) {
		// if (!ordineMissione.isToBeCreated()){
		controlloDatiObbligatoriDaGUI(ordineMissione);
		// }
		if (StringUtils.isEmpty(ordineMissione.getAnno())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Anno");
		} else if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Obbligo di Rientro");
		} else if (StringUtils.isEmpty(ordineMissione.getUid())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Utente");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Utilizzo del Taxi");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoServizio())) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					CodiciErrore.CAMPO_OBBLIGATORIO + ": Utilizzo dell'auto di servizio");
		} else if (StringUtils.isEmpty(ordineMissione.getPersonaleAlSeguito())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Personale al seguito");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					CodiciErrore.CAMPO_OBBLIGATORIO + ": Utilizzo auto a noleggio");
		} else if (StringUtils.isEmpty(ordineMissione.getStato())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Stato");
		} else if (StringUtils.isEmpty(ordineMissione.getValidato())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Validato");
		} else if (StringUtils.isEmpty(ordineMissione.getNumero())) {
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Numero");
		}
	}

	public List<CMISFileAttachment> getAttachments(Long idOrdineMissione)
			throws ComponentException {
		if (idOrdineMissione != null) {
			OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById( OrdineMissione.class,
					idOrdineMissione);
			if (ordineMissione != null && isUserEnabledToViewMissione(ordineMissione)) {
				List<CMISFileAttachment> lista = cmisOrdineMissioneService.getAttachmentsOrdineMissione(ordineMissione,
						idOrdineMissione);
				return lista;
			}
		}
		return null;
	}

	public CMISFileAttachment uploadAllegato(Long idOrdineMissione, InputStream inputStream,
			String name, MimeTypes mimeTypes) throws ComponentException {
		OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById( OrdineMissione.class,
				idOrdineMissione);
		if (ordineMissione != null && isUserEnabledToViewMissione(ordineMissione)) {
			return cmisOrdineMissioneService.uploadAttachmentOrdineMissione(ordineMissione, idOrdineMissione,
					inputStream, name, mimeTypes);
		}
		return null;
	}

	public void gestioneCancellazioneAllegati(String idNodo, Long idOrdineMissione) {
		if (idOrdineMissione != null) {
			OrdineMissione ordineMissione = (OrdineMissione) crudServiceBean.findById( OrdineMissione.class,
					idOrdineMissione);
			if (ordineMissione != null && StringUtils.hasLength(ordineMissione.getIdFlusso()) && isUserEnabledToViewMissione(ordineMissione)) {
				StorageObject folderOrdineMissione = cmisOrdineMissioneService.recuperoFolderOrdineMissione(ordineMissione);
				missioniCMISService.eliminaFilePresenteNelFlusso(idNodo, folderOrdineMissione);
			} else {
				missioniCMISService.deleteNode(idNodo);
			}
		}
	}
	public void popolaCoda(String id){
		OrdineMissione missione = (OrdineMissione)crudServiceBean.findById(OrdineMissione.class, new Long(id));
		popolaCoda(missione);
	}
	public void aggiornaOrdineMissioneApprovato(OrdineMissione ordineMissioneDaAggiornare) {
		ordineMissioneDaAggiornare.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
		ordineMissioneDaAggiornare.setStato(Costanti.STATO_DEFINITIVO);
		gestioneEmailDopoApprovazione(ordineMissioneDaAggiornare);
		OrdineMissioneAnticipo anticipo = getAnticipo(ordineMissioneDaAggiornare);
		if (anticipo != null) {
			anticipo.setStato(Costanti.STATO_DEFINITIVO);
			ordineMissioneAnticipoService.updateAnticipo(anticipo, false);
			DatiIstituto dati = datiIstitutoService.getDatiIstituto(ordineMissioneDaAggiornare.getUoSpesa(),
					ordineMissioneDaAggiornare.getAnno());
			if (dati != null && dati.getMailNotifiche() != null) {
				if (!dati.getMailNotifiche().equals("N")){
					mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo), false,
							true, dati.getMailNotifiche());
				}
			} else {
				List<UsersSpecial> lista = accountService
						.getUserSpecialForUoPerValidazione(ordineMissioneDaAggiornare.getUoSpesa());
				if (lista != null && lista.size() > 0) {
					mailService.sendEmail(subjectAnticipo, getTextMailAnticipo(ordineMissioneDaAggiornare, anticipo),
							false, true, mailService.prepareTo(lista));
				}
			}
		}
		updateOrdineMissione(ordineMissioneDaAggiornare, true);
		popolaCoda(ordineMissioneDaAggiornare);
	}
	public List<StorageObject> getDocumentsOrdineMissione(OrdineMissione missione) throws ComponentException{
		return cmisOrdineMissioneService.getAllDocumentsOrdineMissione(missione);
	}
}
