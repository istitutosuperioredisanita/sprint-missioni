package it.cnr.si.missioni.web.rest;

import it.cnr.si.missioni.web.rest.errors.ErrorVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalDefaultExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler({AuthorizationDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorVM> handleAccessDenied(Exception ex) {
        log.warn("Access denied handled by {}", GlobalDefaultExceptionHandler.class.getCanonicalName(), ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorVM("error.403", "Access Denied"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorVM> processRuntimeException(RuntimeException ex) {
        log.error("Exception handled by {}", GlobalDefaultExceptionHandler.class.getCanonicalName(), ex);

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            return ResponseEntity
                    .status(responseStatus.value())
                    .body(new ErrorVM("error." + responseStatus.value().value(), responseStatus.reason()));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorVM(ex.getLocalizedMessage() != null ? ex.getLocalizedMessage() : "Errore interno"));
    }
}