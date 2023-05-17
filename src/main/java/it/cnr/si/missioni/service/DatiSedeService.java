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

import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.repository.DatiSedeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * Service class for managing users.
 */
@Service
public class DatiSedeService {

    private final Logger log = LoggerFactory.getLogger(DatiSedeService.class);

    @Autowired
    private DatiSedeRepository datiSedeRepository;

    @Transactional(readOnly = true)
    public DatiSede getDatiSede(String sede, LocalDate data) {
        return datiSedeRepository.getDatiSede(sede, data);
    }

    @Transactional(readOnly = true)
    public DatiSede getDatiSede(String sede, ZonedDateTime data) {
        return getDatiSede(sede, data.toLocalDate());
    }
}
