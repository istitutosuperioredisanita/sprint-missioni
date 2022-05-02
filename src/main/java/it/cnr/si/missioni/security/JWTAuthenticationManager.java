package it.cnr.si.missioni.security;

import it.cnr.si.missioni.service.MissioniAceService;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.security.AuthoritiesConstants;
import it.cnr.si.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class JWTAuthenticationManager implements AuthenticationManager {

    private final Logger log = LoggerFactory.getLogger(JWTAuthenticationManager.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private MissioniAceService missioniAceService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String principal = (String) authentication.getPrincipal();
        String credentials = (String) authentication.getCredentials();

        authService.getToken(principal, credentials);

        List<GrantedAuthority> authorities = missioniAceService.getGrantedAuthorities(principal);

        if (authorities.stream().filter(auth -> auth.getAuthority().equals(Costanti.AMMINISTRATORE_MISSIONI)).count() > 0){
            authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
        }

        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.USER));

        User utente = new User(principal.toLowerCase(), credentials, authorities);

        return new UsernamePasswordAuthenticationToken(utente, authentication, authorities);

    }

}
