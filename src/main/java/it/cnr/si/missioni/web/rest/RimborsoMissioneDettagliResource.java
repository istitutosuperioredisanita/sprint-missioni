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
import it.cnr.si.missioni.cmis.CMISFileContent;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import it.cnr.si.missioni.service.RimborsoMissioneDettagliService;
import it.cnr.si.missioni.service.RimborsoMissioneService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class RimborsoMissioneDettagliResource {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneDettagliResource.class);

    @Autowired
    private RimborsoMissioneDettagliService rimborsoMissioneDettagliService;

    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    @Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "/rest/rimborsoMissione/dettagli/get",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getDettagli(HttpServletRequest request,
                                         @RequestParam(value = "idRimborsoMissione") Long idRimborsoMissione) {
        log.debug("REST request per visualizzare i dettagli del Rimborso della Missione");
        try {
            List<RimborsoMissioneDettagli> dettagli = rimborsoMissioneDettagliService.getRimborsoMissioneDettagli(idRimborsoMissione);
            return JSONResponseEntity.ok(dettagli);
        } catch (ComponentException e) {
            log.error("ERRORE getDettagli", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/rimborsoMissione/dettagli/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyDettaglio(@RequestBody RimborsoMissioneDettagli dettaglio, HttpServletRequest request,
                                             HttpServletResponse response) {
        if (dettaglio.getId() != null) {
            try {
                dettaglio = rimborsoMissioneDettagliService.updateRimborsoMissioneDettagli(dettaglio);
            } catch (Exception e) {
                log.error("ERRORE getDettagli", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(dettaglio);
        } else {
            log.error("modifyDettaglio Id non valorizzato");
            return JSONResponseEntity.badRequest("modifyDettaglio Id non valorizzato");
        }
    }

    @RequestMapping(value = "/rest/rimborsoMissione/dettagli/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createDettaglio(@RequestBody RimborsoMissioneDettagli dettaglio, HttpServletRequest request,
                                             HttpServletResponse response) {
        if (dettaglio.getId() == null) {
            try {
                dettaglio = rimborsoMissioneDettagliService.createRimborsoMissioneDettagli(dettaglio);
            } catch (AwesomeException e) {
                log.error("createDettaglio", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("createDettaglio", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(dettaglio);
        } else {
            log.error("createDettaglio Id non valorizzato");
            return JSONResponseEntity.badRequest("createDettaglio Id non valorizzato");
        }
    }

    @RequestMapping(value = "/rest/rimborsoMissione/dettagli/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteDettaglio(@PathVariable Long id, HttpServletRequest request) {
        try {
            rimborsoMissioneDettagliService.deleteRimborsoMissioneDettagli(id);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("deleteDettaglio", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("createDettaglio", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/rimborsoMissione/dettagli/viewAttachments/{idDettaglioRimborsoMissione}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
                                            @PathVariable Long idDettaglioRimborsoMissione) {
        log.debug("REST request per visualizzare gli allegati dei dettagli del Rimborso della Missione");
        try {
            List<CMISFileAttachment> lista = rimborsoMissioneDettagliService.getAttachments(idDettaglioRimborsoMissione);
            return JSONResponseEntity.ok(lista);
        } catch (ComponentException e) {
            log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/public/rimborsoMissione/dettaglio/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegatiDettaglioRimborsoMissione(@RequestParam(value = "idDettaglioRimborso") String idDettaglioRimborsoMissione, @RequestParam(value = "token") String token, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        log.debug("REST request per l'upload di allegati al dettaglio del Rimborso Missione");
        if (idDettaglioRimborsoMissione != null) {
            Long idDettaglioRimborsoLong = Long.valueOf(idDettaglioRimborsoMissione);

            try {
                if (file != null && file.getContentType() != null) {
                    MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
                    if (mimeTypes == null) {
                        return new ResponseEntity<String>("Il tipo di file selezionato: " + file.getContentType() + " non è valido.", HttpStatus.BAD_REQUEST);
                    } else {
                        CMISFileAttachment cmisFileAttachment = rimborsoMissioneDettagliService.uploadAllegato(idDettaglioRimborsoLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
                        if (cmisFileAttachment != null) {
                            return JSONResponseEntity.ok(cmisFileAttachment);
                        } else {
                            String error = "Non è stato possibile salvare il file.";
                            log.error("uploadAllegatiDettaglioRimborsoMissione", error);
                            return JSONResponseEntity.badRequest(error);
                        }
                    }
                } else {
                    String error = "File vuoto o con tipo non specificato.";
                    log.error("uploadAllegatiDettaglioRimborsoMissione", error);
                    return JSONResponseEntity.badRequest(error);
                }
            } catch (Exception e1) {
                log.error("uploadAllegatiDettaglioRimborsoMissione", e1);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
            }
        } else {
            String error = "Id Dettaglio non valorizzato.";
            log.error("uploadAllegatiDettaglioRimborsoMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/public/getAttachment",
            method = RequestMethod.GET)
    @Timed
    public @ResponseBody void getAttachment(HttpServletRequest request,
                                            @RequestParam(value = "id") String id, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per il downlaod degli allegati ");

        if (!StringUtils.isEmpty(id)) {
            try {
                String user = securityService.getCurrentUserLogin();
                if (user != null) {
                    CMISFileContent cmisFileContent = missioniCMISService.getAttachment(id);

                    if (cmisFileContent != null) {
                        String fileName = cmisFileContent.getFileName();
                        res.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment;filename=\"".concat(fileName).concat("\""));
                        res.setContentType(cmisFileContent.getMimeType());

                        try {
                            ServletOutputStream outputStream = res.getOutputStream();
                            IOUtils.copy(cmisFileContent.getStream(), outputStream);
                            outputStream.flush();
                            outputStream.close();
                        } catch (ClientAbortException e) {
                            log.info("client aborted connection while serving content {} {}", id, fileName, e);
                        } catch (IOException e) {
                            log.error("unable to serve content {} {}", id, fileName, e);
                            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        }
                    } else {
                        res.setStatus(HttpStatus.BAD_REQUEST.value());
                    }
                }
            } catch (AwesomeException e) {
                log.error("getAttachment", e);
                throw new AwesomeException(Utility.getMessageException(e));
            }
        }
    }

    @RequestMapping(value = "/rest/deleteAttachment",
            method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<?> deleteAttachment(HttpServletRequest request,
                                              @RequestParam(value = "id") String id, @RequestParam(value = "idRimborsoMissione")  Long idRimborso) {
        log.debug("REST request per il downlaod degli allegati ");

        if (!StringUtils.isEmpty(id)) {
            try {
                rimborsoMissioneService.gestioneCancellazioneAllegati(id, idRimborso);
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
    /*
    @RequestMapping(value = "/rest/deleteAttachment/{id}/{idRimborso}",
            method = RequestMethod.GET)
    @Timed
    public ResponseEntity<?> deleteAttachment(HttpServletRequest request,
                                              @PathVariable String id, @PathVariable Long idRimborso) {
        log.debug("REST request per il downlaod degli allegati ");

        if (!StringUtils.isEmpty(id)) {
            try {
                rimborsoMissioneService.gestioneCancellazioneAllegati(id, idRimborso);
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
    */

}
