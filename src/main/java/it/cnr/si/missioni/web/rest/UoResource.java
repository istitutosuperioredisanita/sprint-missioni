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
import it.cnr.si.missioni.service.UoService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.proxy.json.object.Account;
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

import javax.annotation.security.RolesAllowed;

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
            params = {"uo"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getDirettore(@RequestParam(value = "uo") String uo) {
        log.debug("REST request per recuperare i dati del direttore");

        Account direttore = uoService.getDirettore(uo);
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
    @RequestMapping(value = "add",
            method = RequestMethod.GET,
            params = {"uo"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getDatiUO(@RequestParam(value = "uo") String uo) {
        log.debug("REST request per recuperare i dati del direttore");

        Uo datiUo = uoService.recuperoUo(uo);
        return JSONResponseEntity.ok(datiUo);
    }
}
