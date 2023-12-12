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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneTaxi;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiTaxi;
import it.cnr.si.missioni.service.OrdineMissioneTaxiService;
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
public class OrdineMissioneTaxiResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneTaxiResource.class);

    private final SecurityService securityService;
    private final OrdineMissioneTaxiService ordineMissioneTaxiService;

    @Autowired

    public OrdineMissioneTaxiResource(SecurityService securityService, OrdineMissioneTaxiService ordineMissioneTaxiService) {
        this.securityService = securityService;
        this.ordineMissioneTaxiService = ordineMissioneTaxiService;
    }


    /**
     * GET  /rest/ordineMissione/taxi -> get OrdineMissioneTaxi byId
     */
    @RequestMapping(value = "/rest/ordineMissione/taxi/get",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdineMissioneTaxi(HttpServletRequest request,
                                                   @RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per visualizzare i dati del taxi dell'Ordine di Missione");
        try {
            OrdineMissioneTaxi ordineMissioneTaxi = ordineMissioneTaxiService.getTaxi(idMissione);
            return JSONResponseEntity.ok(ordineMissioneTaxi);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdineMissioneTaxi", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/taxi/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createTaxiOrdineMissione(@RequestBody OrdineMissioneTaxi ordineMissioneTaxi, HttpServletRequest request,
                                                             HttpServletResponse response) {
        if (ordineMissioneTaxi.getId() == null) {
            try {
                ordineMissioneTaxi = ordineMissioneTaxiService.createTaxi(ordineMissioneTaxi);
            } catch (AwesomeException e) {
                log.error("ERRORE createTaxiOrdineMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createTaxiOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissioneTaxi);
        } else {
            String error = "Id Ordine Missione Taxi non valorizzato";
            log.error("ERRORE createTaxiOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/taxi/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyTaxiOrdineMissione(@RequestBody OrdineMissioneTaxi ordineMissioneTaxi, HttpServletRequest request,
                                                             HttpServletResponse response) {
        if (ordineMissioneTaxi.getId() != null) {
            try {
                ordineMissioneTaxi = ordineMissioneTaxiService.updateTaxi(ordineMissioneTaxi);
            } catch (Exception e) {
                log.error("ERRORE modifyTaxiOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissioneTaxi);
        } else {
            String error = "Id Ordine Missione Taxi non valorizzato";
            log.error("ERRORE modifyTaxiOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/taxi/viewAttachments/{idTaxi}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
                                            @PathVariable Long idTaxi) {
        log.debug("REST request per visualizzare gli allegati del taxi");

        try {
            List<CMISFileAttachment> lista = ordineMissioneTaxiService.getAttachments(idTaxi);
            return JSONResponseEntity.ok(lista);
        } catch (ComponentException e) {
            log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }





    @RequestMapping(value = "/rest/public/ordineMissione/taxi/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegatiTaxi(@RequestParam(value = "idTaxi") String idTaxi, @RequestParam(value = "token") String token, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        log.debug("REST request per l'upload di allegati del taxi");

        if (idTaxi != null) {
            Long idTaxiLong = Long.valueOf(idTaxi);
            try {
                if (file != null && file.getContentType() != null) {
                    MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
                    if (mimeTypes == null) {
                        return new ResponseEntity<String>("Il tipo di file selezionato: " + file.getContentType() + " non è valido.", HttpStatus.BAD_REQUEST);
                    } else {
                        CMISFileAttachment cmisFileAttachment = ordineMissioneTaxiService.uploadAllegato(idTaxiLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
                        if (cmisFileAttachment != null) {
                            return JSONResponseEntity.ok(cmisFileAttachment);
                        } else {
                            String error = "Non è stato possibile salvare il file.";
                            log.error("uploadAllegatiTaxi", error);
                            return JSONResponseEntity.badRequest(error);
                        }
                    }
                } else {
                    String error = "File vuoto o con tipo non specificato.";
                    log.error("uploadAllegatiTaxi", error);
                    return JSONResponseEntity.badRequest(error);
                }
            } catch (Exception e1) {
                log.error("uploadAllegatiTaxi", e1);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
            }
        } else {
            String error = "Id Dettaglio non valorizzato.";
            log.error("uploadAllegatiTaxi", error);
            return JSONResponseEntity.badRequest(error);
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/taxi/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteTaxi(@PathVariable Long ids, HttpServletRequest request) {
        try {
            ordineMissioneTaxiService.deleteTaxi(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("ERRORE deleteTaxi", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE deleteTaxi", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/taxi/createSpostamento",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createSpostamentotaxi(@RequestBody SpostamentiTaxi spostamentotaxi, HttpServletRequest request,
                                                          HttpServletResponse response) {
        if (spostamentotaxi.getId() == null) {
            try {
                spostamentotaxi = ordineMissioneTaxiService.createSpostamentoTaxi(spostamentotaxi);
            } catch (AwesomeException e) {
                log.error("ERRORE createSpostamentotaxi", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createSpostamentotaxi", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(spostamentotaxi);
        } else {
            String error = "Id Spostamento Taxi valorizzato";
            log.error("ERRORE createSpostamentotaxi", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/taxi/modifySpostamento",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifySpostamento(@RequestBody SpostamentiTaxi spostamentiTaxi, HttpServletRequest request,
                                               HttpServletResponse response) {
        if (spostamentiTaxi.getId() != null) {
            try {
                spostamentiTaxi = ordineMissioneTaxiService.updateSpostamenti(spostamentiTaxi);
            } catch (Exception e) {
                log.error("ERRORE modifySpostamento", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(spostamentiTaxi);
        } else {
            String error = "Id Spostamento Taxi non valorizzato";
            log.error("ERRORE modifySpostamento", error);
            return JSONResponseEntity.badRequest(error);
        }
    }


    @RequestMapping(value = "/rest/ordineMissione/taxi/spostamenti/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteSpostamento(@PathVariable Long ids, HttpServletRequest request) {
        try {
            ordineMissioneTaxiService.deleteSpostamenti(ids);
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
    @RequestMapping(value = "/rest/ordineMissione/taxi/getSpostamenti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getSpostamenti(HttpServletRequest request,
                                            @RequestParam(value = "idTaxi") Long idTaxi) {
        log.debug("REST request per visualizzare i dati degli spostamenti con il Taxi dell'Ordine di Missione");
        try {
            List<SpostamentiTaxi> SpostamentiTaxi = ordineMissioneTaxiService.getSpostamentiTaxi(idTaxi);
            return JSONResponseEntity.ok(SpostamentiTaxi);
        } catch (ComponentException e) {
            log.error("ERRORE getSpostamenti", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }



    @RequestMapping(value = "/rest/public/printOrdineMissioneTaxi",
            method = RequestMethod.GET)
    @ExceptionHandler(RuntimeException.class)
    @Timed
    public @ResponseBody void printOrdineMissioneTaxi(HttpServletRequest request,
                                                          @RequestParam(value = "idMissione") String idMissione, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'Ordine di Missione");

        if (!StringUtils.isEmpty(idMissione)) {
            try {
                Long idMissioneLong = Long.valueOf(idMissione);
                String user = securityService.getCurrentUserLogin();
                if (user != null && idMissioneLong != null) {
                    Map<String, byte[]> map = ordineMissioneTaxiService.printOrdineMissioneTaxi(idMissioneLong);
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
                            log.error("ERRORE printOrdineMissioneTaxi", e);
                            throw new AwesomeException(Utility.getMessageException(e));
                        }
                    }
                }
            } catch (ComponentException e) {
                log.error("ERRORE printOrdineMissioneTaxi", e);
                throw new AwesomeException(Utility.getMessageException(e));
            }
        }
    }

}
