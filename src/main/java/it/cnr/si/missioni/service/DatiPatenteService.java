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
import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;
import it.cnr.si.missioni.repository.DatiPatenteRepository;
import it.cnr.si.missioni.service.security.SecurityService;
import it.cnr.si.missioni.util.CodiciErrore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service class for managing users.
 */
@Service
public class DatiPatenteService {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteService.class);

    @Autowired
    private DatiPatenteRepository datiPatenteRepository;


    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public DatiPatente getDatiPatente(String user) {
        return datiPatenteRepository.getDatiPatente(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente createDatiPatente(DatiPatente datiPatente) {
        datiPatente.setUid(securityService.getCurrentUserLogin());
        datiPatente.setToBeCreated();
        validaCRUD(datiPatente);
        datiPatente = datiPatenteRepository.save(datiPatente);
        log.debug("Created Information for Dati Patente: {}", datiPatente);

        return datiPatente;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente updateDatiPatente(DatiPatente datiPatente) {

        DatiPatente datiPatenteDB = datiPatenteRepository
                .findById((Long) datiPatente.getId())
                .orElseThrow(() -> new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "Dati patente da aggiornare inesistente."
                ));

        datiPatenteDB.setNumero(datiPatente.getNumero());
        datiPatenteDB.setDataRilascio(datiPatente.getDataRilascio());
        datiPatenteDB.setDataScadenza(datiPatente.getDataScadenza());
        datiPatenteDB.setEnte(datiPatente.getEnte());
        datiPatenteDB.setToBeUpdated();

        validaCRUD(datiPatenteDB);

        datiPatenteDB = datiPatenteRepository.save(datiPatenteDB);

        log.debug("Updated Information for Dati Patente: {}", datiPatenteDB);
        return datiPatenteDB;
    }

    private void validaCRUD(DatiPatente datiPatente) {

        LocalDateTime oggi = LocalDateTime.now();
        if (datiPatente.getDataRilascio() != null) {
            // oggi prima della data rilascio = errore
            if (oggi.isBefore(datiPatente.getDataRilascio())) {
                throw new AwesomeException(
                        CodiciErrore.ERRGEN,
                        "La data di rilascio della patente non può essere successiva alla data odierna."
                );
            }
            if (datiPatente.getDataScadenza() != null) {
                // scadenza prima di rilascio = errore
                if (datiPatente.getDataScadenza().isBefore(datiPatente.getDataRilascio())) {
                    throw new AwesomeException(
                            CodiciErrore.ERRGEN,
                            "La data di rilascio della patente non può essere successiva alla data di scadenza."
                    );
                }
            }
        }
    }
}
