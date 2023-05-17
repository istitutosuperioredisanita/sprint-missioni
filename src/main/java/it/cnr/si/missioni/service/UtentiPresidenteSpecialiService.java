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

import it.cnr.si.missioni.util.data.UtentePresidenteSpeciale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class UtentiPresidenteSpecialiService {
    private final Logger log = LoggerFactory.getLogger(UoService.class);

    @Autowired
    ConfigService configService;

    public UtentePresidenteSpeciale esisteUtente(String uid) {
        if (configService.getUtentiPresidenteSpeciali() != null
                && configService.getUtentiPresidenteSpeciali().getUtentePresidenteSpeciale() != null) {
            for (Iterator<UtentePresidenteSpeciale> iteratorUsers = configService.getUtentiPresidenteSpeciali()
                    .getUtentePresidenteSpeciale().iterator(); iteratorUsers.hasNext(); ) {
                UtentePresidenteSpeciale user = iteratorUsers.next();
                log.debug("Ricerca amministrativi per mail. Utente: " + user.getCodiceUtente());
                if (user != null && user.getCodiceUtente().equals(uid)) {
                    return user;
                }
            }
        }
        return null;
    }

}
