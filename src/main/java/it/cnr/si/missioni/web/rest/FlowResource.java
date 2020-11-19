package it.cnr.si.missioni.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.service.FlowService;
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

import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Base64;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class FlowResource {


    private final AuthenticationManager authenticationManager;

    @Autowired
    private FlowService flowService;

    private final Logger log = LoggerFactory.getLogger(FlowResource.class);

    public FlowResource(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * POST  /rest/flows/aggiornaFlusso -> update flows.
     */
    @RequestMapping(value = "/rest/flows/aggiornaFlussoConAutenticazione",
            method = RequestMethod.GET,
            params = {"id"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity aggiorna(@RequestHeader(value="authorization") String auth, @RequestParam(value = "id") String id) {
        log.debug("REST request per il ritorno del flusso");
        if (auth != null && auth.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = auth.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            String username = values[0]; //Key
            String password = values[1]; //Key
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

            return JSONResponseEntity.ok("1");
        }
        return JSONResponseEntity.badRequest("Credenziali di accesso non inserite");
    }

    /**
     * POST  /rest/flows/aggiornaFlusso -> update flows.
     */
        @RequestMapping(value = "/rest/flows/aggiornaFlusso",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity aggiornaFlusso(@RequestBody FlowResult flow) {
        flowService.aggiornaMissioneFlows((Principal) SecurityUtils.getCurrentUser(), flow);
        return JSONResponseEntity.ok(flow);
    }
}
