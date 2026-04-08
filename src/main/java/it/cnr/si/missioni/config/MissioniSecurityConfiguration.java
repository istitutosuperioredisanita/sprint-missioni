package it.cnr.si.missioni.config;

import it.cnr.si.missioni.security.jwt.JWTFilter;
import it.cnr.si.missioni.security.jwt.TokenProvider;
import it.cnr.si.missioni.service.security.AuthoritiesConstants;
import jakarta.inject.Inject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@Profile("!keycloak")
public class MissioniSecurityConfiguration {

    @Inject
    private TokenProvider tokenProvider;

    @Bean
    public HttpFirewall httpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowSemicolon(true);
        return firewall;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(HttpFirewall httpFirewall) {
        return web -> web.httpFirewall(httpFirewall);
    }

    @Bean
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(new JWTFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(
                                        jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized"
                                )
                        )
                )
                .authorizeHttpRequests(authorize -> authorize

                        // richieste preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // risorse pubbliche/statiche
                        .requestMatchers(
                                "/app/**",
                                "/bower_components/**",
                                "/api/rest/public/**",
                                "/i18n/**",
                                "/content/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/test/**",
                                "/views/**",
                                "/SIGLA/**",
                                "/api/profile/info",
                                "/api/rest/flows",
                                "/error"
                        ).permitAll()

                        // endpoint pubblici di autenticazione/account
                        .requestMatchers(
                                "/oauth/token",
                                "/api/register",
                                "/api/activate",
                                "/api/authenticate",
                                "/api/account/reset-password/init",
                                "/api/account/reset-password/finish",
                                "/api/account/reset_password/init",
                                "/api/account/reset_password/finish"
                        ).permitAll()

                        // actuator
                        .requestMatchers("/management/health", "/management/info").permitAll()
                        .requestMatchers("/management/**").hasAuthority(AuthoritiesConstants.ADMIN)

                        // api protette
                        .requestMatchers("/api/**").authenticated()

                        // tutto il resto
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}