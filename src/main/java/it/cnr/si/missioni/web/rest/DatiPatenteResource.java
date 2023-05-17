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
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.service.DatiPatenteService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class DatiPatenteResource {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteResource.class);

    @Autowired
    private DatiPatenteService datiPatenteService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/datiPatente",
            method = RequestMethod.GET,
            params = {"user"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getDatiPatente(@RequestParam(value = "user") String user) {
        log.debug("REST request per visualizzare i dati della Patente");
        DatiPatente datiPatente = datiPatenteService.getDatiPatente(user);
        return JSONResponseEntity.ok(datiPatente);
    }

    @RequestMapping(value = "/rest/datiPatente",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> registerDatiPatente(@RequestBody DatiPatente datiPatente, HttpServletRequest request,
                                                 HttpServletResponse response) {
        log.debug("Entro nel metodo POST");
        if (datiPatente.getId() == null) {
            log.debug("id vuoto");
            DatiPatente patente = datiPatenteService.getDatiPatente(datiPatente.getUid());
            if (patente != null) {
                String error = "I dati della patente sono già inseriti";
                log.error("registerDatiPatente ", error);
                return JSONResponseEntity.badRequest(error);
            }
            try {
                datiPatente = datiPatenteService.createDatiPatente(datiPatente);
            } catch (Exception e) {
                log.error("registerDatiPatente", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok();
        } else {
            log.debug("id pieno");
            log.debug("recupero USER");
            try {
                datiPatente = datiPatenteService.updateDatiPatente(datiPatente);
                log.debug("modificata patente");
                return JSONResponseEntity.ok();
            } catch (Exception e) {
                log.error("registerDatiPatente", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
        }
    }
}
