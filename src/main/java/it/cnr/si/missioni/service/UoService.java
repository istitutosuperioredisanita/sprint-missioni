package it.cnr.si.missioni.service;

import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.data.Uo;
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

}
