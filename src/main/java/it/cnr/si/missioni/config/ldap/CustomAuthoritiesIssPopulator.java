package it.cnr.si.missioni.config.ldap;

import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.ExternalUserAdminService;
import it.cnr.si.security.AuthoritiesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by francesco on 02/04/15.
 */
@Component
@Profile("iss")
public class CustomAuthoritiesIssPopulator implements LdapAuthoritiesPopulator {

    @Autowired
    ExternalUserAdminService externalUserAdminService;

    private final Logger log = LoggerFactory.getLogger(CustomAuthoritiesIssPopulator.class);

    @Override
    public Collection<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {

        log.debug("security LDAP LdapAuthoritiesPopulator");

        ArrayList<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (externalUserAdminService.isExternalUserAdmin(username))
            list.add(new SimpleGrantedAuthority(Costanti.ROLE_ADMIN));



        return list;
    }
}