package it.cnr.si.missioni.config;

import it.cnr.si.config.SecurityConfiguration;
import it.cnr.si.missioni.security.jwt.JWTConfigurer;
import it.cnr.si.missioni.security.jwt.TokenProvider;

public class MissioniConfiguration extends SecurityConfiguration {

    private final TokenProvider tokenProvider;

    public MissioniConfiguration(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

}
