package it.cnr.si.missioni.config;

import it.cnr.si.config.SecurityConfiguration;
import it.cnr.si.security.jwt.JWTConfigurer;
import it.cnr.si.security.jwt.TokenProvider;

public class MissioniConfiguration extends SecurityConfiguration {

    private final TokenProvider tokenProvider;

    public MissioniConfiguration(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }

}
