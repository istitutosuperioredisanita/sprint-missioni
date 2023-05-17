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

package it.cnr.si.missioni.service.showcase;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.missioni.util.Utility;
import it.cnr.si.service.dto.anagrafica.UserInfoDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Iterator;

@Service
@Profile("showcase")
public class ACEService {
    public UserInfoDto getUtenteAdmin(String user) {
        InputStream is = this.getClass().getResourceAsStream("/it/cnr/missioni/showcase/UserInfo.json");
        try {
            Users users = new ObjectMapper().readValue(is, Users.class);
            for (Iterator<UserInfoDto> iteratorUsers = users.getUsers().iterator(); iteratorUsers.hasNext(); ) {
                UserInfoDto userInfoDto = iteratorUsers.next();
                if (userInfoDto.getUid() != null && userInfoDto.getUid().equalsIgnoreCase(user)) {
                    return userInfoDto;
                }
            }

        } catch (Exception e) {
            throw new AwesomeException(CodiciErrore.ERRGEN, "Errore in fase di lettura del file JSON degli utenti per lo showcase." + Utility.getMessageException(e));
        }
        return null;
    }
}
