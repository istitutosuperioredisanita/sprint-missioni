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

import it.cnr.si.missioni.domain.custom.persistence.DatiIstituto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface DatiIstitutoRepository extends
        JpaRepository<DatiIstituto, Long> {
    @Lock(LockModeType.WRITE)
    @Query("select a from DatiIstituto a where a.istituto = ?1 and a.anno = ?2")
    DatiIstituto getDatiIstitutoAndLock(String istituto, Integer anno);

    @Query("select a from DatiIstituto a where a.istituto = ?1 and a.anno = ?2")
    DatiIstituto getDatiIstituto(String istituto, Integer anno);

    @Query("select a from DatiIstituto a where a.anno = ?1")
    List<DatiIstituto> getDatiIstituti(Integer anno);
}
