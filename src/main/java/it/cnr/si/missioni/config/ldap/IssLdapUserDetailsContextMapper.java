package it.cnr.si.missioni.config.ldap;

import it.cnr.si.missioni.domain.custom.CNRUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Collection;

/**
 * Mapper LDAP che produce un CNRUser come principal.
 * Necessario perché LdapSecurityServiceImpl.getUser() si aspetta CNRUser.
 * Legge givenname, sn e mail dagli attributi configurati in application-iss.yml.
 */
public class IssLdapUserDetailsContextMapper implements UserDetailsContextMapper {

    private static final Logger log = LoggerFactory.getLogger(IssLdapUserDetailsContextMapper.class);

    private final String attrName;
    private final String attrSurname;
    private final String attrMail;

    public IssLdapUserDetailsContextMapper(String attrName, String attrSurname, String attrMail) {
        this.attrName    = attrName    != null ? attrName    : "givenname";
        this.attrSurname = attrSurname != null ? attrSurname : "sn";
        this.attrMail    = attrMail    != null ? attrMail    : "mail";
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx,
                                          String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        CNRUser user = new CNRUser();
        user.setUsername(username);
        user.setAuthorities(authorities);

        // Legge i campi anagrafici dal contesto LDAP ISS
        user.setFirstName(getAttr(ctx, attrName));
        user.setLastName(getAttr(ctx, attrSurname));
        user.setEmail(getAttr(ctx, attrMail));

        // ISS non usa matricola LDAP — lasciamo null (compatibile con getAccountWithoutRole)
        user.setMatricola(null);

        return user;
    }

    @Override
    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        // non necessario (solo lettura)
        throw new UnsupportedOperationException("IssLdapUserDetailsContextMapper is read-only");
    }

    private String getAttr(DirContextOperations ctx, String attrName) {
        try {
            return ctx.getStringAttribute(attrName);
        } catch (Exception e) {
            return null;
        }
    }
}