package it.cnr.si.missioni.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for Spring Security.
 */
public final class LdapSecurityUtils {

    private LdapSecurityUtils() {
    }

    /**
     * Get the current Authentication.
     */
    public static Authentication getAuthentication()  {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return authentication;
    }
}
