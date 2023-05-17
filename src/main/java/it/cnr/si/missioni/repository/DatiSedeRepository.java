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

package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

/**
 * Spring Data JPA repository for the DatiSede entity.
 */
public interface DatiSedeRepository extends
        JpaRepository<DatiSede, Long> {

    @Query("select a from DatiSede a where a.codiceSede = ?1 and a.dataInizio <= ?2 and (a.dataFine is null or a.dataFine >= ?2)")
    DatiSede getDatiSede(String sede, LocalDate data);
}
