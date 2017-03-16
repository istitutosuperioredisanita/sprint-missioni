package it.cnr.si.missioni.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface SpostamentiAutoPropriaRepository extends
		JpaRepository<SpostamentiAutoPropria, Long> {

	@Query("select a from SpostamentiAutoPropria a where a.ordineMissioneAutoPropria = ?1 and stato != 'ANN' order by riga")
    List<SpostamentiAutoPropria> getSpostamenti(OrdineMissioneAutoPropria ordineMissioneAutoPropria);
    
	@Query("select max(riga) from SpostamentiAutoPropria a where a.ordineMissioneAutoPropria = ?1")
    Long getMaxRigaSpostamenti(OrdineMissioneAutoPropria ordineMissioneAutoPropria);
    
}
