package it.cnr.si.missioni.repository.specification;

import it.cnr.si.missioni.util.SecurityUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;

public class SecuritySpecification {

    public static <T> Specification<T> forUser(String username) {
        final String user;
        if (username != null && !username.isEmpty()) {
            user = username;
        } else {
            Authentication auth = SecurityUtils.getCurrentUser();
            user = auth != null ? auth.getName() : null;
        }
        return (root, query, cb) -> cb.equal(root.get("uid"), user);
    }

    public static <T> Specification<T> responsabileGruppo(String username) {
        final String user;
        if (username != null && !username.isEmpty()) {
            user = username;
        } else {
            Authentication auth = SecurityUtils.getCurrentUser();
            user = auth != null ? auth.getName() : null;
        }
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("responsabileGruppo"), user),
                cb.notEqual(root.get("stato"), "INS")
        );
    }
}