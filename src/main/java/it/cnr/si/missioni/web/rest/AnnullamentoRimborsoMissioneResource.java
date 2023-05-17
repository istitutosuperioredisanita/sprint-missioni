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
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoRimborsoMissione;
import it.cnr.si.missioni.service.AnnullamentoRimborsoMissioneService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class AnnullamentoRimborsoMissioneResource {

    private final Logger log = LoggerFactory.getLogger(AnnullamentoRimborsoMissioneResource.class);


    @Autowired
    private SecurityService securityService;


    @Autowired
    private AnnullamentoRimborsoMissioneService annullamentoRimborsoMissioneService;

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnnullamento(HttpServletRequest request,
                                             RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati dei Rimborsi di Missione ");
        List<AnnullamentoRimborsoMissione> annullamenti;
        try {
            annullamenti = annullamentoRimborsoMissioneService.getAnnullamenti(filter, true);
        } catch (ComponentException e) {
            log.error("ERRORE getRimborsoMissione", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(annullamenti);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     *
     * @throws Exception
     */
    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/listToValidate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnnullamentiDaValidare(HttpServletRequest request, RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione ");
        List<AnnullamentoRimborsoMissione> annullamenti;
        try {
            annullamenti = annullamentoRimborsoMissioneService.getAnnullamentiForValidateFlows(filter, true);
        } catch (Exception e) {
            log.error("ERRORE getRimborsoMissioneDaValidare", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(annullamenti);
    }

    /**
     * GET  /rest/annullamentoOrdineMissione -> get annullamento Ordine di missione byId
     */
    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/getById",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAnnullamentoRimborsoMissione(HttpServletRequest request,
                                                             @RequestParam(value = "id") Long idMissione) {
        log.debug("REST request per visualizzare i dati dell' annullamento dell'Ordine di Missione ");
        try {
            AnnullamentoRimborsoMissione annullamento = annullamentoRimborsoMissioneService.getAnnullamentoRimborsoMissione(idMissione);
            return JSONResponseEntity.ok(annullamento);
        } catch (AwesomeException e) {
            log.error("ERRORE getRimborsoMissione", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE getRimborsoMissione", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAnnullamentoRimborsoMissione(@RequestBody AnnullamentoRimborsoMissione annullamento, HttpServletRequest request,
                                                                HttpServletResponse response) {
        if (annullamento.getId() == null) {
            try {
                annullamento = annullamentoRimborsoMissioneService.createAnnullamentoRimborsoMissione(annullamento);
            } catch (AwesomeException e) {
                log.error("ERRORE createAnnullamentoRimborsoMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createAnnullamentoRimborsoMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(annullamento);
        } else {
            String error = "Id Annullamento Rimborso Missione non valorizzato";
            log.error("ERRORE createAnnullamentoRimborsoMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAnnullamentoRimborsoMissione(@RequestBody AnnullamentoRimborsoMissione annullamento, HttpServletRequest request,
                                                                HttpServletResponse response) {
        if (annullamento.getId() != null) {
            try {
                annullamento = annullamentoRimborsoMissioneService.updateAnnullamentoRimborsoMissione(annullamento, null);
            } catch (AwesomeException e) {
                log.error("ERRORE modifyAnnullamentoRimborsoMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE modifyAnnullamentoRimborsoMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(annullamento);
        } else {
            String error = "Id Annullamento Rimborso Missione non valorizzato";
            log.error("ERRORE modifyAnnullamentoRimborsoMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione",
            method = RequestMethod.PUT,
            params = {"confirm"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmAnnullamento(@RequestBody AnnullamentoRimborsoMissione annullamento, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,
                                                 HttpServletRequest request, HttpServletResponse response) {
        String basePath = Arrays.stream(request.getRequestURL().toString().split("/")).limit(3).collect(Collectors.joining("/"));
        if (annullamento.getId() != null) {
            try {
                annullamento = annullamentoRimborsoMissioneService.updateAnnullamentoRimborsoMissione(annullamento, false, confirm, basePath);
            } catch (AwesomeException e) {
                log.error("ERRORE confirmAnnullamentoMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE confirmAnnullamentoMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(annullamento);
        } else {
            String error = "Id Annullamento Missione non valorizzato";
            log.error("ERRORE confirmAnnullamentoMissione", error);
            return JSONResponseEntity.badRequest("Id Rimborso Missione non valorizzato");
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity deleteAnnullamento(@PathVariable Long ids, HttpServletRequest request) {
        try {
            annullamentoRimborsoMissioneService.deleteAnnullamento(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("ERRORE deleteAnnullamentoMissione", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE deleteAnnullamentoMissione", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/viewAttachments/{idAnnullamentoRimborsoMissione}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
                                            @PathVariable Long idAnnullamentoRimborsoMissione) {
        log.debug("REST request per visualizzare gli allegati dell'annullamento rimborso missione");
        try {
            List<CMISFileAttachment> lista = annullamentoRimborsoMissioneService.getAttachments(idAnnullamentoRimborsoMissione);
            return JSONResponseEntity.ok(lista);
        } catch (ComponentException e) {
            log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/viewAttachmentsFromRimborso/{idRimborsoMissione}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachmentsFromRimborso(HttpServletRequest request,
                                                        @PathVariable Long idRimborsoMissione) {
        log.debug("REST request per visualizzare gli allegati dell'annullamento rimborso missione");
        try {
            List<CMISFileAttachment> lista = annullamentoRimborsoMissioneService.getAttachmentsFromRimborso(idRimborsoMissione);
            return JSONResponseEntity.ok(lista);
        } catch (ComponentException e) {
            log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/public/annullamentoRimborsoMissione/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegati(@RequestParam(value = "idAnnullamentoRimborsoMissione") String idAnnullamentoRimborsoMissione, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        log.debug("REST request per l'upload di allegati dell'ordine di missione");
        if (idAnnullamentoRimborsoMissione != null) {
            Long idAnnullamentoRimborsoLong = Long.valueOf(idAnnullamentoRimborsoMissione);
            String user = securityService.getCurrentUserLogin();
            if (user != null) {
                try {
                    if (file != null && file.getContentType() != null) {
                        MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
                        if (mimeTypes == null) {
                            return new ResponseEntity<String>("Il tipo di file selezionato: " + file.getContentType() + " non è valido.", HttpStatus.BAD_REQUEST);
                        } else {
                            CMISFileAttachment cmisFileAttachment = annullamentoRimborsoMissioneService.uploadAllegato(idAnnullamentoRimborsoLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
                            if (cmisFileAttachment != null) {
                                return JSONResponseEntity.ok(cmisFileAttachment);
                            } else {
                                String error = "Non è stato possibile salvare il file.";
                                log.error("uploadAllegatiAnnullamentoRimborsoMissione", error);
                                return JSONResponseEntity.badRequest(error);
                            }
                        }
                    } else {
                        String error = "File vuoto o con tipo non specificato.";
                        log.error("uploadAllegatiAnnullamentoRimborsoMissione", error);
                        return JSONResponseEntity.badRequest(error);
                    }
                } catch (Exception e1) {
                    log.error("uploadAllegatiAnnullamentoRimborsoMissione", e1);
                    return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
                }
            } else {
                String error = "Utente non autorizzato.";
                log.error("uploadAllegatiAnnullamentoRimborsoMissione", error);
                return JSONResponseEntity.badRequest(error);
            }
        } else {
            String error = "Id Dettaglio non valorizzato.";
            log.error("uploadAllegatiAnnullamentoRimborsoMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/annullamentoRimborsoMissione/deleteAttachment/{id}/{idAnnullamento}",
            method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> deleteAttachment(HttpServletRequest request,
                                              @PathVariable String id, @PathVariable Long idAnnullamento) {
        log.debug("REST request per il downlaod degli allegati ");

        if (!StringUtils.isEmpty(id)) {
            try {
                annullamentoRimborsoMissioneService.gestioneCancellazioneAllegati(id, idAnnullamento);
                return JSONResponseEntity.ok();
            } catch (AwesomeException e) {
                log.error("deleteAttachment", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
        } else {
            String error = "Id Allegato non valorizzato";
            log.error("deleteAttachment", error);
            return JSONResponseEntity.badRequest(error);
        }
    }
}
