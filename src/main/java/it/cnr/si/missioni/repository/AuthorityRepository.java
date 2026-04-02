    package it.cnr.si.missioni.repository;

    import it.cnr.si.missioni.domain.custom.Authority;
    import org.springframework.data.jpa.repository.JpaRepository;

    /**
     * Spring Data JPA repository for the Authority entity.
     */
    public interface AuthorityRepository extends JpaRepository<Authority, String> {
    }
