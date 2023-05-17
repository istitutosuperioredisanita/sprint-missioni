/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.config;

import it.cnr.si.missioni.util.SecurityUtils;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class MissioniLoggingGet extends HandlerInterceptorAdapter {
    private final Logger log = LoggerFactory.getLogger("Log Request");

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {


        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name()) && SecurityUtils.getCurrentUser() != null) {
            String uri = request.getRequestURI();
            if (!uri.startsWith("/styles") && !uri.startsWith("/scripts") && !uri.startsWith("/fonts") && !uri.startsWith("/images") && !uri.contains("authentication_check.gif") && !uri.endsWith("ico") && !uri.endsWith("png") && SecurityUtils.getCurrentUser() != null) {
                Object principal = SecurityUtils.getCurrentUser().getPrincipal();
                if (principal instanceof KeycloakPrincipal) {
                    KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
                    String username = "";
                    if (kPrincipal != null && kPrincipal.getKeycloakSecurityContext() != null && kPrincipal.getKeycloakSecurityContext().getIdToken() != null) {
                        username = kPrincipal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();
                    }
                    log.info("{} {} {} {} {} ", username, request.getMethod(), request.getRequestURI(), request.getQueryString(), request.getRemoteAddr());
                }
            }
        }
        return true;
    }
}