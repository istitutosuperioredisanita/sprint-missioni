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
import it.cnr.si.missioni.service.ConfigService;
import it.cnr.si.missioni.service.CronService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.data.Faq;
import it.cnr.si.security.AuthoritiesConstants;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing config.
 */
@RolesAllowed({Costanti.ROLE_ADMIN})
@RestController
@RequestMapping("/api")
public class ConfigResource {

    private final Logger log = LoggerFactory.getLogger(ConfigResource.class);

    @Autowired(required = false)
    private CronService cronService;

    @Autowired
    private ConfigService configService;

    @RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = "/rest/config/message",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getMessage(HttpServletRequest request,
                                        HttpServletResponse response) {
        String message = configService.getMessage();
        return JSONResponseEntity.ok(message);
    }


    @RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = "/rest/config/releaseNotes",
            method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> getReleaseNotes(HttpServletRequest request,
                                             HttpServletResponse response) {
        String content = configService.getReleaseNotes();
        return JSONResponseEntity.ok(content);
    }

    @RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = "/rest/config/faq",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getFaq(HttpServletRequest request,
                                    HttpServletResponse response) {
        Faq faq = configService.getFaq();
        return JSONResponseEntity.ok(faq.getElencoFaq());
    }

    @RequestMapping(value = "/rest/config/message",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> sendMessage(@RequestBody String message, HttpServletRequest request,
                                         HttpServletResponse response) {

        configService.updateMessage(message);
        configService.evictMessage();
        return JSONResponseEntity.ok("");
    }

    /**
     * /**
     * GET  /rest/config/refresh -> rechargeConfig
     */
    @RequestMapping(value = "/rest/config/refresh",
            method = RequestMethod.GET)
    @Timed
    public void reloadConfig() {
        log.debug("REST request per ricaricare la configurazione da Alfresco");
        configService.reloadConfig();
    }

    /**
     * GET  /rest/config/refreshCache -> refreshCache
     */
    @RequestMapping(value = "/rest/config/refreshCache",
            method = RequestMethod.GET)
    @Timed
    public void refreshCache() {
        log.debug("REST request per ricaricare la configurazione da Alfresco");
        cronService.evictCache();
        cronService.evictCachePersone();
        cronService.evictCacheRuoli();
        cronService.evictCacheGrant();
        cronService.evictCacheAccount();
        cronService.evictCacheDirettore();
        cronService.evictCacheIdSede();
        cronService.loadCache();
    }

    /**
     * GET  /rest/config/refreshCache -> refreshCache
     */
    @RequestMapping(value = "/rest/config/refreshCacheTerzoCompenso",
            method = RequestMethod.GET)
    @Timed
    public void refreshCacheTerzoCompenso() {
        log.debug("REST request per svuotare la cache di terzo per compenso");
        cronService.evictCacheTerzoCompenso();
    }

    @RequestMapping(value = "/rest/config/refreshCacheTerzoInfo",
            method = RequestMethod.GET)
    @Timed
    public void refreshCacheTerzoInfo() {
        log.debug("REST request per svuotare la cache di terzo info");
        cronService.evictCacheTerzoInfo();
    }

    /**
     * GET  /rest/config/specialUsers -> rechargeSpecialUser.
     */
    @RequestMapping(value = "/rest/config/specialUsers",
            method = RequestMethod.GET)
    @Timed
    public void reloadConfigSpecialUsers() {
        log.debug("REST request per ricaricare la configurazione di specialUser da Alfresco ");
        configService.reloadUsersSpecialForUo();
    }

    /**
     * GET  /rest/config/datiUo -> rechargeDatiUo.
     */
    @RequestMapping(value = "/rest/config/datiUo",
            method = RequestMethod.GET)
    @Timed
    public void reloadConfigDatiUo() {
        log.debug("REST request per ricaricare la configurazione di Dati Uo da Alfresco");
        configService.reloadDatiUo();
    }

    /**
     * GET  /rest/config/restServicesForCache -> rechargeDatiUo.
     */
    @RequestMapping(value = "/rest/config/restServicesForCache",
            method = RequestMethod.GET)
    @Timed
    public void reloadConfigRestServicesForCache() {
        log.debug("REST request per ricaricare la configurazione di dei Servizi Rest in Cache da Alfresco");
        configService.reloadServicesForCache();
    }

    /**
     * GET  /rest/config/datiUo -> rechargeDatiUo.
     */
    @RequestMapping(value = "/rest/config/resendQueue",
            method = RequestMethod.GET)
    @Timed
    public void resendQueue() {
        log.info("REST request per reinviare in coda i dati");
        configService.resendQueue();
    }

    /**
     * GET  /rest/config/datiUo -> rechargeDatiUo.
     */
    @RequestMapping(value = "/rest/config/populateSignerMissioni",
            method = RequestMethod.GET)
    @Timed
    public void populateSignerMissioni() {
        log.info("REST request per popolare i dati su ACE");
        configService.populateSignerMissioni();
    }

    /**
     * GET  /rest/config/datiUo -> rechargeDatiUo.
     */
    @RequestMapping(value = "/rest/config/aggiornaPersonaleNonDipendente",
            method = RequestMethod.GET)
    @Timed
    public void aggiornaPersonaleNonDipendente() {
        log.info("REST request per aggiornare i dati su ACE");
        configService.aggiornaRapportoPersonaleEsterno();
    }
}
