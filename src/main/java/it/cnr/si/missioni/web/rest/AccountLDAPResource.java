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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * REST controller for current ldap user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountLDAPResource extends AbstractAccountResource {

    protected boolean isUserWithRole() {
        /*
        Collection<ISSUser> authorities = (Collection<ISSUser>) super.securityService.getUser()
                .map(ISSUser::getAuthorities).orElse(Collections.emptyList());


        return authorities.stream()
                .anyMatch(el -> AuthoritiesConstants.USER.equals(el.getAuthority()));
*/
        return Boolean.FALSE;

    }

}
