package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface OrdineMissioneAutoPropriaRepository extends
		JpaRepository<OrdineMissione, String> {

	@Query("select a from OrdineMissioneAutoPropria a where a.ordineMissione = ?1 and a.stato != 'ANN'")
    OrdineMissioneAutoPropria getAutoPropria(OrdineMissione ordineMissione);
    
}
