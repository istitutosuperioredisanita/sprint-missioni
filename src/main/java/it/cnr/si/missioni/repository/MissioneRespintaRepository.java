package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.MissioneRespinta;
import it.cnr.si.missioni.domain.custom.persistence.OrdineMissioneAutoPropria;
import it.cnr.si.missioni.domain.custom.persistence.SpostamentiAutoPropria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface MissioneRespintaRepository extends
		JpaRepository<MissioneRespinta, Long> {

	@Query("select a from MissioneRespinta a where a.idMissione = ?1 and a.tipoOperazioneMissione = ?2 order by dataInserimento desc")
    List<MissioneRespinta> getRespingimenti(Long idMissione, String tipoMissione);
}
