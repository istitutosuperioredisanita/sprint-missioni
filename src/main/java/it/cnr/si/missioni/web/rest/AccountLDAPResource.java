package it.cnr.si.missioni.web.rest;

import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.security.SecurityUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for current ldap user's account.
 */
@RestController
@RequestMapping("/app")
public class AccountLDAPResource {
    /**
     * GET  /rest/account -> get the current user.
     */

	@Autowired
    private TokenStore tokenStore;
    
	@Autowired
    private AccountService accountService;
    
    @RequestMapping(value = "/rest/ldap",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> isAccountLDAP() {
    	if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof LdapUserDetails)
            return new ResponseEntity<String>("{\"isLDAPAccount\": true }", HttpStatus.OK);    	
        return new ResponseEntity<String>("{\"isLDAPAccount\": false }", HttpStatus.OK);    	
    }

    /**
     * GET  /rest/activate -> activate the registered user.
     */
    @RequestMapping(value = "/rest/ldap/account/token",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getAccountFromToken(@RequestParam(value = "token") String token) {
    	OAuth2Authentication auth = tokenStore.readAuthentication(token);
    	if (auth != null){
    		String username = SecurityUtils.getCurrentUserLogin();
    		String risposta = accountService.getAccount(username, true);    		
        	return new ResponseEntity<String>(risposta, HttpStatus.OK);
    	} else {
			return new ResponseEntity<String>("Token non valido", HttpStatus.UNAUTHORIZED);
    	}
    }

}
