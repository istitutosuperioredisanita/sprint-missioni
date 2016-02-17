package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.DataUsersSpecial;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.proxy.cache.json.Services;

import java.io.InputStream;

import javax.annotation.PostConstruct;

import org.apache.chemistry.opencmis.client.api.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ConfigService {

	@Autowired
    private MissioniCMISService missioniCMISService;

	private DataUsersSpecial dataUsersSpecial;
	
    @Autowired
    private Environment env;

	private DatiUo datiUo;
	
    private Services services;

	private RelaxedPropertyResolver propertyResolver;	
	
    @PostConstruct
	public void init(){
    	reloadConfig();
    }
	
	public void reloadConfig() {
		loadUsersSpecialForUo();
		loadDatiUo();
		loadServicesForCache();
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

	private void loadDatiUo() {
		InputStream is = getUo();
		if (is == null){
			throw new AwesomeException(CodiciErrore.ERRGEN, "File dati delle uo non trovato.");
		}
		try {
			this.datiUo = new org.codehaus.jackson.map.ObjectMapper().readValue(is, DatiUo.class); 
		} catch (Exception e) {
			throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON delle uo." + Utility.getMessageException(e));
		}
	}

	private void loadUsersSpecialForUo() {
		InputStream is = getUsersSpecial();
		if (is != null){
			try {
				this.dataUsersSpecial = new ObjectMapper().readValue(is, DataUsersSpecial.class); 
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON dei dati degli utenti speciali per i servizi REST." + Utility.getMessageException(e));
			}
		}
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
		return dataUsersSpecial;
	}

	public DatiUo getDatiUo() {
		return datiUo;
	}
	
	private void loadServicesForCache(){
		InputStream is = getServicesForCache();
		if (is != null){
			try {
				this.services = new ObjectMapper().readValue(is, Services.class); 
			} catch (Exception e) {
				throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON dei servizi REST." + Utility.getMessageException(e));
			}
		} else {
			this.services = null;
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
		return services;
	}
}
