package it.cnr.si.missioni.repository;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.DatiSede;

/**
 * Spring Data JPA repository for the DatiSede entity.
 */
public interface DatiSedeRepository extends
		JpaRepository<DatiSede, Long> {

	@Query("select a from DatiSede a where a.codiceSede = ?1 and a.dataInizio <= ?2 and (a.dataFine is null or a.dataFine >= ?2)")
	DatiSede getDatiSede(String sede, LocalDate data);
}
