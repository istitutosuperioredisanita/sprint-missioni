package it.cnr.si.missioni.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import javax.annotation.PostConstruct;

import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.service.AceService;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloPersonaDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
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

	@Autowired
	private DatiIstitutoService datiIstitutoService;

	@Autowired
	private DatiSedeService datiSedeService;

	@Autowired
	private MissioniAceService missioniAceService;

	@Autowired
	private AccountService accountService;

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
	public void populateSignerMissioni() {
		List<DatiIstituto> list = datiIstitutoService.getDatiIstituti();
		SimpleRuoloWebDto ruoloMissioni = missioniAceService.recuperoRuolo(Costanti.RUOLO_FIRMA);
		SimpleRuoloWebDto ruoloMissioniEstere = missioniAceService.recuperoRuolo(Costanti.RUOLO_FIRMA_ESTERE);
		for (DatiIstituto datiIstituto : list){

			List<SimpleEntitaOrganizzativaWebDto> listaSedi = missioniAceService.recuperoSediDaUo(Utility.replace(datiIstituto.getIstituto(),".",""));

			for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativa : listaSedi){
				RuoloPersonaDto ruoloPersonaDto = preparePersonaDto(entitaOrganizzativa);
				RuoloPersonaDto ruoloPersonaDtoEstera = preparePersonaDto(entitaOrganizzativa);
				ruoloPersonaDto.setRuolo(ruoloMissioni.getId());
				ruoloPersonaDtoEstera.setRuolo(ruoloMissioniEstere.getId());

				DatiSede datiSede = datiSedeService.getDatiSede(entitaOrganizzativa.getIdnsip().toString(), LocalDate.now());
				if (datiSede != null){
					SimplePersonaWebDto persona = missioniAceService.getPersona(datiSede.getResponsabile());
					ruoloPersonaDto.setPersona(persona.getId());
					if (datiSede.isResponsabileEstero()){
						ruoloPersonaDtoEstera.setPersona(persona.getId());
					} else {
						String uid = accountService.getDirectorFromSede(datiSede.getSedeRespEstero());
						SimplePersonaWebDto personaEstera = missioniAceService.getPersona(uid);
						ruoloPersonaDtoEstera.setPersona(personaEstera.getId());
					}
				} else {
					if (datiIstituto.getResponsabile() != null){
						SimplePersonaWebDto persona = missioniAceService.getPersona(datiIstituto.getResponsabile());
						ruoloPersonaDto.setPersona(persona.getId());
						if (datiIstituto.isResponsabileEstero()){
							ruoloPersonaDtoEstera.setPersona(persona.getId());
						} else {
							String uid = accountService.getDirectorFromUo(datiIstituto.getUoRespEstero().replace(".",""));
							SimplePersonaWebDto personaEstera = missioniAceService.getPersona(uid);
							ruoloPersonaDtoEstera.setPersona(personaEstera.getId());
						}
					} else {
						String uid = accountService.getDirectorFromUo(datiIstituto.getIstituto().replace(".",""));
						SimplePersonaWebDto personaEstera = missioniAceService.getPersona(uid);
						ruoloPersonaDto.setPersona(personaEstera.getId());
						ruoloPersonaDtoEstera.setPersona(personaEstera.getId());
					}
				}

				missioniAceService.associaRuoloPersona(ruoloPersonaDto);
				missioniAceService.associaRuoloPersona(ruoloPersonaDtoEstera);
			}

		}
	}
	private RuoloPersonaDto preparePersonaDto(SimpleEntitaOrganizzativaWebDto sede){
		LocalDate now = LocalDate.now();
		LocalDate firstDayOfYear = now.with(TemporalAdjusters.firstDayOfYear());

		RuoloPersonaDto ruoloPersonaDto = new RuoloPersonaDto();
		ruoloPersonaDto.setAdmin(false);
		ruoloPersonaDto.setAttivo(true);
		ruoloPersonaDto.setInizioValidita(now);
		ruoloPersonaDto.setEntitaOrganizzativa(sede.getId());
		return ruoloPersonaDto;
	}
}
