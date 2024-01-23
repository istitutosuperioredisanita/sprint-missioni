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
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.service.OrdineMissioneService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.MissioneFilter;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for managing the current user's account.
 */
@RestControllerAdvice
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class OrdineMissioneResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneResource.class);

    @Autowired
    SecurityService securityService;

    @Autowired
    private OrdineMissioneService ordineMissioneService;

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdiniMissione(HttpServletRequest request,
                                               MissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione ");
        List<OrdineMissione> ordiniMissione;
        try {
            ordiniMissione = ordineMissioneService.getOrdiniMissione(filter, true);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdiniMissione", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(ordiniMissione);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/listDaRimborsare",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdiniMissioneDaRimborsare(HttpServletRequest request,
                                                           MissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione da Rimborsare");
        filter.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
        filter.setValidato("S");
        filter.setDaAnnullare("S");
        filter.setRecuperoAutoPropria("S");
        filter.setRecuperoTaxi("S");
        List<String> listaStati = new ArrayList<>();
        listaStati.add(Costanti.STATO_DEFINITIVO);
        listaStati.add(Costanti.STATO_ANNULLATO_DOPO_APPROVAZIONE_CONSENTITO_RIMBORSO);
        filter.setListaStatiMissione(listaStati);
        List<OrdineMissione> ordiniMissione;
        try {
            ordiniMissione = ordineMissioneService.getOrdiniMissione(filter, false);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdiniMissioneDaRimborsare", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(ordiniMissione);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/listDaAnnullare",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdiniMissioneDaAnnullare(HttpServletRequest request,
                                                          MissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione da Rimborsare");
        filter.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
        filter.setGiaRimborsato("N");
        filter.setValidato("S");
        filter.setDaAnnullare("S");
        List<String> listaStati = new ArrayList<>();
        listaStati.add(Costanti.STATO_DEFINITIVO);
        filter.setListaStatiMissione(listaStati);
        List<OrdineMissione> ordiniMissione;
        try {
            ordiniMissione = ordineMissioneService.getOrdiniMissione(filter, false);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdiniMissioneDaRimborsare", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(ordiniMissione);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/listDaDuplicare",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdiniMissioneDaDuplicare(HttpServletRequest request,
                                                          MissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione da Rimborsare");
        filter.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
        filter.setValidato("S");
        filter.setDaAnnullare("S");
        List<String> listaStati = new ArrayList<>();
        listaStati.add(Costanti.STATO_DEFINITIVO);
        listaStati.add(Costanti.STATO_CONFERMATO);
        filter.setListaStatiMissione(listaStati);
        List<OrdineMissione> ordiniMissione;
        try {
            ordiniMissione = ordineMissioneService.getOrdiniMissione(filter, false);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdiniMissioneDaDuplicare", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(ordiniMissione);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/listToFinal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdiniMissioneToFinal(HttpServletRequest request,
                                                      MissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione ");
        filter.setToFinal("S");
        List<OrdineMissione> ordiniMissione;
        try {
            ordiniMissione = ordineMissioneService.getOrdiniMissione(filter, true);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdiniMissioneToFinal", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(ordiniMissione);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     *
     * @throws Exception
     */
    @RequestMapping(value = "/rest/ordiniMissione/listToValidate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdiniMissioneDaValidare(HttpServletRequest request, MissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione ");
        List<OrdineMissione> ordiniMissione;
        try {
            ordiniMissione = ordineMissioneService.getOrdiniMissioneForValidateFlows(filter, true);
        } catch (Exception e) {
            log.error("ERRORE getOrdiniMissioneDaValidare", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
        return JSONResponseEntity.ok(ordiniMissione);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordine di missione byId
     */
    @RequestMapping(value = "/rest/ordineMissione/getById",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrdineMissione(HttpServletRequest request,
                                               @RequestParam(value = "id") Long idMissione) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione ");
        try {
            OrdineMissione ordineMissione = ordineMissioneService.getOrdineMissione(idMissione, true);
            return JSONResponseEntity.ok(ordineMissione);
        } catch (ComponentException e) {
            log.error("ERRORE getOrdineMissione", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/ordineMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createOrdineMissione(@RequestBody OrdineMissione ordineMissione, HttpServletRequest request,
                                                  HttpServletResponse response) {
        if (ordineMissione.getId() == null) {
            try {
                ordineMissione = ordineMissioneService.createOrdineMissione(ordineMissione);
            } catch (AwesomeException e) {
                log.error("ERRORE createOrdineMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE createOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissione);
        } else {
            String error = "Id Rimborso Missione non valorizzato";
            log.error("ERRORE createOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyOrdineMissione(@RequestBody OrdineMissione ordineMissione, HttpServletRequest request,
                                                  HttpServletResponse response) {
        if (ordineMissione.getId() != null) {
            try {
                ordineMissione = ordineMissioneService.updateOrdineMissione(ordineMissione);
            } catch (AwesomeException e) {
                log.error("ERRORE modifyOrdineMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE modifyOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissione);
        } else {
            String error = "Id Rimborso Missione non valorizzato";
            log.error("ERRORE modifyOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione",
            method = RequestMethod.PUT,
            params = {"confirm"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmOrdineMissione(@RequestBody OrdineMissione ordineMissione, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,
                                                   HttpServletRequest request, HttpServletResponse response) {

        String basePath = Arrays.stream(request.getRequestURL().toString().split("/")).limit(3).collect(Collectors.joining("/"));

        if (ordineMissione.getId() != null) {
            ordineMissione.setDaValidazione(daValidazione);
            try {
                ordineMissione = ordineMissioneService.updateOrdineMissione(ordineMissione, false, confirm, basePath);
            } catch (AwesomeException e) {
                log.error("ERRORE confirmOrdineMissione", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("ERRORE confirmOrdineMissione", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
            return JSONResponseEntity.ok(ordineMissione);
        } else {
            String error = "Id Rimborso Missione non valorizzato";
            log.error("ERRORE confirmOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteOrdineMissione(@PathVariable Long ids, HttpServletRequest request) {
        try {
            ordineMissioneService.deleteOrdineMissione(ids);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("ERRORE deleteOrdineMissione", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("ERRORE deleteOrdineMissione", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/public/printOrdineMissione",
            method = RequestMethod.GET)
    @Timed
    public @ResponseBody void printOrdineMissione(HttpServletRequest request,
                                                  @RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'Ordine di Missione ");

        if (!StringUtils.isEmpty(idMissione)) {
            try {
                String user = securityService.getCurrentUserLogin();
                Long idMissioneLong = Long.valueOf(idMissione);
                if (user != null) {
                    Map<String, byte[]> map = ordineMissioneService.printOrdineMissione(idMissioneLong);
                    if (map != null) {
                        res.setContentType("application/pdf");
                        try {
                            String headerValue = "attachment";
                            for (String key : map.keySet()) {
                                log.debug("Lunghezza " + map.get(key).length);
                                headerValue += "; filename=\"" + key + "\"";
                                OutputStream outputStream = res.getOutputStream();
                                res.setHeader("Content-Disposition", headerValue);
                                InputStream inputStream = new ByteArrayInputStream(map.get(key));
                                IOUtils.copy(inputStream, outputStream);
                                outputStream.flush();
                                inputStream.close();
                                outputStream.close();
                            }
                        } catch (IOException e) {
                            log.error("ERRORE printOrdineMissione", e);
                            throw new AwesomeException(Utility.getMessageException(e));
                        }
                    }
                }
            } catch (Exception e) {
                String exc = ExceptionUtils.getStackTrace(e);
                log.error("ERRORE printOrdineMissione", exc);
                throw new AwesomeException(Utility.getMessageException(e));
            }
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/print/json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> jsonForPrintOrdineMissione(HttpServletRequest request,
                                                        @RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per il json della stampa dell'Ordine di Missione ");
        try {
            String json = ordineMissioneService.jsonForPrintOrdineMissione(idMissione);
            return JSONResponseEntity.ok(json);
        } catch (ComponentException e) {
            log.error("ERRORE jsonForPrintOrdineMissione", e);
            throw new AwesomeException(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/viewAttachments/{idOrdineMissione}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
                                            @PathVariable Long idOrdineMissione) {
        log.debug("REST request per visualizzare gli allegati dell'ordine di missione");
        try {
            List<CMISFileAttachment> lista = ordineMissioneService.getAttachments(idOrdineMissione);
            return JSONResponseEntity.ok(lista);
        } catch (ComponentException e) {
            log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    @RequestMapping(value = "/rest/public/ordineMissione/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegati(@RequestParam(value = "idOrdineMissione") String idOrdineMissione, @RequestParam(value = "token") String token, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        log.debug("REST request per l'upload di allegati dell'ordine di missione");
        if (idOrdineMissione != null) {
            Long idMissioneLong = Long.valueOf(idOrdineMissione);
            String user = securityService.getCurrentUserLogin();
            if (user != null) {
                try {
                    if (file != null && file.getContentType() != null) {
                        MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
                        if (mimeTypes == null) {
                            return new ResponseEntity<String>("Il tipo di file selezionato: " + file.getContentType() + " non è valido.", HttpStatus.BAD_REQUEST);
                        } else {
                            CMISFileAttachment cmisFileAttachment = ordineMissioneService.uploadAllegato(idMissioneLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
                            if (cmisFileAttachment != null) {
                                return JSONResponseEntity.ok(cmisFileAttachment);
                            } else {
                                String error = "Non è stato possibile salvare il file.";
                                log.error("uploadAllegatiOrdineMissione", error);
                                return JSONResponseEntity.badRequest(error);
                            }
                        }
                    } else {
                        String error = "File vuoto o con tipo non specificato.";
                        log.error("uploadAllegatiOrdineMissione", error);
                        return JSONResponseEntity.badRequest(error);
                    }
                } catch (Exception e1) {
                    log.error("uploadAllegatiOrdineMissione", e1);
                    return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
                }
            } else {
                String error = "Utente non autorizzato.";
                log.error("uploadAllegatiOrdineMissione", error);
                return JSONResponseEntity.badRequest(error);
            }
        } else {
            String error = "Id Dettaglio non valorizzato.";
            log.error("uploadAllegatiOrdineMissione", error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    @RequestMapping(value = "/rest/ordine/deleteAttachment",
            method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<?> deleteAttachment(HttpServletRequest request,
                                              @RequestParam(value = "id") String id, @RequestParam(value = "idOrdine")  Long idOrdine) {
        log.debug("REST request per il downlaod degli allegati ");

        if (!StringUtils.isEmpty(id)) {
            try {
                ordineMissioneService.gestioneCancellazioneAllegati(id, idOrdine);
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
