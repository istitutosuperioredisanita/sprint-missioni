/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.service;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.DataUsersSpecial;
import it.cnr.si.missioni.util.data.DatiUo;
import it.cnr.si.missioni.util.data.Faq;
import it.cnr.si.missioni.util.data.UtentiPresidenteSpeciali;
import it.cnr.si.missioni.util.proxy.cache.json.Services;
import it.cnr.si.missioni.util.proxy.json.service.AccountService;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloPersonaDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigService {

    private static final Log logger = LogFactory.getLog(ConfigService.class);
    Services services = null;
    DatiUo datiUo = null;
    UtentiPresidenteSpeciali utentiPresidenteSpeciali = null;
    Faq faq = null;
    DataUsersSpecial dataUsersSpecial = null;
    String message = null;
    @Autowired
    private LoadFilesService loadFilesService;
    @Autowired
    private DatiIstitutoService datiIstitutoService;
    @Autowired
    private DatiSedeService datiSedeService;
    @Autowired(required = false)
    private MissioniAceService missioniAceService;
    @Autowired
    private AccountService accountService;

    @PostConstruct
    public void init() {
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
        if (fromInit) {
            services = loadFilesService.loadServicesForCache();
        }
    }

    private void evictData() {
        loadFilesService.evictUsersSpecialForUo();
        loadFilesService.evictDatiUo();
        loadFilesService.evictDatiUtentiPresidenteSpeciali();
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

    public void updateMessage(String newMessage) {
        message = newMessage;
    }

    @CacheEvict(value = Costanti.NOME_CACHE_MESSAGGIO, allEntries = true)
    public void evictMessage() {
    }

    @Cacheable(value = Costanti.NOME_CACHE_MESSAGGIO)
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
            return IOUtils.toString(this.getClass().getResourceAsStream("/releaseNotes/releaseNotes.md"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "File degli aggiornamenti di versione non trovato");
        }
    }

    public void aggiornaRapportoPersonaleEsterno() {
        try {
            missioniAceService.importPersonaleEsterno();
        } catch (IOException e) {
            throw new AwesomeException(CodiciErrore.ERRGEN,
                    "Aggiornamenti personale esterno");
        }
    }

    public void populateSignerMissioni() {
        List<DatiIstituto> list = datiIstitutoService.getDatiIstituti();
        SimpleRuoloWebDto ruoloMissioni = missioniAceService.recuperoRuolo(Costanti.RUOLO_FIRMA);
        SimpleRuoloWebDto ruoloMissioniEstere = missioniAceService.recuperoRuolo(Costanti.RUOLO_FIRMA_ESTERE);
        for (DatiIstituto datiIstituto : list) {
            try {

                List<SimpleEntitaOrganizzativaWebDto> listaSediEO = missioniAceService.recuperoSediDaUo(Utility.replace(datiIstituto.getIstituto(), ".", ""), LocalDate.now());

                List<SimpleEntitaOrganizzativaWebDto> listaSedi = listaSediEO.stream()
                        .filter(entitaOrganizzativaWebDto -> entitaOrganizzativaWebDto.getIdnsip() != null && entitaOrganizzativaWebDto.getTipo() != null && entitaOrganizzativaWebDto.getTipo().getSigla() != null &&
                                (entitaOrganizzativaWebDto.getTipo().getSigla().equals("UFF") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("SPRINC") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("AREA") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("DIP") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("UFFNODIR") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("SL") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("SSEC") ||
                                        entitaOrganizzativaWebDto.getTipo().getSigla().equals("un")))
                        .collect(Collectors.toList());

                for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativa : listaSedi) {
                    RuoloPersonaDto ruoloPersonaDto = preparePersonaDto(entitaOrganizzativa);
                    RuoloPersonaDto ruoloPersonaDtoEstera = preparePersonaDto(entitaOrganizzativa);
                    ruoloPersonaDto.setRuolo(ruoloMissioni.getId());
                    ruoloPersonaDtoEstera.setRuolo(ruoloMissioniEstere.getId());

                    DatiSede datiSede = datiSedeService.getDatiSede(entitaOrganizzativa.getIdnsip(), LocalDate.now());
                    if (datiSede != null) {
                        SimplePersonaWebDto persona = missioniAceService.getPersona(datiSede.getResponsabile());
                        ruoloPersonaDto.setPersona(persona.getId());
                        if (datiSede.isResponsabileEstero()) {
                            ruoloPersonaDtoEstera.setPersona(persona.getId());
                        } else {
                            String uid = accountService.getDirectorFromSede(datiSede.getSedeRespEstero());
                            SimplePersonaWebDto personaEstera = missioniAceService.getPersona(uid);
                            ruoloPersonaDtoEstera.setPersona(personaEstera.getId());
                        }
                    } else {
                        if (datiIstituto.getResponsabile() != null) {
                            SimplePersonaWebDto persona = missioniAceService.getPersona(datiIstituto.getResponsabile());
                            ruoloPersonaDto.setPersona(persona.getId());
                            if (datiIstituto.isResponsabileEstero()) {
                                ruoloPersonaDtoEstera.setPersona(persona.getId());
                            } else {
                                String uid = accountService.getDirectorFromUo(datiIstituto.getUoRespEstero().replace(".", ""));
                                SimplePersonaWebDto personaEstera = missioniAceService.getPersona(uid);
                                ruoloPersonaDtoEstera.setPersona(personaEstera.getId());
                            }
                        } else {
                            String uid = accountService.getDirectorFromUo(datiIstituto.getIstituto().replace(".", ""));
                            SimplePersonaWebDto personaEstera = missioniAceService.getPersona(uid);
                            ruoloPersonaDto.setPersona(personaEstera.getId());
                            ruoloPersonaDtoEstera.setPersona(personaEstera.getId());
                        }
                    }

                    missioniAceService.associaRuoloPersona(ruoloPersonaDto);
                    missioniAceService.associaRuoloPersona(ruoloPersonaDtoEstera);
                }

            } catch (Exception e) {
                logger.info(e.getMessage() + " for : " + datiIstituto.getIstituto());
            }
        }
    }

    private RuoloPersonaDto preparePersonaDto(SimpleEntitaOrganizzativaWebDto sede) {
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
