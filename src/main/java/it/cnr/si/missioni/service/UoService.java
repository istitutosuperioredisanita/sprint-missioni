package it.cnr.si.missioni.service;

import java.util.Iterator;
import java.util.List;

import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Transactional(propagation = Propagation.REQUIRED)
    public Account getDirettore(String uo) {
    	DatiUo datiUo = configService.getDatiUo();
    	if (datiUo != null && datiUo.getUo() != null){
	    	for (Uo unita : datiUo.getUo()){
	    		if (unita != null && unita.getCodiceUo().equals(uo)){
	    			return accountService.loadAccountFromRest(unita.getUidDirettore());
	    		}
	    	}
    	}
    	return null;
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

}
