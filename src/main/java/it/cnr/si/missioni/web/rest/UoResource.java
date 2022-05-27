package it.cnr.si.missioni.web.rest;

import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.cnr.si.missioni.service.UoService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.proxy.json.object.Account;

import javax.annotation.security.RolesAllowed;
import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class UoResource {

    private final Logger log = LoggerFactory.getLogger(UoResource.class);

    @Autowired
    UoService uoService;
    
    /**
     * GET  /rest/direttore -> get the director.
     */
    @RequestMapping(value = "/rest/direttore",
            method = RequestMethod.GET,
            params = {"username"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getDirettore(@RequestParam(value = "username") String username) {
        log.debug("REST request per recuperare i dati del direttore");
        
        Account direttore = uoService.getDirettoreFromUsername(username);
        return JSONResponseEntity.ok(direttore);
    }

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/personForCds",
            method = RequestMethod.GET,
            params = {"cds"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getPersonForCds(@RequestParam(value = "cds") String cds) {
        log.debug("REST request per recuperare i dati delle persone di un cds");

        String rest = uoService.getPersone(null, cds);
        return JSONResponseEntity.ok(rest);
    }
    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/personForUo",
            method = RequestMethod.GET,
            params = {"uo"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getPersonForUo(@RequestParam(value = "uo") String uo) {
        log.debug("REST request per recuperare i dati delle persone di un UO");

        String rest = uoService.getPersone(uo, null);
        return JSONResponseEntity.ok(rest);
    }
}
