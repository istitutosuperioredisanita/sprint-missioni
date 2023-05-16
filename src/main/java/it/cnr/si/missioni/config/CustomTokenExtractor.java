package it.cnr.si.missioni.config;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class CustomTokenExtractor extends BearerTokenExtractor {
    @Override
    protected String extractToken(HttpServletRequest request) {
        return Optional.ofNullable(super.extractToken(request))
                .orElseGet(() -> {
                   return request.getParameter("token");
                });
    }
}
