package it.cnr.si.missioni.web.rest;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;
import it.cnr.si.missioni.service.OrdineMissioneAnticipoService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.SecurityUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class OrdineMissioneAnticipoResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAnticipoResource.class);


    @Autowired
    private TokenStore tokenStore;

	@Inject
    private OrdineMissioneAnticipoService ordineMissioneAnticipoService;

    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/ordineMissione/anticipo/get",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnticipo(HttpServletRequest request,
    		@RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per visualizzare i dati dell'auto propria dell'Ordine di Missione" );
        try {
            OrdineMissioneAnticipo ordineMissioneAnticipo = ordineMissioneAnticipoService.getAnticipo((Principal) SecurityUtils.getCurrentUser(), idMissione, true);
            return new ResponseEntity<>(
                    ordineMissioneAnticipo,
                    HttpStatus.OK);
		} catch (ComponentException e) {
  	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		} 
//        if (autoPropria == null) {
//            return new ResponseEntity<>(HttpStatus.);
//        }

//        List<String> roles = new ArrayList<>();
//        for (Authority authority : user.getAuthorities()) {
//            roles.add(authority.getName());
//        }
    }

    @RequestMapping(value = "/rest/ordineMissione/anticipo/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAnticipoOrdineMissione(@RequestBody OrdineMissioneAnticipo ordineMissioneAnticipo, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (ordineMissioneAnticipo.getId() == null){
            try {
                ordineMissioneAnticipo = ordineMissioneAnticipoService.createAnticipo((Principal) SecurityUtils.getCurrentUser(), ordineMissioneAnticipo);
    		} catch (AwesomeException e) {
    			return e.getResponse();
    		} catch (ComponentException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (OptimisticLockException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (PersistencyException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (BusyResourceException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		}
            return new ResponseEntity<>(ordineMissioneAnticipo, HttpStatus.CREATED);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione/anticipo/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAnticipoOrdineMissione(@RequestBody OrdineMissioneAnticipo ordineMissioneAnticipo, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (ordineMissioneAnticipo.getId() != null){
            try {
            	ordineMissioneAnticipo = ordineMissioneAnticipoService.updateAnticipo((Principal) SecurityUtils.getCurrentUser(), ordineMissioneAnticipo);
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.INTERNAL_SERVER_ERROR);
    		}
            return new ResponseEntity<>(ordineMissioneAnticipo, HttpStatus.OK);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }


    @RequestMapping(value = "/rest/ordineMissione/anticipo/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteAnticipo(@PathVariable Long ids, HttpServletRequest request) {
		try {
			ordineMissioneAnticipoService.deleteAnticipo((Principal) SecurityUtils.getCurrentUser(), ids);
            return new ResponseEntity<>(HttpStatus.OK);
		} catch (AwesomeException e) {
			return e.getResponse();
		} catch (ComponentException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} catch (OptimisticLockException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} catch (PersistencyException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} catch (BusyResourceException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		}
	}

    @RequestMapping(value = "/rest/public/printOrdineMissioneAnticipo",
            method = RequestMethod.GET)
    @Timed
    public @ResponseBody void printOrdineMissioneAnticipo(HttpServletRequest request,
    		@RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'Ordine di Missione " );
        
        if (!StringUtils.isEmpty(idMissione)){
            try {
            	Long idMissioneLong = new Long (idMissione); 
            	OAuth2Authentication auth = tokenStore.readAuthentication(token);
            	if (auth != null){
                	byte[] print = ordineMissioneAnticipoService.printOrdineMissioneAnticipo(auth, idMissioneLong);
                	
              		res.setContentType("application/pdf");
                	try {
                		String attachFileName = "OrdineMissioneAnticipo"+idMissione+".pdf";
                		String headerValue = "attachment";
                		if (attachFileName != null && !attachFileName.isEmpty()) {
                			headerValue += "; filename=\"" + attachFileName + "\"";
                		}
                		res.setHeader("Content-Disposition", headerValue);
                		OutputStream outputStream = res.getOutputStream();
                		InputStream inputStream = new ByteArrayInputStream(print);

                		IOUtils.copy(inputStream, outputStream);

                		outputStream.flush();

                		inputStream.close();
                		outputStream.close();       	
        			} catch (IOException e) {
            			throw new RuntimeException(Utility.getMessageException(e));
            		} 
                	
            	}
    		} catch (ComponentException e) {
    			throw new RuntimeException(Utility.getMessageException(e));
    		} 
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/anticipo/print/json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> jsonForPrintOrdineMissioneAnticipo(HttpServletRequest request,
    		@RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per il json della stampa dell'anticipo dell'Ordine di Missione " );
        try {
        	String json = ordineMissioneAnticipoService.jsonForPrintOrdineMissione((Principal) SecurityUtils.getCurrentUser(), idMissione);
            return new ResponseEntity<>(
            		json,
                    HttpStatus.OK);
		} catch (ComponentException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} 
    }

    @RequestMapping(value = "/rest/ordineMissione/anticipo",
            method = RequestMethod.PUT,
            params = {"confirm"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmOrdineMissioneAnticipo(@RequestBody OrdineMissioneAnticipo ordineMissioneAnticipo, @RequestParam(value = "confirm") Boolean confirm, 
    		HttpServletRequest request, HttpServletResponse response) {
    	if (ordineMissioneAnticipo.getId() != null){
            try {
				ordineMissioneAnticipo = ordineMissioneAnticipoService.updateAnticipo((Principal) SecurityUtils.getCurrentUser(), ordineMissioneAnticipo, confirm);
    		} catch (AwesomeException e) {
    			return e.getResponse();
    		} catch (ComponentException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (OptimisticLockException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		}
            return new ResponseEntity<>(ordineMissioneAnticipo, HttpStatus.OK);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

}
