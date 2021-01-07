package it.cnr.si.missioni.util.proxy.json.service;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import it.cnr.si.missioni.service.*;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.enums.TipoAppartenenza;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.CallCache;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.DatiDirettore;
import it.cnr.si.missioni.util.proxy.json.object.DatiGruppoSAC;

@SpringBootApplication(scanBasePackages={
		"it.cnr.si.service"})
@Service
public class AccountService {
	private transient static final Log logger = LogFactory.getLog(AccountService.class);

	@Autowired
    private ProxyService proxyService;

	@Autowired
    private ConfigService configService;

	@Autowired
    private DatiSedeService datiSedeService;

	@Autowired
    private MissioniCMISService missioniCMISService;

	@Autowired
    private DatiIstitutoService datiIstitutoService;

	@Autowired
    private UoService uoService;

	@Autowired
	MissioniAceService missioniAceService;

	public UsersSpecial getUoForUsersSpecial(String uid){
		if (configService.getDataUsersSpecial() != null && configService.getDataUsersSpecial().getUsersSpecials() != null ){
			for (Iterator<UsersSpecial> iteratorUsers = configService.getDataUsersSpecial().getUsersSpecials().iterator(); iteratorUsers.hasNext();){
				UsersSpecial user = iteratorUsers.next();
				if (user.getUid() != null && user.getUid().equalsIgnoreCase(uid)){
					return user;
				}
			}
		}
		return null;
	}
	
	public List<UsersSpecial> getUserSpecialForUo(String uo, Boolean isPerValidazione){
		List<UsersSpecial> listaUtenti = new ArrayList<UsersSpecial>();
		logger.info("Ricerca amministrativi per mail della uo: "+uo);
		if (configService.getDataUsersSpecial() != null && configService.getDataUsersSpecial().getUsersSpecials() != null ){
			for (Iterator<UsersSpecial> iteratorUsers = configService.getDataUsersSpecial().getUsersSpecials().iterator(); iteratorUsers.hasNext();){
				UsersSpecial user = iteratorUsers.next();
				logger.debug("Ricerca amministrativi per mail. Utente: "+user.getUid());
				if (Utility.nvl(user.getAll(),"N").equals("N") && isUtenteAbilitatoUo(user.getUoForUsersSpecials(),uo, isPerValidazione)){
					logger.info("User special to be able: "+user.getUid());
					listaUtenti.add(user);
				}
			}
		}
		return listaUtenti;
	}
	
