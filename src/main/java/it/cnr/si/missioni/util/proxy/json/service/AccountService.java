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
import it.cnr.si.model.UserInfoDto;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.SecurityService;
import it.cnr.si.service.dto.anagrafica.simpleweb.SimpleUtenteWebDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


public interface AccountService {


    public UsersSpecial getUoForUsersSpecial(String uid) ;

    public List<UsersSpecial> getUserSpecialForUo(String uo, Boolean isPerValidazione) ;

    public Boolean isUserSpecialEnableToValidateOrder(String user, String uo);

    public List<UsersSpecial> getUserSpecialForUoPerValidazione(String uo);

    public Boolean isUtenteAbilitatoUo(List<UoForUsersSpecial> listUo, String uo, Boolean isPerValidazione);

    public String manageResponseForAccountRest(String body);


    public String createResponseForAccountRest(Account account, UsersSpecial user) ;

    public Account loadAccountFromUsername(String currentLogin);

    public Account loadAccount(Boolean loadSpecialUserData);


    public String getAccount(Boolean loadSpecialUserData);

    public String getAccountFromUsername(String username, Boolean loadSpecialUserData);
    public String getResponseAccountWithoutRole();

    public String getResponseAccountWithoutRole(Boolean loadDataFromUserSpecial);

    // TODO Da eliminare-

    public String getDirectorFromSede(String codiceSede);

    public String getDirectorFromUo(String uo);

    // TODO Fine da eliminare-

    public Boolean isUserSpecialEnableToFinalizeOrder(String user, String uo);


    public String recuperoDirettore(Integer anno, String uo, Boolean isMissioneEstera, Account account, ZonedDateTime data, Boolean isUoRich, Boolean fromDatiSAC);

    public String recuperoDirettore(Integer anno, String uo, Boolean isMissioneEstera, Account account, ZonedDateTime data, Boolean isUoRich);

    public Boolean isUserEnableToWorkUo(String uo) ;
    public String getDirettore(String uo) ;

    public String getEmail(String user);

    public String getBodyAccount(Account account);


}
