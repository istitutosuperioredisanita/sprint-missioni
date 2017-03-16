package it.cnr.si.missioni.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.cnr.si.missioni.domain.custom.persistence.AutoPropria;

/**
 * Spring Data JPA repository for the AutoPropria entity.
 */
public interface AutoPropriaRepository extends JpaRepository<AutoPropria, Long> {
    
    @Query("select a from AutoPropria a where a.uid = ?1")
    List<AutoPropria> getAutoProprie(String user);
    
    @Query("select a from AutoPropria a where a.uid = ?1 and a.targa = ?2")
    AutoPropria getAutoPropria(String user, String targa);
    
//    @Query("select u from User u where u.activated = false and u.createdDate > ?1")
//    List<User> findNotActivatedUsersByCreationDateBefore(DateTime dateTime);
//
//    User findOneByEmail(String email);
}
