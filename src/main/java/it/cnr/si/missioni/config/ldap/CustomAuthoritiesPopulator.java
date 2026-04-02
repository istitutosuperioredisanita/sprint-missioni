package it.cnr.si.missioni.config.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by francesco on 02/04/15.
 */
public class CustomAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    public static final String DEPARTMENT_NUMBER = "departmentnumber";
    public static final String AUTHORITY_PREFIX = "DEPARTMENT_";

    private final Logger log = LoggerFactory.getLogger(CustomAuthoritiesPopulator.class);

    @Override
    public Collection<GrantedAuthority> getGrantedAuthorities(DirContextOperations userData, String username) {

        log.debug("security LDAP LdapAuthoritiesPopulator");

        ArrayList<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (userData != null && userData.attributeExists(DEPARTMENT_NUMBER)) {
            String departmentNumber = userData.getStringAttribute(DEPARTMENT_NUMBER);
            log.debug("add authority {} to user {}", departmentNumber, username);
            list.add(new SimpleGrantedAuthority(AUTHORITY_PREFIX + departmentNumber));
        } else {
            log.debug("no attribute {} defined for user {}", DEPARTMENT_NUMBER, username);
        }


        return list;
    }
}