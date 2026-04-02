package it.cnr.si.missioni.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.cnr.si.missioni.security.jwt.TokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class OAuthCompatibilityResource {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public OAuthCompatibilityResource(TokenProvider tokenProvider,
                                      AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Endpoint di compatibilità con il frontend AngularJS legacy.
     * Accetta la stessa chiamata che faceva Spring Security OAuth2:
     *   POST /oauth/token
     *   Content-Type: application/x-www-form-urlencoded
     *   username=...&password=...&grant_type=password&...
     */
    @PostMapping(
            value = "/oauth/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<OAuthTokenResponse> token(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam(value = "grant_type", defaultValue = "password") String grantType
    ) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication, false);

        // Risposta compatibile con il formato OAuth2 che il frontend si aspetta
        return ResponseEntity.ok(new OAuthTokenResponse(jwt));
    }

    static class OAuthTokenResponse {

        @JsonProperty("access_token")
        private final String accessToken;

        @JsonProperty("token_type")
        private final String tokenType = "bearer";

        @JsonProperty("expires_in")
        private final int expiresIn = 1800;

        @JsonProperty("scope")
        private final String scope = "read write";

        OAuthTokenResponse(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() { return accessToken; }
        public String getTokenType() { return tokenType; }
        public int getExpiresIn() { return expiresIn; }
        public String getScope() { return scope; }
    }
}