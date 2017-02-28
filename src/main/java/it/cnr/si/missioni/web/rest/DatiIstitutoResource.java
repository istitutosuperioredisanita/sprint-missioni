package it.cnr.si.missioni.web.rest;

import java.security.Principal;
import java.util.List;

import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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
import it.cnr.si.missioni.domain.custom.persistence.AutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.service.DatiIstitutoService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class DatiIstitutoResource {

    private final Logger log = LoggerFactory.getLogger(DatiIstitutoResource.class);


	@Autowired
    private DatiIstitutoService datiIstitutoService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/datiIstituto",
            method = RequestMethod.GET,
            params = {"istituto", "anno"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getDatiIstituto(@RequestParam(value = "istituto") String istituto, @RequestParam(value = "anno") Integer anno) {
        log.debug("REST request per visualizzare i dati Istituto");
        DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(istituto, anno);
        return JSONResponseEntity.ok(datiIstituto);
    }

    @RequestMapping(value = "/rest/datiIstituto",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createDatiIstituto(@RequestBody DatiIstituto datiIstituto, HttpServletRequest request,
                                             HttpServletResponse response) {
    	DatiIstituto dati = datiIstitutoService.getDatiIstituto(datiIstituto.getIstituto(), datiIstituto.getAnno());
    	if (dati != null){
    		log.error("ERRORE createDatiIstituto ",CodiciErrore.DATI_GIA_INSERITI);
    		return JSONResponseEntity.badRequest(CodiciErrore.DATI_GIA_INSERITI);
    	}
    	try {
    		datiIstituto = datiIstitutoService.creaDatiIstituto((Principal) SecurityUtils.getCurrentUser(), datiIstituto);
    	} catch (Exception e) {
    		log.error("ERRORE createDatiIstituto ",e);
    		return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    	}
    	return JSONResponseEntity.ok(datiIstituto);
    }

    @RequestMapping(value = "/rest/datiIstituto",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyDatiIstituto(@RequestBody DatiIstituto datiIstituto, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (datiIstituto.getId() != null){
        	DatiIstituto dati = datiIstitutoService.getDatiIstituto(datiIstituto.getIstituto(), datiIstituto.getAnno());
        	if (dati != null){
        		if (!dati.getId().equals(datiIstituto.getId())){
        			log.error("ERRORE modifyDatiIstituto ",CodiciErrore.DATI_INCONGRUENTI);
                    return JSONResponseEntity.badRequest(CodiciErrore.DATI_INCONGRUENTI);
        		}
        	}
            try {
				datiIstituto = datiIstitutoService.updateDatiIstituto((Principal) SecurityUtils.getCurrentUser(), datiIstituto);
    		} catch (Exception e) {
    			log.error("ERRORE modifyDatiIstituto",e);
    			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(datiIstituto);
    	} else {
    		String error = "Id dati Istituto non valorizzato";
			log.error("ERRORE modifyAutoPropria",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/datiIstituto/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteDatiIstituto(@PathVariable Long ids, HttpServletRequest request) {
		try {
			datiIstitutoService.deleteDatiIstituto((Principal) SecurityUtils.getCurrentUser(), ids);
            return JSONResponseEntity.ok();
		} catch (Exception e) {
			log.error("ERRORE deleteDatiIstituto",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

}
