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
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.proxy.json.object.TerzoInfo;
import it.cnr.si.missioni.util.proxy.json.object.UnitaOrganizzativa;
import it.cnr.si.missioni.util.proxy.json.service.TerzoService;
import it.cnr.si.missioni.util.proxy.json.service.UnitaOrganizzativaService;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import it.cnr.si.service.dto.anagrafica.letture.RuoloPersonaWebDto;
import it.cnr.si.service.dto.anagrafica.scritture.RuoloPersonaDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleEntitaOrganizzativaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimplePersonaWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleRuoloWebDto;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import it.iss.si.dto.anagrafica.Destinazione;
import it.iss.si.dto.anagrafica.EmployeeDetails;
import it.iss.si.dto.anagrafica.ResidenzaDomicilio;
import it.iss.si.dto.uo.UoDetails;
import it.iss.si.service.AceService;
import it.iss.si.service.UtilAce;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

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

    @Autowired(required = false)
    TerzoService terzoService;
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

    protected UnitaOrganizzativa findUnitaOrganizzativaBySigla(String siglaUo){
         return unitaOrganizzativaService.loadUoBySiglaEnteInt(siglaUo,DateUtils.getCurrentYear());
    }
    protected UnitaOrganizzativa findUnitaOrganizzativaByUo(String uoSigla){
        return unitaOrganizzativaService.loadUo(uoSigla,null,DateUtils.getCurrentYear());
    }
    protected  String getCodiceUo( UnitaOrganizzativa uo){
        return Optional.ofNullable(uo).map(unitaOrganizzativa -> uo.getCd_unita_organizzativa()).orElse("");
    }
    protected  String getCdUnitaPadre( UnitaOrganizzativa uo ){
        return Optional.ofNullable(uo).map(unitaOrganizzativa -> uo.getCd_unita_padre()).orElse("");
    }

    private Destinazione getUbicazione (EmployeeDetails userDetail){
        if ( Optional.ofNullable(userDetail.getDestinazione()).isPresent()){
            return userDetail.getDestinazione();
        }
        return new Destinazione();

    }


    private ResidenzaDomicilio getResidenza(Integer idAnagrafe){
        return aceService.getResidenza(idAnagrafe);
    }
    protected UserInfoDto getUserInfo(EmployeeDetails userDetail){
        if ( Optional.ofNullable(userDetail).isPresent()){

            UserInfoDto userInfoDto = new UserInfoDto();
            userInfoDto.setDipendente(Boolean.TRUE);
            userInfoDto.setUid(UtilAce.getEmail(userDetail));
            userInfoDto.setMatricola(userDetail.getMatricola());
            userInfoDto.setCognome(userDetail.getCognome());
            userInfoDto.setNome(userDetail.getNome());
            userInfoDto.setEmail_comunicazioni(UtilAce.getEmail(userDetail));
            userInfoDto.setSesso(userDetail.getSesso());
            userInfoDto.setSigla_sede(userDetail.getDestinazione().getSigla());
            userInfoDto.setCodice_fiscale(userDetail.getCodiceFiscale());
            UnitaOrganizzativa unitaOrganizzativa = findUnitaOrganizzativaBySigla(getUbicazione( userDetail).getSigla());
            userInfoDto.setCodice_uo(Utility.getUoSiper(getCodiceUo(unitaOrganizzativa )));
            userInfoDto.setStruttura_appartenenza(getCodiceUo(unitaOrganizzativa));
            userInfoDto.setComune_nascita(userDetail.getLuogoNascita());
            userInfoDto.setData_nascita(DateUtils.getDateAsString(Date.from(userDetail.getDataNascita().toInstant(ZoneOffset.UTC)),DateUtils.PATTERN_DATE_FOR_DOCUMENTALE));
            TerzoInfo terzoInfo = terzoService.loadUserInfo(userInfoDto.getCodice_fiscale());
            if ( Optional.ofNullable(terzoInfo).isPresent()) {
                userInfoDto.setComune_residenza(terzoInfo.getComune_residenza());
                userInfoDto.setIndirizzo_residenza(terzoInfo.getIndirizzo_residenza());
                userInfoDto.setCap_residenza( terzoInfo.getCap_residenza());
                userInfoDto.setProvincia_residenza(terzoInfo.getProvincia_residenza());
                userInfoDto.setComune_nascita(terzoInfo.getComune_nascita());

            }
            /*
            Sull'anagrafica ISS per molte persone non c'è il comune di residenza presente invece nei dati provenienti da NOIPA degli stipendi
            ResidenzaDomicilio residenzaDomicilio
                    = getResidenza(userDetail.getIdAnagrafe());
            if ( Optional.ofNullable(residenzaDomicilio).isPresent()) {
                userInfoDto.setComune_residenza(residenzaDomicilio.getComune());
                userInfoDto.setIndirizzo_residenza(residenzaDomicilio.getIndirizzo());
                userInfoDto.setCap_residenza( residenzaDomicilio.getCap());
                userInfoDto.setProvincia_residenza( residenzaDomicilio.getProvincia());
            }

             */



            return userInfoDto;
        }
        return null;
    }
    //@Cacheable(value = Costanti.NOME_CACHE_DATI_ACCOUNT)
    public UserInfoDto getAccountFromSiper(String currentLogin) {
        EmployeeDetails userDetail =aceService.getPersonaByUsername(currentLogin);
        logger.info("MissioniAceServiceIss->getAccountFromSiper");
        return getUserInfo(userDetail);
    }

    @Cacheable(value = Costanti.NOME_CACHE_GRANT)
    public List<GrantedAuthority> getGrantedAuthorities(String principal) {
        logger.info("MissioniAceServiceIss->getGrantedAuthorities");
        return null;
    }

    @Cacheable(value = Costanti.NOME_CACHE_DATI_DIRETTORE)
    public String getDirettore(String uo) {
        logger.info("MissioniAceServiceIss->getDirettore");
        UnitaOrganizzativa unitaOrganizzativa =findUnitaOrganizzativaByUo(uo);
        if ( Optional.ofNullable(unitaOrganizzativa).isPresent()) {
            EmployeeDetails employeeDetails = aceService.findResponsabileBySigla(unitaOrganizzativa.getSigla_int_ente());
            if (Optional.ofNullable(employeeDetails).isPresent())
                return UtilAce.getEmail( employeeDetails);
        }

        return null;
    }

    protected SimpleEntitaOrganizzativaWebDto getSimpleEntitaOrganizzativaWebDto( Integer idUo) {
        UoDetails uoDetails = aceService.getUo(idUo);
        if ( Optional.ofNullable(uoDetails).isPresent()){
            SimpleEntitaOrganizzativaWebDto simpleEntitaOrganizzativaWebDto = new SimpleEntitaOrganizzativaWebDto();

            simpleEntitaOrganizzativaWebDto.setId(uoDetails.getId());
            simpleEntitaOrganizzativaWebDto.setSigla( uoDetails.getSigla());
            simpleEntitaOrganizzativaWebDto.setDenominazione( uoDetails.getNome());
            Optional.ofNullable(findUnitaOrganizzativaBySigla( uoDetails.getSigla())).ifPresent(uorg->simpleEntitaOrganizzativaWebDto.setCdsuo(uorg.getCd_unita_organizzativa()));

            return simpleEntitaOrganizzativaWebDto;
        }
        return null;
    }
    protected SimplePersonaWebDto getSimplePersonaWebDto(EmployeeDetails userDetail){
        if ( Optional.ofNullable(userDetail).isPresent()){

            SimplePersonaWebDto simplePersonaWebDto = new SimplePersonaWebDto();

            simplePersonaWebDto.setId(userDetail.getIdAnagrafe());
            simplePersonaWebDto.setMatricola(userDetail.getMatricola());
            simplePersonaWebDto.setCognome(userDetail.getCognome());
            simplePersonaWebDto.setNome(userDetail.getNome());
            simplePersonaWebDto.setSede( getSimpleEntitaOrganizzativaWebDto(userDetail.getDestinazione().getIdUo()));
            simplePersonaWebDto.setLastSede(simplePersonaWebDto.getSede());
            simplePersonaWebDto.setCodiceFiscale(userDetail.getCodiceFiscale());

            return simplePersonaWebDto;
        }
        return null;
    }

    protected SimpleUtenteWebDto getSimpleUtenteWebDto(EmployeeDetails userDetail){
        if ( Optional.ofNullable(userDetail).isPresent()){
            SimpleUtenteWebDto simpleUtenteWebDto= new SimpleUtenteWebDto();
            simpleUtenteWebDto.setId(userDetail.getIdAnagrafe());
            simpleUtenteWebDto.setEmail(UtilAce.getEmail(userDetail));
            simpleUtenteWebDto.setUsername(simpleUtenteWebDto.getEmail());
            simpleUtenteWebDto.setPersona(getSimplePersonaWebDto(userDetail));


            return simpleUtenteWebDto;
        }
        return null;
    }
    public SimplePersonaWebDto getPersona(String user) {
        EmployeeDetails userDetail =aceService.getPersonaByUsername(user);
        return getSimplePersonaWebDto( userDetail);
    }

    public List<SimpleUtenteWebDto> findUtentiIstituto(String cds, LocalDate data) {
        logger.info("MissioniAceServiceIss->findUtentiIstituto");
        return Collections.emptyList();
    }

    private List<SimpleUtenteWebDto> getListSimpleUtenteWebDto(List<EmployeeDetails> employeeDetails ){
        if ( Optional.ofNullable(employeeDetails).isPresent()
                && Optional.ofNullable(employeeDetails).get().size()>0){
            List<SimpleUtenteWebDto> s = new ArrayList<SimpleUtenteWebDto>();
            for ( EmployeeDetails employeeDetail:employeeDetails) {
                if ( Optional.ofNullable(UtilAce.getEmail(employeeDetail)).isPresent())
                    s.add( getSimpleUtenteWebDto(employeeDetail));
            }
            return s;
        }
        return Collections.emptyList();
    }
    public List<SimpleUtenteWebDto> findUtentiCdsuo(String uo, LocalDate data) {
        logger.info("MissioniAceServiceIss->findUtentiCdsuo");

        String uoSigla=Utility.getUoSigla(uo);
        UnitaOrganizzativa unitaOrganizzativa =findUnitaOrganizzativaByUo(uoSigla);
        if ( !Optional.ofNullable(unitaOrganizzativa).isPresent())
            return Collections.emptyList();
        return getListSimpleUtenteWebDto(aceService.getPersoneDetailUo( unitaOrganizzativa.getSigla_int_ente(), true ));
    }

    public Integer getSedeResponsabileUtente(String user) {
        //sede responsabile Utente
        logger.info("Da implmentare: MissioniAceServiceIss->getSedeResponsabileUtente");
        return Integer.valueOf(0);
    }

    public SimpleEntitaOrganizzativaWebDto getSede(Integer idEntitaOrganizzativa) {
        logger.info("Da implmentare:MissioniAceServiceIss->findUtentiIstituto");
        return null;
    }


    public void importPersonaleEsterno() throws IOException {
        logger.info("MissioniAceServiceIss->importPersonaleEsterno");
        return ;
    }
}

