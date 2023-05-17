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

import feign.FeignException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.service.AceService;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.enums.TipoAppartenenza;
import it.cnr.si.service.dto.anagrafica.enums.TipoContratto;
import it.cnr.si.service.dto.anagrafica.letture.PersonaEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.letture.PersonaWebDto;
import it.cnr.si.service.dto.anagrafica.letture.RuoloPersonaWebDto;
import it.cnr.si.service.dto.anagrafica.letture.UtenteWebDto;
import it.cnr.si.service.dto.anagrafica.scritture.*;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Profile("!showcase")
@Service
public class MissioniAceService {
    private static final Log logger = LogFactory.getLog(MissioniAceService.class);
    private static final Map<String, TipoContratto> TIPOCONTRATTO = new HashMap<String, TipoContratto>() {
        {
            put("BOR", TipoContratto.BORSISTA);
            put("ASS", TipoContratto.ASSEGNISTA);
            put("COLL", TipoContratto.COLLABORATORE_PROFESSIONALE);
            put("PROF", TipoContratto.COLLABORATORE_PROFESSIONALE);
            put("OCCA", TipoContratto.COLLABORATORE_PROFESSIONALE);
        }
    };
    @Autowired(required = false)
    AceService aceService;

    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediByTerm(String term, LocalDate data) {
        List<SimpleEntitaOrganizzativaWebDto> list = aceService.entitaOrganizzativaFind(null, term, data, null);
        return list;
    }

    private List<SimpleEntitaOrganizzativaWebDto> recuperoSediStoricheByTerm(String term, LocalDate data) {
        for (int i = 1; i < 5; i++) {
            LocalDate dataSottratta = data.minus(i, ChronoUnit.YEARS);
            List<SimpleEntitaOrganizzativaWebDto> list = aceService.entitaOrganizzativaFind(null, term, dataSottratta, null);
            List<SimpleEntitaOrganizzativaWebDto> listaEOAllaData = getSimpleEntitaOrganizzativaWebDtoValid(term, list);
            if (!listaEOAllaData.isEmpty()) {
                return list;
            }
        }
        return new ArrayList<>();
    }

    public SimpleEntitaOrganizzativaWebDto getSede(int sede) {
        return aceService.entitaOrganizzativaById(sede);
    }

    public List<SimpleEntitaOrganizzativaWebDto> recuperoSediDaUo(String uo, LocalDate data) {
        List<SimpleEntitaOrganizzativaWebDto> lista = recuperoSediByTerm(uo, LocalDate.now());
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = getSimpleEntitaOrganizzativaWebDtoValid(uo, lista);
        if (listaEntitaUo.isEmpty()) {
            List<SimpleEntitaOrganizzativaWebDto> listaAllaData = recuperoSediByTerm(uo, data);
            List<SimpleEntitaOrganizzativaWebDto> listaEOAllaData = getSimpleEntitaOrganizzativaWebDtoValid(uo, listaAllaData);
            if (listaEOAllaData.isEmpty()) {
                return recuperoSediStoricheByTerm(uo, data);
            }
            return listaEOAllaData;
        }
        return listaEntitaUo;
    }

    private List<SimpleEntitaOrganizzativaWebDto> getSimpleEntitaOrganizzativaWebDtoValid(String uo, List<SimpleEntitaOrganizzativaWebDto> lista) {
        List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo = Optional.ofNullable(lista.stream()
                .filter(entita -> {
                    return uo.equals(entita.getCdsuo()) && entita.getIdnsip() != null && !"IST".equals(entita.getTipo().getSigla());
                }).collect(Collectors.toList())).orElse(new ArrayList<SimpleEntitaOrganizzativaWebDto>());
        return listaEntitaUo;
    }

    private SimpleEntitaOrganizzativaWebDto recuperoSedePrincipale(List<SimpleEntitaOrganizzativaWebDto> listaEntitaUo) {
        for (SimpleEntitaOrganizzativaWebDto entitaOrganizzativaWebDto : listaEntitaUo) {
            String tipoEntitaOrganizzativa = entitaOrganizzativaWebDto.getTipo().getSigla();
            if (tipoEntitaOrganizzativa.equals("UFF") ||
                    tipoEntitaOrganizzativa.equals("SPRINC") ||
                    tipoEntitaOrganizzativa.equals("AREA") ||
                    tipoEntitaOrganizzativa.equals("DIP") ||
                    tipoEntitaOrganizzativa.equals("un")) {
                return entitaOrganizzativaWebDto;
            }
        }
        return null;
    }

