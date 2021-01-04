package it.cnr.si.missioni.config;
import it.cnr.si.missioni.util.SecurityUtils;
import it.cnr.si.missioni.util.proxy.json.JSONBody;
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
import java.security.Principal;
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
        if (!uri.startsWith("/styles") && !uri.startsWith("/scripts") && !uri.startsWith("/fonts") && !uri.startsWith("/images") && !uri.contains("authentication_check.gif")  && SecurityUtils.getCurrentUser() != null &&
                !uri.startsWith("/api/rest/terzoPerCompenso")){
            String payload = "";
            if (body instanceof JSONBody){
                JSONBody jb = (JSONBody)body;
                if (jb.getClauses() != null){
                    payload = jb.getClauses().stream().map(Object::toString).collect(Collectors.joining(","));
                } else {
                    payload = body.toString();
                }
            } else {
                payload = body.toString();
            }
            log.info(((Principal) SecurityUtils.getCurrentUser()).getName()+" "+httpServletRequest.getMethod()+" "+httpServletRequest.getRequestURI()+" "+httpServletRequest.getQueryString()+" "+payload+" "+httpServletRequest.getRemoteAddr());
        }

        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }
}