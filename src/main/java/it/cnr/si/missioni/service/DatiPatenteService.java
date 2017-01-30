package it.cnr.si.missioni.service;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.DatiPatenteRepository;
import it.cnr.si.missioni.util.CodiciErrore;

import java.security.Principal;
import java.util.Date;

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
public class DatiPatenteService {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteService.class);

    @Inject
    private DatiPatenteRepository datiPatenteRepository;

	@Inject
	private CRUDComponentSession crudServiceBean;

//    public User createUserInformation(String login, String password, String firstName, String lastName, String email,
//                                      String langKey) {
//        User newUser = new User();
//        Authority authority = authorityRepository.findOne("ROLE_USER");
//        Set<Authority> authorities = new HashSet<>();
//        String encryptedPassword = passwordEncoder.encode(password);
//        newUser.setLogin(login);
//        // new user gets initially a generated password
//        newUser.setPassword(encryptedPassword);
//        newUser.setFirstName(firstName);
//        newUser.setLastName(lastName);
//        newUser.setEmail(email);
//        newUser.setLangKey(langKey);
//        // new user is not active
//        newUser.setActivated(false);
//        // new user gets registration key
//        newUser.setActivationKey(RandomUtil.generateActivationKey());
//        authorities.add(authority);
//        newUser.setAuthorities(authorities);
//        userRepository.save(newUser);
//        log.debug("Created Information for User: {}", newUser);
//        return newUser;
//    }
//
//    public void updateUserInformation(String firstName, String lastName, String email) {
//        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());
//        currentUser.setFirstName(firstName);
//        currentUser.setLastName(lastName);
//        currentUser.setEmail(email);
//        userRepository.save(currentUser);
//        log.debug("Changed Information for User: {}", currentUser);
//    }

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
    	try {
    		datiPatente = (DatiPatente)crudServiceBean.creaConBulk(principal, datiPatente);
		} catch (ComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//    	autoPropriaRepository.save(autoPropria);
    	log.debug("Created Information for Dati Patente: {}", datiPatente);
    	return datiPatente;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente updateDatiPatente(Principal principal, DatiPatente datiPatente)  throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {

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

    @Transactional(propagation = Propagation.REQUIRED)
    private void validaCRUD(Principal principal, DatiPatente datiPatente) throws AwesomeException, 
    ComponentException, OptimisticLockException, OptimisticLockException, PersistencyException, BusyResourceException {
		crudServiceBean.lockBulk(principal, datiPatente);
        Date oggi = new Date(System.currentTimeMillis());
        if (datiPatente.getDataRilascio() != null){
            if (oggi.before(DateUtils.datiPatente.getDataRilascio()){
                throw new AwesomeException(CodiciErrore.ERRGEN, "La data di rilascio della patente non può essere successiva alla data odierna.");
            }
            if (datiPatente.getDataScadenza() != null){
                throw new AwesomeException(CodiciErrore.ERRGEN, "La data di rilascio della patente non può essere successiva alla data di scadenza.");
            }
        }
    }
}
