package it.cnr.si.missioni.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.service.OrdineMissioneAutoPropriaService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class OrdineMissioneAutoPropriaResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAutoPropriaResource.class);


    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private OrdineMissioneAutoPropriaService ordineMissioneAutoPropriaService;

    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/ordineMissione/autoPropria/get",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAutoPropria(HttpServletRequest request,
    		@RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per visualizzare i dati dell'auto propria dell'Ordine di Missione" );
        try {
            OrdineMissioneAutoPropria ordineMissioneAutoPropria = ordineMissioneAutoPropriaService.getAutoPropria((Principal) SecurityUtils.getCurrentUser(), idMissione);
            return JSONResponseEntity.ok(ordineMissioneAutoPropria);
		} catch (ComponentException e) {
			log.error("ERRORE getAutoPropria",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		} 
    }

    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/ordineMissione/autoPropria/getSpostamenti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getSpostamenti(HttpServletRequest request,
    		@RequestParam(value = "idAutoPropriaOrdineMissione") Long idAutoPropriaOrdineMissione) {
        log.debug("REST request per visualizzare i dati degli spostamenti con l'auto propria dell'Ordine di Missione" );
        try {
            List<SpostamentiAutoPropria> spostamentiAutoPropria = ordineMissioneAutoPropriaService.getSpostamentiAutoPropria((Principal) SecurityUtils.getCurrentUser(), idAutoPropriaOrdineMissione);
            return JSONResponseEntity.ok(spostamentiAutoPropria);
		} catch (ComponentException e) {
			log.error("ERRORE getSpostamenti",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		} 
    }

    @RequestMapping(value = "/rest/ordineMissione/autoPropria/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAutoPropriaOrdineMissione(@RequestBody OrdineMissioneAutoPropria ordineMissioneAutoPropria, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (ordineMissioneAutoPropria.getId() == null){
            try {
                ordineMissioneAutoPropria = ordineMissioneAutoPropriaService.createAutoPropria((Principal) SecurityUtils.getCurrentUser(), ordineMissioneAutoPropria);
    		} catch (AwesomeException e) {
    			log.error("ERRORE createAutoPropriaOrdineMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE createAutoPropriaOrdineMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(ordineMissioneAutoPropria);
    	} else {
    		String error = "Id Ordine Missione Auto Propria non valorizzato";
    		log.error("ERRORE createAutoPropriaOrdineMissione",error);
            return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione/autoPropria/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAutoPropriaOrdineMissione(@RequestBody OrdineMissioneAutoPropria ordineMissioneAutoPropria, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (ordineMissioneAutoPropria.getId() != null){
            try {
            	ordineMissioneAutoPropria = ordineMissioneAutoPropriaService.updateAutoPropria((Principal) SecurityUtils.getCurrentUser(), ordineMissioneAutoPropria);
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
        		log.error("ERRORE modifyAutoPropriaOrdineMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(ordineMissioneAutoPropria);
    	} else {
    		String error = "Id Ordine Missione Auto Propria non valorizzato";
    		log.error("ERRORE modifyAutoPropriaOrdineMissione",error);
            return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione/autoPropria/modifySpostamento",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifySpostamento(@RequestBody SpostamentiAutoPropria spostamentiAutoPropria, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (spostamentiAutoPropria.getId() != null){
            try {
            	spostamentiAutoPropria = ordineMissioneAutoPropriaService.updateSpostamenti((Principal) SecurityUtils.getCurrentUser(), spostamentiAutoPropria);
    		} catch (AwesomeException|ComponentException|OptimisticLockException|PersistencyException|BusyResourceException e) {
        		log.error("ERRORE modifySpostamento",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(spostamentiAutoPropria);
    	} else {
       		String error = "Id Spostamento Auto Propria non valorizzato";
    		log.error("ERRORE modifySpostamento",error);
            return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione/autoPropria/createSpostamento",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createSpostamentoAutoPropria(@RequestBody SpostamentiAutoPropria spostamentoAutoPropria, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (spostamentoAutoPropria.getId() == null){
            try {
            	spostamentoAutoPropria = ordineMissioneAutoPropriaService.createSpostamentoAutoPropria((Principal) SecurityUtils.getCurrentUser(), spostamentoAutoPropria);
    		} catch (AwesomeException e) {
        		log.error("ERRORE createSpostamentoAutoPropria",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
        		log.error("ERRORE createSpostamentoAutoPropria",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(spostamentoAutoPropria);
    	} else {
       		String error = "Id Spostamento Auto Propria valorizzato";
    		log.error("ERRORE createSpostamentoAutoPropria",error);
            return JSONResponseEntity.badRequest(error);
    	}
    }


    @RequestMapping(value = "/rest/ordineMissione/autoPropria/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteAutoPropria(@PathVariable Long ids, HttpServletRequest request) {
		try {
			ordineMissioneAutoPropriaService.deleteAutoPropria((Principal) SecurityUtils.getCurrentUser(), ids);
            return JSONResponseEntity.ok();
		} catch (AwesomeException e) {
    		log.error("ERRORE deleteAutoPropria",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
    		log.error("ERRORE deleteAutoPropria",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

    @RequestMapping(value = "/rest/ordineMissione/autoPropria/spostamenti/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteSpostamento(@PathVariable Long ids, HttpServletRequest request) {
		try {
			ordineMissioneAutoPropriaService.deleteSpostamenti((Principal) SecurityUtils.getCurrentUser(), ids);
            return JSONResponseEntity.ok();
		} catch (AwesomeException e) {
    		log.error("ERRORE deleteSpostamento",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
    		log.error("ERRORE deleteSpostamento",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

    @RequestMapping(value = "/rest/public/printOrdineMissioneAutoPropria",
            method = RequestMethod.GET)
    @Timed
    public @ResponseBody void printOrdineMissioneAutoPropria(HttpServletRequest request,
    		@RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa della richiesta di auto propria per l'Ordine di Missione " );
        
        if (!StringUtils.isEmpty(idMissione)){
            try {
            	Long idMissioneLong = new Long (idMissione); 
            	OAuth2Authentication auth = tokenStore.readAuthentication(token);
            	if (auth != null){
            		Map<String, byte[]> map = ordineMissioneAutoPropriaService.printOrdineMissioneAutoPropria(auth, idMissioneLong);
            		if (map != null){
                  		res.setContentType("application/pdf");
                    	try {
                    		String headerValue = "attachment";
                    		for (String key : map.keySet()) {
                       			headerValue += "; filename=\"" + key + "\"";
                        		res.setHeader("Content-Disposition", headerValue);
                        		OutputStream outputStream = res.getOutputStream();
                        		InputStream inputStream = new ByteArrayInputStream(map.get(key));

                        		IOUtils.copy(inputStream, outputStream);

                        		outputStream.flush();

                        		inputStream.close();
                        		outputStream.close();       	
                    		}
            			} catch (IOException e) {
                    		log.error("ERRORE printOrdineMissioneAutoPropria",e);
                			throw new RuntimeException(Utility.getMessageException(e));
                		} 
            		}
            	}
    		} catch (ComponentException e) {
        		log.error("ERRORE printOrdineMissioneAutoPropria",e);
    			throw new RuntimeException(Utility.getMessageException(e));
    		} 
        }
    }
}
