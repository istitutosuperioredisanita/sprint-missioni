package it.cnr.si.missioni.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.util.StringUtils;

/**
 * Custom token resolver:
 * - legge il token da Authorization header (Bearer ...)
 * - fallback su parametro query ?token=
 */
public class CustomTokenExtractor implements BearerTokenResolver {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TOKEN_PARAMETER = "token";

    @Override
    public String resolve(HttpServletRequest request) {
        String token = resolveFromHeader(request);

        if (!StringUtils.hasText(token)) {
            token = request.getParameter(TOKEN_PARAMETER);
        }

        return token;
    }

    private String resolveFromHeader(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
            return authorization.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}