package it.cnr.si.missioni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.AnnullamentoRimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface AnnullamentoRimborsoMissioneRepository extends
		JpaRepository<AnnullamentoRimborsoMissione, Long> {

	@Query("select a from AnnullamentoRimborsoMissione a where a.rimborsoMissione = ?1 and a.stato != 'ANN'")
	AnnullamentoRimborsoMissione getAnnullamentoRimborsoMissione(RimborsoMissione rimborsoMissione);
    
}
