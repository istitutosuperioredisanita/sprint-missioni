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


import it.cnr.si.missioni.util.data.UoForUsersSpecial;
import it.cnr.si.missioni.util.data.UsersSpecial;
import it.cnr.si.missioni.util.proxy.json.object.Account;

import java.time.ZonedDateTime;
import java.util.List;


public interface AccountService {


    public UsersSpecial getUoForUsersSpecial(String uid);

    public List<UsersSpecial> getUserSpecialForUo(String uo, Boolean isPerValidazione);

    public Boolean isUserSpecialEnableToValidateOrder(String user, String uo);

    public List<UsersSpecial> getUserSpecialForUoPerValidazione(String uo);

    public Boolean isUtenteAbilitatoUo(List<UoForUsersSpecial> listUo, String uo, Boolean isPerValidazione);

    public String manageResponseForAccountRest(String body);


    public String createResponseForAccountRest(Account account, UsersSpecial user);

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

    public Boolean isUserEnableToWorkUo(String uo);

    public String getDirettore(String uo);

    public String getEmail(String user);

    public String getBodyAccount(Account account);

    public UsersSpecial findOrCreateUserSpecial(String uid);
}
