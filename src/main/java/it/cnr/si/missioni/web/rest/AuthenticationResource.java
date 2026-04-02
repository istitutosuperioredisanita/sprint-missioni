package it.cnr.si.missioni.web.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.cnr.si.missioni.security.jwt.TokenProvider;
import it.cnr.si.missioni.web.rest.vm.LoginVM;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthenticationResource {

    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResource(TokenProvider tokenProvider,
                                  AuthenticationManager authenticationManager) {
        this.tokenProvider = tokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JWTToken> authorize(@RequestBody LoginVM loginVM) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        loginVM.getUsername(),
                        loginVM.getPassword()
                );
        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication, loginVM.isRememberMe());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    static class JWTToken {
        private final String idToken;

        JWTToken(String idToken) { this.idToken = idToken; }

        @JsonProperty("id_token")
        String getIdToken() { return idToken; }
    }
}