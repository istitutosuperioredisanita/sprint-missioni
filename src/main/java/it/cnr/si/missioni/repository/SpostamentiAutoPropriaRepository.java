package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface SpostamentiAutoPropriaRepository extends
		JpaRepository<OrdineMissione, String> {

	@Query("select a from SpostamentiAutoPropria a where a.ordineMissioneAutoPropria = ?1 and stato != 'ANN' order by riga")
    List<SpostamentiAutoPropria> getSpostamenti(OrdineMissioneAutoPropria ordineMissioneAutoPropria);
    
	@Query("select max(riga) from SpostamentiAutoPropria a where a.ordineMissioneAutoPropria = ?1")
    Long getMaxRigaSpostamenti(OrdineMissioneAutoPropria ordineMissioneAutoPropria);
    
}