    public SimpleRuoloWebDto recuperoRuolo(String tipoRuolo) {
        return aceService.getRuoloBySigla(tipoRuolo);
    }

    public RuoloPersonaWebDto associaRuoloPersona(RuoloPersonaDto ruoloPersona) {
        try {
            RuoloPersonaWebDto ruolo = aceService.associaRuoloPersona(ruoloPersona);
            return ruolo;
        } catch (FeignException e) {
            logger.info(e.getMessage() + " for Ruolo: idPersona: " + ruoloPersona.getPersona() + " idRuolo: " + ruoloPersona.getRuolo() + " Id Entita Organizzativa " + ruoloPersona.getEntitaOrganizzativa());
        }
        return null;
    }

    @Cacheable(value = Costanti.NOME_CACHE_DATI_ACCOUNT)
    public UserInfoDto getAccountFromSiper(String currentLogin) {
        logger.info("getAccountFromSiper: " + currentLogin);
        UserInfoDto userInfoDto = aceService.getUserInfoDto(currentLogin, false, true);
        return userInfoDto;
    }

    @Cacheable(value = Costanti.NOME_CACHE_GRANT)
    public List<GrantedAuthority> getGrantedAuthorities(String principal) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        try {
            authorities = aceService.ruoliAttivi(principal).stream()
                    .filter(ruolo -> ruolo.getContesto().getSigla().equals("missioni-app"))
                    .map(a -> new SimpleGrantedAuthority(a.getSigla()))
                    .collect(Collectors.toList());
        } catch (FeignException e) {
            logger.info(e.getMessage() + " for user: " + "\"" + principal + "\"");
        }
        return authorities;
    }

    @Cacheable(value = Costanti.NOME_CACHE_DATI_DIRETTORE)
    public String getDirettore(String username) {
        BossDto direttore = aceService.findResponsabileStruttura(username);
        if (direttore != null) {
            return direttore.getUtente().getUsername();
        }
        return "";
    }

    public SimplePersonaWebDto getPersona(String user) {
        return aceService.getPersonaByUsername(user);
    }

    public List<SimpleUtenteWebDto> findUtentiIstituto(String cds, LocalDate data) {
        logger.info("findUtentiIstituto: " + cds);
        return aceService.findUtentiIstituto(cds, data, TipoAppartenenza.SEDE);
    }

    public List<SimpleUtenteWebDto> findUtentiCdsuo(String uo, LocalDate data) {
        logger.info("findUtentiCdsuo: " + uo);
        List<SimpleUtenteWebDto> lista = aceService.findUtentiCdsuo(uo, data, TipoAppartenenza.SEDE);
        data = data.minusDays(365);
        List<SimpleUtenteWebDto> listaAnnoPrecedente = aceService.findUtentiCdsuo(uo, data, TipoAppartenenza.SEDE);
        for (SimpleUtenteWebDto utenteAnnoPrecedente : listaAnnoPrecedente) {
            boolean utenteDuplicato = false;
            for (SimpleUtenteWebDto utente : lista) {
                if (utenteAnnoPrecedente.getUsername().equals(utente.getUsername())) {
                    utenteDuplicato = true;
                }
            }
            if (!utenteDuplicato) {
                lista.add(utenteAnnoPrecedente);
            }
        }
        List<SimpleUtenteWebDto> listaCessati = aceService.findUtentiCessatiCdsuo(uo, null, TipoAppartenenza.SEDE);
        for (SimpleUtenteWebDto utenteCessato : listaCessati) {
            boolean utenteDuplicato = false;
            for (SimpleUtenteWebDto utente : lista) {
                if (utenteCessato.getUsername().equals(utente.getUsername())) {
                    utenteDuplicato = true;
                }
            }
            if (!utenteDuplicato) {
                lista.add(utenteCessato);
            }
        }

        return lista;
    }

    public Integer getSedeResponsabileUtente(String user) {
        BossDto boss = aceService.findResponsabileUtente(user);
        if (boss != null && boss.getEntitaOrganizzativa() != null) {
            return boss.getEntitaOrganizzativa().getId();
        } else {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Sede responsabile utente non trovata per " + user);
        }
    }

    public SimpleEntitaOrganizzativaWebDto getSede(Integer idEntitaOrganizzativa) {
        return aceService.entitaOrganizzativaById(idEntitaOrganizzativa);
    }


    public void importPersonaleEsterno() throws IOException {
        String utente = "MIGESTERNI";
        try (
                Reader reader = Files.newBufferedReader(Paths.get("src", "test", "resources", "esterni.csv"));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                        .withHeader("NOME", "COGNOME", "CODICE_FISCALE", "USERNAME", "UO", "IDNSIP", "TI_SESSO", "DT_NASCITA",
                                "CD_TIPO_RAPPORTO", "DT_INI_VALIDITA", "DT_FIN_VALIDITA", "EMAIL")
                        .withFirstRecordAsHeader()
                        .withIgnoreHeaderCase()
                        .withTrim())
        ) {
            for (CSVRecord csvRecord : csvParser) {
                String nome = csvRecord.get("NOME");
                String cognome = csvRecord.get("COGNOME");
                String codiceFiscale = csvRecord.get("CODICE_FISCALE");
                String username = csvRecord.get("USERNAME");
                String uo = csvRecord.get("UO");
                String idnsip = csvRecord.get("IDNSIP");
                String tiSesso = csvRecord.get("TI_SESSO");
                String dtNascita = csvRecord.get("DT_NASCITA");
                String cdTipoRapporto = csvRecord.get("CD_TIPO_RAPPORTO");
                String dtIniValidita = csvRecord.get("DT_INI_VALIDITA");
                String dtFinValidita = csvRecord.get("DT_FIN_VALIDITA");
                String email = csvRecord.get("EMAIL");
                String sedeIdByIdNsip = null;
                logger.info("Elaboro riga: " + csvRecord.getRecordNumber());
                try {
                    sedeIdByIdNsip =
                            Optional.ofNullable(aceService.getSedeIdByIdNsip(idnsip))
                                    .orElseGet(() -> aceService.getSedeIdByCdsUo(uo));
                } catch (FeignException _ex) {
                }
                if (!Optional.ofNullable(sedeIdByIdNsip).isPresent()) {
                    continue;
                }
                Optional<String> personaId = Optional.empty();
                try {
                    personaId = Optional.ofNullable(aceService.getPersonaId(codiceFiscale));
                } catch (FeignException _ex) {
                }
                if (!personaId.isPresent()) {
                    PersonaDto personaDto = new PersonaDto();
                    personaDto.setNome(nome);
                    personaDto.setCognome(cognome);
                    personaDto.setSesso(tiSesso);
                    personaDto.setDataNascita(LocalDate.parse(dtNascita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    personaDto.setCodiceFiscale(codiceFiscale);
                    personaDto.setUtenteUva(utente);
                    personaDto.setTipoContratto(
                            Optional.ofNullable(cdTipoRapporto)
                                    .filter(s -> TIPOCONTRATTO.containsKey(cdTipoRapporto))
                                    .map(s -> TIPOCONTRATTO.get(cdTipoRapporto))
                                    .orElse(null)
                    );
                    logger.info(personaDto.toString());
                    final PersonaWebDto personaWebDto = aceService.savePersona(personaDto);


//                    Assert.assertNotNull(personaWebDto);

                    UtenteDto utenteDto = new UtenteDto();
                    utenteDto.setPersona(personaWebDto.getId());
                    utenteDto.setUsername(username);
                    utenteDto.setEmail(email);
                    utenteDto.setUtenteUva(utente);
                    logger.info(utenteDto.toString());

                    final UtenteWebDto utenteWebDto = aceService.createUtente(utenteDto);

//                    Assert.assertNotNull(utenteWebDto);

                    PersonaEntitaOrganizzativaDto personaEntitaOrganizzativaDto = new PersonaEntitaOrganizzativaDto();
                    personaEntitaOrganizzativaDto.setPersona(personaWebDto.getId());
                    personaEntitaOrganizzativaDto.setTipoAppartenenza(TipoAppartenenza.AFFERENZA_UO);
                    personaEntitaOrganizzativaDto.setEntitaOrganizzativa(Integer.valueOf(sedeIdByIdNsip));
                    personaEntitaOrganizzativaDto.setInizioValidita(
                            LocalDate.parse(dtIniValidita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    );
                    personaEntitaOrganizzativaDto.setFineValidita(
                            LocalDate.parse(dtFinValidita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    );
                    logger.info(personaEntitaOrganizzativaDto.toString());
                    final PersonaEntitaOrganizzativaWebDto personaEntitaOrganizzativaWebDto =
                            aceService.savePersonaEntitaOrganizzativa(personaEntitaOrganizzativaDto);

//                    Assert.assertNotNull(personaEntitaOrganizzativaWebDto);

                } else {
                    try {
                        Map<String, Object> params = new HashMap<>();
                        params.put("persona", personaId.get());
                        params.put("tipoAppartenenza", TipoAppartenenza.AFFERENZA_UO);
                        List<PersonaEntitaOrganizzativaWebDto> personeEO;
                        personeEO = aceService.personaEntitaOrganizzativaFind(params)
                                .stream().sorted(Comparator.comparing(PersonaEntitaOrganizzativaWebDto::getInizioValidita)).collect(Collectors.toList());
                        if (personeEO == null || personeEO.isEmpty()) {
                            PersonaEntitaOrganizzativaDto personaEntitaOrganizzativaDto = new PersonaEntitaOrganizzativaDto();
                            personaEntitaOrganizzativaDto.setPersona(Integer.valueOf(personaId.get()));
                            personaEntitaOrganizzativaDto.setTipoAppartenenza(TipoAppartenenza.AFFERENZA_UO);
                            personaEntitaOrganizzativaDto.setEntitaOrganizzativa(Integer.valueOf(sedeIdByIdNsip));
                            personaEntitaOrganizzativaDto.setInizioValidita(
                                    LocalDate.parse(dtIniValidita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            );
                            personaEntitaOrganizzativaDto.setFineValidita(
                                    LocalDate.parse(dtFinValidita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            );
                            logger.info(personaEntitaOrganizzativaDto.toString());
                            final PersonaEntitaOrganizzativaWebDto personaEntitaOrganizzativaWebDto =
                                    aceService.savePersonaEntitaOrganizzativa(personaEntitaOrganizzativaDto);
                        } else {
                            boolean trovataPersonaSenzaDataFine = false;
                            for (PersonaEntitaOrganizzativaWebDto personaEO : personeEO) {
                                if (personaEO.getFineValidita() == null) {
                                    aggiornaPersonaEO(utente, dtIniValidita, dtFinValidita, personaEO);
                                    PersonaEntitaOrganizzativaWebDto personaEOWebDto = personaEO;
                                    aggiornaPersonaEO(utente, dtIniValidita, dtFinValidita, personaEOWebDto);
                                }
                            }

                        }
                    } catch (FeignException _ex) {
                    }
                }
            }
        }
    }

    private void aggiornaPersonaEO(String utente, String dtIniValidita, String dtFinValidita, PersonaEntitaOrganizzativaWebDto personaEOWebDto) {
        PersonaEntitaOrganizzativaDto personaEntitaOrganizzativaDto = new PersonaEntitaOrganizzativaDto();
        personaEntitaOrganizzativaDto.setId(personaEOWebDto.getId());
        personaEntitaOrganizzativaDto.setNote(personaEOWebDto.getNote());
        personaEntitaOrganizzativaDto.setPermissions(personaEOWebDto.getPermissions());
        personaEntitaOrganizzativaDto.setUtenteUva(utente);
        personaEntitaOrganizzativaDto.setProvvedimento(personaEOWebDto.getProvvedimento());
        personaEntitaOrganizzativaDto.setPersona(personaEOWebDto.getPersona().getId());
        personaEntitaOrganizzativaDto.setTipoAppartenenza(personaEOWebDto.getTipoAppartenenza());
        personaEntitaOrganizzativaDto.setEntitaOrganizzativa(personaEOWebDto.getEntitaOrganizzativa().getId());
        personaEntitaOrganizzativaDto.setInizioValidita(
                LocalDate.parse(dtIniValidita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        personaEntitaOrganizzativaDto.setFineValidita(
                LocalDate.parse(dtFinValidita, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        logger.info(personaEntitaOrganizzativaDto.toString());

        aceService.updatePersonaEntitaOrganizzativa(personaEntitaOrganizzativaDto);
    }
}

