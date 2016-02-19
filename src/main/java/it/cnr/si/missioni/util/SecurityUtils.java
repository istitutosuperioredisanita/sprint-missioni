package it.cnr.si.missioni.util;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Created by francesco on 15/01/15.
 */
public class SecurityUtils {
    public static Principal getCurrentUser() {
        return (Principal)SecurityContextHolder.getContext().getAuthentication();
    }
    public static String getCurrentUserLogin(){
    	return it.cnr.si.security.SecurityUtils.getCurrentUserLogin();
    }
}
