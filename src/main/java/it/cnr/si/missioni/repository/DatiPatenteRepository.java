package it.cnr.si.missioni.repository;

import it.cnr.si.missioni.domain.custom.persistence.DatiPatente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface DatiPatenteRepository extends JpaRepository<DatiPatente, String> {
    @Query("select a from DatiPatente a where a.uid = ?1")
    DatiPatente getDatiPatente(String user);
}
