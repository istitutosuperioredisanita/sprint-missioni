package it.cnr.si.missioni.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissioneDettagli;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface RimborsoMissioneDettagliRepository extends
		JpaRepository<RimborsoMissioneDettagli, Long> {

	@Query("select a from RimborsoMissioneDettagli a where a.rimborsoMissione = ?1 and stato != 'ANN' order by riga")
    List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(RimborsoMissione rimborsoMissione);
    
	@Query("select a from RimborsoMissioneDettagli a where a.rimborsoMissione = ?1 and a.dataSpesa = ?2 and stato != 'ANN' order by riga")
    List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(RimborsoMissione rimborsoMissione, LocalDate data);
    
	@Query("select a from RimborsoMissioneDettagli a where a.idRimborsoImpegni = ?1 and stato != 'ANN' order by riga")
    List<RimborsoMissioneDettagli> getRimborsoMissioneDettagli(Long idRimborsoImpegni);
    
	@Query("select max(riga) from RimborsoMissioneDettagli a where a.rimborsoMissione = ?1")
    Long getMaxRigaDettaglio(RimborsoMissione rimborsoMissione);
    
}