	public Boolean isUserSpecialEnableToValidateOrder(String user, String uo){
		if (uo == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "UO non indicata.");
		}
		Uo datiUo = uoService.recuperoUoSigla(uo);
		if (datiUo != null && Utility.nvl(datiUo.getOrdineDaValidare(),"N").equals("N")){
			return true;
		}
		UsersSpecial userSpecial = getUoForUsersSpecial(user);
		if (userSpecial != null){
			if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
				if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
			    	for (UoForUsersSpecial uoForUsersSpecial : userSpecial.getUoForUsersSpecials()){
			    		if (uo.equals(getUoSigla(uoForUsersSpecial)) && Utility.nvl(uoForUsersSpecial.getOrdine_da_validare()).equals("S")){
			    			return true;
			    		}
			    	}
				}
			} else if (userSpecial.getAll().equals("S")){
				return true;
			}
		}
		return false;
	}
	
	public List<UsersSpecial> getUserSpecialForUoPerValidazione(String uo){
		return getUserSpecialForUo(uo, true);
	}
	
	public Boolean isUtenteAbilitatoUo(List<UoForUsersSpecial> listUo, String uo, Boolean isPerValidazione){
		for (Iterator<UoForUsersSpecial> iteratorUo = listUo.iterator(); iteratorUo.hasNext();){
			UoForUsersSpecial uoForUsersSpecial = iteratorUo.next();
			if (uoForUsersSpecial.getCodice_uo() != null && getUoSigla(uoForUsersSpecial).equals(uo)){
				if (isPerValidazione){
					if (Utility.nvl(uoForUsersSpecial.getOrdine_da_validare()).equals("S")){
						return true;
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	public String manageResponseForAccountRest(String body) {
		Account account = getAccount(body);
		if (account != null){
			return getResponseAccount(null, account, Boolean.FALSE);
		}
		return null;
	}

	private String manageResponseForAccountRest(String uid, Account account, Boolean loadSpecialUserData) {
		if (account != null){
			return getResponseAccount(uid, account, loadSpecialUserData);
		}
		return null;
	}

	public String manageResponseForAccountRest(String uid, 
			String body) {
		Account account = getAccount(body);
		if (account != null){
			return getResponseAccount(uid, account, true);
		}
		return null;
	}

	private String getResponseAccount(String uid, Account account, Boolean loadSpecialUserData) {
		UsersSpecial user = null;
		if (loadSpecialUserData){
			user = getUoForUsersSpecial(uid);
		}
		return createResponseForAccountRest(account, user);
	}

	public String createResponseForAccountRest(Account account, UsersSpecial user) {
		if (user != null){
			account.setAllUoForUsersSpecial(user.getAll());
			account.setUoForUsersSpecial(user.getUoForUsersSpecials());
		}
		return getBodyAccount(account);
	}

	public Account loadAccountFromRest(String currentLogin){
		return loadAccountFromRest(currentLogin, false);
	}

	public Account loadAccount(String currentLogin){
		return loadAccountFromRest(currentLogin);
	}

	public Account loadAccountFromRest(String currentLogin, Boolean loadSpecialUserData){
		String risposta = getAccount(currentLogin, loadSpecialUserData);
		if (risposta != null) {
			return getAccount(risposta);
		}
		return null;
	}

	public String getAccount(String currentLogin, Boolean loadSpecialUserData) {
		UserInfoDto userInfoDto = missioniAceService.getAccountFromSiper(currentLogin);
		if (userInfoDto != null){
			Account account = new Account(userInfoDto);
			if (loadSpecialUserData){
				List<String> ruoli = missioniAceService.getRoles(currentLogin);
				if (ruoli != null && ruoli.size() > 0){
					if (ruoli.stream().filter(role -> role.equals(Costanti.AMMINISTRATORE_MISSIONI)).count() > 0){
						ruoli.add(AuthoritiesConstants.ADMIN);
					}

					account.setRoles(ruoli);
				}
			}
			String resp = manageResponseForAccountRest(currentLogin, account, loadSpecialUserData);
			if (resp != null){
				return resp;
			}
			return "";
		}
		return null;
	}

	// TODO Da eliminare-

	public String getDirectorFromSede(String codiceSede) {
		CallCache callCache = new CallCache(HttpMethod.GET, null, Costanti.APP_SIPER, Costanti.REST_UO_DIRECTOR, Costanti.REST_UO_SEDE+codiceSede+"&userinfo=true&ruolo=dir", null, null);
		ResultProxy result = proxyService.processInCache(callCache);
		String risposta = result.getBody();
		try {
			ObjectMapper mapper = new ObjectMapper();
			DatiDirettore [] lista = mapper.readValue(risposta, DatiDirettore[].class);
			if (lista != null && lista.length > 0){
				return lista[0].getUid();
			} else if (lista == null || lista.length == 0){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stato possibile recuperare il responsabile per la sede:" + codiceSede);
			}
		} catch (Exception ex) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i responsabili della sede ("+Utility.getMessageException(ex)+").");
		}
		return risposta;
	}

	public String getDirectorFromUo(String uo) {
		CallCache callCache = new CallCache(HttpMethod.GET, null, Costanti.APP_SIPER, Costanti.REST_UO_DIRECTOR, Costanti.REST_UO_TIT_CA+uo+"&userinfo=true&ruolo=dir", null, null);
		ResultProxy result = proxyService.processInCache(callCache);
		String risposta = result.getBody();
		try {
			ObjectMapper mapper = new ObjectMapper();
			DatiDirettore [] lista = mapper.readValue(risposta, DatiDirettore[].class);
			if (lista != null && lista.length > 0){
				return lista[0].getUid();
			} else if (lista == null || lista.length == 0){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stato possibile recuperare il responsabile per la uo:" + uo);
			}
		} catch (Exception ex) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i responsabili della uo ("+Utility.getMessageException(ex)+").");
		}
		return risposta;
	}

	// TODO Fine da eliminare-

	public Boolean isUserSpecialEnableToFinalizeOrder(String user, String uo){
		if (uo == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "UO non indicata.");
		}
		UsersSpecial userSpecial = getUoForUsersSpecial(user);
		if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
			if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
		    	for (UoForUsersSpecial uoForUsersSpecial : userSpecial.getUoForUsersSpecials()){
		    		if (uo.equals(getUoSigla(uoForUsersSpecial)) && Utility.nvl(uoForUsersSpecial.getRendi_definitivo()).equals("S")){
		    			return true;
		    		}
		    	}
			}
		} else {
			return true;
		}
		return false;
	}
	
	private String getUoSigla(UoForUsersSpecial uo) {
		return uo.getCodice_uo().substring(0,3)+"."+uo.getCodice_uo().substring(3,6);
	}

	private Account getAccount(String risposta) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new ParanamerModule());
			Account account = mapper.readValue(risposta, Account.class);
			return account;
		} catch (Exception ex) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella creazione dell'oggetto JSON dei dati dell'Account dalla REST ("+Utility.getMessageException(ex)+").");
		}
	}

	public String getBodyAccount(Account account) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String risposta = mapper.writeValueAsString(account);
			return risposta;
		} catch (Exception ex) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella generazione del body della response dall'oggetto JSON dei dati dell'Account ("+Utility.getMessageException(ex)+").");
		}
	}

    public String getEmail(String user){
		Account utente = loadAccountFromRest(user);
		if (utente != null){
			return utente.getEmail_comunicazioni();
		}
		return null;
    }

	// TODO Da eliminare-
	public String recuperoDirettore(Integer anno, String uo, Boolean isMissioneEstera, Account account, ZonedDateTime data, Boolean isUoRich ) {
		return recuperoDirettore(anno, uo, isMissioneEstera, account, data, isUoRich, false);
	}
	public String recuperoDirettore(Integer anno, String uo, Boolean isMissioneEstera, Account account, ZonedDateTime data, Boolean isUoRich, Boolean fromDatiSAC) {
		String userNameFirmatario;
		DatiSede dati = null;
		Boolean delegaSpesa = false;
		if (account.getCodice_sede() != null && account.getCodice_uo() != null && Utility.getUoSigla(account.getCodice_uo()).equals(Utility.getUoSigla(uo))){
			dati = datiSedeService.getDatiSede(account.getCodice_sede(), data);
			if (dati != null && dati.getResponsabile() != null && Utility.nvl(dati.getDelegaSpesa()).equals("S")){
				delegaSpesa = true;
			}
		}
		if ((!isUoRich && !delegaSpesa) || (account.getMatricola() == null || (account.getData_cessazione() != null && ZonedDateTime.parse(account.getData_cessazione()).compareTo(data) < 0))){
			userNameFirmatario = recuperoDirettoreDaUo(anno, uo, isMissioneEstera);
		} else {
			if (dati != null && dati.getResponsabile() != null){
				if (!isMissioneEstera || (Utility.nvl(dati.getResponsabileSoloItalia(),"N").equals("N"))){
					userNameFirmatario = dati.getResponsabile();
				} else {
					if (StringUtils.isEmpty(dati.getSedeRespEstero())){
						userNameFirmatario = getDirectorFromSede(dati.getCodice_sede());
					} else {
						DatiSede datiAltraSede = datiSedeService.getDatiSede(dati.getSedeRespEstero(), data);
						if (datiAltraSede != null && datiAltraSede.getResponsabile() != null){
							userNameFirmatario = datiAltraSede.getResponsabile();
						} else {
							userNameFirmatario = getDirectorFromSede(dati.getSedeRespEstero());
						}
					}
				}
			} else {
				userNameFirmatario = recuperoDirettoreDaUo(anno, uo, isMissioneEstera);
			}
			if (StringUtils.isEmpty(userNameFirmatario)){
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non è stato possibile recuperare il direttore per la sede "+dati.getCodice_sede());
			}
		}
		if (userNameFirmatario != null && userNameFirmatario.equalsIgnoreCase(account.getUid())){
			if (uo.startsWith(Costanti.CDS_SAC) && isMissioneEstera && !fromDatiSAC){
				DatiGruppoSAC datiSAC = missioniCMISService.getDatiGruppoSAC(Utility.getUoSiper(uo));
				if (datiSAC != null){
					if (datiSAC.getShortName() != null && datiSAC.getShortName().startsWith(Costanti.CDS_SAC)){
						String uoPadre = datiSAC.getShortName().substring(0, 6);
						userNameFirmatario = recuperoDirettore(anno, uoPadre, isMissioneEstera, account, data, isUoRich, true);
					}
				}
			} else {
				DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(Utility.getUoSigla(uo), anno);
				if (datiIstituto != null && !StringUtils.isEmpty(datiIstituto.getUoRespResponsabili())){
					userNameFirmatario = recuperoDirettore(anno, datiIstituto.getUoRespResponsabili(), isMissioneEstera, account, data, isUoRich, fromDatiSAC);
				}
			}
		}

		return userNameFirmatario;
	}

	private String recuperoDirettoreDaUo(Integer anno, String uo, Boolean isMissioneEstera) {
		return recuperoDirettoreDaUo(anno, uo, isMissioneEstera,false);
	}
	private String recuperoDirettoreDaUo(Integer anno, String uo, Boolean isMissioneEstera, Boolean isDaUoRespEstero) {
		String userNameFirmatario;
		DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(Utility.getUoSigla(uo), anno);
		if (datiIstituto != null && datiIstituto.getResponsabile() != null){
			if (!isMissioneEstera || (Utility.nvl(datiIstituto.getResponsabileSoloItalia(),"N").equals("N"))|| (isMissioneEstera && isDaUoRespEstero )){
				userNameFirmatario = datiIstituto.getResponsabile();
			} else {
				if (!StringUtils.isEmpty(datiIstituto.getUoRespEstero()) && !isDaUoRespEstero){
					userNameFirmatario = recuperoDirettoreDaUo(anno, datiIstituto.getUoRespEstero(), isMissioneEstera, true);
				} else {
					userNameFirmatario = getDirettore(uo);
				}
			}
		} else {
			if (isMissioneEstera && Utility.nvl(datiIstituto.getResponsabileSoloItalia(),"N").equals("S") && !StringUtils.isEmpty(datiIstituto.getUoRespEstero()) && !isDaUoRespEstero){
				userNameFirmatario = recuperoDirettoreDaUo(anno, datiIstituto.getUoRespEstero(), isMissioneEstera, true);
			} else {
				userNameFirmatario = getDirettore(uo);
			}
		}
		if (StringUtils.isEmpty(userNameFirmatario)){
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non è stato possibile recuperare il direttore per la uo "+uo);
		}
		return userNameFirmatario;
	}

	// TODO Fine da eliminare-

	public String getDirettore(String uo){
		List<SimpleUtenteWebDto> utenti = missioniAceService.findUtentiCdsuo(uo, LocalDate.now());
		if (utenti != null && utenti.size() > 0){
			return missioniAceService.getDirettore(utenti.get(0).getUsername());
		}
	}
	public Boolean isUserEnableToWorkUo(Principal principal, String uo){
		UsersSpecial userSpecial = getUoForUsersSpecial(principal.getName());
		boolean uoAbilitata = false;
		if (userSpecial != null){
			if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")){
				if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()){
			    	for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()){
			    		if (uoService.getUoSigla(uoUser).equals(uo)){
			    			uoAbilitata = true;
			    		}
			    	} 
				} else {
	    			uoAbilitata = false;
				}
			} else {
    			uoAbilitata = true;
			}
		} else {
			uoAbilitata = false;
		}
		return uoAbilitata;
	}
	
}
