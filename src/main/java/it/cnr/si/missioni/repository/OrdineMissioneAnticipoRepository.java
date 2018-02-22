package it.cnr.si.missioni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface OrdineMissioneAnticipoRepository extends
		JpaRepository<OrdineMissioneAnticipo, Long> {

	@Query("select a from OrdineMissioneAnticipo a where a.ordineMissione = ?1 and a.stato != 'ANN'")
    OrdineMissioneAnticipo getAnticipo(OrdineMissione ordineMissione);
    
	@Query("select a from OrdineMissioneAnticipo a where a.ordineMissione.id = ?1 and a.stato != 'ANN'")
    OrdineMissioneAnticipo getAnticipo(Long idOrdineMissione);
    
}
