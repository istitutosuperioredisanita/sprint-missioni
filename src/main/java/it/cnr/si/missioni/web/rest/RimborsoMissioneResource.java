package it.cnr.si.missioni.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;

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
import org.springframework.security.core.context.SecurityContextHolder;
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

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.service.RimborsoMissioneService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class RimborsoMissioneResource {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneResource.class);


    @Autowired
    private TokenStore tokenStore;

    
    @Inject
    private RimborsoMissioneService rimborsoMissioneService;

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getRimborsoMissione(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati dei Rimborsi di Missione " );
        List<RimborsoMissione> rimborsiMissione = rimborsoMissioneService.getRimborsiMissione(SecurityUtils.getCurrentUser(), filter, true);
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/listToFinal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getRimborsoMissioneToFinal(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        filter.setToFinal("S");
        List<RimborsoMissione> rimborsiMissione = null;
        		rimborsoMissioneService.getRimborsiMissione(SecurityUtils.getCurrentUser(), filter, true);
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     * @throws Exception 
     */
    @RequestMapping(value = "/rest/rimborsoMissione/listToValidate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getRimborsoMissioneDaValidare(HttpServletRequest request, RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        List<RimborsoMissione> rimborsiMissione;
		try {
			rimborsiMissione = rimborsoMissioneService.getRimborsiMissioneForValidateFlows(SecurityUtils.getCurrentUser(), filter, true);
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissioneDaValidare",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get rimborso di missione byId
     */
    @RequestMapping(value = "/rest/rimborsoMissione/getById",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getRimborsoMissione(HttpServletRequest request,
    		@RequestParam(value = "id") Long idMissione) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        try {
        	RimborsoMissione rimborsoMissione = rimborsoMissioneService.getRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), idMissione, false, true);
            return JSONResponseEntity.ok(rimborsoMissione);
        } catch (AwesomeException e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissione",e);
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity createRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoMissione.getId() == null){
            try {
                rimborsoMissione =  rimborsoMissioneService.createRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), rimborsoMissione);
    		} catch (AwesomeException e) {
    			log.error("ERRORE createRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
    			log.error("ERRORE createRimborsoMissione",e);
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    	    return JSONResponseEntity.badRequest("Id Rimborso Missione non valorizzato");
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity modifyRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoMissione.getId() != null){
    		Principal principal = SecurityContextHolder.getContext().getAuthentication();
            try {
				rimborsoMissione =  rimborsoMissioneService.updateRimborsoMissione(principal, rimborsoMissione);
    		} catch (AwesomeException e) {
    			log.error("ERRORE modifyRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE modifyRimborsoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    	    return JSONResponseEntity.badRequest("Id Rimborso Missione non valorizzato");
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.PUT,
            params = {"confirm"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity confirmRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,  
    		HttpServletRequest request, HttpServletResponse response) {
    	if (rimborsoMissione.getId() != null){
    		rimborsoMissione.setDaValidazione(daValidazione);
            try {
				rimborsoMissione = rimborsoMissioneService.updateRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), rimborsoMissione, false, confirm);
    		} catch (AwesomeException e) {
    			log.error("ERRORE confirmRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE confirmRimborsoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    	    return JSONResponseEntity.badRequest("Id Rimborso Missione non valorizzato");
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity deleteRimborsoMissione(@PathVariable Long ids, HttpServletRequest request) {
		try {
			rimborsoMissioneService.deleteRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), ids);
            return JSONResponseEntity.ok();
		} catch (AwesomeException e) {
			log.error("ERRORE deleteRimborsoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
			log.error("ERRORE deleteRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

    @RequestMapping(value = "/rest/public/printRimborsoMissione",
            method = RequestMethod.GET)
    @Timed
    public @ResponseBody void printRimborsoMissione(HttpServletRequest request,
    		@RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'rimborso di Missione " );
        
        if (!StringUtils.isEmpty(idMissione)){
            try {
            	Long idMissioneLong = new Long (idMissione); 
            	OAuth2Authentication auth = tokenStore.readAuthentication(token);
            	if (auth != null){
            		Map<String, byte[]> map = rimborsoMissioneService.printRimborsoMissione(auth, idMissioneLong);
            		if (map != null){
            			res.setContentType("application/pdf");
                    	try {
                    		String headerValue = "attachment";
                    		for (String key : map.keySet()) {
                    			System.out.println(map.get(key).length);
                    			log.error("Lunghezza "+map.get(key).length);
                       			headerValue += "; filename=\"" + key + "\"";
                        		OutputStream outputStream = res.getOutputStream();
                        		res.setHeader("Content-Disposition", headerValue);
                        		InputStream inputStream = new ByteArrayInputStream(map.get(key));
                        		IOUtils.copy(inputStream, outputStream);
                        		outputStream.flush();
                        		inputStream.close();
                        		outputStream.close();       	
                    		}
            			} catch (IOException e) {
            				log.error("ERRORE deleteRimborsoMissione",e);
                			throw new RuntimeException(Utility.getMessageException(e));
                		} 
            		}
            	}
    		} catch (ComponentException e) {
    			log.error("ERRORE deleteRimborsoMissione",e);
    			throw new RuntimeException(Utility.getMessageException(e));
    		} 
        }
    }
}
