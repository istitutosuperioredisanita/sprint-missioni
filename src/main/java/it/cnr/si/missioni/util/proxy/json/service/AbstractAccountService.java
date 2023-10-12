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

package it.cnr.si.missioni.util.proxy.json.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import it.cnr.si.domain.CNRUser;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.MissioniCMISService;
import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.service.*;
import it.cnr.si.missioni.service.showcase.ACEService;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.missioni.util.data.Uo;
import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.cache.CallCache;
import it.cnr.si.missioni.util.proxy.json.object.Account;
import it.cnr.si.missioni.util.proxy.json.object.DatiDirettore;
import it.cnr.si.missioni.util.proxy.json.object.DatiGruppoSAC;
import it.cnr.si.missioni.util.proxy.json.object.TerzoInfo;
import it.cnr.si.model.UserInfoDto;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = {
        "it.cnr.si.service"})

public abstract class AbstractAccountService implements AccountService{
    private static final Log logger = LogFactory.getLog(AbstractAccountService.class);
    @Autowired(required = false)
    MissioniAceService missioniAceService;
    @Autowired
    private ProxyService proxyService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private DatiSedeService datiSedeService;
    @Autowired
    private MissioniCMISService missioniCMISService;
    @Autowired
    private DatiIstitutoService datiIstitutoService;
    @Autowired
    private UoService uoService;
    @Autowired(required = false)
    private ACEService aceServiceShowcase;
    @Autowired(required = false)
    private TerzoService terzoService;

    @Autowired
    private SecurityService securityService;

    public UsersSpecial getUoForUsersSpecial(String uid) {
        if (configService.getDataUsersSpecial() != null && configService.getDataUsersSpecial().getUsersSpecials() != null) {
            for (Iterator<UsersSpecial> iteratorUsers = configService.getDataUsersSpecial().getUsersSpecials().iterator(); iteratorUsers.hasNext(); ) {
                UsersSpecial user = iteratorUsers.next();
                if (user.getUid() != null && user.getUid().equalsIgnoreCase(uid)) {
                    return user;
                }
            }
        }
        return null;
    }

    public List<UsersSpecial> getUserSpecialForUo(String uo, Boolean isPerValidazione) {
        List<UsersSpecial> listaUtenti = new ArrayList<UsersSpecial>();
        logger.info("Ricerca amministrativi per mail della uo: " + uo);
        if (configService.getDataUsersSpecial() != null && configService.getDataUsersSpecial().getUsersSpecials() != null) {
            for (Iterator<UsersSpecial> iteratorUsers = configService.getDataUsersSpecial().getUsersSpecials().iterator(); iteratorUsers.hasNext(); ) {
                UsersSpecial user = iteratorUsers.next();
                logger.debug("Ricerca amministrativi per mail. Utente: " + user.getUid());
                if (Utility.nvl(user.getAll(), "N").equals("N") && isUtenteAbilitatoUo(user.getUoForUsersSpecials(), uo, isPerValidazione)) {
                    logger.info("User special to be able: " + user.getUid());
                    listaUtenti.add(user);
                }
            }
        }
        return listaUtenti;
    }

