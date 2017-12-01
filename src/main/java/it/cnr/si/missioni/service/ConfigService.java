package it.cnr.si.missioni.service;

import it.cnr.si.missioni.util.data.DataUsersSpecial;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.proxy.cache.json.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ConfigService {

	@Autowired
    private LoadFilesService loadFilesService;
	
	Services services = null;
	DatiUo datiUo = null;
	DataUsersSpecial dataUsersSpecial = null;
	
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
		if (fromInit){
			services = loadFilesService.loadServicesForCache();
		}
	}

	private void evictData() {
		loadFilesService.evictUsersSpecialForUo();
		loadFilesService.evictDatiUo();
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

	
	public DataUsersSpecial getDataUsersSpecial() {
    	return dataUsersSpecial;
	}

	public DatiUo getDatiUo() {
		return datiUo;
	}

	public Services getServices() {
		return services;
	}
}
