package it.cnr.si.missioni.web.rest;

import java.time.ZonedDateTime;

import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.JSONResponseEntity;

import javax.annotation.security.RolesAllowed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class DateResource {

    private final Logger log = LoggerFactory.getLogger(DateResource.class);


    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/date/today",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getToday() {
        log.debug("REST request per visualizzare i dati di Auto Propria");

        return JSONResponseEntity.ok(DateUtils.getDateWithDefaultZoneId(ZonedDateTime.now()));
    }
}
