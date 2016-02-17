package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.DatiIstitutoRepository;

import java.security.Principal;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
public class DatiIstitutoService {

    private final Logger log = LoggerFactory.getLogger(DatiIstitutoService.class);

	@Inject
	private CRUDComponentSession crudServiceBean;

    @Inject
    private DatiIstitutoRepository datiIstitutoRepository;

    @Transactional(readOnly = true)
    public DatiIstituto getDatiIstituto(String istituto, Integer anno) {
        return datiIstitutoRepository.getDatiIstituto(istituto, anno);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private DatiIstituto getDatiIstitutoAndLock(String istituto, Integer anno) {
        return datiIstitutoRepository.getDatiIstitutoAndLock(istituto, anno);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long getNextPG (Principal principal, String istituto, Integer anno) 
    		throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
    	DatiIstituto datiIstituto = null;
   		datiIstituto = getDatiIstitutoAndLock(istituto, anno);
    	Long pgCorrente = null;
    	if (datiIstituto != null){
    		pgCorrente = new Long(datiIstituto.getProgressivoOrdine()+1);
    		datiIstituto.setProgressivoOrdine(pgCorrente);
    		datiIstituto.setUser(principal.getName());
    		datiIstituto.setToBeUpdated();
    		datiIstituto = (DatiIstituto)crudServiceBean.modificaConBulk(principal, datiIstituto);
    		//			    	autoPropriaRepository.save(autoPropria);
    		log.debug("Updated Information for Dati Istituto: {}", datiIstituto);
    		return datiIstituto.getProgressivoOrdine();
    	} else {
    		DatiIstituto datiIstitutoInsert = creaDatiIstituto(principal, istituto, anno);
    		log.debug("Created Information for Dati Istituto: {}", datiIstitutoInsert);
    		return datiIstitutoInsert.getProgressivoOrdine();
    	}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public DatiIstituto creaDatiIstituto(Principal principal, String istituto,
			Integer anno) throws ComponentException {
		DatiIstituto datiIstitutoInsert = new DatiIstituto();
		datiIstitutoInsert.setAnno(anno);
		datiIstitutoInsert.setDescrIstituto("Descrizione CDS:"+istituto);
		datiIstitutoInsert.setIstituto(istituto);
		datiIstitutoInsert.setProgressivoOrdine(new Long(1));
		datiIstitutoInsert.setGestioneRespModulo("N");
		datiIstitutoInsert.setUser(principal.getName());
		datiIstitutoInsert.setToBeCreated();
		datiIstitutoInsert = (DatiIstituto)crudServiceBean.creaConBulk(principal, datiIstitutoInsert);
		return datiIstitutoInsert;
	}
}
