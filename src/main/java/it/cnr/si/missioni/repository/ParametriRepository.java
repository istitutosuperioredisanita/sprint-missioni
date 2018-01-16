package it.cnr.si.missioni.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.DatiSede;
import it.cnr.si.missioni.domain.custom.persistence.Parametri;

/**
 * Spring Data JPA repository for the DatiSede entity.
 */
public interface ParametriRepository extends
		JpaRepository<DatiSede, Long> {

	@Query("select a from Parametri a")
	Parametri getParametri();
}
