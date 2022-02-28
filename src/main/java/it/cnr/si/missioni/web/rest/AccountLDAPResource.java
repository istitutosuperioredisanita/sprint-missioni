package it.cnr.si.missioni.web.rest;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoRimborsoMissione;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;


import com.codahale.metrics.annotation.Timed;
import it.cnr.si.service.SecurityService;
import it.cnr.si.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * REST controller for current ldap user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountLDAPResource {
    /**
     * GET  /rest/account -> get the current user.
     */

	@Autowired
    private AccountService accountService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private Environment env;


/*    @RequestMapping(value = "/rest/ldap",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> isAccountLDAP() {
    	if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof LdapUserDetails)
            return new ResponseEntity<String>("{\"isLDAPAccount\": true }", HttpStatus.OK);    	
        return new ResponseEntity<String>("{\"isLDAPAccount\": false }", HttpStatus.OK);    	
    }*/

    /**
     * GET  /rest/activate -> activate the registered user.
     */
/*    @RequestMapping(value = "/rest/ldap/account/token",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getAccountFromToken() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        if (userDetails != null){
    		String username = userDetails.getUsername();
    		String risposta = accountService.getAccount(username, true);    		
        	return new ResponseEntity<String>(risposta, HttpStatus.OK);
    	} else {
			return new ResponseEntity<String>("Token non valido", HttpStatus.UNAUTHORIZED);
    	}
    }
*/
    @RequestMapping(value = "/siper-account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getSiperAccount() {
        String resp = accountService.getAccount(securityService.getCurrentUserLogin(), true);
        return new ResponseEntity<String>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/account-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getAccountInfo(HttpServletRequest request,
                                                 @RequestParam(value = "username") String username) {
        String resp = accountService.getAccount(username, true);
        return new ResponseEntity<String>(resp, HttpStatus.OK);
    }

    @RequestMapping(value = "/profile/info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Map<String, Object>> profileInfo() {
        List<String> profiles = Arrays.asList(env.getActiveProfiles());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("activeProfiles", profiles);
        map.put("keycloakEnabled", Boolean.valueOf(env.getProperty("keycloak.enabled", "false")));

        profiles
                .stream()
                .filter(profile -> profile.equalsIgnoreCase("dev"))
                .findAny()
                .ifPresent(profile -> map.put("ribbonEnv", profile));

        return new ResponseEntity(map, HttpStatus.OK);
    }
}
