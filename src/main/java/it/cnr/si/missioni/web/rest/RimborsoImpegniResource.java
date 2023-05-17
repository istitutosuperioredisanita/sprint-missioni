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
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.service.RimborsoImpegniService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class RimborsoImpegniResource {

    private final Logger log = LoggerFactory.getLogger(RimborsoImpegniResource.class);
    @Autowired
    private RimborsoImpegniService rimborsoImpegniService;

    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/rimborsoMissione/impegno/getImpegni",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getImpegni(HttpServletRequest request,
                                        @RequestParam(value = "idRimborsoMissione") Long idRimborsoMissione) {
        log.debug("REST request per visualizzare i dati degli impegni per Rimborso Missione");
        try {
            List<RimborsoImpegni> rimborsoImpegni = rimborsoImpegniService.getRimborsoImpegni(idRimborsoMissione);
            return JSONResponseEntity.ok(rimborsoImpegni);
        } catch (ComponentException e) {
            log.error("ERRORE getimpegni", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }


    @RequestMapping(value = "/rest/rimborsoMissione/impegno/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyImpegno(@RequestBody RimborsoImpegni rimborsoImpegni, HttpServletRequest request,
                                           HttpServletResponse response) {
        if (rimborsoImpegni.getId() != null) {
            try {
                rimborsoImpegni = rimborsoImpegniService.updateRimborsoImpegni(rimborsoImpegni);
            } catch (Exception e) {
                log.error("ERRORE modifyRimborsoImpegni", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(rimborsoImpegni);
        } else {
            String error = "Id Rimborso Impegni non valorizzato";
            log.error("ERRORE modifyRimborsoImpegni", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/rimborsoMissione/impegno/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createRimborsoImpegni(@RequestBody RimborsoImpegni rimborsoImpegni, HttpServletRequest request,
                                                   HttpServletResponse response) {
        if (rimborsoImpegni.getId() == null) {
            try {
                rimborsoImpegni = rimborsoImpegniService.createRimborsoImpegni(rimborsoImpegni);
            } catch (AwesomeException e) {
                log.error("ERRORE createRimborsoImpegni", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createRimborsoImpegni", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(rimborsoImpegni);
        } else {
            String error = "Id Rimborso Impegni valorizzato";
            log.error("ERRORE createRimborsoImpegni", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/rimborsoMissione/impegno/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteRimborsoImpegni(@PathVariable Long ids, HttpServletRequest request) {
        try {
            rimborsoImpegniService.deleteRimborsoImpegni(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("ERRORE deleteRimborsoImpegni", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE deleteRimborsoImpegni", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }
}
