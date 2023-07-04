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

import it.cnr.si.config.KeycloakRole;
import it.cnr.si.domain.CNRUser;
import it.cnr.si.security.AuthoritiesConstants;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;


/**
 * REST controller for current ldap user's account.
 */
@RestController
@RequestMapping("/api")
@Profile("keycloak")
public class AccountKeyCloakResource extends  AbstractAccountResource{

    protected boolean isUserWithRole() {
        Collection<KeycloakRole> authorities = (Collection<KeycloakRole>) super.securityService.getUser()
                .map(CNRUser::getAuthorities).orElse(Collections.emptyList());

        return authorities.stream()
                .anyMatch(el -> AuthoritiesConstants.USER.equals(el.getAuthority()));
    }

}