    public Boolean isUserSpecialEnableToValidateOrder(String user, String uo) {
        if (uo == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "UO non indicata.");
        }
        Uo datiUo = uoService.recuperoUoSigla(uo);
        if (datiUo != null && Utility.nvl(datiUo.getOrdineDaValidare(), "N").equals("N")) {
            return true;
        }
        UsersSpecial userSpecial = getUoForUsersSpecial(user);
        if (userSpecial != null) {
            if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")) {
                if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
                    for (UoForUsersSpecial uoForUsersSpecial : userSpecial.getUoForUsersSpecials()) {
                        if (uo.equals(getUoSigla(uoForUsersSpecial)) && Utility.nvl(uoForUsersSpecial.getOrdine_da_validare()).equals("S")) {
                            return true;
                        }
                    }
                }
            } else return userSpecial.getAll().equals("S");
        }
        return false;
    }

    public List<UsersSpecial> getUserSpecialForUoPerValidazione(String uo) {
        return getUserSpecialForUo(uo, true);
    }

    public Boolean isUtenteAbilitatoUo(List<UoForUsersSpecial> listUo, String uo, Boolean isPerValidazione) {
        for (Iterator<UoForUsersSpecial> iteratorUo = listUo.iterator(); iteratorUo.hasNext(); ) {
            UoForUsersSpecial uoForUsersSpecial = iteratorUo.next();
            if (uoForUsersSpecial.getCodice_uo() != null && getUoSigla(uoForUsersSpecial).equals(uo)) {
                if (isPerValidazione) {
                    return Utility.nvl(uoForUsersSpecial.getOrdine_da_validare()).equals("S");
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private String manageResponseForAccountRest(Account account, Boolean loadSpecialUserData) {
        if (account != null) {
            return getResponseAccount(account, loadSpecialUserData);
        }
        return null;
    }

    public String manageResponseForAccountRest(String body) {
        Account account = getAccountFromResp(body);
        if (account != null) {
            return getResponseAccount(account, true);
        }
        return null;
    }

    private UsersSpecial loadUserSpecial(String uid) {
        return getUoForUsersSpecial(uid);
    }

    private String getResponseAccountFromUid(String uid, Account account, Boolean loadSpecialUserData) {
        UsersSpecial user = null;
        if (loadSpecialUserData) {
            user = loadUserSpecial(uid);
        }
        return createResponseForAccountRest(account, user);
    }

     private String getResponseAccount(Account account, Boolean loadSpecialUserData) {
        UsersSpecial user = null;
        if (loadSpecialUserData) {
            user = loadUserSpecial(account.getUid());
        }
        List<String> ruoli = account.getInternalRoles();
        if (ruoli.size() == 0)
            ruoli = Collections.singletonList(AuthoritiesConstants.USER);
        if (ruoli.stream().filter(role -> role.equals(Costanti.AMMINISTRATORE_MISSIONI)).count() > 0) {
            ruoli.add(AuthoritiesConstants.ADMIN);
        }
        account.setInternalRoles(ruoli);
        return createResponseForAccountRest(account, user);
    }

    public String createResponseForAccountRest(Account account, UsersSpecial user) {
        if (user != null) {
            account.setAllUoForUsersSpecial(user.getAll());
            account.setUoForUsersSpecial(user.getUoForUsersSpecials());
        }
        return getBodyAccount(account);
    }

    public Account loadAccountFromUsername(String currentLogin) {
        String risposta = getAccountFromUsername(currentLogin, false);
        if (risposta != null) {
            return getAccountFromResp(risposta);
        }
        return null;
    }

    abstract public Account loadAccount(Boolean loadSpecialUserData);

    private Account getAccountFromUserKeycloak(Boolean loadSpecialUserData, UserInfoDto userInfoDto) {
        Account account = new Account(userInfoDto);
        if (loadSpecialUserData) {
            Optional<CNRUser> user = securityService.getUser();
            user.ifPresent(utente -> {
                final List<String> roles = utente.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toList());
                roles.add(AuthoritiesConstants.USER);
                account.setInternalRoles(roles);
            });

        }
        return account;
    }

    abstract public String getAccount(Boolean loadSpecialUserData) ;

    public String getAccountFromUsername(String username, Boolean loadSpecialUserData) {
        it.cnr.si.service.dto.anagrafica.UserInfoDto userInfoDto = null;
        if (missioniAceService != null) {
            userInfoDto = missioniAceService.getAccountFromSiper(username);
            if (!Optional.ofNullable(userInfoDto.getDipendente()).orElse(Boolean.TRUE)) {
                final TerzoInfo userInfoDtoSIGLA = terzoService.loadUserInfo(userInfoDto.getCodice_fiscale());
                if (userInfoDtoSIGLA != null) {
                    userInfoDto.setSesso(userInfoDtoSIGLA.getSesso());
                    userInfoDto.setComune_nascita(userInfoDtoSIGLA.getComune_nascita());
                    userInfoDto.setProvincia_nascita(userInfoDtoSIGLA.getProvincia_nascita());
                    userInfoDto.setFl_cittadino_italiano(userInfoDtoSIGLA.getFl_cittadino_italiano());
                    userInfoDto.setIndirizzo_residenza(userInfoDtoSIGLA.getIndirizzo_residenza());
                    userInfoDto.setCap_residenza(userInfoDtoSIGLA.getCap_residenza());
                    userInfoDto.setComune_residenza(userInfoDtoSIGLA.getComune_residenza());
                    userInfoDto.setProvincia_residenza(userInfoDtoSIGLA.getProvincia_residenza());
                }
            }
        } else if (aceServiceShowcase != null) {
            userInfoDto = aceServiceShowcase.getUtenteAdmin(username);
        }
        if (userInfoDto != null) {
            Account account = new Account(userInfoDto);
            String resp = manageResponseForAccountRest(username, account, loadSpecialUserData);
            if (resp != null) {
                return resp;
            }
            return "";
        }
        return null;
    }

    private String manageResponseForAccountRest(String uid, Account account, Boolean loadSpecialUserData) {
        if (account != null) {
            return getResponseAccountFromUid(uid, account, loadSpecialUserData);
        }
        return null;
    }

    abstract protected Account getAccountWithoutRole() ;

    public String getResponseAccountWithoutRole() {
        return getResponseAccountWithoutRole(false);
    }

    public String getResponseAccountWithoutRole(Boolean loadDataFromUserSpecial) {
        Account account = getAccountWithoutRole();
        if (account != null) {
            return getResponseAccount(account, loadDataFromUserSpecial);
        }
        return null;
    }

    // TODO Da eliminare-

    public String getDirectorFromSede(String codiceSede) {
        CallCache callCache = new CallCache(HttpMethod.GET, null, Costanti.APP_SIPER, Costanti.REST_UO_DIRECTOR, Costanti.REST_UO_SEDE + codiceSede + "&userinfo=true&ruolo=dir", null, null);
        ResultProxy result = proxyService.processInCache(callCache);
        String risposta = result.getBody();
        try {
            ObjectMapper mapper = new ObjectMapper();
            DatiDirettore[] lista = mapper.readValue(risposta, DatiDirettore[].class);
            if (lista != null && lista.length > 0) {
                return lista[0].getUid();
            } else if (lista == null || lista.length == 0) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stato possibile recuperare il responsabile per la sede:" + codiceSede);
            }
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i responsabili della sede (" + Utility.getMessageException(ex) + ").");
        }
        return risposta;
    }

    public String getDirectorFromUo(String uo) {
        CallCache callCache = new CallCache(HttpMethod.GET, null, Costanti.APP_SIPER, Costanti.REST_UO_DIRECTOR, Costanti.REST_UO_TIT_CA + uo + "&userinfo=true&ruolo=dir", null, null);
        ResultProxy result = proxyService.processInCache(callCache);
        String risposta = result.getBody();
        try {
            ObjectMapper mapper = new ObjectMapper();
            DatiDirettore[] lista = mapper.readValue(risposta, DatiDirettore[].class);
            if (lista != null && lista.length > 0) {
                return lista[0].getUid();
            } else if (lista == null || lista.length == 0) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Non è stato possibile recuperare il responsabile per la uo:" + uo);
            }
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella lettura del file JSON per i responsabili della uo (" + Utility.getMessageException(ex) + ").");
        }
        return risposta;
    }

    // TODO Fine da eliminare-

    public Boolean isUserSpecialEnableToFinalizeOrder(String user, String uo) {
        if (uo == null) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "UO non indicata.");
        }
        UsersSpecial userSpecial = getUoForUsersSpecial(user);
        if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")) {
            if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
                for (UoForUsersSpecial uoForUsersSpecial : userSpecial.getUoForUsersSpecials()) {
                    if (uo.equals(getUoSigla(uoForUsersSpecial)) && Utility.nvl(uoForUsersSpecial.getRendi_definitivo()).equals("S")) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }

    private String getUoSigla(UoForUsersSpecial uo) {
        return uo.getCodice_uo().substring(0, 3) + "." + uo.getCodice_uo().substring(3, 6);
    }

    private Account getAccountFromResp(String risposta) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new ParanamerModule());
            Account account = mapper.readValue(risposta, Account.class);
            return account;
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella creazione dell'oggetto JSON dei dati dell'Account dalla REST (" + Utility.getMessageException(ex) + ").");
        }
    }

    public String getBodyAccount(Account account) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String risposta = mapper.writeValueAsString(account);
            return risposta;
        } catch (Exception ex) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella generazione del body della response dall'oggetto JSON dei dati dell'Account (" + Utility.getMessageException(ex) + ").");
        }
    }

    public String getEmail(String user) {
        Account utente = loadAccountFromUsername(user);
        if (utente != null) {
            return utente.getEmail_comunicazioni();
        }
        return null;
    }

    // TODO Da eliminare-
    public String recuperoDirettore(Integer anno, String uo, Boolean isMissioneEstera, Account account, ZonedDateTime data, Boolean isUoRich) {
        return recuperoDirettore(anno, uo, isMissioneEstera, account, data, isUoRich, false);
    }

    public String recuperoDirettore(Integer anno, String uo, Boolean isMissioneEstera, Account account, ZonedDateTime data, Boolean isUoRich, Boolean fromDatiSAC) {
        String userNameFirmatario;
        DatiSede dati = null;
        Boolean delegaSpesa = false;
        if (account.getCodice_sede() != null && account.getCodice_uo() != null && Utility.getUoSigla(account.getCodice_uo()).equals(Utility.getUoSigla(uo))) {
            dati = datiSedeService.getDatiSede(account.getCodice_sede(), data);
            if (dati != null && dati.getResponsabile() != null && Utility.nvl(dati.getDelegaSpesa()).equals("S")) {
                delegaSpesa = true;
            }
        }
        if ((!isUoRich && !delegaSpesa) || (account.getMatricola() == null || (account.getData_cessazione() != null && ZonedDateTime.parse(account.getData_cessazione()).compareTo(data) < 0))) {
            userNameFirmatario = recuperoDirettoreDaUo(anno, uo, isMissioneEstera);
        } else {
            if (dati != null && dati.getResponsabile() != null) {
                if (!isMissioneEstera || (Utility.nvl(dati.getResponsabileSoloItalia(), "N").equals("N"))) {
                    userNameFirmatario = dati.getResponsabile();
                } else {
                    if (StringUtils.isEmpty(dati.getSedeRespEstero())) {
                        userNameFirmatario = getDirectorFromSede(dati.getCodice_sede());
                    } else {
                        DatiSede datiAltraSede = datiSedeService.getDatiSede(dati.getSedeRespEstero(), data);
                        if (datiAltraSede != null && datiAltraSede.getResponsabile() != null) {
                            userNameFirmatario = datiAltraSede.getResponsabile();
                        } else {
                            userNameFirmatario = getDirectorFromSede(dati.getSedeRespEstero());
                        }
                    }
                }
            } else {
                userNameFirmatario = recuperoDirettoreDaUo(anno, uo, isMissioneEstera);
            }
            if (StringUtils.isEmpty(userNameFirmatario)) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non è stato possibile recuperare il direttore per la sede " + dati.getCodice_sede());
            }
        }
        if (userNameFirmatario != null && userNameFirmatario.equalsIgnoreCase(account.getUid())) {
            if (uo.startsWith(Costanti.CDS_SAC) && isMissioneEstera && !fromDatiSAC) {
                DatiGruppoSAC datiSAC = missioniCMISService.getDatiGruppoSAC(Utility.getUoSiper(uo));
                if (datiSAC != null) {
                    if (datiSAC.getShortName() != null && datiSAC.getShortName().startsWith(Costanti.CDS_SAC)) {
                        String uoPadre = datiSAC.getShortName().substring(0, 6);
                        userNameFirmatario = recuperoDirettore(anno, uoPadre, isMissioneEstera, account, data, isUoRich, true);
                    }
                }
            } else {
                DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(Utility.getUoSigla(uo), anno);
                if (datiIstituto != null && !StringUtils.isEmpty(datiIstituto.getUoRespResponsabili())) {
                    userNameFirmatario = recuperoDirettore(anno, datiIstituto.getUoRespResponsabili(), isMissioneEstera, account, data, isUoRich, fromDatiSAC);
                }
            }
        }

        return userNameFirmatario;
    }

    private String recuperoDirettoreDaUo(Integer anno, String uo, Boolean isMissioneEstera) {
        return recuperoDirettoreDaUo(anno, uo, isMissioneEstera, false);
    }

    private String recuperoDirettoreDaUo(Integer anno, String uo, Boolean isMissioneEstera, Boolean isDaUoRespEstero) {
        String userNameFirmatario;
        DatiIstituto datiIstituto = datiIstitutoService.getDatiIstituto(Utility.getUoSigla(uo), anno);
        if (datiIstituto != null && datiIstituto.getResponsabile() != null) {
            if (!isMissioneEstera || (Utility.nvl(datiIstituto.getResponsabileSoloItalia(), "N").equals("N")) || (isMissioneEstera && isDaUoRespEstero)) {
                userNameFirmatario = datiIstituto.getResponsabile();
            } else {
                if (!StringUtils.isEmpty(datiIstituto.getUoRespEstero()) && !isDaUoRespEstero) {
                    userNameFirmatario = recuperoDirettoreDaUo(anno, datiIstituto.getUoRespEstero(), isMissioneEstera, true);
                } else {
                    userNameFirmatario = getDirettore(uo);
                }
            }
        } else {
            if (isMissioneEstera && Utility.nvl(datiIstituto.getResponsabileSoloItalia(), "N").equals("S") && !StringUtils.isEmpty(datiIstituto.getUoRespEstero()) && !isDaUoRespEstero) {
                userNameFirmatario = recuperoDirettoreDaUo(anno, datiIstituto.getUoRespEstero(), isMissioneEstera, true);
            } else {
                userNameFirmatario = getDirettore(uo);
            }
        }
        if (StringUtils.isEmpty(userNameFirmatario)) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore. Non è stato possibile recuperare il direttore per la uo " + uo);
        }
        return userNameFirmatario;
    }

    // TODO Fine da eliminare-

    public String getDirettore(String uo) {
        List<SimpleUtenteWebDto> utenti = missioniAceService.findUtentiCdsuo(uo, LocalDate.now());
        if (utenti != null && utenti.size() > 0) {
            return missioniAceService.getDirettore(utenti.get(0).getUsername());
        }
        return "";
    }

    public Boolean isUserEnableToWorkUo(String uo) {
        UsersSpecial userSpecial = getUoForUsersSpecial(securityService.getCurrentUserLogin());
        boolean uoAbilitata = false;
        if (userSpecial != null) {
            if (userSpecial.getAll() == null || !userSpecial.getAll().equals("S")) {
                if (userSpecial.getUoForUsersSpecials() != null && !userSpecial.getUoForUsersSpecials().isEmpty()) {
                    for (UoForUsersSpecial uoUser : userSpecial.getUoForUsersSpecials()) {
                        if (uoService.getUoSigla(uoUser).equals(uo)) {
                            uoAbilitata = true;
                        }
                    }
                } else {
                    uoAbilitata = false;
                }
            } else {
                uoAbilitata = true;
            }
        } else {
            uoAbilitata = false;
        }
        return uoAbilitata;
    }

}
