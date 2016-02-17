package it.cnr.si.missioni.web.rest;

import it.cnr.si.missioni.service.UoService;
import it.cnr.si.missioni.util.proxy.json.object.Account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class UoResource {

    private final Logger log = LoggerFactory.getLogger(UoResource.class);

    @Autowired
    UoService uoService;
    
    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/direttore",
            method = RequestMethod.GET,
            params = {"uo"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Account> getDirettore(@RequestParam(value = "uo") String uo) {
        log.debug("REST request per recuperare i dati del direttore");
        
        Account direttore = uoService.getDirettore(uo);
        return new ResponseEntity<>(
            direttore,
            HttpStatus.OK);
    }

}
