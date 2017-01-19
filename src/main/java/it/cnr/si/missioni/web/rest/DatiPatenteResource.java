package it.cnr.si.missioni.web.rest;

import java.security.Principal;

import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.service.DatiPatenteService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class DatiPatenteResource {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteResource.class);

	@Autowired
    private DatiPatenteService datiPatenteService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/datiPatente",
            method = RequestMethod.GET,
            params = {"user"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getDatiPatente(@RequestParam(value = "user") String user) {
        log.debug("REST request per visualizzare i dati della Patente");
        DatiPatente datiPatente = datiPatenteService.getDatiPatente(user);
        return JSONResponseEntity.ok(datiPatente);
    }

    @RequestMapping(value = "/rest/datiPatente",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity registerDatiPatente(@RequestBody DatiPatente datiPatente, HttpServletRequest request,
                                             HttpServletResponse response) {
    	log.debug("Entro nel metodo POST");
    	if (datiPatente.getId() == null){
        	log.debug("id vuoto");
        	DatiPatente patente = datiPatenteService.getDatiPatente(datiPatente.getUid());
        	if (patente != null){
            	log.debug("patente trovata");
                return JSONResponseEntity.badRequest("I dati della patente sono già inseriti");
        	}
            datiPatente = datiPatenteService.createDatiPatente((Principal) SecurityUtils.getCurrentUser(), datiPatente);
            return JSONResponseEntity.ok();
    	} else {
    		log.debug("id pieno");
    		log.debug("recupero USER");
    		try {
        		datiPatente = datiPatenteService.updateDatiPatente((Principal) SecurityUtils.getCurrentUser(), datiPatente);
        		log.debug("modificata patente");
                return JSONResponseEntity.ok();
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}    		
    	}
    }
}
