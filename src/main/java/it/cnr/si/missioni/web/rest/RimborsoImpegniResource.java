package it.cnr.si.missioni.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import it.cnr.si.missioni.service.OrdineMissioneAutoPropriaService;
import it.cnr.si.missioni.service.RimborsoImpegniService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class RimborsoImpegniResource {

	@Autowired
	private RimborsoImpegniService rimborsoImpegniService;
	
    private final Logger log = LoggerFactory.getLogger(RimborsoImpegniResource.class);


    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/rimborsoMissione/impegno/getImpegni",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getImpegni(HttpServletRequest request,
    		@RequestParam(value = "idRimborsoMissione") Long idRimborsoMissione) {
        log.debug("REST request per visualizzare i dati degli impegni per Rimborso Missione" );
        try {
            List<RimborsoImpegni> rimborsoImpegni = rimborsoImpegniService.getRimborsoImpegni((Principal) SecurityUtils.getCurrentUser(), idRimborsoMissione);
            return JSONResponseEntity.ok(rimborsoImpegni);
		} catch (ComponentException e) {
			log.error("ERRORE getimpegni",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		} 
    }


    @RequestMapping(value = "/rest/rimborsoMissione/impegno/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyImpegno(@RequestBody RimborsoImpegni rimborsoImpegni, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoImpegni.getId() != null){
            try {
            	rimborsoImpegni = rimborsoImpegniService.updateRimborsoImpegni((Principal) SecurityUtils.getCurrentUser(), rimborsoImpegni);
    		} catch (Exception e) {
        		log.error("ERRORE modifyRimborsoImpegni",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoImpegni);
    	} else {
       		String error = "Id Rimborso Impegni non valorizzato";
    		log.error("ERRORE modifyRimborsoImpegni",error);
            return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione/impegno/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createRimborsoImpegni(@RequestBody RimborsoImpegni rimborsoImpegni, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoImpegni.getId() == null){
            try {
            	rimborsoImpegni = rimborsoImpegniService.createRimborsoImpegni((Principal) SecurityUtils.getCurrentUser(), rimborsoImpegni);
    		} catch (AwesomeException e) {
        		log.error("ERRORE createRimborsoImpegni",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
        		log.error("ERRORE createRimborsoImpegni",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoImpegni);
    	} else {
       		String error = "Id Rimborso Impegni valorizzato";
    		log.error("ERRORE createRimborsoImpegni",error);
            return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione/impegno/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteRimborsoImpegni(@PathVariable Long ids, HttpServletRequest request) {
		try {
			rimborsoImpegniService.deleteRimborsoImpegni((Principal) SecurityUtils.getCurrentUser(), ids);
            return JSONResponseEntity.ok();
		} catch (AwesomeException e) {
    		log.error("ERRORE deleteRimborsoImpegni",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
    		log.error("ERRORE deleteRimborsoImpegni",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}
}
