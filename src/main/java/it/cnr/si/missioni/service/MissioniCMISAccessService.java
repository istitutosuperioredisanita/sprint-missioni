package it.cnr.si.missioni.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import it.cnr.si.missioni.util.Costanti;

/**
 * Created by francesco on 13/01/16.
 */

@Component
public class MissioniCMISAccessService {

	private final static Logger log = LoggerFactory.getLogger(MissioniCMISAccessService.class);

	private String url;

	private String username;

	private String password;

	private int maxItemsPerPage;

	@Value("${cmis.alfresco}")
	private String alfresco;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxItemsPerPage() {
		return maxItemsPerPage;
	}

	public void setMaxItemsPerPage(int maxItemsPerPage) {
		this.maxItemsPerPage = maxItemsPerPage;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAlfresco() {
		return alfresco;
	}

	public void setAlfresco(String alfresco) {
		this.alfresco = alfresco;
	}

	@Cacheable(value = Costanti.NOME_CACHE_TICKET_ALFRESCO)
	public String getTicket(String username, String password) throws Exception {
		log.info("getTicket");

		String ticketURL = alfresco + "service/api/login.json";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

		JSONObject body = new JSONObject();
		try {
			body.put("username", username);
			body.put("password", password);
			HttpEntity<String> requestEntity = new HttpEntity(body.toString(), headers);
			ResponseEntity<String> result = new RestTemplate().exchange(ticketURL, HttpMethod.POST, requestEntity,
					String.class);
			return new JSONObject(new JSONObject(result.getBody()).getString("data")).getString("ticket");

		} catch (Exception e) {
			throw new Exception("unable to create ticket for user " + username, e);
		}

	}

	@CacheEvict(value = Costanti.NOME_CACHE_TICKET_ALFRESCO, allEntries = true)
	@Scheduled(fixedDelay = 600000)
	public void cacheEvict() {
	}
}
