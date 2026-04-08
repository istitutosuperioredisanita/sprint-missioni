package it.cnr.si.missioni.web.rest;

import it.cnr.si.missioni.service.security.AuthoritiesConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api")
public class AccountLDAPResource extends AbstractAccountResource {

    @Override
    protected boolean isUserWithRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities == null) {
            return false;
        }
        return authorities.stream()
                .anyMatch(a -> AuthoritiesConstants.USER.equals(a.getAuthority())
                        || AuthoritiesConstants.ADMIN.equals(a.getAuthority()));
    }
}