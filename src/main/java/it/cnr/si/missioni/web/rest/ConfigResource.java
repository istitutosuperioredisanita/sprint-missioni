package it.cnr.si.missioni.web.rest;

import com.codahale.metrics.annotation.Timed;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.service.ConfigService;
import it.cnr.si.missioni.service.CronService;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.data.Faq;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * REST controller for managing config.
 */
@RolesAllowed({AuthoritiesConstants.ADMIN})
@RestController
@RequestMapping("/api")
public class ConfigResource {

	private final Logger log = LoggerFactory.getLogger(ConfigResource.class);

	@Autowired
	private CronService cronService;

	@Autowired
	private ConfigService configService;

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = "/rest/config/message",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getMessage(HttpServletRequest request,
                                             HttpServletResponse response) {
    	String message = configService.getMessage();
    	return JSONResponseEntity.ok(message);
    }

	@RolesAllowed({AuthoritiesConstants.USER})
    @RequestMapping(value = "/rest/config/faq",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getFaq(HttpServletRequest request,
                                             HttpServletResponse response) {
    	Faq faq = configService.getFaq();
    	return JSONResponseEntity.ok(faq.getElencoFaq());
    }

	@RequestMapping(value = "/rest/config/message",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> sendMessage(@RequestBody  String message, HttpServletRequest request,
                                             HttpServletResponse response) {

    	configService.updateMessage(message);
    	configService.evictMessage();
    	return JSONResponseEntity.ok("");
    }
	/**
	/**
	 * GET  /rest/config/refresh -> rechargeConfig
	 */
	@RequestMapping(value = "/rest/config/refresh",
			method = RequestMethod.GET)
	@Timed
	public void reloadConfig() {
		log.debug("REST request per ricaricare la configurazione da Alfresco");
		configService.reloadConfig();
	}

	/**
	 * GET  /rest/config/refreshCache -> refreshCache
	 */
	@RequestMapping(value = "/rest/config/refreshCache",
			method = RequestMethod.GET)
	@Timed
	public void refreshCache() {
		log.debug("REST request per ricaricare la configurazione da Alfresco");
		cronService.evictCache();
		cronService.loadCache();
	}

	/**
	 * GET  /rest/config/refreshCache -> refreshCache
	 */
	@RequestMapping(value = "/rest/config/refreshCacheTerzoCompenso",
			method = RequestMethod.GET)
	@Timed
	public void refreshCacheTerzoCompenso() {
		log.debug("REST request per svuotare la cache di terzo per compenso");
		cronService.evictCacheTerzoCompenso();
	}

	/**
	 * GET  /rest/config/specialUsers -> rechargeSpecialUser.
	 */
	@RequestMapping(value = "/rest/config/specialUsers",
			method = RequestMethod.GET)
	@Timed
	public void reloadConfigSpecialUsers() {
		log.debug("REST request per ricaricare la configurazione di specialUser da Alfresco ");
		configService.reloadUsersSpecialForUo();
	}

	/**
	 * GET  /rest/config/datiUo -> rechargeDatiUo.
	 */
	@RequestMapping(value = "/rest/config/datiUo",
			method = RequestMethod.GET)
	@Timed
	public void reloadConfigDatiUo() {
		log.debug("REST request per ricaricare la configurazione di Dati Uo da Alfresco");
		configService.reloadDatiUo();
	}

	/**
	 * GET  /rest/config/restServicesForCache -> rechargeDatiUo.
	 */
	@RequestMapping(value = "/rest/config/restServicesForCache",
			method = RequestMethod.GET)
	@Timed
	public void reloadConfigRestServicesForCache() {
		log.debug("REST request per ricaricare la configurazione di dei Servizi Rest in Cache da Alfresco");
		configService.reloadServicesForCache();
	}

	/**
	 * GET  /rest/config/datiUo -> rechargeDatiUo.
	 */
	@RequestMapping(value = "/rest/config/resendQueue",
			method = RequestMethod.GET)
	@Timed
	public void resendQueue() {
		log.debug("REST request per reinviare in coda i dati");
		configService.resendQueue();
	}
}
