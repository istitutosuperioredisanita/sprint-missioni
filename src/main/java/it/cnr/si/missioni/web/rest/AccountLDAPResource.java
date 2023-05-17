/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.cnr.si.config.KeycloakRole;
import it.cnr.si.domain.CNRUser;
import it.cnr.si.missioni.service.showcase.ACEService;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


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

    @Autowired(required = false)
    private ACEService aceServiceShowcase;

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
    @RequestMapping(value = "/current-account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getAccount() {
        try {
            boolean isUserWithRole = isUserWithRole();
            String resp = "";
            if (isUserWithRole) {
                resp = accountService.getAccount(true);
            } else {
                resp = accountService.getResponseAccountWithoutInfo(false);
            }
            if (resp != null) {
                return new ResponseEntity<String>(resp, HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);
            }
        } catch (HttpClientErrorException.Unauthorized _ex) {
            return new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isUserWithRole() {
        Collection<KeycloakRole> authorities = (Collection<KeycloakRole>) securityService.getUser()
                .map(CNRUser::getAuthorities).orElse(Collections.emptyList());

        return authorities.stream()
                .anyMatch(el -> AuthoritiesConstants.USER.equals(el.getAuthority()));
    }

    @RequestMapping(value = "/account-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getAccountInfo(HttpServletRequest request,
                                                 @RequestParam(value = "username") String username) {
        String resp = accountService.getAccountFromUsername(username, true);
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
