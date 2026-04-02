package it.cnr.si.missioni.service.security;

import it.cnr.si.missioni.domain.custom.CNRUser;
import it.cnr.si.missioni.model.UserInfoDto;

import java.util.Optional;

public interface SecurityService {
    Optional<CNRUser> getUser ();
    Optional<String> getMatricola();
    Optional<UserInfoDto> getUserInfo();
    String getCurrentUserLogin();
    boolean isAuthenticated();
    boolean isCurrentUserInRole(String authority);
}
