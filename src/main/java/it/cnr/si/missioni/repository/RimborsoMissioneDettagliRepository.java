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

import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface RimborsoMissioneDettagliRepository extends
        JpaRepository<RimborsoMissioneDettagli, Long> {

    @Query("select a from RimborsoMissioneDettagli a where a.rimborsoMissione = ?1 and stato != 'ANN' order by riga")
    List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(RimborsoMissione rimborsoMissione);

    @Query("select a from RimborsoMissioneDettagli a where a.rimborsoMissione = ?1 and a.dataSpesa = ?2 and stato != 'ANN' order by riga")
    List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(RimborsoMissione rimborsoMissione, LocalDate data);

    @Query("select a from RimborsoMissioneDettagli a where a.idRimborsoImpegni = ?1 and stato != 'ANN' order by riga")
    List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Long idRimborsoImpegni);

    @Query("select max(riga) from RimborsoMissioneDettagli a where a.rimborsoMissione = ?1")
    Long getMaxRigaDettaglio(RimborsoMissione rimborsoMissione);

}
