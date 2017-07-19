package it.cnr.si.missioni.service;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.DataUsersSpecial;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.proxy.cache.json.Services;

@Service
public class ConfigService {

	@Autowired
    private MissioniCMISService missioniCMISService;

    @Autowired
    private Environment env;

	private RelaxedPropertyResolver propertyResolver;	
	
	@PostConstruct
	public void init(){
		loadData();
	}


	
	public void reloadConfig() {
		evict();
		loadData();
	}

	private void loadData() {
		loadUsersSpecialForUo();
		loadDatiUo();
		loadServicesForCache();
	}

	private void evict() {
		evictCacheDatiUo();
		evictCacheServicesSigla();
		evictCacheUserSpecial();
	}

	@CacheEvict(value = Costanti.NOME_CACHE_DATI_UO, allEntries = true)
	private void evictCacheDatiUo() throws ComponentException {
	}

	@CacheEvict(value = Costanti.NOME_CACHE_SERVICES_SIGLA, allEntries = true)
	private void evictCacheServicesSigla() throws ComponentException {
	}

	@CacheEvict(value = Costanti.NOME_CACHE_USER_SPECIAL, allEntries = true)
	private void evictCacheUserSpecial() throws ComponentException {
	}

	public void reloadUsersSpecialForUo() {
		loadUsersSpecialForUo();
	}

	public void reloadServicesForCache() {
		loadServicesForCache();
	}

	public void reloadDatiUo() {
		loadDatiUo();
	}

    @Cacheable(value=Costanti.NOME_CACHE_DATI_UO)
	private DatiUo loadDatiUo() {
		InputStream is = getUo();
		if (is == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "File dati delle uo non trovato.");
		}
		try {
			return new org.codehaus.jackson.map.ObjectMapper().readValue(is, DatiUo.class); 
		} catch (Exception e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle uo." + Utility.getMessageException(e));
		}
	}

    @Cacheable(value=Costanti.NOME_CACHE_USER_SPECIAL)
	private DataUsersSpecial loadUsersSpecialForUo() {
		InputStream is = getUsersSpecial();
		if (is != null){
			try {
				return new ObjectMapper().readValue(is, DataUsersSpecial.class); 
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON dei dati degli utenti speciali per i servizi REST." + Utility.getMessageException(e));
			}
		}
		return null;
	}

	private InputStream getUsersSpecial() {
		InputStream is = null;
		String fileName = getFileNameFromUsersSpecial();
		Document node = (Document) missioniCMISService.getNodeByPath(missioniCMISService.getBasePath().getPathConfig()+"/"+missioniCMISService.sanitizeFilename(fileName));
		if (node == null || node.getContentStream() == null){
			is = this.getClass().getResourceAsStream("/it/cnr/missioni/sourceData/"+fileName);
		} else {
			is = node.getContentStream().getStream();
		}
		return is;
	}

	private InputStream getUo() {
		InputStream is = null;
		String fileName = getFileNameFromDatiUo();
		Document node = (Document) missioniCMISService.getNodeByPath(missioniCMISService.getBasePath().getPathConfig()+"/"+missioniCMISService.sanitizeFilename(fileName));
		if (node == null || node.getContentStream() == null){
			is = this.getClass().getResourceAsStream("/it/cnr/missioni/sourceData/"+fileName);
		} else {
			is = node.getContentStream().getStream();
		}
		return is;
	}

	private String getFileNameFromDatiUo(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return "datiUoDev.json";
   		} else {
   			return "datiUo.json";
   		}
	}
	
	private String getFileNameFromRestServices(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return "restServicesDev.json";
   		} else {
   			return "restServices.json";
   		}
	}
	
	private String getFileNameFromUsersSpecial(){
   		if (env.acceptsProfiles(Costanti.SPRING_PROFILE_DEVELOPMENT)) {
   			return "usersSpecialForUoDev.json";
   		} else {
   			return "usersSpecialForUo.json";
   		}
	}
	
	public DataUsersSpecial getDataUsersSpecial() {
    	return loadUsersSpecialForUo();
	}

	public DatiUo getDatiUo() {
		return loadDatiUo();
	}
	
    @Cacheable(value=Costanti.NOME_CACHE_SERVICES_SIGLA)
	private Services loadServicesForCache(){
		InputStream is = getServicesForCache();
		if (is != null){
			try {
				return new ObjectMapper().readValue(is, Services.class); 
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON dei servizi REST." + Utility.getMessageException(e));
			}
		} else {
			return null;
		}
	}

	private InputStream getServicesForCache() {
		InputStream is = null;
		this.propertyResolver = new RelaxedPropertyResolver(env, "cache.");
		
    	if (this.propertyResolver != null && Boolean.valueOf(this.propertyResolver.getProperty("init_cache")).equals(true)) {
    		String fileName = getFileNameFromRestServices();

    		Document node = (Document) missioniCMISService.getNodeByPath(missioniCMISService.getBasePath().getPathConfig()+"/"+missioniCMISService.sanitizeFilename(fileName));
    		if (node == null || node.getContentStream() == null){
    			is = this.getClass().getResourceAsStream("/it/cnr/missioni/cache/"+fileName);
    		} else {
    			is = node.getContentStream().getStream();
    		}
    	}
		return is;
	}

	public Services getServices() {
		return loadServicesForCache();
	}
}
