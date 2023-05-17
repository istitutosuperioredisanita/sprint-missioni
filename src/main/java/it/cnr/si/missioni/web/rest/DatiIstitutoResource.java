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
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.service.DatiIstitutoService;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class DatiIstitutoResource {

    private final Logger log = LoggerFactory.getLogger(DatiIstitutoResource.class);


    @Autowired
    private DatiIstitutoService datiIstitutoService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/datiIstituto",
            method = RequestMethod.GET,
            params = {"istituto", "anno"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getDatiIstituto(@RequestParam(value = "istituto") String istituto, @RequestParam(value = "anno") Integer anno) {
        log.debug("REST request per visualizzare i dati Istituto");
        DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(istituto, anno);
        return JSONResponseEntity.ok(datiIstituto);
    }

    @RequestMapping(value = "/rest/datiIstituto",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createDatiIstituto(@RequestBody DatiIstituto datiIstituto, HttpServletRequest request,
                                                HttpServletResponse response) {
        DatiIstituto dati = datiIstitutoService.getDatiIstituto(datiIstituto.getIstituto(), datiIstituto.getAnno());
        if (dati != null) {
            log.error("ERRORE createDatiIstituto ", CodiciErrore.DATI_GIA_INSERITI);
            return JSONResponseEntity.badRequest(CodiciErrore.DATI_GIA_INSERITI);
        }
        try {
            datiIstituto = datiIstitutoService.creaDatiIstituto(datiIstituto);
        } catch (Exception e) {
            log.error("ERRORE createDatiIstituto ", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(datiIstituto);
    }

    @RequestMapping(value = "/rest/datiIstituto",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyDatiIstituto(@RequestBody DatiIstituto datiIstituto, HttpServletRequest request,
                                                HttpServletResponse response) {
        if (datiIstituto.getId() != null) {
            DatiIstituto dati = datiIstitutoService.getDatiIstituto(datiIstituto.getIstituto(), datiIstituto.getAnno());
            if (dati != null) {
                if (!dati.getId().equals(datiIstituto.getId())) {
                    log.error("ERRORE modifyDatiIstituto ", CodiciErrore.DATI_INCONGRUENTI);
                    return JSONResponseEntity.badRequest(CodiciErrore.DATI_INCONGRUENTI);
                }
            }
            try {
                datiIstituto = datiIstitutoService.updateDatiIstituto(datiIstituto);
            } catch (Exception e) {
                log.error("ERRORE modifyDatiIstituto", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(datiIstituto);
        } else {
            String error = "Id dati Istituto non valorizzato";
            log.error("ERRORE modifyAutoPropria", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/datiIstituto/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteDatiIstituto(@PathVariable Long ids, HttpServletRequest request) {
        try {
            datiIstitutoService.deleteDatiIstituto(ids);
            return JSONResponseEntity.ok();
        } catch (Exception e) {
            log.error("ERRORE deleteDatiIstituto", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/datiIstituto/ribalta",
            method = RequestMethod.GET)
    @Timed
    public void ribalta() {
        log.debug("REST request per ribaltare i dati istituto");
        datiIstitutoService.ribaltaDatiIstituti();
        log.debug("END REST request per ribaltare i dati istituto");
    }

}
