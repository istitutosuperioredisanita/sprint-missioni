package it.cnr.si.missioni.config;

import it.cnr.si.missioni.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Component
public class MissioniLoggingGet implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger("Log Request");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {

        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())
                && request.getMethod().equals(HttpMethod.GET.name()) && SecurityUtils.getCurrentUser() != null) {
            String uri = request.getRequestURI();
            if (!uri.startsWith("/styles") && !uri.startsWith("/scripts") && !uri.startsWith("/fonts") && !uri.startsWith("/images") && !uri.contains("authentication_check.gif")  && !uri.endsWith("ico") && !uri.endsWith("png") && SecurityUtils.getCurrentUser() != null ){
                log.info(((Principal) SecurityUtils.getCurrentUser()).getName()+" "+request.getMethod()+" "+request.getRequestURI()+" "+request.getQueryString()+" "+request.getRemoteAddr());
            }
        }

        return true;
    }
}