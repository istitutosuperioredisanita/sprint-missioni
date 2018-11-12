package it.cnr.si.missioni.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.RimborsoImpegni;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;

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
