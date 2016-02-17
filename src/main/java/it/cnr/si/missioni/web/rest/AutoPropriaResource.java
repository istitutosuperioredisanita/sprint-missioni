package it.cnr.si.missioni.web.rest;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AutoPropria;
import it.cnr.si.missioni.service.AutoPropriaService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.SecurityUtils;

import java.security.Principal;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
public class AutoPropriaResource {

    private final Logger log = LoggerFactory.getLogger(AutoPropriaResource.class);


//    @Inject
//    private AutoPropriaRepository autoPropriaRepository;

	@Inject
    private AutoPropriaService autoPropriaService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/autoPropria",
            method = RequestMethod.GET,
            params = {"user"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AutoPropria>> getAutoProprie(@RequestParam(value = "user") String user) {
        log.debug("REST request per visualizzare i dati di Auto Propria");
        List<AutoPropria> autoProprie = autoPropriaService.getAutoProprie(user);
//        if (autoPropria == null) {
//            return new ResponseEntity<>(HttpStatus.);
//        }

//        List<String> roles = new ArrayList<>();
//        for (Authority authority : user.getAuthorities()) {
//            roles.add(authority.getName());
//        }
        return new ResponseEntity<>(
            autoProprie,
            HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/autoPropria",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAutoPropria(@RequestBody AutoPropria autoPropria, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (autoPropria.getId() == null){
        	AutoPropria auto = autoPropriaService.getAutoPropria(autoPropria.getUid(), autoPropria.getTarga());
        	if (auto != null){
                return new ResponseEntity<String>(CodiciErrore.TARGA_GIA_INSERITA, HttpStatus.BAD_REQUEST);
        	}
            try {
                autoPropria = autoPropriaService.createAutoPropria((Principal) SecurityUtils.getCurrentUser(), autoPropria.getUid(), autoPropria);
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    		}
            return new ResponseEntity<>(autoPropria, HttpStatus.CREATED);
    	} else {
	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/rest/autoPropria",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAutoPropria(@RequestBody AutoPropria autoPropria, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (autoPropria.getId() != null){
        	AutoPropria auto = autoPropriaService.getAutoPropria(autoPropria.getUid(), autoPropria.getTarga());
        	if (auto != null){
        		if (!auto.getId().equals(autoPropria.getId())){
                    return new ResponseEntity<String>(CodiciErrore.TARGA_GIA_INSERITA, HttpStatus.BAD_REQUEST);
        		}
        	}
            try {
				autoPropria = autoPropriaService.updateAutoPropria((Principal) SecurityUtils.getCurrentUser(), autoPropria);
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    		}
            return new ResponseEntity<>(autoPropria, HttpStatus.OK);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/rest/autoPropria/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteAutoPropria(@PathVariable Long ids, HttpServletRequest request) {
		try {
			autoPropriaService.deleteAutoPropria((Principal) SecurityUtils.getCurrentUser(), ids);
            return new ResponseEntity<>(HttpStatus.OK);
		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
