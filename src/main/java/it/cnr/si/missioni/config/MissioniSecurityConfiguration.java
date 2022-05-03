package it.cnr.si.missioni.config;

import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.security.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpointHandlerMapping;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("!keycloak")
@Order(1)
public class MissioniSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthorizationServerEndpointsConfiguration endpoints;

    @Autowired
    private List<AuthorizationServerConfigurer> configurers = Collections.emptyList();

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    public void configure(ClientDetailsServiceConfigurer clientDetails) throws Exception {
        for (AuthorizationServerConfigurer configurer : configurers) {
            configurer.configure(clientDetails);
        }
    }

    protected void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        for (AuthorizationServerConfigurer configurer : configurers) {
            configurer.configure(oauthServer);
        }
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

        web.ignoring()
        	.antMatchers("/views/**")
            .antMatchers("/api/rest/public/**")
            .antMatchers("/SIGLA/**")
                .antMatchers("/api/profile/info")
                .antMatchers("/api/rest/flows")
        ;
        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthorizationServerSecurityConfigurer configurer = new AuthorizationServerSecurityConfigurer();
        FrameworkEndpointHandlerMapping handlerMapping = endpoints.oauth2EndpointHandlerMapping();
        http.setSharedObject(FrameworkEndpointHandlerMapping.class, handlerMapping);
        configure(configurer);
        http.apply(configurer);
        String tokenEndpointPath = handlerMapping.getServletPath("/oauth/token");
        String tokenKeyPath = handlerMapping.getServletPath("/oauth/token_key");
        String checkTokenPath = handlerMapping.getServletPath("/oauth/check_token");
        if (!endpoints.getEndpointsConfigurer().isUserDetailsServiceOverride()) {
            UserDetailsService userDetailsService = http.getSharedObject(UserDetailsService.class);
            endpoints.getEndpointsConfigurer().userDetailsService(userDetailsService);
        }

        http
                .csrf()
                .disable()
                .exceptionHandling()
                .and()
                .headers()
                .frameOptions()
                .disable()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/activate").permitAll()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/account/reset-password/init").permitAll()
                .antMatchers("/api/account/reset-password/finish").permitAll()
                .antMatchers("/api/**").authenticated()
                .antMatchers("/management/health").permitAll()
                .antMatchers("/management/info").permitAll()
                .antMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers(tokenEndpointPath).fullyAuthenticated()
                .antMatchers(tokenKeyPath).access(configurer.getTokenKeyAccess())
                .antMatchers(checkTokenPath).access(configurer.getCheckTokenAccess())
                .and()
                .requestMatchers()
                .antMatchers(tokenEndpointPath, tokenKeyPath, checkTokenPath)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
        // @formatter:on
        http.setSharedObject(ClientDetailsService.class, clientDetailsService);


    }

}
