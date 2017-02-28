package it.cnr.si.missioni.service;

import java.security.Principal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.DatiPatenteRepository;
import it.cnr.si.missioni.util.CodiciErrore;

/**
 * Service class for managing users.
 */
@Service
public class DatiPatenteService {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteService.class);

    @Autowired
    private DatiPatenteRepository datiPatenteRepository;

	@Autowired
	private CRUDComponentSession crudServiceBean;

    @Transactional(readOnly = true)
    public DatiPatente getDatiPatente(String user) {
        return datiPatenteRepository.getDatiPatente(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente createDatiPatente(Principal principal, DatiPatente datiPatente) {
    	datiPatente.setUser(principal.getName());
    	datiPatente.setToBeCreated();
    	//effettuo controlli di validazione operazione CRUD
    	validaCRUD(principal, datiPatente);
    	datiPatente = (DatiPatente)crudServiceBean.creaConBulk(principal, datiPatente);
    	log.debug("Created Information for Dati Patente: {}", datiPatente);
    	return datiPatente;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente updateDatiPatente(Principal principal, DatiPatente datiPatente)  {

    	DatiPatente datiPatenteDB = (DatiPatente)crudServiceBean.findById(principal, DatiPatente.class, datiPatente.getId());

		if (datiPatenteDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati patente da aggiornare inesistente.");
		
		datiPatenteDB.setNumero(datiPatente.getNumero());
		datiPatenteDB.setDataRilascio(datiPatente.getDataRilascio());
		datiPatenteDB.setDataScadenza(datiPatente.getDataScadenza());
		datiPatenteDB.setEnte(datiPatente.getEnte());
		datiPatenteDB.setToBeUpdated();

		//effettuo controlli di validazione operazione CRUD
		validaCRUD(principal, datiPatenteDB);

		datiPatente = (DatiPatente)crudServiceBean.modificaConBulk(principal, datiPatenteDB);
    	
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for Dati Patente: {}", datiPatente);
    	return datiPatente;
    }

    private void validaCRUD(Principal principal, DatiPatente datiPatente){
        Date oggi = new Date(System.currentTimeMillis());
        if (datiPatente.getDataRilascio() != null){
            if (oggi.before(datiPatente.getDataRilascio())){
                throw new AwesomeException(CodiciErrore.ERRGEN, "La data di rilascio della patente non può essere successiva alla data odierna.");
            }
            if (datiPatente.getDataScadenza() != null){
                if (datiPatente.getDataScadenza().before(datiPatente.getDataRilascio())){
                    throw new AwesomeException(CodiciErrore.ERRGEN, "La data di rilascio della patente non può essere successiva alla data di scadenza.");
                }
            }
        }
    }
}
