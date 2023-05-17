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
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.service.TerzoPerCompensoService;
import it.cnr.si.missioni.util.*;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompensoJson;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class TerzoPerCompensoResource {

    public final static String PROXY_URL = "proxyURL";
    private final Logger log = LoggerFactory.getLogger(TerzoPerCompensoResource.class);
    @Autowired
    private TerzoPerCompensoService terzoPerCompensoService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/terzoPerCompenso",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> recuperoTerzoPerCompenso(@RequestBody JSONBody body, @RequestParam(value = PROXY_URL) String url, @RequestParam(value = "dataDa") String dataDa, @RequestParam(value = "dataA") String dataA, @RequestParam(value = "codice_fiscale") String cf, HttpServletRequest request,
                                                      HttpServletResponse response) {

        try {
            if (StringUtils.isEmpty(cf)) {
                throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO + ": Codice Fiscale");
            }
            String key = cf;

            if (!StringUtils.isEmpty(dataA)) {
                Date data = DateUtils.parseDate(dataA, DateUtils.PATTERN_DATE);
                key += data.getTime();
            }
            if (!StringUtils.isEmpty(dataDa)) {
                Date data = DateUtils.parseDate(dataDa, DateUtils.PATTERN_DATE);
                key += data.getTime();
            }

            TerzoPerCompensoJson terzi = terzoPerCompensoService.getTerzi(key, body, url, request.getQueryString(), request.getHeader(Costanti.HEADER_FOR_PROXY_AUTHORIZATION));
            if (terzi.getElements().size() == 0) {
                log.warn("TerzoPerCompenso ha restituito un array vuoto per {} {} {} {}", key, body, url, request.getQueryString());
            }
            return JSONResponseEntity.ok(terzi);
        } catch (AwesomeException e) {
            log.error("ERRORE recuperoTerzoPerCompenso", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE recuperoTerzoPerCompenso", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }
}
