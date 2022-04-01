package it.cnr.si.missioni.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.cnr.si.missioni.security.jwt.TokenProvider;
import it.cnr.si.service.SecurityService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoOrdineMissione;
import it.cnr.si.missioni.service.AnnullamentoOrdineMissioneService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AnnullamentoOrdineMissioneResource {

    private final Logger log = LoggerFactory.getLogger(AnnullamentoOrdineMissioneResource.class);


    @Autowired
    private SecurityService securityService;

    
    @Autowired
    private AnnullamentoOrdineMissioneService annullamentoOrdineMissioneService;

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/annullamentoOrdineMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnnullamento(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati dei Rimborsi di Missione " );
        List<AnnullamentoOrdineMissione> annullamenti;
		try {
			annullamenti = annullamentoOrdineMissioneService.getAnnullamenti( filter, true);
		} catch (ComponentException e) {
			log.error("ERRORE getRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(annullamenti);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     * @throws Exception 
     */
    @RequestMapping(value = "/rest/annullamentoOrdineMissione/listToValidate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnnullamentiDaValidare(HttpServletRequest request, RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        List<AnnullamentoOrdineMissione> annullamenti;
		try {
			annullamenti = annullamentoOrdineMissioneService.getAnnullamentiForValidateFlows(filter, true);
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissioneDaValidare",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(annullamenti);
    }

    /**
     * GET  /rest/annullamentoOrdineMissione -> get annullamento Ordine di missione byId
     */
    @RequestMapping(value = "/rest/annullamentoOrdineMissione/getById",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnnullamentoOrdineMissione(HttpServletRequest request,
    		@RequestParam(value = "id") Long idMissione) {
        log.debug("REST request per visualizzare i dati dell' annullamento dell'Ordine di Missione " );
        try {
        	AnnullamentoOrdineMissione annullamento = annullamentoOrdineMissioneService.getAnnullamentoOrdineMissione( idMissione, true);
        	return JSONResponseEntity.ok(annullamento);
        } catch (AwesomeException e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
    }

    @RequestMapping(value = "/rest/annullamentoOrdineMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAnnullamentoOrdineMissione(@RequestBody AnnullamentoOrdineMissione annullamento, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (annullamento.getId() == null){
            try {
            	annullamento =  annullamentoOrdineMissioneService.createAnnullamentoOrdineMissione( annullamento);
    		} catch (AwesomeException e) {
    			log.error("ERRORE createAnnullamentoOrdineMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE createAnnullamentoOrdineMissione",e);
    			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(annullamento);
    	} else {
    		String error = "Id Annullamento Ordine Missione non valorizzato";
			log.error("ERRORE createAnnullamentoOrdineMissione",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/annullamentoOrdineMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAnnullamentoOrdineMissione(@RequestBody AnnullamentoOrdineMissione annullamento, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (annullamento.getId() != null){
            try {
            	annullamento =  annullamentoOrdineMissioneService.updateAnnullamentoOrdineMissione(annullamento, null);
    		} catch (AwesomeException e) {
    			log.error("ERRORE modifyAnnullamentoOrdineMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE modifyAnnullamentoOrdineMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(annullamento);
    	} else {
    		String error = "Id Annullamento Ordine Missione non valorizzato";
			log.error("ERRORE modifyAnnullamentoOrdineMissione",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/annullamentoOrdineMissione",
            method = RequestMethod.PUT,
            params = {"confirm"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmAnnullamento(@RequestBody AnnullamentoOrdineMissione annullamento, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,  
    		HttpServletRequest request, HttpServletResponse response) {
    	String basePath = Arrays.stream(request.getRequestURL().toString().split("/")).limit(3).collect(Collectors.joining("/"));
    	if (annullamento.getId() != null){
    		annullamento.setDaValidazione(daValidazione);
            try {
            	annullamento = annullamentoOrdineMissioneService.updateAnnullamentoOrdineMissione( annullamento, false, confirm, basePath);
    		} catch (AwesomeException e) {
    			log.error("ERRORE confirmAnnullamentoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE confirmAnnullamentoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(annullamento);
    	} else {
    		String error = "Id Annullamento Missione non valorizzato";
			log.error("ERRORE confirmAnnullamentoMissione",error);
    	    return JSONResponseEntity.badRequest("Id Rimborso Missione non valorizzato");
    	}
    }

    @RequestMapping(value = "/rest/annullamentoOrdineMissione/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity deleteAnnullamento(@PathVariable Long ids, HttpServletRequest request) {
		try {
			annullamentoOrdineMissioneService.deleteAnnullamento( ids);
            return JSONResponseEntity.ok();
		} catch (AwesomeException e) {
			log.error("ERRORE deleteAnnullamentoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE deleteAnnullamentoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

    @RequestMapping(value = "/rest/public/printAnnullamentoMissione",
            method = RequestMethod.GET)
    @Timed
    @ExceptionHandler(RuntimeException.class)
    public @ResponseBody void printAnnullamentoMissione(HttpServletRequest request,
    		@RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'rimborso di Missione " );
        
        if (!StringUtils.isEmpty(idMissione)){
            try {
            	Long idMissioneLong = new Long (idMissione);
				String user = securityService.getCurrentUserLogin();
				if (user != null ){
            		Map<String, byte[]> map = annullamentoOrdineMissioneService.printAnnullamentoMissione(idMissioneLong);
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
}
