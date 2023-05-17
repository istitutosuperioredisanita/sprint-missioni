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
import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.AutoPropria;
import it.cnr.si.missioni.service.AutoPropriaService;
import it.cnr.si.missioni.util.CodiciErrore;
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
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class AutoPropriaResource {

    private final Logger log = LoggerFactory.getLogger(AutoPropriaResource.class);


    @Autowired
    private AutoPropriaService autoPropriaService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/autoPropria",
            method = RequestMethod.GET,
            params = {"user"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAutoProprie(@RequestParam(value = "user") String user) {
        log.debug("REST request per visualizzare i dati di Auto Propria");
        List<AutoPropria> autoProprie = autoPropriaService.getAutoProprie(user);
        return JSONResponseEntity.ok(autoProprie);
    }

    @RequestMapping(value = "/rest/autoPropria",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAutoPropria(@RequestBody AutoPropria autoPropria, HttpServletRequest request,
                                               HttpServletResponse response) {
        if (autoPropria.getId() == null) {
            AutoPropria auto = autoPropriaService.getAutoPropria(autoPropria.getUid(), autoPropria.getTarga());
            if (auto != null) {
                log.error("ERRORE createAutoPropria ", CodiciErrore.TARGA_GIA_INSERITA);
                return JSONResponseEntity.badRequest(CodiciErrore.TARGA_GIA_INSERITA);
            }
            try {
                autoPropria = autoPropriaService.createAutoPropria(autoPropria.getUid(), autoPropria);
            } catch (AwesomeException | ComponentException | OptimisticLockException | PersistencyException |
                     BusyResourceException e) {
                log.error("ERRORE createAutoPropria ", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(autoPropria);
        } else {
            String error = "Id Auto Propria già valorizzato";
            log.error("ERRORE createAutoPropria", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/autoPropria",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAutoPropria(@RequestBody AutoPropria autoPropria, HttpServletRequest request,
                                               HttpServletResponse response) {
        if (autoPropria != null && autoPropria.getId() != null) {
            AutoPropria auto = autoPropriaService.getAutoPropria(autoPropria.getUid(), autoPropria.getTarga());
            if (auto != null && !autoPropria.getId().equals(auto.getId())) {
                log.error("ERRORE modifyAutoPropria ", CodiciErrore.TARGA_GIA_INSERITA);
                return JSONResponseEntity.badRequest(CodiciErrore.TARGA_GIA_INSERITA);
            }
            try {
                autoPropria = autoPropriaService.updateAutoPropria(autoPropria);
            } catch (AwesomeException | ComponentException | OptimisticLockException | PersistencyException |
                     BusyResourceException e) {
                log.error("ERRORE modifyAutoPropria", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(autoPropria);
        } else {
            String error = "Id Auto Propria non valorizzato";
            log.error("ERRORE modifyAutoPropria", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/autoPropria/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteAutoPropria(@PathVariable Long ids, HttpServletRequest request) {
        try {
            autoPropriaService.deleteAutoPropria(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException | ComponentException | OptimisticLockException | PersistencyException |
                 BusyResourceException e) {
            log.error("ERRORE deleteAutoPropria", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

}
