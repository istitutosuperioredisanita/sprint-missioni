package it.cnr.si.missioni.service;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.spring.storage.StorageObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISOrdineMissioneService;
import it.cnr.si.missioni.cmis.CMISRimborsoMissioneService;
import it.cnr.si.missioni.cmis.ResultFlows;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.Costanti;
import org.springframework.util.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class FlowsMissioniService {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneService.class);

    @Autowired
    private Environment env;

    @Autowired
	OrdineMissioneService ordineMissioneService;
	
    @Autowired
	AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;
	
    @Autowired
	RimborsoMissioneService rimborsoMissioneService;
	
    @Autowired
	CMISOrdineMissioneService cmisOrdineMissioneService;
	
	@Autowired
	private CRUDComponentSession crudServiceBean;
	
    @Autowired
	CMISRimborsoMissioneService cmisRimborsoMissioneService;

	@Autowired
	AccountService accountService;

	@Autowired
	MailService mailService;

	@Autowired
	ProxyService proxyService;

	@Value("${spring.proxy.vecchiaScrivania.url}")
	private String urlVecchiaScrivania;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaOrdineMissioneFlowsNewTransaction(Principal principal, Serializable idOrdineMissione) throws Exception {
    	return aggiornaOrdineMissioneFlows(principal, idOrdineMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaAnnullamentoOrdineMissioneFlowsNewTransaction(Principal principal, Serializable idAnnullamentoMissione) throws Exception {
    	return aggiornaAnnullamentoOrdineMissioneFlows(principal, idAnnullamentoMissione);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlowsNewTransaction(Principal principal, Serializable idRimborsoMissione) throws Exception {
    	return aggiornaRimborsoMissioneFlows(principal, idRimborsoMissione);
    }

	public ResultFlows aggiornaOrdineMissioneFlows(Principal principal, Serializable idOrdineMissione)
			throws ComponentException, Exception {
		if (idOrdineMissione != null){
			OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, idOrdineMissione);
			if (ordineMissione.isStatoInviatoAlFlusso() && !ordineMissione.isMissioneDaValidare()){
				ResultFlows result = retrieveDataFromFlows(ordineMissione);
				if (result == null){
					return null;
				}
				if (result.isApprovato()){
					log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
					ordineMissioneService.aggiornaOrdineMissioneApprovato(principal, ordineMissione);
					return result;
				} else if (result.isAnnullato()){
					log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
					ordineMissioneService.aggiornaOrdineMissioneAnnullato(principal, ordineMissione);
					return result;
				} else if (result.isStateReject()){
					log.info("Trovato in Scrivania Digitale un ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", ordineMissione.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
					ordineMissioneService.aggiornaOrdineMissioneAnnullato(principal, ordineMissione);
					Account account = accountService.getAccount(ordineMissione.getUid());
					String subject = "Ordine di missione respinto ed annullato.";
//TODO
					String text = "L'ordine di missione "+ordineMissione.getAnno()+"/"+ordineMissione.getNumero()+" di "+account.getCognome()+" "+account.getNome()+" a "+ordineMissione.getDestinazione()+" per "+ ordineMissione.getOggetto()+" è stato respinto ed annullato con la seguente motivazione: "+""+". Nel caso la richiesta di missione fosse ancora valida sarà necessario inserirla nuovamente.";
					sendMailFlussoRespintoVecchiaScrivania(ordineMissione.getUid(), ordineMissione.getUidInsert(), ordineMissione.getUtuv(), subject, text);
					return result;
				}
			}
		}
		return null;
	}

	private ResultFlows retrieveDataFromFlows(OrdineMissione ordineMissione)
			throws ComponentException {
		ResultFlows result = null;

		StorageObject storage = null;
		try {
			storage = cmisOrdineMissioneService.getStorageObjectOrdineMissione(ordineMissione);
		} catch (ComponentException e1) {
			throw new ComponentException("Errore nel recupero del contenuto del file sul documentale ("
					+ Utility.getMessageException(e1) + ")", e1);
		}
		if (storage != null) {
			StorageObject so = null;

		}


		return result;
	}

	private ResultFlows retrieveDataFromFlows(AnnullamentoOrdineMissione annullamento)
			throws ComponentException {
		ResultFlows result = null;
//				cmisOrdineMissioneService.getFlowsOrdineMissione(annullamento.getIdFlusso());
		return result;
	}

	private ResultFlows retrieveDataFromFlows(RimborsoMissione rimborsoMissione)
			throws ComponentException {
		ResultFlows result = null;
		StorageObject storage = null;
		try {
			storage = cmisRimborsoMissioneService.getStorageRimborsoMissione(rimborsoMissione);
		} catch (ComponentException e1) {
			throw new ComponentException("Errore nel recupero del contenuto del file sul documentale ("
					+ Utility.getMessageException(e1) + ")", e1);
		}
		if (storage != null) {
			result.setState(storage.getPropertyValue("wfcnr:statoFlusso"));
			result.setComment(storage.getPropertyValue("wfcnr:commentoFirma"));
		}
		return result;
	}

	public ResultFlows aggiornaAnnullamentoOrdineMissioneFlows(Principal principal, Serializable idAnnullamento)
			throws ComponentException, Exception {
		if (idAnnullamento != null){
			AnnullamentoOrdineMissione annullamento = (AnnullamentoOrdineMissione)crudServiceBean.findById(principal, AnnullamentoOrdineMissione.class, idAnnullamento);
			if (annullamento.isStatoInviatoAlFlusso() && !annullamento.isMissioneDaValidare()){
				ResultFlows result = null;
				if (result == null){
					return null;
				}
				if (result.isApprovato()){

					OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
					log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} approvato.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
//TODO					annullamentoOrdineMissioneService.aggiornaAnnullamentoMissioneApprovato(principal, annullamento, ordineMissione);
					return result;
				} else if (result.isAnnullato()){
					OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
	    			log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} annullato.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
//TODO	    			annullamentoOrdineMissioneService.aggiornaAnnullamentoOrdineMissioneAnnullato(principal, annullamento);
					return result;
				} else if (result.isStateReject()){
					OrdineMissione ordineMissione = (OrdineMissione)crudServiceBean.findById(principal, OrdineMissione.class, annullamento.getOrdineMissione().getId());
	    			log.info("Trovato in Scrivania Digitale un annullamento ordine di missione con id {} della uo {}, anno {}, numero {} respinto.", annullamento.getId(), ordineMissione.getUoRich(), ordineMissione.getAnno(), ordineMissione.getNumero());
//TODO	    			annullamentoOrdineMissioneService.aggiornaAnnullamentoRespinto(principal, result, annullamento);
			    	return result;
				}
			}
		}
		return null;
	}

	private boolean isDevProfile(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return true;
   		}
   		return false;
	}

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ResultFlows aggiornaRimborsoMissioneFlows(Principal principal, Serializable idRimborsoMissione) throws ComponentException, Exception {
		if (idRimborsoMissione != null){
			RimborsoMissione rimborsoMissione = (RimborsoMissione)crudServiceBean.findById(principal, RimborsoMissione.class, idRimborsoMissione);
	    	if (rimborsoMissione.isStatoInviatoAlFlusso() && !rimborsoMissione.isMissioneDaValidare()){
	    		ResultFlows result = retrieveDataFromFlows(rimborsoMissione);
				if (result == null){
					return null;
				}
	    		if (result.isApprovato()){
	    			log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} approvato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
//TODO
//	    			rimborsoMissioneService.aggiornaRimborsoMissioneApprovato(principal, rimborsoMissione);
					return result;
	    		} else if (result.isAnnullato()){
	    			log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} annullato.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
					rimborsoMissioneService.aggiornaRimborsoMissioneAnnullato(principal, rimborsoMissione);
					return result;
				} else if (result.isStateReject()){
	    			log.info("Trovato in Scrivania Digitale un rimborso missione con id {} della uo {}, anno {}, numero {} respinto.", rimborsoMissione.getId(), rimborsoMissione.getUoRich(), rimborsoMissione.getAnno(), rimborsoMissione.getNumero());
//TODO
//					rimborsoMissioneService.aggiornaRimborsoMissioneRespinto(principal, result, rimborsoMissione);
			    	return result;
				}
//	    	} else {
//	    		rimborsoMissioneService.ribaltaMissione(principal, rimborsoMissione);
	    	}
		}
    	return null;
    }

	private void sendMailFlussoRespintoVecchiaScrivania(String uid, String uidInsert, String utuv, String subject, String text){
		List<String> listaMail = new ArrayList<>();

		String mailUid = getEmailUser(uid);

		aggiungiMail(listaMail, mailUid);
		String mailUidInsert = getEmailUser(uidInsert);
		aggiungiMail(listaMail, mailUidInsert);
		String mailUtuv = getEmailUser(utuv);
		aggiungiMail(listaMail, mailUtuv);
		if (listaMail.size() > 0){
			String[] mailsTo = mailService.preparaElencoMail(listaMail);
			mailService.sendEmail(subject, text, false, true, mailsTo);
		}
	}

	private String getEmailUser(String uid) {
		if (!uid.equals(Costanti.USER_MISSIONI)){
			return accountService.getEmail(uid);
		}
		return null;
	}

	private void aggiungiMail(List<String> listaMail, String mailUid) {
		if (mailUid != null) {
			listaMail.add(mailUid);
		}
	}


}
