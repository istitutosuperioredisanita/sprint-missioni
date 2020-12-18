package it.cnr.si.missioni.web.rest;

import com.codahale.metrics.annotation.Timed;
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.domain.custom.persistence.MissioneRespinta;
import it.cnr.si.missioni.service.DatiPatenteService;
import it.cnr.si.missioni.service.MissioneRespintaService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class MissioneRespintaResource {

    private final Logger log = LoggerFactory.getLogger(MissioneRespintaResource.class);

	@Autowired
    private MissioneRespintaService missioneRespintaService;
    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/missioniRespinte/history",
            method = RequestMethod.GET,
            params = {"tipoMissione", "idMissione"},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getMissioniRespinte(@RequestParam(value = "tipoMissione") String tipoMissione,
											@RequestParam (value="idMissione") Long idMissione) {
        log.debug("REST request per visualizzare la cronologia dei respingimenti delle missioni");
        List<MissioneRespinta> cronologia = missioneRespintaService.getCronologiaRespingimentiMissione(SecurityUtils.getCurrentUser(), tipoMissione, idMissione);
        return JSONResponseEntity.ok(cronologia);
    }
}
