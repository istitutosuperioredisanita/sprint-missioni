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

package it.cnr.si.missioni.web.rest.showcase;

import com.codahale.metrics.annotation.Timed;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.service.showcase.SIGLAService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing config.
 */
@RestController
@RequestMapping("/ace/v1")
public class ACEResource {

    private final Logger log = LoggerFactory.getLogger(ACEResource.class);

    @Autowired
    private SIGLAService siglaService;


    @RequestMapping(value = "/utente/admin",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getTerziPerCompenso(HttpServletRequest request,
                                                 HttpServletResponse response) {
        log.debug("REST request di showcase per i terzi.");
        try {
            TerzoPerCompensoJson dati = siglaService.getTerziPerCompenso();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getTerziPerCompensoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsNazioneAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getNazioni(@RequestBody JSONBody body, HttpServletRequest request,
                                        HttpServletResponse response) {
        log.debug("REST request di showcase per le nazioni.");
        try {
            NazioneJson dati = siglaService.getNazioni();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getNazioniShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsCdSAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getCds(@RequestBody JSONBody body, HttpServletRequest request,
                                    HttpServletResponse response) {
        log.debug("REST request di showcase per i cds.");
        try {
            CdsJson dati = siglaService.getCds();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getCdsShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsUnitaOrganizzativaAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getUo(@RequestBody JSONBody body, HttpServletRequest request,
                                   HttpServletResponse response) {
        log.debug("REST request di showcase per le uo.");
        try {
            UnitaOrganizzativaJson dati = siglaService.getUo();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getuoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsCdRAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getCdr(@RequestBody JSONBody body, HttpServletRequest request,
                                    HttpServletResponse response) {
        log.debug("REST request di showcase per i CDR.");
        try {
            CdrJson dati = siglaService.getCdr();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getCDRShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsCapitoloAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getVoci(@RequestBody JSONBody body, HttpServletRequest request,
                                     HttpServletResponse response) {
        log.debug("REST request di showcase per le Voci.");
        try {
            VoceJson dati = siglaService.getVoci();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getVociShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsProgettiAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getProgetti(@RequestBody JSONBody body, HttpServletRequest request,
                                         HttpServletResponse response) {
        log.debug("REST request di showcase per i Progetti.");
        try {
            ProgettoJson dati = siglaService.getProgetti();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getProgettoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsGAEAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getGae(@RequestBody JSONBody body, HttpServletRequest request,
                                    HttpServletResponse response) {
        log.debug("REST request di showcase per le GAE.");
        try {
            GaeJson dati = siglaService.getGae();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getGaeShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsImpegnoAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getImpegno(@RequestBody JSONBody body, HttpServletRequest request,
                                        HttpServletResponse response) {
        log.debug("REST request di showcase per le Impegno.");
        try {
            ImpegnoJson dati = siglaService.getImpegno();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getImpegnoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsImpegnoGaeAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getImpegnoGae(@RequestBody JSONBody body, HttpServletRequest request,
                                           HttpServletResponse response) {
        log.debug("REST request di showcase per le ImpegnoGae.");
        try {
            ImpegnoGaeJson dati = siglaService.getImpegnoGae();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getImpegnoGaeShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsTerzoAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getTerzo(@RequestBody JSONBody body, HttpServletRequest request,
                                      HttpServletResponse response) {
        log.debug("REST request di showcase per le Terzo.");
        try {
            TerzoJson dati = siglaService.getTerzo();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getTerzoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsBancaAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getBanca(@RequestBody JSONBody body, HttpServletRequest request,
                                      HttpServletResponse response) {
        log.debug("REST request di showcase per le Banca.");
        try {
            BancaJson dati = siglaService.getBanca();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getBancaShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsInquadramentoAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getInquadramento(@RequestBody JSONBody body, HttpServletRequest request,
                                              HttpServletResponse response) {
        log.debug("REST request di showcase per le Inquadramento.");
        try {
            InquadramentoJson dati = siglaService.getInquadramento();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getInquadramentoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsModalitaPagamentoAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getModpag(@RequestBody JSONBody body, HttpServletRequest request,
                                       HttpServletResponse response) {
        log.debug("REST request di showcase per le Modpag.");
        try {
            ModalitaPagamentoJson dati = siglaService.getModpag();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getModpagShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsMissioneTipoSpesaAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getTipoSpesa(@RequestBody JSONBody body, HttpServletRequest request,
                                          HttpServletResponse response) {
        log.debug("REST request di showcase per le Tipo Spesa.");
        try {
            TipoSpesaJson dati = siglaService.getTipoSpesa();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getTipoSpesaShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsDivisaAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getDivisa(@RequestBody JSONBody body, HttpServletRequest request,
                                       HttpServletResponse response) {
        log.debug("REST request di showcase per le Divisa.");
        try {
            DivisaJson dati = siglaService.getDivisa();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getTipoSpesaShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/ConsRiepilogoSiopeMandatiRestAction.json",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getStatoPagamento(@RequestBody JSONBody body, HttpServletRequest request,
                                               HttpServletResponse response) {
        log.debug("REST request di showcase per il mandato.");
        try {
            StatoPagamentoJson dati = siglaService.getStatoPagamento();
            return JSONResponseEntity.ok(dati);
        } catch (ComponentException e) {
            log.error("getMandatoShowcase", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

}
