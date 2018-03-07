package it.cnr.si.missioni.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.MimeTypes;
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
@RequestMapping("/api")
public class RimborsoMissioneResource {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneResource.class);


    @Autowired
    private TokenStore tokenStore;

    
    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsoMissione(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati dei Rimborsi di Missione " );
        List<RimborsoMissione> rimborsiMissione;
		try {
			rimborsiMissione = rimborsoMissioneService.getRimborsiMissione(SecurityUtils.getCurrentUser(), filter, true);
		} catch (ComponentException e) {
			log.error("ERRORE getRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/listToFinal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsoMissioneToFinal(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        filter.setToFinal("S");
        List<RimborsoMissione> rimborsiMissione = null;
        		try {
					rimborsoMissioneService.getRimborsiMissione(SecurityUtils.getCurrentUser(), filter, true);
				} catch (ComponentException e) {
					log.error("ERRORE getRimborsoMissioneToFinal",e);
		            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
				}
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
    public ResponseEntity<?> getRimborsoMissioneDaValidare(HttpServletRequest request, RimborsoMissioneFilter filter) {
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
    public ResponseEntity<?> getRimborsoMissione(HttpServletRequest request,
    		@RequestParam(value = "id") Long idMissione) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        try {
        	RimborsoMissione rimborsoMissione = rimborsoMissioneService.getRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), idMissione, true, true);
        	if (rimborsoMissione != null){
            	rimborsoMissione.setTotaleRimborsoComplessivo(rimborsoMissione.getTotaleRimborso());
            	rimborsoMissione.setTotaleRimborsoSenzaAnticipi(rimborsoMissione.getTotaleRimborsoSenzaSpeseAnticipate());
            	rimborsoMissione.setRimborsoMissioneDettagli(null);
        	}
        	return JSONResponseEntity.ok(rimborsoMissione);
        } catch (AwesomeException e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoMissione.getId() == null){
            try {
                rimborsoMissione =  rimborsoMissioneService.createRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), rimborsoMissione);
    		} catch (AwesomeException e) {
    			log.error("ERRORE createRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE createRimborsoMissione",e);
    			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    		String error = "Id Rimborso Missione non valorizzato";
			log.error("ERRORE createRimborsoMissione",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoMissione.getId() != null){
    		Principal principal = SecurityContextHolder.getContext().getAuthentication();
            try {
				rimborsoMissione =  rimborsoMissioneService.updateRimborsoMissione(principal, rimborsoMissione, null);
    		} catch (AwesomeException e) {
    			log.error("ERRORE modifyRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE modifyRimborsoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    		String error = "Id Rimborso Missione non valorizzato";
			log.error("ERRORE modifyRimborsoMissione",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.PUT,
            params = {"confirm"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,  
    		HttpServletRequest request, HttpServletResponse response) {
    	String basePath = Arrays.stream(request.getRequestURL().toString().split("/")).limit(3).collect(Collectors.joining("/"));
    	if (rimborsoMissione.getId() != null){
    		rimborsoMissione.setDaValidazione(daValidazione);
            try {
				rimborsoMissione = rimborsoMissioneService.updateRimborsoMissione((Principal) SecurityUtils.getCurrentUser(), rimborsoMissione, false, confirm, basePath);
    		} catch (AwesomeException e) {
    			log.error("ERRORE confirmRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE confirmRimborsoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    		String error = "Id Rimborso Missione non valorizzato";
			log.error("ERRORE confirmRimborsoMissione",error);
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
		} catch (Exception e) {
			log.error("ERRORE deleteRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

    @RequestMapping(value = "/rest/public/printRimborsoMissione",
            method = RequestMethod.GET)
    @Timed
    @ExceptionHandler(RuntimeException.class)
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
                    			log.debug("Lunghezza "+map.get(key).length);
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
                			throw new AwesomeException(Utility.getMessageException(e));
                		} 
            		}
            	}
    		} catch (ComponentException e) {
    			log.error("ERRORE printRimborsoMissione",e);
    			throw new AwesomeException(Utility.getMessageException(e));
    		} 
        }
    }
    @RequestMapping(value = "/rest/rimborsoMissione/viewAttachments/{idRimborsoMissione}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
    		@PathVariable Long idRimborsoMissione) {
        log.debug("REST request per visualizzare gli allegati dell'ordine di missione" );
        try {
            List<CMISFileAttachment> lista = rimborsoMissioneService.getAttachments((Principal) SecurityUtils.getCurrentUser(), idRimborsoMissione);
            return JSONResponseEntity.ok(lista);
		} catch (ComponentException e) {
			log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		} 
    }

    @RequestMapping(value = "/rest/public/rimborsoMissione/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegati(@RequestParam(value = "idRimborso") String idRimborsoMissione, @RequestParam(value = "token") String token, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
    	log.debug("REST request per l'upload di allegati dell'ordine di missione" );
    	if (idRimborsoMissione != null){
    		Long idRimborsoLong = new Long (idRimborsoMissione); 
    		OAuth2Authentication auth = tokenStore.readAuthentication(token);

    		if (auth != null){
    			Principal principal = (Principal) auth;
    			try {
    				if (file != null && file.getContentType() != null){
    					MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
    					if (mimeTypes == null){
    						return new ResponseEntity<String>("Il tipo di file selezionato: "+file.getContentType()+ " non è valido.", HttpStatus.BAD_REQUEST);
    					} else {
    						CMISFileAttachment cmisFileAttachment = rimborsoMissioneService.uploadAllegato(principal, idRimborsoLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
    						if (cmisFileAttachment != null){
    							return JSONResponseEntity.ok(cmisFileAttachment);
    						} else {
    							String error = "Non è stato possibile salvare il file.";
    							log.error("uploadAllegatiRimborsoMissione", error);
    							return JSONResponseEntity.badRequest(error);
    						}
    					}
    				}else {
    					String error = "File vuoto o con tipo non specificato.";
    					log.error("uploadAllegatiRimborsoMissione", error);
    					return JSONResponseEntity.badRequest(error);
    				}
    			} catch (Exception e1) {
    				log.error("uploadAllegatiRimborsoMissione", e1);
    				return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
    			}
    		} else {
    			String error = "Utente non autorizzato.";
    			log.error("uploadAllegatiRimborsoMissione", error);
    			return JSONResponseEntity.badRequest(error);
    		}
    	} else {
    		String error = "Id Dettaglio non valorizzato.";
    		log.error("uploadAllegatiRimborsoMissione", error);
    		return JSONResponseEntity.badRequest(error);
    	}
    }
}
