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
import it.cnr.si.missioni.domain.custom.persistence.MissioneRespinta;
import it.cnr.si.missioni.service.MissioneRespintaService;
import it.cnr.si.missioni.util.JSONResponseEntity;
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
import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class MissioneRespintaResource {

    private final Logger log = LoggerFactory.getLogger(MissioneRespintaResource.class);

    @Autowired
    private MissioneRespintaService missioneRespintaService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/missioniRespinte/history",
            method = RequestMethod.GET,
            params = {"tipoMissione", "idMissione"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getMissioniRespinte(@RequestParam(value = "tipoMissione") String tipoMissione,
                                                 @RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per visualizzare la cronologia dei respingimenti delle missioni");
        List<MissioneRespinta> cronologia = missioneRespintaService.getCronologiaRespingimentiMissione(tipoMissione, idMissione);
        return JSONResponseEntity.ok(cronologia);
    }
}
