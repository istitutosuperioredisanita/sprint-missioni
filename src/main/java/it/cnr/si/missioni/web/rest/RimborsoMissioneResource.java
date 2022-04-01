package it.cnr.si.missioni.web.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.cnr.jada.DetailedRuntimeException;
import it.cnr.si.service.SecurityService;
import it.cnr.si.spring.storage.StorageObject;
import it.cnr.si.spring.storage.config.StoragePropertyNames;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.CMISFileAttachment;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.service.RimborsoMissioneService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.web.filter.RimborsoMissioneFilter;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class RimborsoMissioneResource {

    private final Logger log = LoggerFactory.getLogger(RimborsoMissioneResource.class);


    @Autowired
    private SecurityService securityService;

    
    @Autowired
    private RimborsoMissioneService rimborsoMissioneService;

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/list",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsoMissione(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati dei Rimborsi di Missione " );
        List<RimborsoMissione> rimborsiMissione;
		try {
			rimborsiMissione = rimborsoMissioneService.getRimborsiMissione(filter, true);
			if (Utility.nvl(filter.getRecuperoTotali(),"N").equals("S")){
				for (RimborsoMissione rimborso : rimborsiMissione){
					rimborsoMissioneService.retrieveDetails( rimborso);
					impostaTotaliRimborso(rimborso);
				}
			}
		} catch (ComponentException e) {
			log.error("ERRORE getRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/listToFinal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsoMissioneToFinal(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        filter.setToFinal("S");
        List<RimborsoMissione> rimborsiMissione = null;
        		try {
					rimborsoMissioneService.getRimborsiMissione(filter, true);
				} catch (ComponentException e) {
					log.error("ERRORE getRimborsoMissioneToFinal",e);
		            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
				}
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     */
    @RequestMapping(value = "/rest/rimborsoMissione/listToBeDeleted",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsiMissioneDaCancellare(HttpServletRequest request,
    		RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati dei Rimborsi di Missione da cancellare" );
        List<RimborsoMissione> rimborsiMissione;
        filter.setStatoFlusso("APP");
        filter.setStato("DEF");
		try {
			rimborsiMissione = rimborsoMissioneService.getRimborsiMissione(filter, false);
		} catch (ComponentException e) {
			log.error("ERRORE getRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get Ordini di missione per l'utente
     * @throws Exception 
     */
    @RequestMapping(value = "/rest/rimborsoMissione/listToValidate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsoMissioneDaValidare(HttpServletRequest request, RimborsoMissioneFilter filter) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        List<RimborsoMissione> rimborsiMissione;
		try {
			rimborsiMissione = rimborsoMissioneService.getRimborsiMissioneForValidateFlows(filter, true);
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissioneDaValidare",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
        return JSONResponseEntity.ok(rimborsiMissione);
    }

    /**
     * GET  /rest/rimborsoMissione -> get rimborso di missione byId
     */
    @RequestMapping(value = "/rest/rimborsoMissione/getById",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getRimborsoMissione(HttpServletRequest request,
    		@RequestParam(value = "id") Long idMissione) {
        log.debug("REST request per visualizzare i dati degli Ordini di Missione " );
        try {
        	RimborsoMissione rimborsoMissione = rimborsoMissioneService.getRimborsoMissione( idMissione, true, true);
        	impostaTotaliRimborso(rimborsoMissione);
        	return JSONResponseEntity.ok(rimborsoMissione);
        } catch (AwesomeException e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE getRimborsoMissione",e);
			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
    }

	protected void impostaTotaliRimborso(RimborsoMissione rimborsoMissione) {
		if (rimborsoMissione != null){
			rimborsoMissione.setTotaleRimborsoComplessivo(rimborsoMissione.getTotaleRimborso());
			rimborsoMissione.setTotaleRimborsoSenzaAnticipi(rimborsoMissione.getTotaleRimborsoSenzaSpeseAnticipate());
			rimborsoMissione.setRimborsoMissioneDettagli(null);
		}
	}

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoMissione.getId() == null){
            try {
                rimborsoMissione =  rimborsoMissioneService.createRimborsoMissione( rimborsoMissione);
    		} catch (AwesomeException e) {
    			log.error("ERRORE createRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE createRimborsoMissione",e);
    			return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    		String error = "Id Rimborso Missione non valorizzato";
			log.error("ERRORE createRimborsoMissione",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> modifyRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, HttpServletRequest request,
                                             HttpServletResponse response) {
    	if (rimborsoMissione.getId() != null){
            try {
				rimborsoMissione =  rimborsoMissioneService.updateRimborsoMissione(rimborsoMissione, null);
    		} catch (AwesomeException e) {
    			log.error("ERRORE modifyRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE modifyRimborsoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    		String error = "Id Rimborso Missione non valorizzato";
			log.error("ERRORE modifyRimborsoMissione",error);
    	    return JSONResponseEntity.badRequest(error);
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione",
            method = RequestMethod.PUT,
            params = {"confirm"}, 
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> confirmRimborsoMissione(@RequestBody RimborsoMissione rimborsoMissione, @RequestParam(value = "confirm") Boolean confirm, @RequestParam(value = "daValidazione") String daValidazione,  
    		HttpServletRequest request, HttpServletResponse response) {
    	String basePath = Arrays.stream(request.getRequestURL().toString().split("/")).limit(3).collect(Collectors.joining("/"));
    	if (rimborsoMissione.getId() != null){
    		rimborsoMissione.setDaValidazione(daValidazione);
            try {
				rimborsoMissione = rimborsoMissioneService.updateRimborsoMissione( rimborsoMissione, false, confirm, basePath);
    		} catch (AwesomeException e) {
    			log.error("ERRORE confirmRimborsoMissione",e);
    			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
    		} catch (Exception e) {
    			log.error("ERRORE confirmRimborsoMissione",e);
                return JSONResponseEntity.badRequest(Utility.getMessageException(e));
    		}
            return JSONResponseEntity.ok(rimborsoMissione);
    	} else {
    		String error = "Id Rimborso Missione non valorizzato";
			log.error("ERRORE confirmRimborsoMissione",error);
    	    return JSONResponseEntity.badRequest("Id Rimborso Missione non valorizzato");
    	}
    }

    @RequestMapping(value = "/rest/rimborsoMissione/{ids}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity deleteRimborsoMissione(@PathVariable Long ids, HttpServletRequest request) {
		try {
			rimborsoMissioneService.deleteRimborsoMissione( ids);
            return JSONResponseEntity.ok();
		} catch (AwesomeException e) {
			log.error("ERRORE deleteRimborsoMissione",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE deleteRimborsoMissione",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
	}

    @RequestMapping(value = "/rest/public/printRimborsoMissione",
            method = RequestMethod.GET)
    @Timed
    @ExceptionHandler(RuntimeException.class)
    public @ResponseBody void printRimborsoMissione(HttpServletRequest request,
    		@RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse res) {
        log.debug("REST request per la stampa dell'rimborso di Missione " );

		String user = securityService.getCurrentUserLogin();
		if (user != null && !StringUtils.isEmpty(idMissione)){
            try {
            	Long idMissioneLong = new Long (idMissione);
            		Map<String, byte[]> map = rimborsoMissioneService.printRimborsoMissione(idMissioneLong);
            		if (map != null){
            			res.setContentType("application/pdf");
                    	try {
                    		String headerValue = "attachment";
                    		for (String key : map.keySet()) {
                    			System.out.println(map.get(key).length);
                    			log.debug("Lunghezza "+map.get(key).length);
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
            				log.error("ERRORE deleteRimborsoMissione",e);
                			throw new AwesomeException(Utility.getMessageException(e));
                		}
            	}
    		} catch (ComponentException e) {
    			log.error("ERRORE printRimborsoMissione",e);
    			throw new AwesomeException(Utility.getMessageException(e));
    		} 
        }
    }
    @RequestMapping(value = "/rest/rimborsoMissione/viewAttachments/{idRimborsoMissione}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAttachments(HttpServletRequest request,
    		@PathVariable Long idRimborsoMissione) {
        log.debug("REST request per visualizzare gli allegati dell'ordine di missione" );
        try {
            List<CMISFileAttachment> lista = rimborsoMissioneService.getAttachments( idRimborsoMissione);
            return JSONResponseEntity.ok(lista);
		} catch (ComponentException e) {
			log.error("getAttachments", e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		} 
    }

    @RequestMapping(value = "/rest/public/rimborsoMissione/uploadAllegati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Timed
    public ResponseEntity<?> uploadAllegati(@RequestParam(value = "idRimborso") String idRimborsoMissione, @RequestParam(value = "token") String token, HttpServletRequest req, @RequestParam("file") MultipartFile file) {
    	log.debug("REST request per l'upload di allegati dell'ordine di missione" );
		String user = securityService.getCurrentUserLogin();
		if (user != null && idRimborsoMissione != null){
    		Long idRimborsoLong = new Long (idRimborsoMissione);
    			try {
    				if (file != null && file.getContentType() != null){
    					MimeTypes mimeTypes = Utility.getMimeType(file.getContentType());
    					if (mimeTypes == null){
    						return new ResponseEntity<String>("Il tipo di file selezionato: "+file.getContentType()+ " non è valido.", HttpStatus.BAD_REQUEST);
    					} else {
    						CMISFileAttachment cmisFileAttachment = rimborsoMissioneService.uploadAllegato(idRimborsoLong, file.getInputStream(), file.getOriginalFilename(), mimeTypes);
    						if (cmisFileAttachment != null){
    							return JSONResponseEntity.ok(cmisFileAttachment);
    						} else {
    							String error = "Non è stato possibile salvare il file.";
    							log.error("uploadAllegatiRimborsoMissione", error);
    							return JSONResponseEntity.badRequest(error);
    						}
    					}
    				}else {
    					String error = "File vuoto o con tipo non specificato.";
    					log.error("uploadAllegatiRimborsoMissione", error);
    					return JSONResponseEntity.badRequest(error);
    				}
    			} catch (Exception e1) {
    				log.error("uploadAllegatiRimborsoMissione", e1);
    				return JSONResponseEntity.badRequest(Utility.getMessageException(e1));
    			}
    	} else {
    		String error = "Id Dettaglio non valorizzato.";
    		log.error("uploadAllegatiRimborsoMissione", error);
    		return JSONResponseEntity.badRequest(error);
    	}
    }



	@RequestMapping(value = "/rest/public/zipDocumentiRimborsoMissione",
			method = RequestMethod.GET)
	@Timed
	@ExceptionHandler(RuntimeException.class)
	@SuppressWarnings("unchecked")
	public @ResponseBody void scaricaZip(HttpServletRequest request,
										 @RequestParam(value = "idMissione") String idMissione, @RequestParam(value = "token") String token, HttpServletResponse response) {
		Long idMissioneLong = new Long (idMissione);
		String user = securityService.getCurrentUserLogin();
		if (user != null && idMissioneLong != null){
			final ZipOutputStream zos;
			try {
				zos = new ZipOutputStream(response.getOutputStream());
			} catch (IOException e) {
				throw new AwesomeException(Utility.getMessageException(e));
			}
			response.setContentType("application/zip");
			response.setDateHeader("Expires", 0);
			RimborsoMissione rimborsoMissione = rimborsoMissioneService.getRimborsoMissione(idMissioneLong, false, false);
			response.setHeader("Content-disposition", "attachment; filename=DocumentiRimborsoMissione"+Utility.getUoSiper(rimborsoMissione.getUoSpesa())+"_"+rimborsoMissione.getAnno()+"_"+rimborsoMissione.getNumero()+".zip");
				try {
					String headerValue = "attachment";
							List<StorageObject> documenti = rimborsoMissioneService.getAllDocumentsMissione(rimborsoMissione);


								documenti.stream()
										.filter(storageObject -> !Optional.ofNullable(storageObject.getPropertyValue(StoragePropertyNames.BASE_TYPE_ID.value()))
												.map(String.class::cast)
												.filter(s -> s.equals(StoragePropertyNames.CMIS_FOLDER.value()))
												.isPresent())
										.forEach(documento -> {
											try {
												ZipEntry zipEntryFile = new ZipEntry((String)documento.getPropertyValue(StoragePropertyNames.NAME.value()));
												zos.putNextEntry(zipEntryFile);
												IOUtils.copyLarge(rimborsoMissioneService.getResource(documento), zos);
											} catch (ZipException e) {
												log.warn("Cannot add entry to zip file", e);
											} catch (IOException e) {
												throw new DetailedRuntimeException(e);
											}

										});
				zos.close();
				response.getOutputStream().flush();
				} catch (IOException e) {
					log.error("ERRORE scaricaZip",e);
					throw new AwesomeException(Utility.getMessageException(e));
				}
		}
	}
}
