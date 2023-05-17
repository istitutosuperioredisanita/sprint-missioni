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
import it.cnr.si.missioni.util.proxy.json.JSONBody;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.stream.Collectors;

@ControllerAdvice
public class MissioniLoggingAdapter extends RequestBodyAdviceAdapter {

    private final Logger log = LoggerFactory.getLogger("Log Request");
    @Autowired
    HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(MethodParameter methodParameter, Type type,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage,
                                MethodParameter parameter, Type targetType,
                                Class<? extends HttpMessageConverter<?>> converterType) {

        String uri = httpServletRequest.getRequestURI();
        if (!uri.startsWith("/styles") && !uri.startsWith("/scripts") && !uri.startsWith("/fonts") && !uri.startsWith("/images") && !uri.contains("authentication_check.gif") && SecurityUtils.getCurrentUser() != null &&
                !uri.startsWith("/api/rest/terzoPerCompenso")) {
            String payload = "";
            if (body instanceof JSONBody) {
                JSONBody jb = (JSONBody) body;
                if (jb.getClauses() != null) {
                    payload = jb.getClauses().stream().map(Object::toString).collect(Collectors.joining(","));
                } else {
                    payload = body.toString();
                }
            } else {
                payload = body.toString();
            }
            Object principal = SecurityUtils.getCurrentUser().getPrincipal();
            if (principal instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kPrincipal = (KeycloakPrincipal<KeycloakSecurityContext>) principal;
                String username = "";
                if (kPrincipal != null && kPrincipal.getKeycloakSecurityContext() != null && kPrincipal.getKeycloakSecurityContext().getIdToken() != null) {
                    username = kPrincipal.getKeycloakSecurityContext().getIdToken().getPreferredUsername();
                }
                log.info("{} {} {} {} {} {} ", username, httpServletRequest.getMethod(), httpServletRequest.getRequestURI(), httpServletRequest.getQueryString(), payload, httpServletRequest.getRemoteAddr());
            }
        }

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}