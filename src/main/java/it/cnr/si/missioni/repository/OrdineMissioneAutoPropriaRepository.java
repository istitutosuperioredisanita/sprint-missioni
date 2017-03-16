package it.cnr.si.missioni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface OrdineMissioneAutoPropriaRepository extends
		JpaRepository<OrdineMissioneAutoPropria, Long> {

	@Query("select a from OrdineMissioneAutoPropria a where a.ordineMissione = ?1 and a.stato != 'ANN'")
    OrdineMissioneAutoPropria getAutoPropria(OrdineMissione ordineMissione);
    
}
