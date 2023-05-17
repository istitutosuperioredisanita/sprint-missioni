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
import it.cnr.si.missioni.domain.custom.FlowResult;
import it.cnr.si.missioni.service.FlowService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.JSONResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@RolesAllowed(Costanti.ROLE_FLOWS)
public class FlowResource {

    private final Logger log = LoggerFactory.getLogger(FlowResource.class);
    @Autowired
    private FlowService flowService;

    /**
     * POST  /rest/flows/aggiornaFlusso -> update flows.
     */
    @RequestMapping(value = "/cambioStatoDomanda",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity aggiornaFlusso(@RequestBody FlowResult flow) {
        flowService.aggiornaMissioneFlows(flow);
        return JSONResponseEntity.ok(flow);
    }
}
