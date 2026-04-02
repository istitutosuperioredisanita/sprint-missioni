package it.cnr.si.missioni.service.ldap;

import it.cnr.si.missioni.domain.custom.CNRUser;
import it.cnr.si.missioni.model.UserInfoDto;
import it.cnr.si.missioni.service.security.AuthoritiesConstants;
import it.cnr.si.missioni.service.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@Profile("!keycloak")
public class LdapSecurityServiceImpl implements SecurityService, InitializingBean {

    private final Logger log = LoggerFactory.getLogger(LdapSecurityServiceImpl.class);

    public Optional<CNRUser> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.warn("authentication is null");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CNRUser cnrUser) {
            return Optional.of(cnrUser);
        }

        if (principal instanceof UserDetails userDetails) {
            if (userDetails instanceof CNRUser cnrUser) {
                return Optional.of(cnrUser);
            }
            log.warn("principal is UserDetails but not {}", CNRUser.class.getSimpleName());
            return Optional.empty();
        }

        log.warn("principal is not an instance of {}", CNRUser.class.getSimpleName());
        return Optional.empty();
    }

    public Optional<String> getMatricola() {
        return getUser().map(CNRUser::getMatricola);
    }

    @Override
    public Optional<UserInfoDto> getUserInfo() {
        return Optional.empty();
    }

    public String getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        String userName = null;

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof UserDetails springSecurityUser) {
                userName = springSecurityUser.getUsername();
            } else if (principal instanceof String) {
                userName = (String) principal;
            }
        }

        return userName;
    }

    public boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (AuthoritiesConstants.ANONYMOUS.equals(authority.getAuthority())) {
                    return false;
                }
            }
        }
        return authentication.isAuthenticated();
    }

    public boolean isCurrentUserInRole(String authority) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(authority));
        }

        return false;
    }

    @Override
    public void afterPropertiesSet() {
        log.info("Initialize SecurityService of sprint-ldap");
    }
}