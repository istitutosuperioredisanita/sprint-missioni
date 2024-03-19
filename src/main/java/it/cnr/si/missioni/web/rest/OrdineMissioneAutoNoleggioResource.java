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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoNoleggio;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoNoleggio;
import it.cnr.si.missioni.service.OrdineMissioneAutoNoleggioService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import org.apache.commons.io.IOUtils;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class OrdineMissioneAutoNoleggioResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneAutoNoleggioResource.class);

    private final SecurityService securityService;
    private final OrdineMissioneAutoNoleggioService ordineMissioneAutoNoleggioService;

    @Autowired

    public OrdineMissioneAutoNoleggioResource(SecurityService securityService, OrdineMissioneAutoNoleggioService ordineMissioneAutoNoleggioService) {
        this.securityService = securityService;
        this.ordineMissioneAutoNoleggioService = ordineMissioneAutoNoleggioService;
    }


    /**
     * GET  /rest/ordineMissione/autoNoleggio -> get OrdineMissioneAutoNoleggio byId
     */
    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/get",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdineMissioneAutoNoleggio(HttpServletRequest request,
                                                           @RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per visualizzare i dati dell'auto noleggiata dell'Ordine di Missione");
        try {
            OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio = ordineMissioneAutoNoleggioService.getAutoNoleggio(idMissione);
            return JSONResponseEntity.ok(ordineMissioneAutoNoleggio);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdineMissioneAutoNoleggio", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createAutoNoleggioOrdineMissione(@RequestBody OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio, HttpServletRequest request,
                                                              HttpServletResponse response) {
        if (ordineMissioneAutoNoleggio.getId() == null) {
            try {
                ordineMissioneAutoNoleggio = ordineMissioneAutoNoleggioService.createAutoNoleggio(ordineMissioneAutoNoleggio);
            } catch (AwesomeException e) {
                log.error("ERRORE createAutoNoleggioOrdineMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createAutoNoleggioOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissioneAutoNoleggio);
        } else {
            String error = "Id Ordine Missione AutoNoleggio non valorizzato";
            log.error("ERRORE createAutoNoleggioOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyAutoNoleggioOrdineMissione(@RequestBody OrdineMissioneAutoNoleggio ordineMissioneAutoNoleggio, HttpServletRequest request,
                                                              HttpServletResponse response) {
        if (ordineMissioneAutoNoleggio.getId() != null) {
            try {
                ordineMissioneAutoNoleggio = ordineMissioneAutoNoleggioService.updateAutoNoleggio(ordineMissioneAutoNoleggio);
            } catch (Exception e) {
                log.error("ERRORE modifyAutoNoleggioOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissioneAutoNoleggio);
        } else {
            String error = "Id Ordine Missione AutoNoleggio non valorizzato";
            log.error("ERRORE modifyAutoNoleggioOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/viewAttachments/{idAutoNoleggio}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
                                            @PathVariable Long idAutoNoleggio) {
        log.debug("REST request per visualizzare gli allegati dell' auto noleggiata");

        try {
            List<CMISFileAttachment> lista = ordineMissioneAutoNoleggioService.getAttachments(idAutoNoleggio);
            return JSONResponseEntity.ok(lista);
        } catch (ComponentException e) {
            log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }


    @RequestMapping(value = "/rest/public/ordineMissione/autoNoleggio/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegatiAutoNoleggio(@RequestParam(value = "idAutoNoleggio") String idAutoNoleggio, @RequestParam(value = "token") String token, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        log.debug("REST request per l'upload di allegati dell'auto noleggiata ");

        if (idAutoNoleggio != null) {
            Long idAutoNoleggioLong = Long.valueOf(idAutoNoleggio);
            try {
                if (file != null && file.getContentType() != null) {
                    MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
                    if (mimeTypes == null) {
                        return new ResponseEntity<String>("Il tipo di file selezionato: " + file.getContentType() + " non è valido.", HttpStatus.BAD_REQUEST);
                    } else {
                        CMISFileAttachment cmisFileAttachment = ordineMissioneAutoNoleggioService.uploadAllegato(idAutoNoleggioLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
                        if (cmisFileAttachment != null) {
                            return JSONResponseEntity.ok(cmisFileAttachment);
                        } else {
                            String error = "Non è stato possibile salvare il file.";
                            log.error("uploadAllegatiAutoNoleggio", error);
                            return JSONResponseEntity.badRequest(error);
                        }
                    }
                } else {
                    String error = "File vuoto o con tipo non specificato.";
                    log.error("uploadAllegatiAutoNoleggio", error);
                    return JSONResponseEntity.badRequest(error);
                }
            } catch (Exception e1) {
                log.error("uploadAllegatiAutoNoleggio", e1);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
            }
        } else {
            String error = "Id Dettaglio non valorizzato.";
            log.error("uploadAllegatiAutoNoleggio", error);
            return JSONResponseEntity.badRequest(error);
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteAutoNoleggio(@PathVariable Long ids, HttpServletRequest request) {
        try {
            ordineMissioneAutoNoleggioService.deleteAutoNoleggio(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("ERRORE deleteAutoNoleggio", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE deleteAutoNoleggio", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/createSpostamento",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createSpostamentoAutoNoleggio(@RequestBody SpostamentiAutoNoleggio spostamentoautoNoleggio, HttpServletRequest request,
                                                           HttpServletResponse response) {
        if (spostamentoautoNoleggio.getId() == null) {
            try {
                spostamentoautoNoleggio = ordineMissioneAutoNoleggioService.createSpostamentoAutoNoleggio(spostamentoautoNoleggio);
            } catch (AwesomeException e) {
                log.error("ERRORE createSpostamentoAutoNoleggio", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createSpostamentoAutoNoleggio", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(spostamentoautoNoleggio);
        } else {
            String error = "Id Spostamento Auto Noleggio non valorizzato";
            log.error("ERRORE createSpostamentoAutoNoleggio", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/modifySpostamento",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifySpostamento(@RequestBody SpostamentiAutoNoleggio spostamentiAutoNoleggio, HttpServletRequest request,
                                               HttpServletResponse response) {
        if (spostamentiAutoNoleggio.getId() != null) {
            try {
                spostamentiAutoNoleggio = ordineMissioneAutoNoleggioService.updateSpostamenti(spostamentiAutoNoleggio);
            } catch (Exception e) {
                log.error("ERRORE modifySpostamento", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(spostamentiAutoNoleggio);
        } else {
            String error = "Id Spostamento Auto Noleggio non valorizzato";
            log.error("ERRORE modifySpostamento", error);
            return JSONResponseEntity.badRequest(error);
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/spostamenti/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteSpostamento(@PathVariable Long ids, HttpServletRequest request) {
        try {
            ordineMissioneAutoNoleggioService.deleteSpostamenti(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("ERRORE deleteSpostamento", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE deleteSpostamento", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }


    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/ordineMissione/autoNoleggio/getSpostamenti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getSpostamenti(HttpServletRequest request,
                                            @RequestParam(value = "idAutoNoleggio") Long idAutoNoleggio) {
        log.debug("REST request per visualizzare i dati degli spostamenti con l'auto noleggiata dell'Ordine di Missione");
        try {
            List<SpostamentiAutoNoleggio> spostamentiAutoNoleggio = ordineMissioneAutoNoleggioService.getSpostamentiAutoNoleggio(idAutoNoleggio);
            return JSONResponseEntity.ok(spostamentiAutoNoleggio);
        } catch (ComponentException e) {
            log.error("ERRORE getSpostamenti", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }


    @RequestMapping(value = "/rest/public/printOrdineMissioneAutoNoleggio",
            method = RequestMethod.GET)
    @ExceptionHandler(RuntimeException.class)
    @Timed
    public @ResponseBody void printOrdineMissioneAutoNoleggio(HttpServletRequest request,
                                                              @RequestParam(value = "idMissione") String idMissione, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'Ordine di Missione");

        if (!StringUtils.isEmpty(idMissione)) {
            try {
                Long idMissioneLong = Long.valueOf(idMissione);
                String user = securityService.getCurrentUserLogin();
                if (user != null && idMissioneLong != null) {
                    Map<String, byte[]> map = ordineMissioneAutoNoleggioService.printOrdineMissioneAutoNoleggio(idMissioneLong);
                    if (map != null) {
                        res.setContentType("application/pdf");
                        try {
                            String headerValue = "attachment";
                            for (String key : map.keySet()) {
                                headerValue += "; filename=\"" + key + "\"";


                                res.setHeader("Content-Disposition", headerValue);
                                OutputStream outputStream = res.getOutputStream();
                                InputStream inputStream = new ByteArrayInputStream(map.get(key));

                                IOUtils.copy(inputStream, outputStream);

                                outputStream.flush();

                                inputStream.close();
                                outputStream.close();

                            }
                        } catch (IOException e) {
                            log.error("ERRORE printOrdineMissioneAutoNoleggio", e);
                            throw new AwesomeException(Utility.getMessageException(e));
                        }
                    }
                }
            } catch (ComponentException e) {
                log.error("ERRORE printOrdineMissioneAutoNoleggio", e);
                throw new AwesomeException(Utility.getMessageException(e));
            }
        }
    }


}
