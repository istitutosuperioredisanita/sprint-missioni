package it.cnr.si.missioni.config.ldap;

import it.cnr.si.missioni.config.ldap.CustomAuthoritiesPopulator;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.ldap.LdapBindAuthenticationManagerFactory;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import java.util.Properties;

@Configuration
@Profile("!keycloak")
@ConditionalOnProperty(prefix = "spring.ldap", name = "enabled", havingValue = "true")
public class SecurityConfigurationLDAP {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfigurationLDAP.class);

    @Inject
    private Environment env;

    private Properties ldapProperties() {
        return Binder.get(env)
                .bind("spring.ldap", Properties.class)
                .orElse(new Properties());
    }

    @Bean
    public UserDetailsContextMapper userDetailsContextMapper() {
        return new LdapUserDetailsMapper();
    }

    @Bean
    public LdapContextSource ldapContextSource() {
        Properties properties = ldapProperties();
        String url = properties.getProperty("url");

        if (url == null || url.isBlank()) {
            throw new IllegalStateException("No LDAP configuration found: spring.ldap.url is missing");
        }

        log.info("ldap server: {}", url);

        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(url);
        contextSource.setUserDn(properties.getProperty("managerDn"));
        contextSource.setPassword(properties.getProperty("managerPassword"));
        contextSource.afterPropertiesSet();
        return contextSource;
    }

    @Bean(name = "ldapAuthenticationManager")
    public AuthenticationManager ldapAuthenticationManager(
            LdapContextSource ldapContextSource,
            UserDetailsContextMapper userDetailsContextMapper) {

        Properties properties = ldapProperties();

        LdapBindAuthenticationManagerFactory factory =
                new LdapBindAuthenticationManagerFactory(ldapContextSource);

        factory.setUserSearchBase(properties.getProperty("userSearchBase"));
        factory.setUserSearchFilter(properties.getProperty("userSearchFilter"));
        factory.setUserDetailsContextMapper(userDetailsContextMapper);
        factory.setLdapAuthoritiesPopulator(new CustomAuthoritiesPopulator());

        return factory.createAuthenticationManager();
    }
}