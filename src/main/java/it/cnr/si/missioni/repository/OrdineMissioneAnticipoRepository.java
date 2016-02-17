package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAnticipo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface OrdineMissioneAnticipoRepository extends
		JpaRepository<OrdineMissione, String> {

	@Query("select a from OrdineMissioneAnticipo a where a.ordineMissione = ?1 and a.stato != 'ANN'")
    OrdineMissioneAnticipo getAnticipo(OrdineMissione ordineMissione);
    
}
