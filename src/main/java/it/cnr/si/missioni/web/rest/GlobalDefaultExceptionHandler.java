package it.cnr.si.missioni.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import it.cnr.si.web.rest.errors.ErrorVM;

@RestControllerAdvice
public class GlobalDefaultExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorVM> processRuntimeException(Exception ex) throws Exception {
        ResponseEntity.BodyBuilder builder;
        ErrorVM errorVM;

        log.error("exception handled by " + GlobalDefaultExceptionHandler.class.getCanonicalName(), ex);

        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            builder = ResponseEntity.status(responseStatus.value());
            errorVM = new ErrorVM("error." + responseStatus.value().value(), responseStatus.reason());
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            errorVM = new ErrorVM(ex.getLocalizedMessage());
        }
        return builder.body(errorVM);
    }

}
