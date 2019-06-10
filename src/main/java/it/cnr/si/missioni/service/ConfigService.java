package it.cnr.si.missioni.service;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.data.DataUsersSpecial;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.data.Faq;
import it.cnr.si.missioni.util.data.UtentiPresidenteSpeciali;
import it.cnr.si.missioni.util.proxy.cache.json.Services;

@Service
public class ConfigService {

	@Autowired
    private LoadFilesService loadFilesService;
	
	Services services = null;
	DatiUo datiUo = null;
	UtentiPresidenteSpeciali utentiPresidenteSpeciali = null;
	Faq faq = null;
	DataUsersSpecial dataUsersSpecial = null;
	String message = null;
	
	@PostConstruct
	public void init(){
		loadData(true);
	}


	public void reloadConfig() {
		evictData();
		loadData(false);
	}

	private void loadData(Boolean fromInit) {
		dataUsersSpecial = loadFilesService.loadUsersSpecialForUo();
		datiUo = loadFilesService.loadDatiUo();
		utentiPresidenteSpeciali = loadFilesService.loadDatiUtentiPresidenteSpeciali();
		
		faq = loadFilesService.loadFaq();
		if (fromInit){
			services = loadFilesService.loadServicesForCache();
		}
	}

	private void evictData() {
		loadFilesService.evictUsersSpecialForUo();
		loadFilesService.evictDatiUo();
		loadFilesService.evictDatiUtentiPresidenteSpeciali();;
		loadFilesService.evictFaq();
	}

	public void reloadUsersSpecialForUo() {
		loadFilesService.evictUsersSpecialForUo();
		dataUsersSpecial = loadFilesService.loadUsersSpecialForUo();
	}

	public void reloadServicesForCache() {
		loadFilesService.evictServicesForCache();
		services = loadFilesService.loadServicesForCache();
	}

	public void reloadDatiUo() {
		loadFilesService.evictDatiUo();
		datiUo = loadFilesService.loadDatiUo();
	}

	public void reloadDatiUtentiPresidenteSpeciali() {
		loadFilesService.evictDatiUtentiPresidenteSpeciali();
		utentiPresidenteSpeciali = loadFilesService.loadDatiUtentiPresidenteSpeciali();
	}

	public void reloadFaq() {
		loadFilesService.evictFaq();
		faq = loadFilesService.loadFaq();
	}

	
	public DataUsersSpecial getDataUsersSpecial() {
    	return dataUsersSpecial;
	}

	public Faq getFaq() {
		return faq;
	}

	public DatiUo getDatiUo() {
		return datiUo;
	}

	public Services getServices() {
		return services;
	}

    public void updateMessage(String newMessage){
    	message = newMessage;
    }

	@CacheEvict(value = Costanti.NOME_CACHE_MESSAGGIO, allEntries = true)
	public void evictMessage() {
	}

	@Cacheable(value=Costanti.NOME_CACHE_MESSAGGIO)
    public String getMessage() {
    	return message;
    }


	public UtentiPresidenteSpeciali getUtentiPresidenteSpeciali() {
		return utentiPresidenteSpeciali;
	}

	public void resendQueue() {
		loadFilesService.resendQueue();
	}
    public String getReleaseNotes() {
    	try {
			return IOUtils.toString(this.getClass().getResourceAsStream("/releaseNotes/releaseNotes.md"), "utf-8");
		} catch (IOException e) {
			throw new AwesomeException(CodiciErrore.ERRGEN,
					"File degli aggiornamenti di versione non trovato");
		}
    }
}
