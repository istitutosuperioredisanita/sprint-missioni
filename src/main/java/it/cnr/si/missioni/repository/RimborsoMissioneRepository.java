package it.cnr.si.missioni.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface RimborsoMissioneRepository extends
		JpaRepository<OrdineMissione, String> {

	@Query("select a from RimborsoMissione a where a.uid = ?1")
    List<RimborsoMissione> getRimborsiMissione(String user);
    
}
