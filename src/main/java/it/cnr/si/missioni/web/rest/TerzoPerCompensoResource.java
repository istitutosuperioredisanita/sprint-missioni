package it.cnr.si.missioni.web.rest;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.service.TerzoPerCompensoService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompenso;
import it.cnr.si.missioni.util.proxy.json.object.TerzoPerCompensoJson;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RolesAllowed({AuthoritiesConstants.USER})
@RequestMapping("/api")
public class TerzoPerCompensoResource {

    private final Logger log = LoggerFactory.getLogger(TerzoPerCompensoResource.class);
    public final static String PROXY_URL = "proxyURL";
    @Autowired
    private TerzoPerCompensoService terzoPerCompensoService;

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/terzoPerCompenso",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> recuperoTerzoPerCompenso(@RequestBody JSONBody body, @RequestParam(value=PROXY_URL) String url, @RequestParam(value="dataDa") String dataDa, @RequestParam(value="dataA") String dataA, @RequestParam(value="codice_fiscale") String cf, HttpServletRequest request,
            HttpServletResponse response) {
    	
        try {
    		if (StringUtils.isEmpty(cf)){
    			throw new AwesomeException(CodiciErrore.ERRGEN, CodiciErrore.CAMPO_OBBLIGATORIO+": Codice Fiscale");
    		}
        	String key = cf;
    		
    		if (!StringUtils.isEmpty(dataA)){
        		Date data = DateUtils.parseDate(dataA, DateUtils.PATTERN_DATE);
        		key += data.getTime();
    		}
    		if (!StringUtils.isEmpty(dataDa)){
        		Date data = DateUtils.parseDate(dataDa, DateUtils.PATTERN_DATE);
        		key += data.getTime();
    		}
    		
        	TerzoPerCompensoJson terzi = terzoPerCompensoService.getTerzi(key, body, url, request.getQueryString(), request.getHeader(Costanti.HEADER_FOR_PROXY_AUTHORIZATION));
            return JSONResponseEntity.ok(terzi);
		} catch (AwesomeException e) {
			log.error("ERRORE recuperoTerzoPerCompenso",e);
			return JSONResponseEntity.getResponse(HttpStatus.BAD_REQUEST, Utility.getMessageException(e));
		} catch (Exception e) {
			log.error("ERRORE recuperoTerzoPerCompenso",e);
            return JSONResponseEntity.badRequest(Utility.getMessageException(e));
		}
    }
}
