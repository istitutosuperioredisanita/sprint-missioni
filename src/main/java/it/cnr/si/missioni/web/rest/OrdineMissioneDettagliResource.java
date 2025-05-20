package it.cnr.si.missioni.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneDettagli;
import it.cnr.si.missioni.service.OrdineMissioneDettagliService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller per la gestione dei dettagli di un Ordine Missione.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class OrdineMissioneDettagliResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneDettagliResource.class);

    @Autowired
    private OrdineMissioneDettagliService ordineMissioneDettagliService;

    /**
     * Restituisce l'elenco dei dettagli di un ordine missione dato il suo ID.
     */
    @RequestMapping(value = "/rest/ordineMissione/dettagli/get",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getDettagli(HttpServletRequest request,
                                         @RequestParam(value = "idOrdineMissione") Long idOrdineMissione) {
        log.debug("REST request per visualizzare i dettagli dell'Ordine Missione");
        try {
            List<OrdineMissioneDettagli> dettagli = ordineMissioneDettagliService.getOrdineMissioneDettagli(idOrdineMissione);
            return JSONResponseEntity.ok(dettagli);
        } catch (ComponentException e) {
            log.error("Errore in getDettagli", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }

    /**
     * Modifica un dettaglio esistente.
     */
    @RequestMapping(value = "/rest/ordineMissione/dettagli/modify",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyDettaglio(@RequestBody OrdineMissioneDettagli dettaglio, HttpServletRequest request,
                                             HttpServletResponse response) {
        if (dettaglio.getId() != null) {
            try {
                dettaglio = ordineMissioneDettagliService.updateOrdineMissioneDettagli(dettaglio);
                return JSONResponseEntity.ok(dettaglio);
            } catch (Exception e) {
                log.error("Errore in modifyDettaglio", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
        } else {
            String error = "modifyDettaglio: ID non valorizzato";
            log.error(error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    /**
     * Crea un nuovo dettaglio.
     */
    @RequestMapping(value = "/rest/ordineMissione/dettagli/create",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createDettaglio(@RequestBody OrdineMissioneDettagli dettaglio, HttpServletRequest request,
                                             HttpServletResponse response) {
        if (dettaglio.getId() == null) {
            try {
                dettaglio = ordineMissioneDettagliService.createOrdineMissioneDettagli(dettaglio);
                return JSONResponseEntity.ok(dettaglio);
            } catch (AwesomeException e) {
                log.error("Errore in createDettaglio", e);
                return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
            } catch (Exception e) {
                log.error("Errore in createDettaglio", e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
            }
        } else {
            String error = "createDettaglio: ID deve essere nullo per creare un nuovo record";
            log.error(error);
            return JSONResponseEntity.badRequest(error);
        }
    }

    /**
     * Elimina un dettaglio dato il suo ID.
     */
    @RequestMapping(value = "/rest/ordineMissione/dettagli/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteDettaglio(@PathVariable Long id, HttpServletRequest request) {
        try {
            ordineMissioneDettagliService.deleteOrdineMissioneDettagli(id);
            return JSONResponseEntity.ok();
        } catch (AwesomeException e) {
            log.error("Errore in deleteDettaglio", e);
            return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
        } catch (Exception e) {
            log.error("Errore in deleteDettaglio", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
        }
    }
}
