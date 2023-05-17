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
import it.cnr.si.missioni.repository.CRUDComponentSession;
import it.cnr.si.missioni.repository.DatiPatenteRepository;
import it.cnr.si.missioni.util.CodiciErrore;
import it.cnr.si.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Service class for managing users.
 */
@Service
public class DatiPatenteService {

    private final Logger log = LoggerFactory.getLogger(DatiPatenteService.class);

    @Autowired
    private DatiPatenteRepository datiPatenteRepository;

    @Autowired
    private CRUDComponentSession crudServiceBean;

    @Autowired
    private SecurityService securityService;

    @Transactional(readOnly = true)
    public DatiPatente getDatiPatente(String user) {
        return datiPatenteRepository.getDatiPatente(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente createDatiPatente(DatiPatente datiPatente) {
        datiPatente.setUser(securityService.getCurrentUserLogin());
        datiPatente.setToBeCreated();
        //effettuo controlli di validazione operazione CRUD
        validaCRUD(datiPatente);
        datiPatente = (DatiPatente) crudServiceBean.creaConBulk(datiPatente);
        log.debug("Created Information for Dati Patente: {}", datiPatente);
        return datiPatente;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatiPatente updateDatiPatente(DatiPatente datiPatente) {

        DatiPatente datiPatenteDB = (DatiPatente) crudServiceBean.findById(DatiPatente.class, datiPatente.getId());

        if (datiPatenteDB == null)
            throw new AwesomeException(CodiciErrore.ERRGEN, "Dati patente da aggiornare inesistente.");

        datiPatenteDB.setNumero(datiPatente.getNumero());
        datiPatenteDB.setDataRilascio(datiPatente.getDataRilascio());
        datiPatenteDB.setDataScadenza(datiPatente.getDataScadenza());
        datiPatenteDB.setEnte(datiPatente.getEnte());
        datiPatenteDB.setToBeUpdated();

        //effettuo controlli di validazione operazione CRUD
        validaCRUD(datiPatenteDB);

        datiPatente = (DatiPatente) crudServiceBean.modificaConBulk(datiPatenteDB);

//    	autoPropriaRepository.save(autoPropria);
        log.debug("Updated Information for Dati Patente: {}", datiPatente);
        return datiPatente;
    }

    private void validaCRUD(DatiPatente datiPatente) {
        Date oggi = new Date(System.currentTimeMillis());
        if (datiPatente.getDataRilascio() != null) {
            if (oggi.before(datiPatente.getDataRilascio())) {
                throw new AwesomeException(CodiciErrore.ERRGEN, "La data di rilascio della patente non può essere successiva alla data odierna.");
            }
            if (datiPatente.getDataScadenza() != null) {
                if (datiPatente.getDataScadenza().before(datiPatente.getDataRilascio())) {
                    throw new AwesomeException(CodiciErrore.ERRGEN, "La data di rilascio della patente non può essere successiva alla data di scadenza.");
                }
            }
        }
    }
}
