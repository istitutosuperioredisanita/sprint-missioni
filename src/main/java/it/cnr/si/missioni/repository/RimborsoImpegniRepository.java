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

import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface RimborsoImpegniRepository extends
        JpaRepository<RimborsoImpegni, Long> {

    @Query("select a from RimborsoImpegni a where a.rimborsoMissione = ?1 and stato != 'ANN' order by id")
    List<RimborsoImpegni> getRimborsoImpegni(RimborsoMissione rimborsoMissione);

    @Query("select a from RimborsoImpegni a where a.rimborsoMissione = ?1 and stato != 'ANN' and a.esercizioOriginaleObbligazione = ?2 and a.pgObbligazione = ?3 order by id")
    List<RimborsoImpegni> getRimborsoImpegni(RimborsoMissione rimborsoMissione, Integer esercizio, Long pgObbligazione);
}
