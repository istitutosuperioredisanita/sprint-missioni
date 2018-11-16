package it.cnr.si.missioni.service;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.DatiIstitutoRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;

/**
 * Service class for managing users.
 */
@Service
public class DatiIstitutoService {

    private final Logger log = LoggerFactory.getLogger(DatiIstitutoService.class);

	@Autowired
	private CRUDComponentSession crudServiceBean;

    @Autowired
    private DatiIstitutoRepository datiIstitutoRepository;

    @Transactional(readOnly = true)
    public DatiIstituto getDatiIstituto(String istituto, Integer anno) {
        return datiIstitutoRepository.getDatiIstituto(istituto, anno);
    }

    private DatiIstituto getDatiIstitutoAndLock(String istituto, Integer anno) {
        return datiIstitutoRepository.getDatiIstitutoAndLock(istituto, anno);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long getNextPG (Principal principal, String istituto, Integer anno, String tipo)throws ComponentException{
    	DatiIstituto datiIstituto = null;
   		datiIstituto = getDatiIstitutoAndLock(istituto, anno);
    	Long pgCorrente = null;
    	if (datiIstituto != null){
            if (Costanti.TIPO_RIMBORSO_MISSIONE.equals(tipo) ) {
        		pgCorrente = new Long(datiIstituto.getProgressivoRimborso()+1);
        		datiIstituto.setProgressivoRimborso(pgCorrente);
    		} else{
                pgCorrente = new Long(datiIstituto.getProgressivoOrdine()+1);
        		datiIstituto.setProgressivoOrdine(pgCorrente);
    		}
    		datiIstituto.setUser(principal.getName());
    		datiIstituto.setToBeUpdated();
    		datiIstituto = (DatiIstituto)crudServiceBean.modificaConBulk(principal, datiIstituto);
    		log.debug("Updated Information for Dati Istituto: {}", datiIstituto);
    	} else {
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Dati uo non presenti per il codice "+istituto+" nell'anno "+anno);
//    		DatiIstituto datiIstitutoInsert = null;
//    		if (Costanti.TIPO_RIMBORSO_MISSIONE.equals(tipo) ) {
//        		datiIstitutoInsert = creaDatiIstitutoRimborso(principal, istituto, anno);
//    		} else {
//        		datiIstitutoInsert = creaDatiIstitutoOrdine(principal, istituto, anno);
//    		}
//    		datiIstituto = datiIstitutoInsert;
//    		log.debug("Created Information for Dati Istituto: {}", datiIstitutoInsert);
    	}
		if (Costanti.TIPO_RIMBORSO_MISSIONE.equals(tipo) ) {
			return datiIstituto.getProgressivoRimborso();
		} else {
			return datiIstituto.getProgressivoOrdine();
		}
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public DatiIstituto creaDatiIstitutoOrdine(Principal principal, String istituto, Integer anno) throws ComponentException {
		return creaDatiIstituto(principal, istituto, anno, Costanti.TIPO_ORDINE_DI_MISSIONE);
	}
    @Transactional(propagation = Propagation.REQUIRED)
    public DatiIstituto creaDatiIstitutoRimborso(Principal principal, String istituto, Integer anno ) throws ComponentException {
		return creaDatiIstituto(principal, istituto, anno, Costanti.TIPO_RIMBORSO_MISSIONE);
	}
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void ribaltaDatiIstituti(Principal principal) throws ComponentException {
    	List<DatiIstituto> lista = datiIstitutoRepository.getDatiIstituti(new Integer(DateUtils.getCurrentYear()));
    	if (lista != null){
        	for (DatiIstituto datiIstituto : lista){
        		DatiIstituto datiIstitutoInsert;
				try {
					datiIstitutoInsert = (DatiIstituto)datiIstituto.clone();
	        		Integer anno = datiIstituto.getAnno() + 1;
	        		if (getDatiIstituto(datiIstitutoInsert.getIstituto(), anno) == null){
	        			datiIstitutoInsert.setAnno(anno);        			
	            		datiIstitutoInsert.setProgressivoOrdine(new Long(0));
	            		datiIstitutoInsert.setProgressivoRimborso(new Long(0));
	            		datiIstitutoInsert.setUser(principal.getName());
	            		datiIstitutoInsert.setPg_ver_rec(new Long(1));
	            		datiIstitutoInsert.setId(null);
	            		datiIstitutoInsert.setDataBloccoRimborsi(null);
	            		datiIstitutoInsert.setDataBloccoRimborsiTam(null);
	            		datiIstitutoInsert.setDataBloccoInsRimborsi(null);
	            		datiIstitutoInsert.setDataBloccoInsRimborsiTam(null);
	            		datiIstitutoInsert.setToBeCreated();
	            		datiIstitutoInsert = (DatiIstituto)crudServiceBean.creaConBulk(principal, datiIstitutoInsert);
	        		}
				} catch (CloneNotSupportedException e) {
		    		throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nel clone di Dati Istituto.");
				}
        	}
    	}
    }
    
	private DatiIstituto creaDatiIstituto(Principal principal, String istituto, Integer anno, String tipo) throws ComponentException {
		DatiIstituto datiIstitutoInsert = new DatiIstituto();
		datiIstitutoInsert.setAnno(anno);
		datiIstitutoInsert.setDescrIstituto("Descrizione CDS:"+istituto);
		datiIstitutoInsert.setIstituto(istituto);
		datiIstitutoInsert.setProgressivoOrdine(Costanti.TIPO_ORDINE_DI_MISSIONE.equals(tipo) ? new Long(1) : new Long(0));
		datiIstitutoInsert.setProgressivoRimborso(Costanti.TIPO_RIMBORSO_MISSIONE.equals(tipo) ? new Long(1) : new Long(0));
		datiIstitutoInsert.setGestioneRespModulo("N");
		datiIstitutoInsert.setUser(principal.getName());
		datiIstitutoInsert.setToBeCreated();
		datiIstitutoInsert = (DatiIstituto)crudServiceBean.creaConBulk(principal, datiIstitutoInsert);
		return datiIstitutoInsert;
	}

    @Transactional(propagation = Propagation.REQUIRED)
	public DatiIstituto creaDatiIstituto(Principal principal, DatiIstituto datiIstituto) throws ComponentException {
		datiIstituto.setProgressivoOrdine(new Long(0));
		datiIstituto.setProgressivoRimborso(new Long(0));
		datiIstituto.setUser(principal.getName());
		datiIstituto.setToBeCreated();
		datiIstituto= (DatiIstituto)crudServiceBean.creaConBulk(principal, datiIstituto);
		return datiIstituto;
	}

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiIstituto updateDatiIstituto(Principal principal, DatiIstituto datiIstituto) throws ComponentException {

    	DatiIstituto datiIstitutoDB = (DatiIstituto)crudServiceBean.findById(principal, DatiIstituto.class, datiIstituto.getId());

    	if (datiIstitutoDB==null)
    		throw new AwesomeException(CodiciErrore.ERRGEN, "Dati istituto da aggiornare inesistenti.");

    	datiIstitutoDB.setDescrIstituto(datiIstituto.getDescrIstituto());
    	datiIstitutoDB.setGestioneRespModulo(datiIstituto.getGestioneRespModulo());
    	datiIstitutoDB.setProgressivoOrdine(datiIstituto.getProgressivoOrdine());
    	datiIstitutoDB.setProgressivoRimborso(datiIstituto.getProgressivoRimborso());
    	datiIstitutoDB.setToBeUpdated();

    	datiIstituto = (DatiIstituto)crudServiceBean.modificaConBulk(principal, datiIstitutoDB);

    	//	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for DatiIstituto: {}", datiIstituto);
    	return datiIstituto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteDatiIstituto(Principal principal, Long idDatiIstituto) throws ComponentException {
    	DatiIstituto datiIstituto = (DatiIstituto)crudServiceBean.findById(principal, DatiIstituto.class, idDatiIstituto);

		//effettuo controlli di validazione operazione CRUD
		if (datiIstituto != null){
			if (datiIstituto.getProgressivoOrdine().compareTo(new Long(0)) > 0 ||
					datiIstituto.getProgressivoRimborso().compareTo(new Long(0)) > 0 ){
	    		throw new AwesomeException(CodiciErrore.ERRGEN, "Dati istituto già utilizzati, impossibile effettuare la cancellazione.");
			}
			datiIstituto.setToBeDeleted();
			crudServiceBean.eliminaConBulk(principal, datiIstituto);
		}
	}
}
