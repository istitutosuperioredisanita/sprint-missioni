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

import it.cnr.si.missioni.util.Costanti;


import it.cnr.si.missioni.util.DateUtils;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.letture.RuoloPersonaWebDto;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloPersonaDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import it.iss.si.service.AceService;
import it.iss.si.web.dto.EmployeeDetails;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@SpringBootApplication(scanBasePackages = {
        "it.iss.si.*"})
@Profile("iss")
@Service
public class MissioniAceServiceIss implements MissioniAceService{
    private static final Log logger = LogFactory.getLog(MissioniAceServiceIss.class);

    @Autowired
    private UnitaOrganizzativaService unitaOrganizzativaService;


    @Autowired(required = false)
    AceService aceService;
    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediByTerm(String term, LocalDate data){
        logger.info("MissioniAceServiceIss->recuperoSediByTerm");
        return Collections.emptyList();
    }

    public SimpleEntitaOrganizzativaWebDto getSede(int sede) {
        logger.info("MissioniAceServiceIss->SimpleEntitaOrganizzativaWebDto");
        return null;
    }

    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediDaUo(String uo, LocalDate data) {

        logger.info("MissioniAceServiceIss->recuperoSediDaUo");
        return Collections.emptyList();
    }


    public SimpleRuoloWebDto recuperoRuolo(String tipoRuolo) {
        logger.info("MissioniAceServiceIss->recuperoRuolo");
        return null;
    }

    public RuoloPersonaWebDto associaRuoloPersona(RuoloPersonaDto ruoloPersona) {
        logger.info("MissioniAceServiceIss->associaRuoloPersona");
        return null;
    }

    //@Cacheable(value = Costanti.NOME_CACHE_DATI_ACCOUNT)
    public UserInfoDto getAccountFromSiper(String currentLogin) {
        EmployeeDetails detail =aceService.getPersonaByUsername(currentLogin);
        logger.info("MissioniAceServiceIss->getAccountFromSiper");
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setCognome(detail.getCognome());
        userInfoDto.setNome(detail.getNome());
        userInfoDto.setCodice_fiscale(detail.getCodiceFiscale());
        userInfoDto.setCodice_uo(detail.getDestinazione().getSigla());
        userInfoDto.setStruttura_appartenenza("000");
        userInfoDto.setComune_nascita(detail.getLuogoNascita());
        userInfoDto.setData_nascita(DateUtils.getDateAsString(Date.from(detail.getDataNascita().toInstant(ZoneOffset.UTC)),DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
        return userInfoDto;
    }

    @Cacheable(value = Costanti.NOME_CACHE_GRANT)
    public List<GrantedAuthority> getGrantedAuthorities(String principal) {
        logger.info("MissioniAceServiceIss->getGrantedAuthorities");
        return null;
    }

    @Cacheable(value = Costanti.NOME_CACHE_DATI_DIRETTORE)
    public String getDirettore(String username) {
        logger.info("MissioniAceServiceIss->getDirettore");
        return null;
    }

    public SimplePersonaWebDto getPersona(String user) {
        logger.info("MissioniAceServiceIss->getPersona");
        return null;
    }

    public List<SimpleUtenteWebDto> findUtentiIstituto(String cds, LocalDate data) {
        logger.info("MissioniAceServiceIss->findUtentiIstituto");
        return Collections.emptyList();
    }

    public List<SimpleUtenteWebDto> findUtentiCdsuo(String uo, LocalDate data) {
        logger.info("MissioniAceServiceIss->findUtentiCdsuo");
        return Collections.emptyList();
    }

    public Integer getSedeResponsabileUtente(String user) {
        logger.info("MissioniAceServiceIss->getSedeResponsabileUtente");
        return Integer.valueOf(0);
    }

    public SimpleEntitaOrganizzativaWebDto getSede(Integer idEntitaOrganizzativa) {
        logger.info("MissioniAceServiceIss->findUtentiIstituto");
        return null;
    }


    public void importPersonaleEsterno() throws IOException {
        logger.info("MissioniAceServiceIss->importPersonaleEsterno");
        return ;
    }
}

