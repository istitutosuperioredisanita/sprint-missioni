package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface OrdineMissioneRepository extends
		JpaRepository<OrdineMissione, String> {

	@Query("select a from OrdineMissione a where a.uid = ?1")
    List<OrdineMissione> getOrdiniMissione(String user);
    
}
