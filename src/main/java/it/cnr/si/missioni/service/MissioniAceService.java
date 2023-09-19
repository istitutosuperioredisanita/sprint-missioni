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
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.letture.RuoloPersonaWebDto;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloPersonaDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;


public interface MissioniAceService {


    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediByTerm(String term, LocalDate data);



    public SimpleEntitaOrganizzativaWebDto getSede(int sede);

    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediDaUo(String uo, LocalDate data);


    public SimpleRuoloWebDto recuperoRuolo(String tipoRuolo) ;

    public RuoloPersonaWebDto associaRuoloPersona(RuoloPersonaDto ruoloPersona) ;

    @Cacheable(value = Costanti.NOME_CACHE_DATI_ACCOUNT)
    public UserInfoDto getAccountFromSiper(String currentLogin) ;

    @Cacheable(value = Costanti.NOME_CACHE_GRANT)
    public List<GrantedAuthority> getGrantedAuthorities(String principal) ;

    @Cacheable(value = Costanti.NOME_CACHE_DATI_DIRETTORE)
    public String getDirettore(String username) ;

    public SimplePersonaWebDto getPersona(String user) ;

    public List<SimpleUtenteWebDto> findUtentiIstituto(String cds, LocalDate data);

    public List<SimpleUtenteWebDto> findUtentiCdsuo(String uo, LocalDate data);

    public Integer getSedeResponsabileUtente(String user);

    public SimpleEntitaOrganizzativaWebDto getSede(Integer idEntitaOrganizzativa);

    public void importPersonaleEsterno() throws IOException;
}

