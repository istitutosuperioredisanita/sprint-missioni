package it.cnr.si.missioni.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.service.FlowService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.nio.charset.StandardCharsets;

import java.util.Base64;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@RolesAllowed(Costanti.ROLE_FLOWS)
public class FlowResource {

    @Autowired
    private FlowService flowService;

    private final Logger log = LoggerFactory.getLogger(FlowResource.class);

    /**
     * POST  /rest/flows/aggiornaFlusso -> update flows.
     */
    @RequestMapping(value = "/cambioStatoDomanda",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity aggiornaFlusso(@RequestBody FlowResult flow) {
        flowService.aggiornaMissioneFlows( flow);
        return JSONResponseEntity.ok(flow);
    }
}
