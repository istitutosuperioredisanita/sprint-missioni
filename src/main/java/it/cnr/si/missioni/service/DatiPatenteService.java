package it.cnr.si.missioni.service;


import java.util.Date;

import com.rabbitmq.client.AMQP;
import it.cnr.si.service.SecurityService;
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

    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public DatiPatente getDatiPatente(String user) {
        return datiPatenteRepository.getDatiPatente(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente createDatiPatente(DatiPatente datiPatente) {
    	datiPatente.setUser(securityService.getCurrentUserLogin());
    	datiPatente.setToBeCreated();
    	//effettuo controlli di validazione operazione CRUD
    	validaCRUD(datiPatente);
    	datiPatente = (DatiPatente)crudServiceBean.creaConBulk(datiPatente);
    	log.debug("Created Information for Dati Patente: {}", datiPatente);
    	return datiPatente;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente updateDatiPatente(DatiPatente datiPatente)  {

    	DatiPatente datiPatenteDB = (DatiPatente)crudServiceBean.findById( DatiPatente.class, datiPatente.getId());

		if (datiPatenteDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Dati patente da aggiornare inesistente.");
		
		datiPatenteDB.setNumero(datiPatente.getNumero());
		datiPatenteDB.setDataRilascio(datiPatente.getDataRilascio());
		datiPatenteDB.setDataScadenza(datiPatente.getDataScadenza());
		datiPatenteDB.setEnte(datiPatente.getEnte());
		datiPatenteDB.setToBeUpdated();

		//effettuo controlli di validazione operazione CRUD
		validaCRUD(datiPatenteDB);

		datiPatente = (DatiPatente)crudServiceBean.modificaConBulk( datiPatenteDB);
    	
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for Dati Patente: {}", datiPatente);
    	return datiPatente;
    }

    private void validaCRUD(DatiPatente datiPatente){
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
