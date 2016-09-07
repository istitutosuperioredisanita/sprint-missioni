package it.cnr.si.missioni.web.rest;

import it.cnr.jada.ejb.session.BusyResourceException;
import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.jada.ejb.session.PersistencyException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.service.OrdineMissioneService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.OrdineMissioneFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class OrdineMissioneResource {

    private final Logger log = LoggerFactory.getLogger(OrdineMissioneResource.class);


    @Autowired
    private TokenStore tokenStore;

    
    @Inject
    private OrdineMissioneService ordineMissioneService;

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrdineMissione>> getOrdiniMissione(HttpServletRequest request,
    		OrdineMissioneFilter filter) throws Exception {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        List<OrdineMissione> ordiniMissione = ordineMissioneService.getOrdiniMissione(SecurityUtils.getCurrentUser(), filter, true);
        return new ResponseEntity<>(
        		ordiniMissione,
        		HttpStatus.OK);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/listDaRimborsare",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrdineMissione>> getOrdiniMissioneDaRimborsare(HttpServletRequest request,
    		OrdineMissioneFilter filter) throws Exception {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione da Rimborsare" );
        filter.setStatoFlusso(Costanti.STATO_APPROVATO_FLUSSO);
        filter.setValidato("S");
        List<String> listaStati = new ArrayList<>();
        listaStati.add(Costanti.STATO_DEFINITIVO);
        listaStati.add(Costanti.STATO_CONFERMATO);
        filter.setListaStatiMissione(listaStati);
        List<OrdineMissione> ordiniMissione = ordineMissioneService.getOrdiniMissione(SecurityUtils.getCurrentUser(), filter, false);
        return new ResponseEntity<>(
        		ordiniMissione,
        		HttpStatus.OK);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/ordiniMissione/listToFinal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrdineMissione>> getOrdiniMissioneToFinal(HttpServletRequest request,
    		OrdineMissioneFilter filter) throws Exception {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        filter.setToFinal("S");
        List<OrdineMissione> ordiniMissione = ordineMissioneService.getOrdiniMissione(SecurityUtils.getCurrentUser(), filter, true);
        return new ResponseEntity<>(
        		ordiniMissione,
        		HttpStatus.OK);
    }

    /**
     * GET  /rest/ordineMissione -> get Ordini di missione per l'utente
     * @throws Exception 
     */
    @RequestMapping(value = "/rest/ordiniMissione/listToValidate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrdineMissione>> getOrdiniMissioneDaValidare(HttpServletRequest request, OrdineMissioneFilter filter) throws Exception {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        List<OrdineMissione> ordiniMissione = ordineMissioneService.getOrdiniMissioneForValidateFlows(SecurityUtils.getCurrentUser(), filter, true);
        return new ResponseEntity<>(
        		ordiniMissione,
        		HttpStatus.OK);
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
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        try {
            OrdineMissione ordineMissione = ordineMissioneService.getOrdineMissione((Principal) SecurityUtils.getCurrentUser(), idMissione, true);
            return new ResponseEntity<>(
                    ordineMissione,
                    HttpStatus.OK);
		} catch (ComponentException e) {
  	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
		} 
    }

    @RequestMapping(value = "/rest/ordineMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createOrdineMissione(@RequestBody OrdineMissione ordineMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (ordineMissione.getId() == null){
            try {
                ordineMissione = ordineMissioneService.createOrdineMissione((Principal) SecurityUtils.getCurrentUser(), ordineMissione);
    		} catch (AwesomeException e) {
    			return e.getResponse();
    		} catch (ComponentException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (OptimisticLockException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (PersistencyException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (BusyResourceException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		}
            return new ResponseEntity<>(ordineMissione, HttpStatus.CREATED);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyOrdineMissione(@RequestBody OrdineMissione ordineMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (ordineMissione.getId() != null){
    		Principal principal = SecurityContextHolder.getContext().getAuthentication();
            try {
				ordineMissione = ordineMissioneService.updateOrdineMissione(principal, ordineMissione);
    		} catch (AwesomeException e) {
    			return e.getResponse();
    		} catch (ComponentException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (Exception e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		}
            return new ResponseEntity<>(ordineMissione, HttpStatus.OK);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione",
            method = RequestMethod.PUT,
            params = {"confirm"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmOrdineMissione(@RequestBody OrdineMissione ordineMissione, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,  
    		HttpServletRequest request, HttpServletResponse response) {
    	if (ordineMissione.getId() != null){
    		ordineMissione.setDaValidazione(daValidazione);
            try {
				ordineMissione = ordineMissioneService.updateOrdineMissione((Principal) SecurityUtils.getCurrentUser(), ordineMissione, false, confirm);
    		} catch (AwesomeException e) {
    			return e.getResponse();
    		} catch (ComponentException e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		} catch (Exception e) {
    			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
    		}
            return new ResponseEntity<>(ordineMissione, HttpStatus.OK);
    	} else {
    	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
    	}
    }

    @RequestMapping(value = "/rest/ordineMissione/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> deleteOrdineMissione(@PathVariable Long ids, HttpServletRequest request) {
		try {
			ordineMissioneService.deleteOrdineMissione((Principal) SecurityUtils.getCurrentUser(), ids);
            return new ResponseEntity<>(HttpStatus.OK);
		} catch (AwesomeException e) {
			return e.getResponse();
		} catch (ComponentException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} catch (OptimisticLockException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} catch (PersistencyException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} catch (BusyResourceException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		}
	}

    @RequestMapping(value = "/rest/public/printOrdineMissione",
            method = RequestMethod.GET)
    @Timed
    public @ResponseBody void printOrdineMissione(HttpServletRequest request,
    		@RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'Ordine di Missione " );
        
        if (!StringUtils.isEmpty(idMissione)){
            try {
            	Long idMissioneLong = new Long (idMissione); 
            	OAuth2Authentication auth = tokenStore.readAuthentication(token);
            	if (auth != null){
            		Map<String, byte[]> map = ordineMissioneService.printOrdineMissione(auth, idMissioneLong);
            		if (map != null){
            			res.setContentType("application/pdf");
                    	try {
                    		String headerValue = "attachment";
                    		for (String key : map.keySet()) {
                    			System.out.println(map.get(key).length);
                    			log.error("Lunghezza "+map.get(key).length);
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
                			throw new RuntimeException(Utility.getMessageException(e));
                		} 
            		}
            	}
    		} catch (ComponentException e) {
    			throw new RuntimeException(Utility.getMessageException(e));
    		} 
        }
    }

    @RequestMapping(value = "/rest/ordineMissione/print/json",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> jsonForPrintOrdineMissione(HttpServletRequest request,
    		@RequestParam(value = "idMissione") Long idMissione) {
        log.debug("REST request per il json della stampa dell'Ordine di Missione " );
        try {
        	String json = ordineMissioneService.jsonForPrintOrdineMissione((Principal) SecurityUtils.getCurrentUser(), idMissione);
            return new ResponseEntity<>(
            		json,
                    HttpStatus.OK);
		} catch (ComponentException e) {
			return new ResponseEntity<String>(Utility.getMessageException(e), HttpStatus.BAD_REQUEST);
		} 
    }

    @RequestMapping(value = "/rest/ordineMissione/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegatiOrdineMissione(HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        log.debug("REST request per l'upload di allegati all'Ordine di Missione " );
//        if (ordineMissione.getId() != null){
//            	ordineMissioneService.uploadAllegatoOrdineMissione((Principal) SecurityUtils.getCurrentUser(), (Long)ordineMissione.getId(), file.getInputStream());
//            try {
                return new ResponseEntity<>(
                		null,
                        HttpStatus.OK);
//    		} catch (ComponentException e) {
//    			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//    		} 
//    	} else {
//  	      return new ResponseEntity<String>(CodiciErrore.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
//    	}
    }
	private void controlloCampiObbligatori(OrdineMissione ordineMissione) {
		if (!ordineMissione.isToBeCreated()){
			controlloDatiObbligatoriDaGUI(ordineMissione);
		}
		if (StringUtils.isEmpty(ordineMissione.getAnno())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Anno");
		} else if (StringUtils.isEmpty(ordineMissione.getObbligoRientro())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Obbligo di Rientro");
		} else if (StringUtils.isEmpty(ordineMissione.getUid())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utente");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoTaxi())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo del Taxi");
		} else if (StringUtils.isEmpty(ordineMissione.getUtilizzoAutoNoleggio())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Utilizzo auto a noleggio");
		} else if (StringUtils.isEmpty(ordineMissione.getStato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Stato");
		} else if (StringUtils.isEmpty(ordineMissione.getValidato())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Validato");
		} else if (StringUtils.isEmpty(ordineMissione.getNumero())){
			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Numero");
		}
	}
}
