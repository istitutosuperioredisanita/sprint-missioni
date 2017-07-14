package it.cnr.si.missioni.web.rest;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.cmis.CMISFileAttachmentComplete;
import it.cnr.si.missioni.service.ManualService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.AuthoritiesConstants;

/**
 * REST controller for managing config.
 */
@RolesAllowed({AuthoritiesConstants.USER})
@RestController
@RequestMapping("/app")
public class ManualResource {

	private final Logger log = LoggerFactory.getLogger(ManualResource.class);
	
	@Autowired
	private ManualService manualService;
	
    @RequestMapping(value = "/rest/manual",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getManuals(HttpServletRequest request) {
        log.debug("REST request per visualizzare gli allegati dei dettagli del Rimborso della Missione" );
        try {
            List<CMISFileAttachmentComplete> lista = manualService.getManuals();
            return JSONResponseEntity.ok(lista);
		} catch (ComponentException e) {
			log.error("getManuals", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		} 
    }
}
