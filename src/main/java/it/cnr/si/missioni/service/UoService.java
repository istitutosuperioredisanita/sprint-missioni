package it.cnr.si.missioni.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;

import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UoService {
    private final Logger log = LoggerFactory.getLogger(UoService.class);

    @Autowired
    ConfigService configService;
    
    @Autowired
    AccountService accountService;

	@Autowired
	MissioniAceService missioniAceService;

	@Autowired
    private Environment env;

    @Transactional(propagation = Propagation.REQUIRED)
    public Account getDirettore(String uo) {
    	String direttore = null;
/*    	if (isDevProfile()){
			Uo unitaOrg = recuperoUo(uo);
			if (unitaOrg != null && unitaOrg.getCodiceUo() != null && unitaOrg.getCodiceUo().equals(uo)){
				direttore = unitaOrg.getUidDirettore();
			}
		} else {*/
			direttore = accountService.getDirettore(uo);
//		}
    	if (direttore != null){
			return accountService.loadAccountFromRest(direttore);
		}
		return null;
    }

	@Transactional(propagation = Propagation.REQUIRED)
	public Account getDirettoreFromUsername(String username) {
		String direttore = null;
		direttore = missioniAceService.getDirettore(username);
		if (direttore != null){
			return accountService.loadAccountFromRest(direttore);
		}
		return null;
	}

	private boolean isDevProfile(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return true;
   		}
   		return false;
	}

	public Uo recuperoUo(String codiceUo){
    	DatiUo datiUo = configService.getDatiUo();
		return recuperoUo(datiUo, codiceUo, false);
	}

    public Uo recuperoUoSigla(String codiceUo){
    	DatiUo datiUo = configService.getDatiUo();
		return recuperoUo(datiUo, codiceUo, true);
	}

    public String getUo(String uo, Boolean uoSigla){
    	if (uoSigla){
        	if (uo != null){
            	return uo.substring(0,3)+"."+uo.substring(3,6);
        	}
    	} else {
    		return uo;
    	}
    	return "";
    }
    
	private Uo recuperoUo(DatiUo datiUo, String codiceUo, Boolean uoSigla) {
		List<Uo> uos = datiUo.getUo();
		for (Iterator<Uo> iterator = uos.iterator(); iterator.hasNext();){
			Uo uo = iterator.next();
			if (uo != null && uo.getCodiceUo() != null && getUo(uo.getCodiceUo(), uoSigla).equals(codiceUo)){
				return uo;
			}
		}
		return null;
	}

	public String getUoSigla(UoForUsersSpecial uo) {
		return getUo(uo.getCodice_uo(), true);
	}

	@Cacheable(value = Costanti.NOME_CACHE_DATI_PERSONE)
	public String getPersone(String uo, String cds) {
		List<SimpleUtenteWebDto> list = null;
		if (cds != null){
			list = missioniAceService.findUtentiIstituto(cds, LocalDate.now());
		} else {
			list = missioniAceService.findUtentiCdsuo(uo, LocalDate.now());
		}
		List<Account> listaAccount = new ArrayList<>();
    	if (list != null){
			for (SimpleUtenteWebDto persona : list){
				listaAccount.add(new Account(persona));
			}
		}
		List<Account> listaOrdineta = listaAccount.stream().sorted(Comparator.comparing(Account::getCognome)).collect(Collectors.toList());
		ObjectMapper mapper = new ObjectMapper();
		String risposta = null;
		try {
			risposta = mapper.writeValueAsString(listaOrdineta);
		} catch (JsonProcessingException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella conversione del JSON della listaAccount ("+ Utility.getMessageException(e)+").");
		}
		return risposta;
	}

}
