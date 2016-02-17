package it.cnr.si.missioni.web.rest;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.service.DatiPatenteService;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.SecurityUtils;

import java.security.Principal;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class DatiPatenteResource {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteResource.class);


//    @Inject
//    private AutoPropriaRepository autoPropriaRepository;

	@Inject
    private DatiPatenteService datiPatenteService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/datiPatente",
            method = RequestMethod.GET,
            params = {"user"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DatiPatente> getDatiPatente(@RequestParam(value = "user") String user) {
        log.debug("REST request per visualizzare i dati della Patente");
        DatiPatente datiPatente = datiPatenteService.getDatiPatente(user);
//        if (autoPropria == null) {
//            return new ResponseEntity<>(HttpStatus.);
//        }

//        List<String> roles = new ArrayList<>();
//        for (Authority authority : user.getAuthorities()) {
//            roles.add(authority.getName());
//        }
        return new ResponseEntity<>(
            datiPatente,
            HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/datiPatente",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> registerDatiPatente(@RequestBody DatiPatente datiPatente, HttpServletRequest request,
                                             HttpServletResponse response) {
    	log.debug("Entro nel metodo POST");
    	if (datiPatente.getId() == null){
        	log.debug("id vuoto");
        	DatiPatente patente = datiPatenteService.getDatiPatente(datiPatente.getUid());
        	if (patente != null){
            	log.debug("patente trovata");
                return new ResponseEntity<String>("I dati della patente sono già inseriti", HttpStatus.BAD_REQUEST);
        	}
            datiPatente = datiPatenteService.createDatiPatente((Principal) SecurityUtils.getCurrentUser(), datiPatente);
        	log.debug("creata patente");
            return new ResponseEntity<>(HttpStatus.CREATED);
    	} else {
    		log.debug("id pieno");
    		log.debug("recupero USER");
   		
    		try {
        		datiPatente = datiPatenteService.updateDatiPatente((Principal) SecurityUtils.getCurrentUser(), datiPatente);
        		log.debug("modificata patente");
        		return new ResponseEntity<>(HttpStatus.CREATED);
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    		}    		
    	}
//    	AutoPropria user = userRepository.findOne(userDTO.getLogin());
//        if (user != null) {
//            return new ResponseEntity<String>("login already in use", HttpStatus.BAD_REQUEST);
//        } else {
//            if (userRepository.findOneByEmail(userDTO.getEmail()) != null) {
//                return new ResponseEntity<String>("e-mail address already in use", HttpStatus.BAD_REQUEST);
//            }
//            user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(), userDTO.getFirstName(),
//                    userDTO.getLastName(), userDTO.getEmail().toLowerCase(), userDTO.getLangKey());
//            final Locale locale = Locale.forLanguageTag(user.getLangKey());
//            String content = createHtmlContentFromTemplate(user, locale, request, response);
//            mailService.sendActivationEmail(user.getEmail(), content, locale);
//            return new ResponseEntity<>(HttpStatus.CREATED);
//        }
//      return new ResponseEntity<String>("login already in use", HttpStatus.BAD_REQUEST);
    }
}
