package it.cnr.si.missioni.repository.specification;

import it.cnr.si.missioni.domain.custom.persistence.OrdineMissione;
import it.cnr.si.missioni.domain.custom.persistence.RimborsoMissione;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Subquery;

public class RimborsoSpecification {

    public static Specification<OrdineMissione> nonRimborsato() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            var rim = sub.from(RimborsoMissione.class);
            sub.select(rim.get("id"))
                    .where(
                            cb.equal(rim.get("ordineMissione").get("id"), root.get("id")),
                            cb.notEqual(rim.get("stato"), "ANN"),
                            cb.notEqual(rim.get("stato"), "ANA")
                    );
            return cb.not(cb.exists(sub));
        };
    }

    public static Specification<OrdineMissione> giaRimborsato() {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            var rim = sub.from(RimborsoMissione.class);
            sub.select(rim.get("id"))
                    .where(
                            cb.equal(rim.get("ordineMissione").get("id"), root.get("id")),
                            cb.notEqual(rim.get("stato"), "ANN"),
                            cb.notEqual(rim.get("stato"), "ANA")
                    );
            return cb.exists(sub);
        };
    }
}