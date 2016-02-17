package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AutoPropria;
import it.cnr.si.missioni.repository.AutoPropriaRepository;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.util.CodiciErrore;

import java.security.Principal;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Service class for managing users.
 */
@Service
public class AutoPropriaService {

    private final Logger log = LoggerFactory.getLogger(AutoPropriaService.class);

    @Inject
    private AutoPropriaRepository autoPropriaRepository;

	@Inject
	private CRUDComponentSession<AutoPropria> crudServiceBean;

    @Transactional(readOnly = true)
    public List<AutoPropria> getAutoProprie(String user) {
        return autoPropriaRepository.getAutoProprie(user);
    }

    @Transactional(readOnly = true)
    public AutoPropria getAutoPropria(String user, String targa) {
        return autoPropriaRepository.getAutoPropria(user, targa);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AutoPropria createAutoPropria(Principal principal, String user, AutoPropria autoPropria)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
    	autoPropria.setUid(user);
    	autoPropria.setUser(principal.getName());
    	autoPropria.setToBeCreated();
		validaCRUD(autoPropria);
		autoPropria = (AutoPropria)crudServiceBean.creaConBulk(principal, autoPropria);
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for User: {}", autoPropria);
    	return autoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AutoPropria updateAutoPropria(Principal principal, AutoPropria autoPropria)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

    	AutoPropria autoPropriaDB = (AutoPropria)crudServiceBean.findById(principal, AutoPropria.class, autoPropria.getId());

		if (autoPropriaDB==null)
			throw new AwesomeException(CodiciErrore.ERRGEN, "Auto Propria da aggiornare inesistente.");
		
		autoPropriaDB.setCartaCircolazione(autoPropria.getCartaCircolazione());
		autoPropriaDB.setMarca(autoPropria.getMarca());
		autoPropriaDB.setModello(autoPropria.getModello());
		autoPropriaDB.setTarga(autoPropria.getTarga());
		autoPropriaDB.setPolizzaAssicurativa(autoPropria.getPolizzaAssicurativa());
		autoPropriaDB.setToBeUpdated();

//		//effettuo controlli di validazione operazione CRUD
		validaCRUD(autoPropriaDB);

		autoPropria = (AutoPropria)crudServiceBean.modificaConBulk(principal, autoPropriaDB);
    	
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Updated Information for Dati Patente: {}", autoPropria);
    	return autoPropria;
    }

    @Transactional(propagation = Propagation.REQUIRED)
	public void deleteAutoPropria(Principal principal, Long idAutoPropria) throws AwesomeException, ComponentException, OptimisticLockException, PersistencyException, BusyResourceException {
		AutoPropria autoPropria = (AutoPropria)crudServiceBean.findById(principal, AutoPropria.class, idAutoPropria);

		//effettuo controlli di validazione operazione CRUD
		if (autoPropria != null){
			autoPropria.setToBeDeleted();
			crudServiceBean.eliminaConBulk(principal, autoPropria);
		}
	}

	private void validaCRUD(AutoPropria autoPropria) throws AwesomeException {
		if (autoPropria != null){
			if (StringUtils.isEmpty(autoPropria.getMarca())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": marca");
			} else if (StringUtils.isEmpty(autoPropria.getModello())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": modello");
			} else if (StringUtils.isEmpty(autoPropria.getTarga())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": targa");
			} else if (StringUtils.isEmpty(autoPropria.getCartaCircolazione())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Carta Circolazione");
			} else if (StringUtils.isEmpty(autoPropria.getPolizzaAssicurativa())){
				throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Polizza Assicurativa");
			} 
		}
	}
}
